package js.java.isolate.sim.eventsys;

import java.util.EnumMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.isolate.sim.FATwriter;

public class eventGenerator {
   private static FATwriter debugMode = null;
   public static final eventGenerator.TYPES T_GLEIS_STATUS = eventGenerator.TYPES.T_GLEIS_STATUS;
   public static final eventGenerator.TYPES T_GLEIS_STELLUNG = eventGenerator.TYPES.T_GLEIS_STELLUNG;
   public static final eventGenerator.TYPES T_GLEIS_FSSPEICHER = eventGenerator.TYPES.T_GLEIS_FSSPEICHER;
   public static final eventGenerator.TYPES T_ZUG_FAHRT = eventGenerator.TYPES.T_ZUG_FAHRT;
   public static final eventGenerator.TYPES T_ZUG_WURDEGRUEN = eventGenerator.TYPES.T_ZUG_WURDEGRUEN;
   public static final eventGenerator.TYPES T_ZUG_ABFAHRT = eventGenerator.TYPES.T_ZUG_ABFAHRT;
   public static final eventGenerator.TYPES T_ZUG_ANKUNFT = eventGenerator.TYPES.T_ZUG_ANKUNFT;
   public static final eventGenerator.TYPES T_ZUG_AUSFAHRT = eventGenerator.TYPES.T_ZUG_AUSFAHRT;
   public static final eventGenerator.TYPES T_ZUG_EINFAHRT = eventGenerator.TYPES.T_ZUG_EINFAHRT;
   public static final eventGenerator.TYPES T_ZUG_KUPPELN = eventGenerator.TYPES.T_ZUG_KUPPELN;
   public static final eventGenerator.TYPES T_ZUG_FLÜGELN = eventGenerator.TYPES.T_ZUG_FLÜGELN;
   public static final eventGenerator.TYPES T_ZUG_LOKFLÜGELN = eventGenerator.TYPES.T_ZUG_LOKFLÜGELN;
   public static final eventGenerator.TYPES T_ZUG_ROT = eventGenerator.TYPES.T_ZUG_ROT;
   public static final eventGenerator.TYPES T_FS_SETZEN = eventGenerator.TYPES.T_FS_SETZEN;
   public static final eventGenerator.TYPES T_FS_LOESCHEN = eventGenerator.TYPES.T_FS_LOESCHEN;
   private EnumMap<eventGenerator.HOOKKIND, eventGenerator.hookMgr> hooks = new EnumMap(eventGenerator.HOOKKIND.class);

   public static void setDebug(FATwriter b) {
      debugMode = b;
   }

   public static boolean isDebug() {
      return debugMode != null;
   }

   protected eventGenerator() {
      for (eventGenerator.HOOKKIND k : eventGenerator.HOOKKIND.values()) {
         this.hooks.put(k, new eventGenerator.hookMgr());
      }
   }

   protected final boolean call(eventGenerator.TYPES typ, eventmsg e) {
      try {
         if (debugMode != null) {
            debugMode.writeln("hook call: " + typ + "(" + e.toString() + ")");
         }

         boolean ret = true;

         for (eventGenerator.HOOKKIND k : eventGenerator.HOOKKIND.values()) {
            if (ret) {
               boolean v = ((eventGenerator.hookMgr)this.hooks.get(k)).call(typ, e);
               if (k.relevant) {
                  ret = v;
               }
            }
         }

         return ret;
      } catch (Exception var9) {
         System.out.println("Ex: " + var9.getMessage());
         var9.printStackTrace();
         Logger.getLogger("stslogger").log(Level.SEVERE, "Caught eG call", var9);
         return true;
      }
   }

   public final boolean hasHook(eventGenerator.TYPES typ) {
      for (eventGenerator.HOOKKIND k : eventGenerator.HOOKKIND.values()) {
         if (this.hasHook(k, typ)) {
            return true;
         }
      }

      return false;
   }

   public final boolean hasHook(eventGenerator.HOOKKIND k, eventGenerator.TYPES typ) {
      return ((eventGenerator.hookMgr)this.hooks.get(k)).hasHook(typ);
   }

   public final boolean hasHookRegistered(eventGenerator.TYPES typ, Class<? extends eventGenerator.eventCall> e) {
      for (eventGenerator.HOOKKIND k : eventGenerator.HOOKKIND.values()) {
         boolean r = this.hasHookRegistered(k, typ, e);
         if (r) {
            return true;
         }
      }

      return false;
   }

   public final boolean hasHookRegistered(eventGenerator.HOOKKIND k, eventGenerator.TYPES typ, Class<? extends eventGenerator.eventCall> e) {
      return ((eventGenerator.hookMgr)this.hooks.get(k)).hasHookRegistered(typ, e);
   }

   public final boolean hasThisHookRegistered(eventGenerator.TYPES typ, eventGenerator.eventCall e) {
      for (eventGenerator.HOOKKIND k : eventGenerator.HOOKKIND.values()) {
         boolean r = this.hasThisHookRegistered(k, typ, e);
         if (r) {
            return true;
         }
      }

      return false;
   }

   public final boolean hasThisHookRegistered(eventGenerator.HOOKKIND k, eventGenerator.TYPES typ, eventGenerator.eventCall e) {
      return ((eventGenerator.hookMgr)this.hooks.get(k)).hasThisHookRegistered(typ, e);
   }

   public final boolean hasDataHookRegistered(eventGenerator.TYPES typ, Object e) {
      for (eventGenerator.HOOKKIND k : eventGenerator.HOOKKIND.values()) {
         boolean r = this.hasDataHookRegistered(k, typ, e);
         if (r) {
            return true;
         }
      }

      return false;
   }

   public final boolean hasDataHookRegistered(eventGenerator.HOOKKIND k, eventGenerator.TYPES typ, Object e) {
      return ((eventGenerator.hookMgr)this.hooks.get(k)).hasDataHookRegistered(typ, e);
   }

   public void registerHook(eventGenerator.TYPES typ, eventGenerator.eventCall e) {
      this.registerHook(eventGenerator.HOOKKIND.WORKER, typ, e);
   }

   public void registerHook(eventGenerator.HOOKKIND k, eventGenerator.TYPES typ, eventGenerator.eventCall e) {
      ((eventGenerator.hookMgr)this.hooks.get(k)).registerHook(typ, e);
   }

   public void unregisterHook(eventGenerator.TYPES typ, eventGenerator.eventCall e) {
      for (eventGenerator.HOOKKIND k : eventGenerator.HOOKKIND.values()) {
         ((eventGenerator.hookMgr)this.hooks.get(k)).unregisterHook(typ, e);
      }
   }

   public void unregisterHookTypes(eventGenerator.TYPES typ, Class<? extends eventGenerator.eventCall> e) {
      for (eventGenerator.HOOKKIND k : eventGenerator.HOOKKIND.values()) {
         ((eventGenerator.hookMgr)this.hooks.get(k)).unregisterHookTypes(typ, e);
      }
   }

   public void unregisterAllHooks() {
      for (eventGenerator.HOOKKIND k : eventGenerator.HOOKKIND.values()) {
         ((eventGenerator.hookMgr)this.hooks.get(k)).unregisterAllHooks();
      }
   }

   public static enum HOOKKIND {
      PREINFO(false),
      WORKER(true),
      POSTINFO(false);

      private boolean relevant;

      private HOOKKIND(boolean relevant) {
         this.relevant = relevant;
      }
   }

   public static enum TYPES {
      T_GLEIS_STATUS,
      T_GLEIS_STELLUNG,
      T_GLEIS_FSSPEICHER,
      T_ZUG_FAHRT,
      T_ZUG_WURDEGRUEN,
      T_ZUG_ABFAHRT,
      T_ZUG_ANKUNFT,
      T_ZUG_AUSFAHRT,
      T_ZUG_EINFAHRT,
      T_ZUG_KUPPELN,
      T_ZUG_FLÜGELN,
      T_ZUG_LOKFLÜGELN,
      T_ZUG_ROT,
      T_FS_SETZEN,
      T_FS_LOESCHEN;
   }

   public interface eventCall extends Comparable {
      boolean hookCall(eventGenerator.TYPES var1, eventmsg var2);

      String funkName();
   }

   private static class hookMgr extends EnumMap<eventGenerator.TYPES, CopyOnWriteArraySet<eventGenerator.eventCall>> {
      hookMgr() {
         super(eventGenerator.TYPES.class);

         for (eventGenerator.TYPES i : eventGenerator.TYPES.values()) {
            this.put(i, new CopyOnWriteArraySet());
         }
      }

      protected final boolean call(eventGenerator.TYPES typ, eventmsg e) {
         try {
            if (eventGenerator.debugMode != null) {
               eventGenerator.debugMode.writeln("hook call: " + typ + "(" + e.toString() + ")");
            }

            boolean ret = true;

            for (eventGenerator.eventCall ev : (CopyOnWriteArraySet)this.get(typ)) {
               e.currentReturn = ret;
               ret &= ev.hookCall(typ, e);
            }

            return ret;
         } catch (Exception var6) {
            System.out.println("Ex: " + var6.getMessage());
            var6.printStackTrace();
            Logger.getLogger("stslogger").log(Level.SEVERE, "Caught eG call", var6);
            return true;
         }
      }

      public final boolean hasHook(eventGenerator.TYPES typ) {
         return !((CopyOnWriteArraySet)this.get(typ)).isEmpty();
      }

      public final boolean hasHookRegistered(eventGenerator.TYPES typ, Class<? extends eventGenerator.eventCall> e) {
         boolean ret = false;

         for (eventGenerator.eventCall ev : (CopyOnWriteArraySet)this.get(typ)) {
            if (e.isInstance(ev)) {
               ret = true;
               break;
            }
         }

         return ret;
      }

      public final boolean hasThisHookRegistered(eventGenerator.TYPES typ, eventGenerator.eventCall e) {
         boolean ret = false;

         for (eventGenerator.eventCall ev : (CopyOnWriteArraySet)this.get(typ)) {
            if (ev.equals(e)) {
               ret = true;
               break;
            }
         }

         return ret;
      }

      public final boolean hasDataHookRegistered(eventGenerator.TYPES typ, Object e) {
         boolean ret = false;

         for (eventGenerator.eventCall ev : (CopyOnWriteArraySet)this.get(typ)) {
            if (ev.equals(e)) {
               ret = true;
               break;
            }
         }

         return ret;
      }

      public void registerHook(eventGenerator.TYPES typ, eventGenerator.eventCall e) {
         ((CopyOnWriteArraySet)this.get(typ)).add(e);
         if (eventGenerator.debugMode != null) {
            eventGenerator.debugMode.writeln("hook registered: " + typ + " (" + e.funkName() + ")");
         }
      }

      public void unregisterHook(eventGenerator.TYPES typ, eventGenerator.eventCall e) {
         boolean removed = ((CopyOnWriteArraySet)this.get(typ)).remove(e);
         if (eventGenerator.debugMode != null) {
            eventGenerator.debugMode.writeln("hook unregistered: " + typ + " (" + e.funkName() + ") " + removed);
         }
      }

      public void unregisterHookTypes(eventGenerator.TYPES typ, Class<? extends eventGenerator.eventCall> e) {
         for (eventGenerator.eventCall ev : (CopyOnWriteArraySet)this.get(typ)) {
            if (e.isInstance(ev)) {
               ((CopyOnWriteArraySet)this.get(typ)).remove(ev);
            }
         }
      }

      public void unregisterAllHooks() {
         for (eventGenerator.TYPES i : eventGenerator.TYPES.values()) {
            ((CopyOnWriteArraySet)this.get(i)).clear();
         }
      }
   }
}
