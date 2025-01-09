package js.java.tools;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class ArrayListModel<T> extends ArrayList<T> implements ListModel {
   private final ArrayList listeners = new ArrayList();
   private final Runnable notifyRun = new Runnable() {
      public void run() {
         ArrayListModel.this.notifyListenersEventLoop();
      }
   };
   private final Runnable clearRun = new Runnable() {
      public void run() {
         ArrayListModel.super.clear();
         ArrayListModel.this.notifyListenersEventLoop();
      }
   };
   private T remove_o;
   private boolean remove_b;

   public Object getElementAt(int index) {
      return this.get(index);
   }

   public int getSize() {
      return this.size();
   }

   public void removeListDataListener(ListDataListener l) {
      this.listeners.remove(l);
   }

   public void addListDataListener(ListDataListener l) {
      this.listeners.add(l);
   }

   private void notifyListeners() {
      if (SwingUtilities.isEventDispatchThread()) {
         this.notifyListenersEventLoop();
      } else {
         try {
            SwingUtilities.invokeAndWait(this.notifyRun);
         } catch (InvocationTargetException | InterruptedException var2) {
            Logger.getLogger(ArrayListModel.class.getName()).log(Level.SEVERE, "caught", var2);
         }
      }
   }

   private void notifyListenersEventLoop() {
      ListDataEvent le = new ListDataEvent(this, 0, 0, this.getSize());

      for (int i = 0; i < this.listeners.size(); i++) {
         ((ListDataListener)this.listeners.get(i)).contentsChanged(le);
      }
   }

   public boolean add(T o) {
      boolean b = super.add(o);
      if (b) {
         this.notifyListeners();
      }

      return b;
   }

   public void add(int index, T element) {
      super.add(index, element);
      this.notifyListeners();
   }

   public boolean addAll(Collection<? extends T> o) {
      boolean b = super.addAll(o);
      if (b) {
         this.notifyListeners();
      }

      return b;
   }

   public void clear() {
      if (SwingUtilities.isEventDispatchThread()) {
         super.clear();
         this.notifyListeners();
      } else {
         try {
            SwingUtilities.invokeAndWait(this.clearRun);
         } catch (InvocationTargetException | InterruptedException var2) {
            Logger.getLogger(ArrayListModel.class.getName()).log(Level.SEVERE, "caught", var2);
         }
      }
   }

   public T remove(final int i) {
      Runnable c = new Runnable() {
         public void run() {
            ArrayListModel.this.remove_o = (T)ArrayListModel.super.remove(i);
            ArrayListModel.this.notifyListeners();
         }
      };
      if (SwingUtilities.isEventDispatchThread()) {
         c.run();
      } else {
         try {
            SwingUtilities.invokeAndWait(c);
         } catch (InvocationTargetException | InterruptedException var4) {
            Logger.getLogger(ArrayListModel.class.getName()).log(Level.SEVERE, "caught", var4);
         }
      }

      return this.remove_o;
   }

   public boolean remove(final Object o) {
      Runnable c = new Runnable() {
         public void run() {
            ArrayListModel.this.remove_b = ArrayListModel.super.remove(o);
            if (ArrayListModel.this.remove_b) {
               ArrayListModel.this.notifyListeners();
            }
         }
      };
      if (SwingUtilities.isEventDispatchThread()) {
         c.run();
      } else {
         try {
            SwingUtilities.invokeAndWait(c);
         } catch (InvocationTargetException | InterruptedException var4) {
            Logger.getLogger(ArrayListModel.class.getName()).log(Level.SEVERE, "caught", var4);
         }
      }

      return this.remove_b;
   }

   public T set(int index, T element) {
      T o = (T)super.set(index, element);
      this.notifyListeners();
      return o;
   }
}
