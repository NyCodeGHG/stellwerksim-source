package org.relayirc.core;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.AccessControlException;
import org.relayirc.util.Debug;

public class IdentServer implements Runnable {
   private String _userName = null;
   private ServerSocket _echoServer = null;
   private Socket _clientSocket = null;

   public IdentServer(String userName) {
      super();
      this._userName = userName;
      Thread t = new Thread(this);
      t.setName("IdentServer");
      t.start();
   }

   public void stop() {
      Debug.println("IdentServer: stopping");

      try {
         this._echoServer.close();
      } catch (Exception var3) {
         Debug.println("   _echoServer is null");
      }

      try {
         this._clientSocket.close();
      } catch (Exception var2) {
         Debug.println("   _clientSocket is null");
      }
   }

   public void run() {
      BufferedReader is;
      DataOutputStream os;
      try {
         this._echoServer = new ServerSocket(113);
         this._echoServer.setSoTimeout(60000);
         this._clientSocket = this._echoServer.accept();
         is = new BufferedReader(new InputStreamReader(new DataInputStream(this._clientSocket.getInputStream())));
         os = new DataOutputStream(this._clientSocket.getOutputStream());
      } catch (InterruptedIOException var5) {
         Debug.println("IdentServer: exiting due to InterruptedIOException");
         return;
      } catch (BindException var6) {
         this.printWarning();
         return;
      } catch (SocketException var7) {
         Debug.println("IdentServer: exiting due to SocketException");
         return;
      } catch (AccessControlException var8) {
         return;
      } catch (Exception var9) {
         var9.printStackTrace();
         return;
      }

      try {
         String line;
         do {
            line = is.readLine();
         } while(line == null);

         String resp = line + " : USERID : UNIX : " + this._userName;
         os.writeBytes(resp);
         this.stop();
      } catch (Exception var10) {
         var10.printStackTrace();
      }
   }

   public void printWarning() {
   }
}
