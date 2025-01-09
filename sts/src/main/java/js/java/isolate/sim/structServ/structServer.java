package js.java.isolate.sim.structServ;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.isolate.sim.sim.stellwerksim_main;
import js.java.isolate.sim.sim.plugin.ServImpl;
import js.java.isolate.sim.sim.plugin.pluginServ;

public class structServer extends structDeliverer implements Runnable {
   private static final int TIMEPORT = 3288;
   private Socket tcp;
   private Thread listenThread;
   private volatile boolean running = true;
   private String host;
   private pluginServ bypass = null;

   public structServer(String u, stellwerksim_main my_main) {
      super(my_main);

      try {
         String h;
         if (u.startsWith("http")) {
            URL url = new URL(u);
            h = url.getHost();
         } else {
            h = "public8.stellwerksim.de";
         }

         this.host = h;
         this.init();
      } catch (Exception var5) {
         Logger.getLogger("stslogger").log(Level.SEVERE, "ctor()", var5);
      }
   }

   public structServer(stellwerksim_main my_main) {
      this(my_main.getParameter("running"), my_main);
      Logger.getLogger("stslogger").log(Level.SEVERE, "Structserv created by " + my_main.getParameter("nick3"));
   }

   protected void finalize() throws Throwable {
      this.stop();
      super.finalize();
   }

   private void init() throws IOException {
      this.tcp = new Socket(this.host, 3288);
      this.listenThread = new Thread(this);
      this.listenThread.start();
   }

   public void stop() {
      this.running = false;

      try {
         this.tcp.close();
      } catch (IOException var2) {
      }
   }

   protected void runCommand2(String cmd, ServImpl.OutputWriter output) throws IOException {
      if (this.bypass != null) {
         this.bypass.injectCommand(cmd, output);
      } else {
         String[] c = cmd.split(" ");
         if (c[0].equals("plugin")) {
            this.bypass = this.my_main.attachPluginClient(output);
         } else {
            super.runCommand(cmd, output);
         }
      }
   }

   public void run() {
      try {
         BufferedReader input = new BufferedReader(new InputStreamReader(this.tcp.getInputStream(), "UTF-8"));
         ServImpl.OutputWriter output = new ServImpl.OutputWriter(new OutputStreamWriter(this.tcp.getOutputStream(), "UTF-8"));
         output.write("sts-welcome-debug-server");
         output.write(":AID:" + this.my_main.getGleisbild().getAid());
         output.flush();

         while (this.running) {
            String cmd = input.readLine();
            this.runCommand2(cmd, output);
         }
      } catch (Exception var6) {
         Logger.getLogger("stslogger").log(Level.SEVERE, "run()", var6);
      }

      this.idhash.clear();

      try {
         this.tcp.close();
      } catch (IOException var5) {
      }

      try {
         if (this.running) {
            this.init();
         }
      } catch (IOException var4) {
      }
   }
}
