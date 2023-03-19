package js.java.tools.dialogs;

import java.awt.Frame;
import java.io.IOException;
import java.net.URL;

public class htmlmessage1 extends message1 implements Runnable {
   private String htmltext = null;
   private URL htmlurl = null;

   public htmlmessage1(Frame parent, boolean modal, String title, String text) {
      super(parent, modal, title, text);
      this.htmltext = text;
   }

   public htmlmessage1(Frame parent, boolean modal, String title, URL u) {
      super(parent, modal, title, "");
      this.htmlurl = u;
   }

   public void run() {
      this.textArea.setContentType("text/html");
      if (this.htmltext != null) {
         this.textArea.setText(this.htmltext);
         this.textArea.setCaretPosition(0);
      }

      if (this.htmlurl != null) {
         try {
            this.textArea.setPage(this.htmlurl);
         } catch (IOException var2) {
            this.textArea.setContentType("text/plain");
            this.textArea.setText("Übertragungsfehler");
         }
      }
   }

   @Override
   public void show(int w, int h) {
      this.run();
      this.setModal(false);
      super.show(w, h);
   }
}
