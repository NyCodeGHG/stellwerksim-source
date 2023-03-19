package js.java.isolate.sim.gleisbild.gecWorker;

import java.awt.event.MouseEvent;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.tools.actions.AbstractEvent;
import js.java.tools.actions.AbstractListener;
import js.java.tools.actions.ListenerList;

public class gecBase<T extends AbstractEvent> {
   private ListenerList<T> changeListeners = new ListenerList();
   protected gleisbildEditorControl gec = null;

   public gecBase() {
      super();
   }

   public final void addChangeListener(AbstractListener<T> l) {
      this.changeListeners.addListener(l);
   }

   public final void removeChangeListener(AbstractListener<T> l) {
      this.changeListeners.removeListener(l);
   }

   protected void fireEvent(T e) {
      this.changeListeners.fireEvent(e);
   }

   public void init(gleisbildEditorControl gec, gecBase lastMode) {
      this.gec = gec;
   }

   public void deinit(gecBase nextMode) {
      this.changeListeners = new ListenerList();
   }

   public void mouseClicked(MouseEvent e) {
   }

   public void mousePressed(MouseEvent e) {
   }

   public void mouseReleased(MouseEvent e) {
   }

   public void mouseEntered(MouseEvent e) {
   }

   public void mouseExited(MouseEvent e) {
   }

   public void mouseDragged(MouseEvent e) {
   }

   public void mouseMoved(MouseEvent e) {
   }
}
