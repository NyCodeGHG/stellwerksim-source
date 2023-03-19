package js.java.isolate.sim.sim.plugin;

public abstract class ServBase {
   public static final int CONTROLSERVPORT = 3791;
   public static final int PLUGINSERVPORT = 3691;
   public static final String BCASTMAGIC = "STSBCASTCLIENT";

   public ServBase() {
      super();
   }

   public static enum FAILREASON {
      SUCCESS,
      UNKNOWN,
      BUSY,
      WRONGMODE;
   }
}
