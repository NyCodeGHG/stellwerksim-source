package js.java.schaltungen.switchbase;

public class DataSwitch {
   @SwitchOption("painter.thread")
   public boolean painterThread = true;
   @SwitchOption("painter.buffer")
   public boolean paintbuffer = true;
   @SwitchOption("painter.flipimage")
   public boolean flipImage = true;
   @SwitchOption("painter.repaint")
   public boolean panelrepaint = true;
   @SwitchOption("painter.firepainter")
   public boolean firepainter = true;
   @SwitchOption("painter.awtpaint")
   public boolean awtpaint = true;
   @SwitchOption("ui.multisplit")
   public boolean multisplit = true;
   @SwitchOption("ui.zugplan")
   public boolean zugplan = true;

   public DataSwitch() {
      super();
   }
}
