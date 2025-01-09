package de.deltaga.eb;

import java.lang.management.ManagementFactory;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public final class BasicEventBus implements EventBus, BasicEventBusMBean {
   static final Logger logger = Logger.getLogger("EventBus");
   private final List<BasicEventBus.HandlerInfo> handlers = new CopyOnWriteArrayList();
   private final BlockingQueue<Object> queue = new LinkedBlockingQueue();
   private final BlockingQueue<BasicEventBus.HandlerInfo> killQueue = new LinkedBlockingQueue();
   private final Map<Class<?>, BasicEventBus.HandlerTypeInfo> handlerTypeExecutor = new ConcurrentHashMap();
   private final Map<String, ExecutorService> executorGroup = new ConcurrentHashMap();
   private final ExecutorService executorService;
   private final boolean waitForHandlers;
   private final ReentrantLock putLock = new ReentrantLock();
   private final Condition notFull = this.putLock.newCondition();
   private boolean shutdown = false;
   private final MBeanServer mbs;
   private ObjectName name;
   private static final EventPublishListener publishListenerNullPattern = new EventPublishListener() {
      @Override
      public void published(Class<?> type, Object event) {
      }
   };

   public BasicEventBus() {
      this(Executors.newCachedThreadPool(new ThreadFactory() {
         private final ThreadFactory delegate = Executors.defaultThreadFactory();

         public Thread newThread(Runnable r) {
            Thread t = this.delegate.newThread(r);
            t.setDaemon(true);
            return t;
         }
      }), false);
   }

   public BasicEventBus(ExecutorService executorService, boolean waitForHandlers) {
      Thread eventQueueThread = new Thread(new BasicEventBus.EventQueueRunner(), "EventQueue Consumer Thread");
      eventQueueThread.setDaemon(true);
      eventQueueThread.start();
      Thread killQueueThread = new Thread(new BasicEventBus.KillQueueRunner(), "KillQueue Consumer Thread");
      killQueueThread.setDaemon(true);
      killQueueThread.start();
      this.executorService = executorService;
      this.waitForHandlers = waitForHandlers;
      this.mbs = ManagementFactory.getPlatformMBeanServer();

      try {
         this.name = new ObjectName("de.deltaga.EventBus:type=Master");
         this.mbs.registerMBean(this, this.name);
      } catch (Exception var6) {
      }
   }

   @Override
   public void subscribe(Object subscriber) {
      boolean subscribedAlready = false;

      for (BasicEventBus.HandlerInfo info : this.handlers) {
         Object otherSubscriber = info.getSubscriber();
         if (otherSubscriber == null) {
            try {
               this.killQueue.put(info);
            } catch (InterruptedException var11) {
               logger.log(Level.SEVERE, null, var11);
            }
         } else if (subscriber == otherSubscriber) {
            subscribedAlready = true;
         }
      }

      if (!subscribedAlready) {
         Method[] methods = subscriber.getClass().getDeclaredMethods();

         for (Method method : methods) {
            EventHandler eh = (EventHandler)method.getAnnotation(EventHandler.class);
            if (eh != null) {
               Class<?>[] parameters = method.getParameterTypes();
               if (parameters.length != 1) {
                  throw new IllegalArgumentException("EventHandler methods must specify a single Object paramter.");
               }

               BasicEventBus.HandlerInfo infox;
               if (eh.weak()) {
                  infox = new BasicEventBus.WeakHandlerInfo(parameters[0], method, subscriber, eh);
               } else {
                  infox = new BasicEventBus.StrongHandlerInfo(parameters[0], method, subscriber, eh);
               }

               this.handlers.add(infox);
               if (!this.handlerTypeExecutor.containsKey(parameters[0])) {
                  this.handlerTypeExecutor.put(parameters[0], new BasicEventBus.HandlerTypeInfo(parameters[0]));
               }
            }
         }
      }
   }

   @Override
   public void subscribe(Object subscriber, Method method, Class<?> eventtype) {
      EventHandler eh = (EventHandler)method.getAnnotation(EventHandler.class);
      if (eh != null) {
         Class<?>[] parameters = method.getParameterTypes();
         if (parameters.length != 1) {
            throw new IllegalArgumentException("EventHandler methods must specify a single Object paramter.");
         } else if (!parameters[0].isAssignableFrom(eventtype)) {
            throw new IllegalArgumentException("EventHandler parameter and given eventtype are not assignable.");
         } else {
            BasicEventBus.HandlerInfo info;
            if (eh.weak()) {
               info = new BasicEventBus.WeakHandlerInfo(eventtype, method, subscriber, eh);
            } else {
               info = new BasicEventBus.StrongHandlerInfo(eventtype, method, subscriber, eh);
            }

            this.handlers.add(info);
            if (!this.handlerTypeExecutor.containsKey(eventtype)) {
               this.handlerTypeExecutor.put(eventtype, new BasicEventBus.HandlerTypeInfo(eventtype));
            }
         }
      }
   }

   @Override
   public void unsubscribe(Object subscriber) {
      List<BasicEventBus.HandlerInfo> killList = new ArrayList();

      for (BasicEventBus.HandlerInfo info : this.handlers) {
         Object obj = info.getSubscriber();
         if (obj == null || obj == subscriber) {
            killList.add(info);
         }
      }

      for (BasicEventBus.HandlerInfo kill : killList) {
         this.handlers.remove(kill);
         kill.shutdown();
      }
   }

   @Override
   public void registerTypeListener(Class<?> eventtype, EventPublishListener listener) {
      BasicEventBus.HandlerTypeInfo hti;
      synchronized (this.handlerTypeExecutor) {
         hti = (BasicEventBus.HandlerTypeInfo)this.handlerTypeExecutor.get(eventtype);
         if (hti == null) {
            hti = new BasicEventBus.HandlerTypeInfo(eventtype);
            this.handlerTypeExecutor.put(eventtype, hti);
         }
      }

      hti.publishListener = listener;
   }

   @Override
   public void publish(Object event) {
      if (event != null) {
         try {
            BasicEventBus.HandlerTypeInfo hti = this.findBestTypeInfo(event.getClass());
            if (hti == null) {
               logger.log(Level.INFO, "No subscriber for {0}", event.getClass().getName());
            } else {
               if (hti.maxCount > 0) {
                  this.putLock.lockInterruptibly();

                  try {
                     while (true) {
                        int max = hti.maxCount - hti.messageCount;
                        Class<?> c = event.getClass();

                        for (Object o : this.queue) {
                           if (c.isInstance(o)) {
                              max--;
                           }
                        }

                        if (max > 0) {
                           break;
                        }

                        logger.log(Level.INFO, "Full of {0}", c.getName());
                        if (hti.dropOnMax) {
                           logger.log(Level.INFO, "Dropped new of {0}", c.getName());
                           return;
                        }

                        this.notFull.await();
                        logger.log(Level.INFO, "END Full of {0}", c.getName());
                     }
                  } finally {
                     this.putLock.unlock();
                  }
               }

               hti.messages++;
               this.queue.put(event);
               hti.publishNotification(event);
            }
         } catch (InterruptedException var11) {
            logger.log(Level.SEVERE, null, var11);
            throw new RuntimeException(var11);
         }
      }
   }

   @Override
   public boolean hasPendingEvents() {
      return !this.queue.isEmpty();
   }

   @Override
   public int getQueueSize() {
      return this.queue.size();
   }

   private void shutdown() {
      this.shutdown = true;

      for (BasicEventBus.HandlerInfo h : this.handlers) {
         h.shutdown();
      }

      this.handlers.clear();
      this.queue.clear();

      for (BasicEventBus.HandlerTypeInfo h : this.handlerTypeExecutor.values()) {
         h.shutdown();
      }

      this.handlerTypeExecutor.clear();

      try {
         this.mbs.unregisterMBean(this.name);
      } catch (InstanceNotFoundException var3) {
         logger.log(Level.SEVERE, null, var3);
      } catch (MBeanRegistrationException var4) {
         logger.log(Level.SEVERE, null, var4);
      }
   }

   private void notifySubscribers(final Object evt) {
      List<BasicEventBus.HandlerInfoCallable> vetoList = new ArrayList();
      final List<BasicEventBus.HandlerInfoCallable> reguList = new ArrayList();

      for (BasicEventBus.HandlerInfo info : this.handlers) {
         if (info.matchesEvent(evt)) {
            BasicEventBus.HandlerInfoCallable hc = new BasicEventBus.HandlerInfoCallable(info, evt);
            if (info.isVetoHandler()) {
               vetoList.add(hc);
            } else {
               reguList.add(hc);
            }
         }
      }

      boolean vetoCalled = false;

      try {
         for (Future<Boolean> f : this.executorService.invokeAll(vetoList)) {
            if ((Boolean)f.get()) {
               vetoCalled = true;
            }
         }
      } catch (Exception var8) {
         vetoCalled = true;
         logger.log(Level.SEVERE, null, var8);
      }

      if (vetoCalled && evt instanceof VetoEvent) {
         vetoCalled = false;
      }

      if (!vetoCalled) {
         if (this.waitForHandlers) {
            try {
               this.executorService.invokeAll(reguList);
            } catch (Exception var7) {
               logger.log(Level.SEVERE, null, var7);
            }
         } else {
            final BasicEventBus.HandlerTypeInfo hti = this.findBestTypeInfo(evt.getClass());
            hti.working++;
            hti.messageCount++;
            hti.executor.submit(new Runnable() {
               public void run() {
                  try {
                     for (BasicEventBus.HandlerInfoCallable hic : reguList) {
                        hic.call();
                     }
                  } catch (Exception var28) {
                     BasicEventBus.logger.log(Level.SEVERE, null, var28);
                  } finally {
                     hti.messageCount--;
                     BasicEventBus.this.putLock.lock();

                     try {
                        BasicEventBus.this.notFull.signalAll();
                     } finally {
                        BasicEventBus.this.putLock.unlock();
                     }

                     if (evt instanceof FollowUpEventKeeper) {
                        BasicEventBus.this.publish(((FollowUpEventKeeper)evt).getFollowUpEvent());
                     }

                     if (evt instanceof BusShutdownEvent) {
                        BasicEventBus.this.shutdown();
                     }
                  }
               }
            });
         }
      }
   }

   private BasicEventBus.HandlerTypeInfo findBestTypeInfo(Class c) {
      BasicEventBus.HandlerTypeInfo ret = (BasicEventBus.HandlerTypeInfo)this.handlerTypeExecutor.get(c);
      if (ret == null) {
         for (Entry<Class<?>, BasicEventBus.HandlerTypeInfo> e : this.handlerTypeExecutor.entrySet()) {
            if (((Class)e.getKey()).isAssignableFrom(c)) {
               ret = (BasicEventBus.HandlerTypeInfo)e.getValue();
               break;
            }
         }
      }

      return ret;
   }

   private class EventQueueRunner implements Runnable {
      private EventQueueRunner() {
      }

      public void run() {
         try {
            while (!BasicEventBus.this.shutdown) {
               BasicEventBus.this.notifySubscribers(BasicEventBus.this.queue.take());
               BasicEventBus.this.putLock.lock();

               try {
                  BasicEventBus.this.notFull.signalAll();
               } finally {
                  BasicEventBus.this.putLock.unlock();
               }
            }
         } catch (InterruptedException var5) {
            BasicEventBus.logger.log(Level.SEVERE, null, var5);
            throw new RuntimeException(var5);
         }
      }
   }

   private abstract static class HandlerInfo implements BasicEventBus.HandlerInfoMBean {
      private final Class<?> eventClass;
      private final Method method;
      private final boolean vetoHandler;
      private volatile long called = 0L;
      private volatile long exceptions = 0L;
      private final MBeanServer mbs;
      private ObjectName name;
      private final EventFilter[] filters;

      public HandlerInfo(Class<?> eventClass, Method method, EventHandler eh) {
         this.eventClass = eventClass;
         this.method = method;
         this.vetoHandler = eh.canVeto();
         Filter[] filtersAnnos = eh.filters();
         this.filters = new EventFilter[filtersAnnos.length];

         for (int i = 0; i < filtersAnnos.length; i++) {
            try {
               this.filters[i] = (EventFilter)filtersAnnos[i].value().newInstance();
            } catch (IllegalAccessException | InstantiationException var8) {
               Logger.getLogger(BasicEventBus.class.getName()).log(Level.SEVERE, null, var8);
            }
         }

         this.mbs = ManagementFactory.getPlatformMBeanServer();

         try {
            this.name = new ObjectName("de.deltaga.EventBus:type=" + method.toGenericString() + "@" + this.hashCode());
            this.mbs.registerMBean(this, this.name);
         } catch (Exception var7) {
         }
      }

      private void shutdown() {
         try {
            this.mbs.unregisterMBean(this.name);
         } catch (InstanceNotFoundException var2) {
         } catch (MBeanRegistrationException var3) {
         }
      }

      public boolean matchesEvent(Object event) {
         for (EventFilter f : this.filters) {
            if (f != null) {
               Object sub = this.getSubscriber();
               if (sub != null && !f.accept(sub, event)) {
                  return false;
               }
            }
         }

         return this.eventClass.isAssignableFrom(event.getClass());
      }

      public Method getMethod() {
         return this.method;
      }

      @Override
      public String getMethodName() {
         return this.method.toGenericString();
      }

      @Override
      public long getCalled() {
         return this.called;
      }

      @Override
      public long getExceptions() {
         return this.exceptions;
      }

      public abstract Object getSubscriber();

      public boolean isVetoHandler() {
         return this.vetoHandler;
      }

      public String toString() {
         return "Class: " + this.eventClass.getName() + ", Call: " + this.method.toString();
      }
   }

   private class HandlerInfoCallable implements Callable<Boolean> {
      private final BasicEventBus.HandlerInfo handlerInfo;
      private final Object event;

      public HandlerInfoCallable(BasicEventBus.HandlerInfo handlerInfo, Object event) {
         this.handlerInfo = handlerInfo;
         this.event = event;
      }

      public Boolean call() {
         try {
            Object subscriber = this.handlerInfo.getSubscriber();
            if (subscriber == null) {
               BasicEventBus.this.killQueue.put(this.handlerInfo);
               return false;
            } else {
               this.handlerInfo.called++;
               this.handlerInfo.getMethod().invoke(subscriber, this.event);
               return false;
            }
         } catch (Exception var3) {
            Throwable cause = var3;
            this.handlerInfo.exceptions++;

            while (cause.getCause() != null) {
               cause = cause.getCause();
            }

            if (cause instanceof VetoException) {
               BasicEventBus.this.publish(new VetoEvent(this.event));
               return true;
            } else {
               BasicEventBus.this.publish(new BusExceptionEvent(this.handlerInfo, cause, this.event));
               BasicEventBus.logger
                  .log(Level.SEVERE, "Event Handler exception on handler " + this.handlerInfo.toString() + " with event " + this.event.toString(), cause);
               return false;
            }
         }
      }
   }

   public interface HandlerInfoMBean {
      String getMethodName();

      long getCalled();

      long getExceptions();
   }

   private class HandlerTypeInfo implements BasicEventBus.HandlerTypeInfoMBean {
      final Class<?> type;
      final ExecutorService executor;
      volatile int messageCount = 0;
      final int maxCount;
      final boolean dropOnMax;
      volatile long working = 0L;
      volatile long messages = 0L;
      private EventPublishListener publishListener = BasicEventBus.publishListenerNullPattern;
      private final MBeanServer mbs;
      private ObjectName name;

      HandlerTypeInfo(Class<?> type) {
         this.type = type;
         Message m = (Message)type.getAnnotation(Message.class);
         int threads = 1;
         if (m != null && m.maximum() > 0) {
            this.maxCount = m.maximum();
            this.dropOnMax = m.dropOnMax();
         } else {
            this.maxCount = 0;
            this.dropOnMax = false;
         }

         if (m != null && m.parallelism() > 1) {
            threads = m.parallelism();
         }

         synchronized (BasicEventBus.this.executorGroup) {
            if (m != null && !m.threadgroup().isEmpty() && BasicEventBus.this.executorGroup.containsKey(m.threadgroup())) {
               this.executor = (ExecutorService)BasicEventBus.this.executorGroup.get(m.threadgroup());
            } else {
               this.executor = Executors.newFixedThreadPool(threads, new ThreadFactory() {
                  private final ThreadFactory delegate = Executors.defaultThreadFactory();

                  public Thread newThread(Runnable r) {
                     Thread t = this.delegate.newThread(r);
                     t.setName("Message_" + HandlerTypeInfo.this.type.getName());
                     t.setDaemon(true);
                     return t;
                  }
               });
               if (m != null && !m.threadgroup().isEmpty()) {
                  BasicEventBus.this.executorGroup.put(m.threadgroup(), this.executor);
               }
            }
         }

         this.mbs = ManagementFactory.getPlatformMBeanServer();

         try {
            this.name = new ObjectName("de.deltaga.EventBus.HandlerTypeInfo:name=" + type.getName());
            this.mbs.registerMBean(this, this.name);
         } catch (Exception var7) {
         }
      }

      private void shutdown() {
         this.executor.shutdown();

         try {
            this.mbs.unregisterMBean(this.name);
         } catch (InstanceNotFoundException var2) {
         } catch (MBeanRegistrationException var3) {
         }
      }

      private void publishNotification(Object event) {
         this.publishListener.published(this.type, event);
      }

      @Override
      public long getWorking() {
         return this.working;
      }

      @Override
      public long getMessages() {
         return this.messages;
      }

      @Override
      public long getMax() {
         return (long)this.maxCount;
      }

      @Override
      public long getCurrent() {
         return (long)this.messageCount;
      }
   }

   public interface HandlerTypeInfoMBean {
      long getWorking();

      long getMessages();

      long getMax();

      long getCurrent();
   }

   private class KillQueueRunner implements Runnable {
      private KillQueueRunner() {
      }

      public void run() {
         try {
            while (!BasicEventBus.this.shutdown) {
               BasicEventBus.HandlerInfo info = (BasicEventBus.HandlerInfo)BasicEventBus.this.killQueue.take();
               if (info.getSubscriber() == null) {
                  BasicEventBus.this.handlers.remove(info);
                  info.shutdown();
               }
            }
         } catch (InterruptedException var2) {
            BasicEventBus.logger.log(Level.SEVERE, null, var2);
            throw new RuntimeException(var2);
         }
      }
   }

   private static class StrongHandlerInfo extends BasicEventBus.HandlerInfo {
      private final Object subscriber;

      public StrongHandlerInfo(Class<?> eventClass, Method method, Object subscriber, EventHandler eh) {
         super(eventClass, method, eh);
         this.subscriber = subscriber;
      }

      @Override
      public Object getSubscriber() {
         return this.subscriber;
      }
   }

   private static class WeakHandlerInfo extends BasicEventBus.HandlerInfo {
      private final WeakReference<?> subscriber;

      public WeakHandlerInfo(Class<?> eventClass, Method method, Object subscriber, EventHandler eh) {
         super(eventClass, method, eh);
         this.subscriber = new WeakReference(subscriber);
      }

      @Override
      public Object getSubscriber() {
         return this.subscriber.get();
      }
   }
}
