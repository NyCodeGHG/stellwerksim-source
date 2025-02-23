package js.java.isolate.sim.gleis.displayBar;

class UnknownDisplayException extends ConnectorException {
   private final String dest;

   UnknownDisplayException(String dest) {
      this.dest = dest;
   }

   String getDest() {
      return this.dest;
   }
}
