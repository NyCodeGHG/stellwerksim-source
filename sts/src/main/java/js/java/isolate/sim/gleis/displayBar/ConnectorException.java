package js.java.isolate.sim.gleis.displayBar;

abstract class ConnectorException extends Exception {
   private Exception parent = null;

   ConnectorException() {
      super();
   }

   ConnectorException(Exception e) {
      super();
      this.parent = e;
   }
}
