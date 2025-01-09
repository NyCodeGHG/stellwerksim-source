package js.java.isolate.sim.eventsys.events;

import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.sim.TEXTTYPE;

public class hotlineDefektEvent extends event {
   private String text = "";
   private int counter;
   private boolean inPong = false;

   public hotlineDefektEvent(Simulator sim) {
      super(sim, "9999");
      this.text = "Die Störung des StiTz-Anschlusses dieses Stellwerks wurde erfasst. Die Techniker wurden informiert, für die nächsten 60 Minuten selbstständig auf Störungen im Gleisfeld zu achten und ggf. auszurücken. Ein Anruf bei der Hotline wird bestätigen, dass die Telefonstörung behoben wurde, Code 9999!";
   }

   @Override
   protected boolean init(eventContainer e) {
      this.showMessageNow(this.text, TEXTTYPE.REPLY);
      this.my_main.getAudio().playMessage();
      this.counter = 60;
      this.acceptingCall();
      this.callMeIn(1);
      return true;
   }

   @Override
   protected void startCall(String token) {
      if (!this.inPong) {
         this.text = "Die Telefonverbindung scheint zu funktionieren. Danke für die Mitarbeit!";
         this.showMessageNow(this.text);
         this.eventDone();
      }
   }

   @Override
   public boolean pong() {
      this.counter--;
      if (this.counter > 0) {
         this.callMeIn(1);
         this.inPong = true;

         try {
            callUncalled();
            this.acceptingCall();
         } finally {
            this.inPong = false;
         }
      } else {
         this.text = "Achtung: Bitte überprüfen Sie die Telefonverbindung erneut! Sollte diese immer noch nicht funktionieren, bitte erneut per Funk mitteilen.";
         this.showMessageNow(this.text);
         this.eventDone();
      }

      return false;
   }

   @Override
   public String getText() {
      return this.text;
   }

   @Override
   public String funkName() {
      return "Telefonverbindung gestört";
   }

   @Override
   public String funkAntwort() {
      return this.text;
   }
}
