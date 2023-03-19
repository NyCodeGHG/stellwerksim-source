package de.deltaga.eb;

import java.lang.reflect.Method;

public interface EventBus {
   void subscribe(Object var1);

   void subscribe(Object var1, Method var2, Class<?> var3);

   void unsubscribe(Object var1);

   void publish(Object var1);

   boolean hasPendingEvents();

   void registerTypeListener(Class<?> var1, EventPublishListener var2);
}
