package js.java.tools.gui.dataTransferDisplay;

import javax.swing.Timer;

public class DataTransferDisplayComponent extends LedComponent implements DataTransferDisplayInterface {
   private final Timer offTimer = new Timer(150, a -> this.turnOff());

   public DataTransferDisplayComponent() {
      this.offTimer.setRepeats(false);
   }

   @Override
   public void gotData() {
      this.setLed(!this.isLed());
      this.offTimer.restart();
   }

   private void turnOff() {
      this.setLed(false);
   }
}
