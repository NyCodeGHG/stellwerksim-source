package js.java.isolate.sim.gleis.displayBar;

class UndefinedSWWertException extends ConnectorException {
   private final String dest;

   UndefinedSWWertException(String dest) {
      this.dest = dest;
   }

   String getDest() {
      return this.dest;
   }
}
