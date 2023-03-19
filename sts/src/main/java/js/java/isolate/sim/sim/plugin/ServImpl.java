package js.java.isolate.sim.sim.plugin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.tools.streams.TeeInputStream;
import js.java.tools.streams.TeeOutputStream;
import js.java.tools.xml.xmllistener;
import js.java.tools.xml.xmlreader;
import org.xml.sax.Attributes;

public abstract class ServImpl extends ServBase implements Runnable, xmllistener, SessionClose {
   private ServerSocket tcp;
   private Thread listenThread;
   private Thread workerThread;
   protected volatile boolean running = true;
   protected xmlreader xmlr = new xmlreader();
   private LinkedBlockingQueue<ServImpl.commandSpool> cmdQueue = new LinkedBlockingQueue();
   protected ServImpl.OutputWriter currentBuffer = null;

   protected ServImpl(int servport) {
      super();

      try {
         this.workerThread = new Thread(this);
         this.workerThread.setName("plugin-workerThread");
         this.workerThread.start();
         this.tcp = new ServerSocket(servport);
         this.listenThread = new Thread(new ServImpl.listenRunner());
         this.listenThread.setName("plugin-listenThread");
         this.listenThread.start();
      } catch (IOException var3) {
         var3.printStackTrace();
         this.tcp = null;
      } catch (SecurityException var4) {
         this.tcp = null;
      } catch (Exception var5) {
         var5.printStackTrace();
         this.tcp = null;
      }
   }

   protected ServImpl() {
      super();
      this.tcp = null;

      try {
         this.workerThread = new Thread(this);
         this.workerThread.setName("plugin-workerThread");
         this.workerThread.start();
      } catch (Exception var2) {
         var2.printStackTrace();
      }
   }

   public final boolean isWorking() {
      return this.tcp != null;
   }

   @Override
   public void close() {
      this.running = false;
      this.cmdQueue.offer(new ServImpl.commandSpool(null, null));
   }

   protected void addNewClient(Socket client) {
      Thread t = new Thread(this.clientRunnerFactory(client));
      t.setName("plugin-clientThread");
      t.start();
   }

   protected ServImpl.clientRunner clientRunnerFactory(Socket client) {
      return new ServImpl.clientRunner(client, System.out, System.out);
   }

   protected void connected(ServImpl.OutputWriter output) {
   }

   protected void finish(ServImpl.OutputWriter output) {
   }

   protected void runCommand(String cmd, ServImpl.OutputWriter output) throws InterruptedException {
      this.cmdQueue.put(new ServImpl.commandSpool(cmd, output));
   }

   public void run() {
      while(this.running) {
         try {
            ServImpl.commandSpool c = (ServImpl.commandSpool)this.cmdQueue.take();
            if (c.cmd != null) {
               this.currentBuffer = c.output;
               if (c.cmd.startsWith("<") && this.xmlr.isCleanXML(c.cmd)) {
                  this.xmlr.updateDataByString(c.cmd);
               }
            } else {
               this.running = false;
            }
         } catch (InterruptedException var2) {
            this.running = false;
         } catch (Exception var3) {
            var3.printStackTrace();
            this.xmlError(var3);
         }
      }

      try {
         this.tcp.close();
      } catch (Exception var4) {
      }

      this.tcp = null;
   }

   protected abstract void xmlError(Exception var1);

   public abstract void parseStartTag(String var1, Attributes var2);

   public void parseEndTag(String tag, Attributes attrs, String pcdata) {
   }

   public static class OutputWriter extends BufferedWriter {
      private final ServImpl.clientRunner tee;

      OutputWriter(Writer out, ServImpl.clientRunner tee) {
         super(out);
         this.tee = tee;
      }

      public OutputWriter(Writer out) {
         super(out);
         this.tee = null;
      }

      public void setDebug(boolean b) {
         try {
            this.tee.setOutDebug(b);
         } catch (NullPointerException var3) {
         }
      }
   }

   protected class clientRunner extends ServImpl.runner {
      private Socket tcp;
      private BufferedReader input;
      private ServImpl.OutputWriter output;
      private TeeOutputStream outtee;
      private TeeInputStream intee;

      public clientRunner(Socket t, OutputStream inputStreamTee, OutputStream outputStreamTee) {
         super();
         this.tcp = t;

         try {
            this.intee = new TeeInputStream(this.tcp.getInputStream(), inputStreamTee);
            this.intee.setEnable(false);
            this.input = new BufferedReader(new InputStreamReader(this.intee, "UTF-8"));
            this.outtee = new TeeOutputStream(this.tcp.getOutputStream(), outputStreamTee);
            this.outtee.setEnable(false);
            this.output = new ServImpl.OutputWriter(new OutputStreamWriter(this.outtee, "UTF-8"), this);
         } catch (IOException var6) {
            var6.printStackTrace();
         }
      }

      public void setDebug(boolean enable) {
         this.intee.setEnable(enable);
         this.outtee.setEnable(enable);
      }

      public void setInDebug(boolean enable) {
         this.intee.setEnable(enable);
      }

      public void setOutDebug(boolean enable) {
         this.outtee.setEnable(enable);
      }

      @Override
      public void run() {
         ServImpl.this.connected(this.output);
         super.run();
      }

      @Override
      protected void run_impl() throws Exception {
         this.stopped = this.stopped || !this.tcp.isConnected();
         if (!this.stopped) {
            try {
               String cmd = this.input.readLine();
               if (cmd != null && !cmd.isEmpty()) {
                  ServImpl.this.runCommand(cmd, this.output);
               } else {
                  this.stopped = true;
               }
            } catch (IOException var2) {
               this.stopped = true;
            }
         }
      }

      @Override
      protected void run_finish() {
         try {
            ServImpl.this.finish(this.output);
            this.tcp.close();
         } catch (Exception var2) {
            var2.printStackTrace();
         }
      }
   }

   private class commandSpool {
      final String cmd;
      final ServImpl.OutputWriter output;

      commandSpool(String cmd, ServImpl.OutputWriter output) {
         super();
         this.cmd = cmd;
         this.output = output;
      }
   }

   private class listenRunner extends ServImpl.runner {
      private listenRunner() {
         super();
      }

      @Override
      protected void run_impl() throws Exception {
         Socket newConnection = ServImpl.this.tcp.accept();
         System.out.println("Connected");
         ServImpl.this.addNewClient(newConnection);
      }

      @Override
      protected void run_finish() {
         try {
            ServImpl.this.tcp.close();
         } catch (Exception var2) {
         }
      }
   }

   protected abstract class runner implements Runnable {
      protected boolean stopped = false;

      protected runner() {
         super();
      }

      public void run() {
         while(ServImpl.this.running && !this.stopped) {
            try {
               this.run_impl();
            } catch (Exception var2) {
            }
         }

         this.run_finish();
      }

      protected abstract void run_impl() throws Exception;

      protected abstract void run_finish();
   }
}
