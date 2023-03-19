package js.java.schaltungen.timesystem;

public interface timedelivery {
   long ZEIT_SEKUNDE = 1000L;
   long ZEIT_HALBEMINUTE = 30000L;
   long ZEIT_MINUTE = 60000L;
   long ZEIT_STUNDE = 3600000L;

   boolean isPause();

   long getSimutime();

   String getSimutimeString();

   String getSimutimeString(long var1);
}
