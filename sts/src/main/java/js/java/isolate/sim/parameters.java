package js.java.isolate.sim;

@Deprecated
public class parameters {
   public parameters() {
      super();
   }

   public static String getParameter(String p) {
      if (p.compareTo("sound1") == 0) {
         return "/js/java/extras/welcome.wav";
      } else if (p.compareTo("sound2") == 0) {
         return "/js/java/extras/ZugEin.wav";
      } else if (p.compareTo("sound3") == 0) {
         return "/js/java/extras/Meldung.wav";
      } else if (p.compareTo("sound4") == 0) {
         return "/js/java/extras/phone1.wav";
      } else if (p.compareTo("sound5") == 0) {
         return "/js/java/extras/chat.wav";
      } else if (p.compareTo("sound6") == 0) {
         return "/js/java/extras/warten.wav";
      } else if (p.compareTo("sound7") == 0) {
         return "/js/java/extras/phone2.wav";
      } else if (p.compareTo("sound8") == 0) {
         return "/js/java/extras/counter.wav";
      } else if (p.startsWith("dingdong")) {
         return "/js/java/extras/" + p + ".wav";
      } else if (p.compareTo("sound10") == 0) {
         return "/js/java/extras/alarm.wav";
      } else if (p.compareTo("soundFUSE") == 0) {
         return "/js/java/extras/fuse.wav";
      } else if (p.compareTo("soundBEEP") == 0) {
         return "/js/java/extras/beep.wav";
      } else {
         return p.compareTo("start") == 0 ? "/js/java/extras/start.wav" : null;
      }
   }
}
