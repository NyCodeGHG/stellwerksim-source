package js.java.isolate.sim.gleisbild;

import java.awt.Dimension;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.gleis.gleis;
import js.java.tools.TextHelper;
import js.java.tools.imageencoder;
import js.java.tools.imagesaver;
import js.java.tools.urlConnModifier;
import js.java.tools.gui.dataTransferDisplay.DataTransferDisplayComponent;
import js.java.tools.streams.UTF8DataOutputStream;
import js.java.tools.xml.xmllistener;
import js.java.tools.xml.xmlreader;
import org.xml.sax.Attributes;

public class gleisbildModelStore extends gleisbildModel implements xmllistener, imagesaver, PaintSaveInterface {
   private gleisbildModelStore.ioLoader loaderThread = null;
   private gleisbildModelStore.ioSaver saverThread = null;
   private gleisbildModelStore.ioPinger pingThread = null;
   private urlConnModifier urlmod = null;
   protected xmlreader xmlr = null;
   protected DataTransferDisplayComponent mon = null;
   private int pet_x;
   private int pet_y;
   private int pet_w;
   private int pet_h;

   public gleisbildModelStore(GleisAdapter _theapplet) {
      super(_theapplet);
   }

   public void load(String datenurl, gleisbildModelStore.ioDoneMessage callback) {
      this.allOff();
      if (this.loaderThread == null) {
         this.loaderThread = new gleisbildModelStore.ioLoader(datenurl, callback);
         this.loaderThread.start();
      }
   }

   public void save(String datenurl, gleisbildModelStore.ioDoneMessage callback) {
      this.allOff();
      if (this.saverThread == null) {
         this.saverThread = new gleisbildModelStore.ioSaver(datenurl, callback);
         this.saverThread.start();
      }
   }

   public void ping(String datenurl) {
      if (this.pingThread == null) {
         this.pingThread = new gleisbildModelStore.ioPinger(datenurl);
         this.pingThread.start();
      }
   }

   public void setUrlConnModifier(urlConnModifier u) {
      this.urlmod = u;
   }

   @Override
   public void close() {
      super.close();
      this.xmlr = null;
   }

   public void setDataTransferMonitor(DataTransferDisplayComponent m) {
      this.mon = m;
   }

   private long getData(String url) throws IOException {
      if (this.xmlr == null) {
         this.xmlr = new xmlreader(this.mon);
         this.registerTags();
      }

      try {
         this.xmlr.updateData(url);
      } catch (OutOfMemoryError var3) {
         System.out.println("Out of Memory!");
         this.theapplet.showStatus("Speichermangel!", 4);
         throw new IOException("Out of Memory!");
      } catch (NullPointerException var4) {
         System.out.println("Datenfehler");
         this.theapplet.showStatus("Datenfehler!", 4);
         throw new NullPointerException("Datenfehler!");
      }

      this.theapplet.setProgress(100);
      this.structureChanged();
      return this.xmlr.getDataSize();
   }

   protected void registerTags() {
      this.xmlr.registerTag("gleis", this);
      this.xmlr.registerTag("gleise", this);
      this.xmlr.registerTag("gleisbild", this);
      this.xmlr.registerTag("gleiszeile", this);
      this.xmlr.registerTag("displayconnectors", this);
      this.xmlr.registerTag("displayconnect", this);
   }

   public void parseStartTag(String tag, Attributes attrs) {
      if (tag.compareTo("gleisbild") == 0) {
         this.gl_resize(1, 1);
         this.getDisplayBar().clear();
         this.pet_x = 0;
         this.pet_y = 0;

         try {
            this.setAid(Integer.parseInt(attrs.getValue("aid")));
            this.pet_w = Integer.parseInt(attrs.getValue("width"));
            this.pet_h = Integer.parseInt(attrs.getValue("height"));
         } catch (NumberFormatException var5) {
            this.pet_w = 1;
            this.pet_h = 1;
         }

         try {
            this.setRegion(attrs.getValue("region"), Integer.parseInt(attrs.getValue("rid")));
         } catch (NumberFormatException var4) {
            this.setRegion("unk", 1);
         }

         this.setAnlagenname(attrs.getValue("name"));
         this.gl_resize(this.pet_w, this.pet_h);
         String ext = attrs.getValue("extend");
         if (ext != null) {
            this.gleisbildextend = gleisbild_extend.createFromBase64(ext);
         } else {
            this.gleisbildextend = new gleisbild_extend();
         }

         this.theapplet.setProgress(5);
      } else if (tag.compareTo("gleise") == 0) {
         gleis.prepareNachbar();
         this.theapplet.setProgress(20);
      } else if (tag.compareTo("gleiszeile") == 0) {
         this.pet_x = 0;
         this.pet_y = Integer.parseInt(attrs.getValue("z"));
         this.theapplet.setProgress(20 + this.pet_y * 30 / this.pet_h);
      } else if (tag.compareTo("displayconnectors") == 0) {
         this.getDisplayBar().clear();
      } else if (tag.compareTo("displayconnect") == 0) {
         this.getDisplayBar().addEntry(attrs);
      }
   }

   public void parseEndTag(String tag, Attributes attrs, String pcdata) {
      if (tag.compareTo("gleis") == 0) {
         gleis gl = this.getXY_null(this.pet_x, this.pet_y);
         if (gl != null) {
            try {
               gl.init(attrs, pcdata);
            } catch (Exception var6) {
            }
         }

         ++this.pet_x;
      } else if (tag.compareTo("gleise") == 0) {
         for(gleis gl : this) {
            gl.reset();
            gl.resetN();
         }

         for(gleis gl : this) {
            gl.paint1(null, null, 1, 1, 1);
         }
      } else if (tag.compareTo("gleisbild") == 0) {
         gleis.startNachbar();
         this.getDisplayBar().handleLegacy();
         this.theapplet.setProgress(100);
      }
   }

   protected boolean saveData(String url, StringBuffer data) throws IOException {
      this.theapplet.setProgress(80);
      data.append("lastvalue=1&");
      if (url.startsWith("stdout")) {
         System.out.println("Savedata:");
         String[] lines = data.toString().split("&");

         for(String l : lines) {
            System.out.println(URLDecoder.decode(l));
         }

         System.gc();
         this.theapplet.setProgress(100);
         return true;
      } else {
         URL u = new URL(url);
         URLConnection urlConn = u.openConnection();
         if (this.urlmod != null) {
            this.urlmod.modify(urlConn);
         }

         urlConn.setDoInput(true);
         urlConn.setDoOutput(true);
         urlConn.setUseCaches(false);
         urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
         UTF8DataOutputStream printout = new UTF8DataOutputStream(urlConn.getOutputStream());
         String content = data.toString();
         this.theapplet.setProgress(-90);
         printout.write(content);
         printout.flush();
         printout.close();
         BufferedReader input = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
         Throwable var9 = null;

         boolean success;
         try {
            success = false;
            this.theapplet.setProgress(-95);

            String str;
            while(null != (str = input.readLine())) {
               if (str.equalsIgnoreCase("[DONE]")) {
                  success = true;
               } else {
                  this.theapplet.showStatus(str, 0);
               }
            }
         } catch (Throwable var18) {
            var9 = var18;
            throw var18;
         } finally {
            if (input != null) {
               if (var9 != null) {
                  try {
                     input.close();
                  } catch (Throwable var17) {
                     var9.addSuppressed(var17);
                  }
               } else {
                  input.close();
               }
            }
         }

         System.gc();
         this.theapplet.setProgress(100);
         return success;
      }
   }

   protected StringBuffer createSaveData(StringBuffer data) {
      this.theapplet.setProgress(0);
      data.append("aid=").append(this.getAid()).append("&");
      data.append("width=").append(this.getGleisWidth()).append("&");
      data.append("height=").append(this.getGleisHeight()).append("&");
      this.setSelectedGleis(null, true);
      int total = this.getGleisHeight();

      for(gleis gl : this) {
         this.theapplet.setProgress((total - gl.getRow()) / 10);
         data.append(TextHelper.urlEncode("data[]"));
         data.append('=');
         data.append(TextHelper.urlEncode(gl.getSaveString()));
         data.append('&');
      }

      data.append(TextHelper.urlEncode("aenderungspunkte"));
      data.append("=").append(this.getChangeC()).append("&");
      this.getDisplayBar().saveData(data);
      if (this.gleisbildextend != null) {
         data.append("gleisbildextend=").append(TextHelper.urlEncode(this.gleisbildextend.toBase64())).append("&");
      }

      try {
         data.append("shot=").append(imageencoder.encodeurl(this)).append("&");
      } catch (IOException var5) {
         System.out.println("JPEG-Exception: " + var5.getMessage());
         Logger.getLogger("stslogger").log(Level.SEVERE, "Caught JPEG EX", var5);
      }

      return data;
   }

   private boolean sendData(String url) throws IOException {
      StringBuffer data = new StringBuffer();
      data = this.createSaveData(data);
      return this.saveData(url, data);
   }

   public Dimension getSaveSize() {
      return new Dimension(this.getWidth(), this.getHeight());
   }

   public void paintSave(Graphics g) {
      gleisbildPainter painter = new gleisbildPainter();
      gleis.setAllowSmooth(true);
      painter.paintComponent(this, g, new scaleHolder());
   }

   @Override
   public gleisbildModel getModel() {
      return this;
   }

   @Override
   public boolean isEditorView() {
      return false;
   }

   @Override
   public boolean isMasstabView() {
      return false;
   }

   @Override
   public int getWidth() {
      return this.getGleisWidth() * 12;
   }

   @Override
   public int getHeight() {
      return this.getGleisHeight() * 12;
   }

   public interface ioDoneMessage {
      void done(boolean var1);
   }

   private class ioLoader extends gleisbildModelStore.ioRunner {
      ioLoader(String run_data, gleisbildModelStore.ioDoneMessage m) {
         super(run_data, m);
         this.setName("GB load");
      }

      public void run() {
         boolean suc = false;
         gleisbildModelStore.this.clear();
         gleisbildModelStore.this.theapplet.showStatus("Daten laden...", 0);

         try {
            long size = 0L;
            size = gleisbildModelStore.this.getData(this.run_data);
            gleisbildModelStore.this.resetChangeC();
            gleisbildModelStore.this.theapplet.showStatus("Daten erfolgreich geladen, " + size + " Bytes.", 0);
            suc = true;
         } catch (IOException var8) {
            Logger.getLogger("stslogger").log(Level.SEVERE, "GB laden (" + this.run_data + ")", var8);
            gleisbildModelStore.this.theapplet.showStatus("IO Fehler: Daten konnten nicht geladen werden!", 1);
            gleisbildModelStore.this.theapplet.showStatus(var8.getMessage(), 4);
         } catch (Exception var9) {
            Logger.getLogger("stslogger").log(Level.SEVERE, "GB laden", var9);
            gleisbildModelStore.this.theapplet.showStatus("Allgemeiner Fehler: Daten konnten nicht geladen werden!", 1);
            gleisbildModelStore.this.theapplet.showStatus(var9.getMessage(), 4);
         } finally {
            gleisbildModelStore.this.loaderThread = null;
         }

         this.done(suc);
      }
   }

   private class ioPinger extends gleisbildModelStore.ioRunner {
      ioPinger(String run_data) {
         super(run_data);
         this.setName("GB ping");
      }

      public void run() {
         gleisbildModelStore.this.theapplet.showStatus("Server Session update...", 0);

         try {
            StringBuffer data = new StringBuffer();
            data.append("&ping=1");
            gleisbildModelStore.this.saveData(this.run_data, data);
         } catch (Exception var5) {
            gleisbildModelStore.this.theapplet.showStatus("IO Fehler: Keine Verbindung zum Server!", 1);
         } finally {
            gleisbildModelStore.this.pingThread = null;
         }
      }
   }

   private abstract class ioRunner extends Thread {
      protected final String run_data;
      private final gleisbildModelStore.ioDoneMessage done;

      ioRunner(String run_data) {
         this(run_data, null);
      }

      ioRunner(String run_data, gleisbildModelStore.ioDoneMessage m) {
         super();
         this.run_data = run_data;
         this.done = m;
      }

      protected void done(final boolean m) {
         if (this.done != null) {
            if (SwingUtilities.isEventDispatchThread()) {
               this.done.done(m);
            } else {
               SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                     ioRunner.this.done.done(m);
                  }
               });
            }
         }
      }
   }

   private class ioSaver extends gleisbildModelStore.ioRunner {
      ioSaver(String run_data, gleisbildModelStore.ioDoneMessage m) {
         super(run_data, m);
         this.setName("GB save");
      }

      public void run() {
         boolean suc = false;
         gleisbildModelStore.this.theapplet.showStatus("Daten auf Server speichern...", 0);

         try {
            boolean s = gleisbildModelStore.this.sendData(this.run_data);
            if (s) {
               gleisbildModelStore.this.resetChangeC();
               gleisbildModelStore.this.theapplet.showStatus("Daten erfolgreich gesendet!", 0);
               suc = true;
            } else {
               gleisbildModelStore.this.theapplet.showStatus("Daten nicht gespeichert!", 1);
            }
         } catch (IOException var7) {
            gleisbildModelStore.this.theapplet.showStatus("IO Fehler: Daten konnten nicht gespeichert werden!", 1);
            gleisbildModelStore.this.theapplet.showStatus(var7.getMessage(), 4);
            System.out.println("Ex: " + var7.getMessage());
         } catch (Exception var8) {
            gleisbildModelStore.this.theapplet.showStatus("Allgemeiner Fehler: Daten konnten nicht gespeichert werden!", 1);
            gleisbildModelStore.this.theapplet.showStatus(var8.getMessage(), 4);
            Logger.getLogger("stslogger").log(Level.SEVERE, "GB sichern", var8);
            System.out.println("Ex: " + var8.getMessage());
         } finally {
            gleisbildModelStore.this.saverThread = null;
         }

         this.done(suc);
      }
   }
}
