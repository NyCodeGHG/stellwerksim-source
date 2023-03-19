package js.java.tools;

public class twoText implements Comparable {
   private String text = "";
   private String cmptext = "";

   public twoText(String _text, String _cmptext) {
      super();
      this.text = _text;
      this.cmptext = _cmptext;
   }

   public twoText(String _text) {
      super();
      this.text = _text;
      this.cmptext = _text;
   }

   public String getCmpText() {
      return this.cmptext;
   }

   public String getText() {
      return this.text;
   }

   public int compareTo(Object o) {
      System.out.println("compareTo " + this.cmptext + "/" + o.toString());
      if (o instanceof twoText) {
         System.out.println("compareTo " + this.cmptext + " TT " + ((twoText)o).cmptext);
         return this.cmptext.compareTo(((twoText)o).cmptext);
      } else {
         return this.cmptext.compareTo(o.toString());
      }
   }

   public String toString() {
      return this.text;
   }
}
