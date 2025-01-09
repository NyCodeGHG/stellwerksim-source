package js.java.tools.actions;

import javax.swing.event.EventListenerList;

public class ListenerList<T extends AbstractEvent> extends EventListenerList {
   public void addListener(AbstractListener<T> l) {
      this.add(AbstractListener.class, l);
   }

   public void removeListener(AbstractListener<T> l) {
      this.remove(AbstractListener.class, l);
   }

   public void fireEvent(T fooEvent) {
      Object[] listeners = this.getListenerList();

      for (int i = listeners.length - 2; i >= 0; i -= 2) {
         if (listeners[i] == AbstractListener.class) {
            ((AbstractListener)listeners[i + 1]).action(fooEvent);
         }
      }
   }

   public void clear() {
      this.listenerList = new Object[0];
   }
}
