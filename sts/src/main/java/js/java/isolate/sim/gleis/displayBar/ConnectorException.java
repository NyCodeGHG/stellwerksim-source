package js.java.isolate.sim.gleis.displayBar;

abstract class ConnectorException extends Exception {
   private Exception parent = null;

   ConnectorException() {
   }

   ConnectorException(Exception e) {
      this.parent = e;
   }
}
