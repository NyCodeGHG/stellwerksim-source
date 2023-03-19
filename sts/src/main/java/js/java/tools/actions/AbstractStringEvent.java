package js.java.tools.actions;

public class AbstractStringEvent extends AbstractEvent<String> {
   public AbstractStringEvent(String source) {
      super(source);
   }

   public AbstractStringEvent() {
      super("");
   }

   public String getEventString() {
      return this.getSource();
   }
}
