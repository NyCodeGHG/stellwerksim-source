package js.java.isolate.fahrplaneditor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import js.java.tools.urlConnModifier;
import js.java.tools.streams.UTF8DataOutputStream;

class saver extends SwingWorker<String, Integer> {
   private String url;
   private urlConnModifier urlmod = null;
   private StringBuffer data;
   private fahrplaneditor my_main;
   private boolean success = false;
   private boolean zsradded = false;

   public void setUrlConnModifier(urlConnModifier u) {
      this.urlmod = u;
   }

   saver(fahrplaneditor m, String _url, StringBuffer d) {
      this.my_main = m;
      this.url = _url;
      this.data = d;
   }

   public void dump() {
      String[] s = this.data.toString().split("&");

      for (String t : s) {
         System.out.println(URLDecoder.decode(t));
      }
   }

   public void exit() {
      String m = "Fahrplan wurde gespeichert.";
      if (this.zsradded) {
         m = m + "\nZug zum ZugScript-Lauf vorgemerkt.";
      }

      JOptionPane.showMessageDialog(this.my_main, m, "Speichern erfolgreich", 1);
      this.my_main.closeEditor();
   }

   public void save() {
      this.my_main.startLoad();
      this.my_main.showMessage("Fahrplan wird übertragen");
      super.execute();
   }

   protected void process(List<Integer> chunks) {
      for (int i : chunks) {
         this.my_main.setLoad(i, 100);
      }
   }

   protected void done() {
      try {
         String ret = (String)this.get();
         if (ret != null) {
            this.my_main.cleanSaveStatus();
            this.my_main.showMessage("Fehler beim Übertragen");
            if (!ret.isEmpty()) {
               JOptionPane.showMessageDialog(this.my_main, "Fehler beim Speichern:\n" + ret, "Fehler beim Speichern", 0);
            } else {
               JOptionPane.showMessageDialog(this.my_main, "Fehler beim Speichern!", "Fehler beim Speichern", 0);
            }
         } else {
            this.my_main.showMessage("Fahrplan wurde übertragen");
            this.exit();
         }
      } catch (ExecutionException | InterruptedException var2) {
         Logger.getLogger(saver.class.getName()).log(Level.SEVERE, null, var2);
      }

      this.my_main.endLoad();
   }

   protected String doInBackground() {
      String ret = null;

      try {
         this.publish(new Integer[]{10});
         URL u = new URL(this.url);
         URLConnection urlConn = u.openConnection();
         if (this.urlmod != null) {
            this.urlmod.modify(urlConn);
         }

         urlConn.setDoInput(true);
         urlConn.setDoOutput(true);
         urlConn.setUseCaches(false);
         urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
         UTF8DataOutputStream printout = new UTF8DataOutputStream(urlConn.getOutputStream());
         String content = this.data.toString();
         this.publish(new Integer[]{20});
         printout.write(content);
         printout.flush();
         printout.close();
         this.publish(new Integer[]{60});
         BufferedReader input = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
         Throwable var7 = null;

         try {
            this.success = false;
            this.zsradded = false;
            this.publish(new Integer[]{80});

            String str;
            while (null != (str = input.readLine())) {
               System.out.println(str);
               if (str.equalsIgnoreCase("[DONE]")) {
                  this.success = true;
               }

               if (str.equalsIgnoreCase("[ZSRADDED]")) {
                  this.zsradded = true;
               }
            }
         } catch (Throwable var17) {
            var7 = var17;
            throw var17;
         } finally {
            if (input != null) {
               if (var7 != null) {
                  try {
                     input.close();
                  } catch (Throwable var16) {
                     var7.addSuppressed(var16);
                  }
               } else {
                  input.close();
               }
            }
         }

         this.publish(new Integer[]{100});
         System.out.println("Save DONE: " + this.success);
         if (this.zsradded) {
            this.my_main.showMessage("Zug zum ZugScript-Lauf vorgemerkt!");
         }

         if (this.success) {
            this.my_main.showMessage("Fahrplan erfolgreich gespeichert");
         } else {
            ret = "Es konnten nicht alle Daten übertragen werden.";
         }
      } catch (Exception var19) {
         ret = "Es konnten nicht alle Daten übertragen werden.\n(" + var19.getMessage() + ")";
      }

      return ret;
   }
}
