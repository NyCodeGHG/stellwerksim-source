package js.java.tools.logging;

import java.util.concurrent.LinkedBlockingQueue;

public abstract class WriterQueue extends Thread {
   private final LinkedBlockingQueue<String> messages = new LinkedBlockingQueue();
   private boolean running = true;

   public WriterQueue() {
      this.start();
   }

   public void exit() {
      this.running = false;
      this.interrupt();
   }

   public void offer(String le) {
      this.messages.offer(le);
   }

   public void run() {
      while (this.running) {
         try {
            String m = (String)this.messages.take();
            this.write(m);
         } catch (InterruptedException var2) {
            return;
         } catch (Exception var3) {
            System.out.println("Logger exception: " + var3.getMessage());
            var3.printStackTrace();
         }
      }
   }

   protected abstract void write(String var1);
}
