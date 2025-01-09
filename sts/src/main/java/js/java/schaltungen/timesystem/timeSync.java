package js.java.schaltungen.timesystem;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Timer;
import java.util.TimerTask;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.tools.gui.clock.timerecipient;

public class timeSync implements Runnable, SessionClose {
   private static final int TIMEPORT = 3288;
   private DatagramChannel udp;
   private Thread listenThread = new Thread(this);
   private byte instanz;
   private volatile boolean running = true;
   private volatile boolean syncReceived = false;
   private volatile int timerDelay = 500;
   private timerecipient client = null;
   private Timer repeatTimer = new Timer();

   public timeSync(String host, int instanz, timerecipient client) throws IOException {
      this.instanz = (byte)instanz;
      this.client = client;
      this.init(host);
   }

   public timeSync(URL url, timerecipient client) throws IOException {
      this.client = client;
      String h = url.getHost();
      String q = url.getQuery();
      String[] params = q.split("&");

      for (String param : params) {
         try {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            if (name.equals("instanz")) {
               this.instanz = Byte.parseByte(value);
               break;
            }
         } catch (NumberFormatException | ArrayIndexOutOfBoundsException var12) {
         }
      }

      this.init(h);
   }

   protected void finalize() throws Throwable {
      try {
         this.stop();
      } finally {
         super.finalize();
      }
   }

   private void init(String host) throws IOException {
      InetSocketAddress socket = new InetSocketAddress(host, 3288);
      this.udp = DatagramChannel.open();
      this.udp.connect(socket);
   }

   private ByteBuffer getBuffer() {
      return ByteBuffer.allocate(20);
   }

   public void sync() {
      if (this.running) {
         this.syncReceived = false;
         this.timerDelay = 500;
         this.saveSync();
      } else {
         throw new IllegalStateException();
      }
   }

   private synchronized void saveSync() {
      if (!this.syncReceived && this.udp.isOpen() && this.running) {
         if (!this.listenThread.isAlive()) {
            this.listenThread.start();
         }

         try {
            ByteBuffer sendpacket = this.getBuffer();
            sendpacket.rewind();
            sendpacket.putLong(System.currentTimeMillis());
            sendpacket.putInt(0);
            sendpacket.putInt(0);
            sendpacket.put(this.instanz);
            sendpacket.put(this.instanz);
            sendpacket.putShort((short)0);
            sendpacket.rewind();
            this.udp.write(sendpacket);
         } catch (IOException var2) {
         }

         if (this.timerDelay < 120000 && this.running) {
            this.repeatTimer.schedule(new timeSync.syncTimer(), (long)this.timerDelay);
         }

         this.timerDelay *= 2;
      }
   }

   public synchronized void stop() {
      if (this.running) {
         this.running = false;
         this.repeatTimer.cancel();

         try {
            this.udp.close();
         } catch (IOException var2) {
         }
      }
   }

   @Override
   public void close() {
      this.stop();
   }

   public void run() {
      ByteBuffer receivepacket = this.getBuffer();

      while (this.running) {
         try {
            receivepacket.rewind();
            this.udp.read(receivepacket);
            receivepacket.rewind();
            long t1 = receivepacket.getLong();
            long t2 = (long)receivepacket.getInt();
            t2 = (long)receivepacket.getInt() * 1000L + t2;
            long t3 = System.currentTimeMillis();
            long offset = (t3 - t1) / 2L + (t3 - t2);
            byte inst = receivepacket.get();
            inst = receivepacket.get();
            short tagescode = receivepacket.getShort();
            this.syncReceived = true;
            this.client.timeChange(offset, tagescode, (int)(t3 - t1));
         } catch (IOException var12) {
         }
      }
   }

   private class syncTimer extends TimerTask {
      private syncTimer() {
      }

      public void run() {
         timeSync.this.saveSync();
      }
   }
}
