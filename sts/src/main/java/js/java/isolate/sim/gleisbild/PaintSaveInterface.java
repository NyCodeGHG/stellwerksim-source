package js.java.isolate.sim.gleisbild;

public interface PaintSaveInterface {
   gleisbildModel getModel();

   boolean isEditorView();

   boolean isMasstabView();

   void repaint();

   int getWidth();

   int getHeight();
}
