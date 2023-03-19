package js.java.schaltungen.timesystem;

import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TimeFormat {
   private String formatString = "";

   public static TimeFormat getInstance(TimeFormat.STYLE format) {
      switch(format) {
         case HM:
            return new TimeFormat("%02d:%02d");
         case HMS:
            return new TimeFormat("%02d:%02d:%02d");
         default:
            throw new IllegalArgumentException();
      }
   }

   protected TimeFormat(String formatString) {
      super();
      this.formatString = formatString;
   }

   public long parse(String timestring) throws ParseException {
      return this.string2time(timestring);
   }

   public long string2time(String timestring) throws ParseException {
      if (timestring.isEmpty()) {
         return 0L;
      } else {
         int h = 0;
         int m = 0;
         int s = 0;
         String[] spl = timestring.split(":");

         try {
            for(int i = 0; i < spl.length; ++i) {
               switch(i) {
                  case 0:
                     h = Integer.parseInt(spl[i]);
                     break;
                  case 1:
                     m = Integer.parseInt(spl[i]);
                     break;
                  case 2:
                     s = Integer.parseInt(spl[i]);
               }
            }
         } catch (NumberFormatException var7) {
            Logger.getLogger("stslogger").log(Level.SEVERE, "NFE: " + timestring, var7);
            throw new ParseException("NFE: " + timestring, 0);
         }

         return (long)h * 3600000L + (long)m * 60000L + (long)s * 1000L;
      }
   }

   public String format(long time) {
      return this.formatTime(time);
   }

   public String formatTime(long time) {
      int h = (int)(time / 3600000L);
      int m = (int)(time / 60000L % 60L);
      int s = (int)(time / 1000L % 60L);
      return String.format(this.formatString, h, m, s);
   }

   public static enum STYLE {
      HM,
      HMS;
   }
}
