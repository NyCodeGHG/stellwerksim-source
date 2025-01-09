package js.java.schaltungen;

import java.awt.EventQueue;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Properties;
import javax.swing.InputMap;
import javax.swing.JApplet;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import js.java.tools.logging.ExceptionDialog;

@Deprecated
public class simpleappletmaster extends JApplet {
   public static final String majorRelease = "4.0 moving panels";
   public static final String copyright = "(c) JS 2004-2015";
   public static final int M_MESSAGE = 0;
   public static final int M_ERROR = 1;
   public static final int M_WARNING = 2;
   public static final int M_INFO = 3;
   public static final int M_EXCEPTION = 4;
   public static final int M_DIALOG = 5;
   private static simpleappletmaster myself = null;

   public void init() {
      String p = this.getParameter("log");
      String user = this.getParameter("netbeans_user");
      String pass = this.getParameter("netbeans_pass");
      if (user != null && pass != null) {
         System.out.println("HTTP_AUTH PASSWORT aktiv: " + user + "/" + pass);
         Authenticator.setDefault(new simpleappletmaster.MyAuthenticator(user, pass));
      }

      try {
         EventQueue.invokeAndWait(new Runnable() {
            public void run() {
               simpleappletmaster.this.awtInit();
            }
         });
      } catch (Exception var5) {
         var5.printStackTrace();
      }
   }

   protected void awtInit() {
   }

   public simpleappletmaster() {
      myself = this;
      System.out.println("*** Build: " + this.getBuild());
   }

   public static int static_getBuild() {
      return myself.getBuild();
   }

   public int getBuild() {
      int b = 0;

      try {
         Properties prop = new Properties();
         prop.load(this.getClass().getClassLoader().getResourceAsStream("js/java/build/build.property"));
         b = Integer.parseInt(prop.getProperty("build", b + ""));
      } catch (Exception var3) {
         System.out.println("Ex build loader: " + var3.getMessage());
         var3.printStackTrace();
      }

      return b;
   }

   public void showStatus(String s, int type) {
   }

   public void showStatus(String s) {
      this.showStatus(s, 0);
   }

   public void setDefaultLookAndFeel() {
      this.setLookAndFeel("System");
   }

   public void setLookAndFeel(final String cmd) {
      if (SwingUtilities.isEventDispatchThread()) {
         this.setLookAndFeelAWT(cmd);
      } else {
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               simpleappletmaster.this.setLookAndFeelAWT(cmd);
            }
         });
      }
   }

   private void setLookAndFeelAWT(String cmd) {
      String plaf = UIManager.getSystemLookAndFeelClassName();
      if (cmd.equals("Metal")) {
         plaf = "javax.swing.plaf.metal.MetalLookAndFeel";
      } else if (cmd.equals("Motif")) {
         plaf = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
      } else if (cmd.equals("Windows")) {
         plaf = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
      } else if (cmd.equals("Nimbus")) {
         plaf = UIManager.getSystemLookAndFeelClassName();

         for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
               plaf = info.getClassName();
               break;
            }
         }
      } else if (cmd.equals("Liquid")) {
         plaf = "com.birosoft.liquid.LiquidLookAndFeel";
      } else if (cmd.equals("NimROD")) {
         plaf = "com.nilo.plaf.nimrod.NimRODLookAndFeel";
      } else if (cmd.equals("System")) {
         plaf = UIManager.getSystemLookAndFeelClassName();
      }

      try {
         UIManager.setLookAndFeel(plaf);
         SwingUtilities.updateComponentTreeUI(this);
         InputMap map = (InputMap)UIManager.get("SplitPane.ancestorInputMap");
         KeyStroke keyStrokeF6 = KeyStroke.getKeyStroke(117, 0);
         KeyStroke keyStrokeF8 = KeyStroke.getKeyStroke(119, 0);
         map.remove(keyStrokeF6);
         map.remove(keyStrokeF8);
      } catch (Exception var10) {
         var10.printStackTrace();
         System.err.println(var10.toString());
         System.err.println("Style error! Using default.");
         if (!cmd.equals("System")) {
            this.setLookAndFeelAWT("System");
         }
      }

      try {
         System.setProperty("apple.laf.useScreenMenuBar", "true");
      } catch (Exception var9) {
      }

      ToolTipManager.sharedInstance().setDismissDelay(60000);

      try {
         System.getProperties().put("sun.awt.exception.handler", ExceptionDialog.class.getName());
      } catch (Exception var8) {
      }

      try {
         Thread.setDefaultUncaughtExceptionHandler(new ExceptionDialog());
      } catch (Exception var7) {
      }
   }

   public void setLookAndFeel(final LookAndFeel m) {
      if (SwingUtilities.isEventDispatchThread()) {
         try {
            UIManager.setLookAndFeel(m);
            SwingUtilities.updateComponentTreeUI(this);
         } catch (Exception var4) {
            System.err.println(var4.toString());
            System.err.println("Style error! Using default.");
            this.setLookAndFeel("System");
            var4.printStackTrace();
         }
      } else {
         try {
            SwingUtilities.invokeAndWait(new Runnable() {
               public void run() {
                  simpleappletmaster.this.setLookAndFeel(m);
               }
            });
         } catch (Exception var3) {
         }
      }
   }

   public void start() {
      super.start();
   }

   public void stop() {
      super.stop();
   }

   public void destroy() {
      super.destroy();
      myself = null;
   }

   public static void stackDump() {
      StackTraceElement[] se = Thread.currentThread().getStackTrace();
      String r = "Stack dump:\n";

      for (int i = 0; i < se.length; i++) {
         r = r + se[i].toString() + "\n";
      }

      System.out.println(r);
   }

   public static void showMem(String pos) {
      long heapSize = Runtime.getRuntime().totalMemory();
      long heapMaxSize = Runtime.getRuntime().maxMemory();
      long heapFreeSize = Runtime.getRuntime().freeMemory();
      System.out.println("Mem @ " + pos + " max: " + heapMaxSize + " free: " + heapFreeSize + " cur: " + heapSize);
   }

   public boolean isPreventClose() {
      return false;
   }

   public String getPreventCloseMessage() {
      return "";
   }

   static class MyAuthenticator extends Authenticator {
      final String kuser;
      final String kpass;

      MyAuthenticator(String user, String pass) {
         this.kuser = user;
         this.kpass = pass;
      }

      public PasswordAuthentication getPasswordAuthentication() {
         System.err.println("Feeding username and password for " + this.getRequestingScheme());
         return new PasswordAuthentication(this.kuser, this.kpass.toCharArray());
      }
   }
}
