package js.java.tools.http;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class NanoWSD extends NanoHTTPD {
   private static final Logger LOG = Logger.getLogger(NanoWSD.class.getName());
   public static final String HEADER_UPGRADE = "upgrade";
   public static final String HEADER_UPGRADE_VALUE = "websocket";
   public static final String HEADER_CONNECTION = "connection";
   public static final String HEADER_CONNECTION_VALUE = "Upgrade";
   public static final String HEADER_WEBSOCKET_VERSION = "sec-websocket-version";
   public static final String HEADER_WEBSOCKET_VERSION_VALUE = "13";
   public static final String HEADER_WEBSOCKET_KEY = "sec-websocket-key";
   public static final String HEADER_WEBSOCKET_ACCEPT = "sec-websocket-accept";
   public static final String HEADER_WEBSOCKET_PROTOCOL = "sec-websocket-protocol";
   private static final String WEBSOCKET_KEY_MAGIC = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
   private static final char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

   private static String encodeBase64(byte[] buf) {
      int size = buf.length;
      char[] ar = new char[(size + 2) / 3 * 4];
      int a = 0;
      int i = 0;

      while (i < size) {
         byte b0 = buf[i++];
         byte b1 = i < size ? buf[i++] : 0;
         byte b2 = i < size ? buf[i++] : 0;
         int mask = 63;
         ar[a++] = ALPHABET[b0 >> 2 & mask];
         ar[a++] = ALPHABET[(b0 << 4 | (b1 & 255) >> 4) & mask];
         ar[a++] = ALPHABET[(b1 << 2 | (b2 & 255) >> 6) & mask];
         ar[a++] = ALPHABET[b2 & mask];
      }

      switch (size % 3) {
         case 1:
            a--;
            ar[a] = '=';
         case 2:
            a--;
            ar[a] = '=';
         default:
            return new String(ar);
      }
   }

   public static String makeAcceptKey(String key) throws NoSuchAlgorithmException {
      MessageDigest md = MessageDigest.getInstance("SHA-1");
      String text = key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
      md.update(text.getBytes(), 0, text.length());
      byte[] sha1hash = md.digest();
      return encodeBase64(sha1hash);
   }

   public NanoWSD(int port) {
      super(port);
   }

   public NanoWSD(String hostname, int port) {
      super(hostname, port);
   }

   private boolean isWebSocketConnectionHeader(Map<String, String> headers) {
      String connection = (String)headers.get("connection");
      return connection != null && connection.toLowerCase().contains("Upgrade".toLowerCase());
   }

   protected boolean isWebsocketRequested(NanoHTTPD.IHTTPSession session) {
      Map<String, String> headers = session.getHeaders();
      String upgrade = (String)headers.get("upgrade");
      boolean isCorrectConnection = this.isWebSocketConnectionHeader(headers);
      boolean isUpgrade = "websocket".equalsIgnoreCase(upgrade);
      return isUpgrade && isCorrectConnection;
   }

   protected abstract NanoWSD.WebSocket openWebSocket(NanoHTTPD.IHTTPSession var1);

   @Override
   public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
      Map<String, String> headers = session.getHeaders();
      if (this.isWebsocketRequested(session)) {
         if (!"13".equalsIgnoreCase((String)headers.get("sec-websocket-version"))) {
            return newFixedLengthResponse(
               NanoHTTPD.Response.Status.BAD_REQUEST, "text/plain", "Invalid Websocket-Version " + (String)headers.get("sec-websocket-version")
            );
         } else if (!headers.containsKey("sec-websocket-key")) {
            return newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "text/plain", "Missing Websocket-Key");
         } else {
            NanoWSD.WebSocket webSocket = this.openWebSocket(session);
            NanoHTTPD.Response handshakeResponse = webSocket.getHandshakeResponse();

            try {
               handshakeResponse.addHeader("sec-websocket-accept", makeAcceptKey((String)headers.get("sec-websocket-key")));
            } catch (NoSuchAlgorithmException var6) {
               return newFixedLengthResponse(
                  NanoHTTPD.Response.Status.INTERNAL_ERROR, "text/plain", "The SHA-1 Algorithm required for websockets is not available on the server."
               );
            }

            if (headers.containsKey("sec-websocket-protocol")) {
               handshakeResponse.addHeader("sec-websocket-protocol", ((String)headers.get("sec-websocket-protocol")).split(",")[0]);
            }

            return handshakeResponse;
         }
      } else {
         return this.serveHttp(session);
      }
   }

   protected NanoHTTPD.Response serveHttp(NanoHTTPD.IHTTPSession session) {
      return super.serve(session);
   }

   @Override
   protected boolean useGzipWhenAccepted(NanoHTTPD.Response r) {
      return false;
   }

   public static enum State {
      UNCONNECTED,
      CONNECTING,
      OPEN,
      CLOSING,
      CLOSED;
   }

   public abstract static class WebSocket {
      private final InputStream in;
      private OutputStream out;
      private NanoWSD.WebSocketFrame.OpCode continuousOpCode = null;
      private final List<NanoWSD.WebSocketFrame> continuousFrames = new LinkedList();
      private NanoWSD.State state = NanoWSD.State.UNCONNECTED;
      private final NanoHTTPD.IHTTPSession handshakeRequest;
      private final NanoHTTPD.Response handshakeResponse = new NanoHTTPD.Response(NanoHTTPD.Response.Status.SWITCH_PROTOCOL, null, (InputStream)null, 0L) {
         @Override
         protected void send(OutputStream out) {
            WebSocket.this.out = out;
            WebSocket.this.state = NanoWSD.State.CONNECTING;
            super.send(out);
            WebSocket.this.state = NanoWSD.State.OPEN;
            WebSocket.this.onOpen();
            WebSocket.this.readWebsocket();
         }
      };

      public WebSocket(NanoHTTPD.IHTTPSession handshakeRequest) {
         this.handshakeRequest = handshakeRequest;
         this.in = handshakeRequest.getInputStream();
         this.handshakeResponse.addHeader("upgrade", "websocket");
         this.handshakeResponse.addHeader("connection", "Upgrade");
      }

      public boolean isOpen() {
         return this.state == NanoWSD.State.OPEN;
      }

      protected abstract void onOpen();

      protected abstract void onClose(NanoWSD.WebSocketFrame.CloseCode var1, String var2, boolean var3);

      protected abstract void onMessage(NanoWSD.WebSocketFrame var1);

      protected abstract void onPong(NanoWSD.WebSocketFrame var1);

      protected abstract void onException(IOException var1);

      protected void debugFrameReceived(NanoWSD.WebSocketFrame frame) {
      }

      protected void debugFrameSent(NanoWSD.WebSocketFrame frame) {
      }

      public void close(NanoWSD.WebSocketFrame.CloseCode code, String reason, boolean initiatedByRemote) throws IOException {
         NanoWSD.State oldState = this.state;
         this.state = NanoWSD.State.CLOSING;
         if (oldState == NanoWSD.State.OPEN) {
            this.sendFrame(new NanoWSD.WebSocketFrame.CloseFrame(code, reason));
         } else {
            this.doClose(code, reason, initiatedByRemote);
         }
      }

      private void doClose(NanoWSD.WebSocketFrame.CloseCode code, String reason, boolean initiatedByRemote) {
         if (this.state != NanoWSD.State.CLOSED) {
            if (this.in != null) {
               try {
                  this.in.close();
               } catch (IOException var6) {
                  NanoWSD.LOG.log(Level.FINE, "close failed", var6);
               }
            }

            if (this.out != null) {
               try {
                  this.out.close();
               } catch (IOException var5) {
                  NanoWSD.LOG.log(Level.FINE, "close failed", var5);
               }
            }

            this.state = NanoWSD.State.CLOSED;
            this.onClose(code, reason, initiatedByRemote);
         }
      }

      public NanoHTTPD.IHTTPSession getHandshakeRequest() {
         return this.handshakeRequest;
      }

      public NanoHTTPD.Response getHandshakeResponse() {
         return this.handshakeResponse;
      }

      private void handleCloseFrame(NanoWSD.WebSocketFrame frame) throws IOException {
         NanoWSD.WebSocketFrame.CloseCode code = NanoWSD.WebSocketFrame.CloseCode.NormalClosure;
         String reason = "";
         if (frame instanceof NanoWSD.WebSocketFrame.CloseFrame) {
            code = ((NanoWSD.WebSocketFrame.CloseFrame)frame).getCloseCode();
            reason = ((NanoWSD.WebSocketFrame.CloseFrame)frame).getCloseReason();
         }

         if (this.state == NanoWSD.State.CLOSING) {
            this.doClose(code, reason, false);
         } else {
            this.close(code, reason, true);
         }
      }

      private void handleFrameFragment(NanoWSD.WebSocketFrame frame) throws IOException {
         if (frame.getOpCode() != NanoWSD.WebSocketFrame.OpCode.Continuation) {
            if (this.continuousOpCode != null) {
               throw new NanoWSD.WebSocketException(NanoWSD.WebSocketFrame.CloseCode.ProtocolError, "Previous continuous frame sequence not completed.");
            }

            this.continuousOpCode = frame.getOpCode();
            this.continuousFrames.clear();
            this.continuousFrames.add(frame);
         } else if (frame.isFin()) {
            if (this.continuousOpCode == null) {
               throw new NanoWSD.WebSocketException(NanoWSD.WebSocketFrame.CloseCode.ProtocolError, "Continuous frame sequence was not started.");
            }

            this.onMessage(new NanoWSD.WebSocketFrame(this.continuousOpCode, this.continuousFrames));
            this.continuousOpCode = null;
            this.continuousFrames.clear();
         } else {
            if (this.continuousOpCode == null) {
               throw new NanoWSD.WebSocketException(NanoWSD.WebSocketFrame.CloseCode.ProtocolError, "Continuous frame sequence was not started.");
            }

            this.continuousFrames.add(frame);
         }
      }

      private void handleWebsocketFrame(NanoWSD.WebSocketFrame frame) throws IOException {
         this.debugFrameReceived(frame);
         if (frame.getOpCode() == NanoWSD.WebSocketFrame.OpCode.Close) {
            this.handleCloseFrame(frame);
         } else if (frame.getOpCode() == NanoWSD.WebSocketFrame.OpCode.Ping) {
            this.sendFrame(new NanoWSD.WebSocketFrame(NanoWSD.WebSocketFrame.OpCode.Pong, true, frame.getBinaryPayload()));
         } else if (frame.getOpCode() == NanoWSD.WebSocketFrame.OpCode.Pong) {
            this.onPong(frame);
         } else if (frame.isFin() && frame.getOpCode() != NanoWSD.WebSocketFrame.OpCode.Continuation) {
            if (this.continuousOpCode != null) {
               throw new NanoWSD.WebSocketException(NanoWSD.WebSocketFrame.CloseCode.ProtocolError, "Continuous frame sequence not completed.");
            }

            if (frame.getOpCode() != NanoWSD.WebSocketFrame.OpCode.Text && frame.getOpCode() != NanoWSD.WebSocketFrame.OpCode.Binary) {
               throw new NanoWSD.WebSocketException(NanoWSD.WebSocketFrame.CloseCode.ProtocolError, "Non control or continuous frame expected.");
            }

            this.onMessage(frame);
         } else {
            this.handleFrameFragment(frame);
         }
      }

      public void ping(byte[] payload) throws IOException {
         this.sendFrame(new NanoWSD.WebSocketFrame(NanoWSD.WebSocketFrame.OpCode.Ping, true, payload));
      }

      private void readWebsocket() {
         try {
            while (this.state == NanoWSD.State.OPEN) {
               this.handleWebsocketFrame(NanoWSD.WebSocketFrame.read(this.in));
            }
         } catch (CharacterCodingException var6) {
            this.onException(var6);
            this.doClose(NanoWSD.WebSocketFrame.CloseCode.InvalidFramePayloadData, var6.toString(), false);
         } catch (IOException var7) {
            this.onException(var7);
            if (var7 instanceof NanoWSD.WebSocketException) {
               this.doClose(((NanoWSD.WebSocketException)var7).getCode(), ((NanoWSD.WebSocketException)var7).getReason(), false);
            }
         } finally {
            this.doClose(NanoWSD.WebSocketFrame.CloseCode.InternalServerError, "Handler terminated without closing the connection.", false);
         }
      }

      public void send(byte[] payload) throws IOException {
         this.sendFrame(new NanoWSD.WebSocketFrame(NanoWSD.WebSocketFrame.OpCode.Binary, true, payload));
      }

      public void send(String payload) throws IOException {
         this.sendFrame(new NanoWSD.WebSocketFrame(NanoWSD.WebSocketFrame.OpCode.Text, true, payload));
      }

      public synchronized void sendFrame(NanoWSD.WebSocketFrame frame) throws IOException {
         this.debugFrameSent(frame);
         frame.write(this.out);
      }
   }

   public static class WebSocketException extends IOException {
      private static final long serialVersionUID = 1L;
      private final NanoWSD.WebSocketFrame.CloseCode code;
      private final String reason;

      public WebSocketException(NanoWSD.WebSocketFrame.CloseCode code, String reason) {
         this(code, reason, null);
      }

      public WebSocketException(NanoWSD.WebSocketFrame.CloseCode code, String reason, Exception cause) {
         super(code + ": " + reason, cause);
         this.code = code;
         this.reason = reason;
      }

      public WebSocketException(Exception cause) {
         this(NanoWSD.WebSocketFrame.CloseCode.InternalServerError, cause.toString(), cause);
      }

      public NanoWSD.WebSocketFrame.CloseCode getCode() {
         return this.code;
      }

      public String getReason() {
         return this.reason;
      }
   }

   public static class WebSocketFrame {
      public static final Charset TEXT_CHARSET = Charset.forName("UTF-8");
      private NanoWSD.WebSocketFrame.OpCode opCode;
      private boolean fin;
      private byte[] maskingKey;
      private byte[] payload;
      private transient int _payloadLength;
      private transient String _payloadString;

      public static String binary2Text(byte[] payload) throws CharacterCodingException {
         return new String(payload, TEXT_CHARSET);
      }

      public static String binary2Text(byte[] payload, int offset, int length) throws CharacterCodingException {
         return new String(payload, offset, length, TEXT_CHARSET);
      }

      private static int checkedRead(int read) throws IOException {
         if (read < 0) {
            throw new EOFException();
         } else {
            return read;
         }
      }

      public static NanoWSD.WebSocketFrame read(InputStream in) throws IOException {
         byte head = (byte)checkedRead(in.read());
         boolean fin = (head & 128) != 0;
         NanoWSD.WebSocketFrame.OpCode opCode = NanoWSD.WebSocketFrame.OpCode.find((byte)(head & 15));
         if ((head & 112) != 0) {
            throw new NanoWSD.WebSocketException(
               NanoWSD.WebSocketFrame.CloseCode.ProtocolError, "The reserved bits (" + Integer.toBinaryString(head & 112) + ") must be 0."
            );
         } else if (opCode == null) {
            throw new NanoWSD.WebSocketException(
               NanoWSD.WebSocketFrame.CloseCode.ProtocolError, "Received frame with reserved/unknown opcode " + (head & 15) + "."
            );
         } else if (opCode.isControlFrame() && !fin) {
            throw new NanoWSD.WebSocketException(NanoWSD.WebSocketFrame.CloseCode.ProtocolError, "Fragmented control frame.");
         } else {
            NanoWSD.WebSocketFrame frame = new NanoWSD.WebSocketFrame(opCode, fin);
            frame.readPayloadInfo(in);
            frame.readPayload(in);
            return (NanoWSD.WebSocketFrame)(frame.getOpCode() == NanoWSD.WebSocketFrame.OpCode.Close ? new NanoWSD.WebSocketFrame.CloseFrame(frame) : frame);
         }
      }

      public static byte[] text2Binary(String payload) throws CharacterCodingException {
         return payload.getBytes(TEXT_CHARSET);
      }

      private WebSocketFrame(NanoWSD.WebSocketFrame.OpCode opCode, boolean fin) {
         this.setOpCode(opCode);
         this.setFin(fin);
      }

      public WebSocketFrame(NanoWSD.WebSocketFrame.OpCode opCode, boolean fin, byte[] payload) {
         this(opCode, fin, payload, null);
      }

      public WebSocketFrame(NanoWSD.WebSocketFrame.OpCode opCode, boolean fin, byte[] payload, byte[] maskingKey) {
         this(opCode, fin);
         this.setMaskingKey(maskingKey);
         this.setBinaryPayload(payload);
      }

      public WebSocketFrame(NanoWSD.WebSocketFrame.OpCode opCode, boolean fin, String payload) throws CharacterCodingException {
         this(opCode, fin, payload, null);
      }

      public WebSocketFrame(NanoWSD.WebSocketFrame.OpCode opCode, boolean fin, String payload, byte[] maskingKey) throws CharacterCodingException {
         this(opCode, fin);
         this.setMaskingKey(maskingKey);
         this.setTextPayload(payload);
      }

      public WebSocketFrame(NanoWSD.WebSocketFrame.OpCode opCode, List<NanoWSD.WebSocketFrame> fragments) throws NanoWSD.WebSocketException {
         this.setOpCode(opCode);
         this.setFin(true);
         long _payloadLength = 0L;

         for (NanoWSD.WebSocketFrame inter : fragments) {
            _payloadLength += (long)inter.getBinaryPayload().length;
         }

         if (_payloadLength >= 0L && _payloadLength <= 2147483647L) {
            this._payloadLength = (int)_payloadLength;
            byte[] payload = new byte[this._payloadLength];
            int offset = 0;

            for (NanoWSD.WebSocketFrame inter : fragments) {
               System.arraycopy(inter.getBinaryPayload(), 0, payload, offset, inter.getBinaryPayload().length);
               offset += inter.getBinaryPayload().length;
            }

            this.setBinaryPayload(payload);
         } else {
            throw new NanoWSD.WebSocketException(NanoWSD.WebSocketFrame.CloseCode.MessageTooBig, "Max frame length has been exceeded.");
         }
      }

      public WebSocketFrame(NanoWSD.WebSocketFrame clone) {
         this.setOpCode(clone.getOpCode());
         this.setFin(clone.isFin());
         this.setBinaryPayload(clone.getBinaryPayload());
         this.setMaskingKey(clone.getMaskingKey());
      }

      public byte[] getBinaryPayload() {
         return this.payload;
      }

      public byte[] getMaskingKey() {
         return this.maskingKey;
      }

      public NanoWSD.WebSocketFrame.OpCode getOpCode() {
         return this.opCode;
      }

      public String getTextPayload() {
         if (this._payloadString == null) {
            try {
               this._payloadString = binary2Text(this.getBinaryPayload());
            } catch (CharacterCodingException var2) {
               throw new RuntimeException("Undetected CharacterCodingException", var2);
            }
         }

         return this._payloadString;
      }

      public boolean isFin() {
         return this.fin;
      }

      public boolean isMasked() {
         return this.maskingKey != null && this.maskingKey.length == 4;
      }

      private String payloadToString() {
         if (this.payload == null) {
            return "null";
         } else {
            StringBuilder sb = new StringBuilder();
            sb.append('[').append(this.payload.length).append("b] ");
            if (this.getOpCode() == NanoWSD.WebSocketFrame.OpCode.Text) {
               String text = this.getTextPayload();
               if (text.length() > 100) {
                  sb.append(text.substring(0, 100)).append("...");
               } else {
                  sb.append(text);
               }
            } else {
               sb.append("0x");

               for (int i = 0; i < Math.min(this.payload.length, 50); i++) {
                  sb.append(Integer.toHexString(this.payload[i] & 255));
               }

               if (this.payload.length > 50) {
                  sb.append("...");
               }
            }

            return sb.toString();
         }
      }

      private void readPayload(InputStream in) throws IOException {
         this.payload = new byte[this._payloadLength];
         int read = 0;

         while (read < this._payloadLength) {
            read += checkedRead(in.read(this.payload, read, this._payloadLength - read));
         }

         if (this.isMasked()) {
            for (int i = 0; i < this.payload.length; i++) {
               this.payload[i] = (byte)(this.payload[i] ^ this.maskingKey[i % 4]);
            }
         }

         if (this.getOpCode() == NanoWSD.WebSocketFrame.OpCode.Text) {
            this._payloadString = binary2Text(this.getBinaryPayload());
         }
      }

      private void readPayloadInfo(InputStream in) throws IOException {
         byte b = (byte)checkedRead(in.read());
         boolean masked = (b & 128) != 0;
         this._payloadLength = (byte)(127 & b);
         if (this._payloadLength == 126) {
            this._payloadLength = (checkedRead(in.read()) << 8 | checkedRead(in.read())) & 65535;
            if (this._payloadLength < 126) {
               throw new NanoWSD.WebSocketException(
                  NanoWSD.WebSocketFrame.CloseCode.ProtocolError, "Invalid data frame 2byte length. (not using minimal length encoding)"
               );
            }
         } else if (this._payloadLength == 127) {
            long _payloadLength = (long)checkedRead(in.read()) << 56
               | (long)checkedRead(in.read()) << 48
               | (long)checkedRead(in.read()) << 40
               | (long)checkedRead(in.read()) << 32
               | (long)(checkedRead(in.read()) << 24)
               | (long)(checkedRead(in.read()) << 16)
               | (long)(checkedRead(in.read()) << 8)
               | (long)checkedRead(in.read());
            if (_payloadLength < 65536L) {
               throw new NanoWSD.WebSocketException(
                  NanoWSD.WebSocketFrame.CloseCode.ProtocolError, "Invalid data frame 4byte length. (not using minimal length encoding)"
               );
            }

            if (_payloadLength < 0L || _payloadLength > 2147483647L) {
               throw new NanoWSD.WebSocketException(NanoWSD.WebSocketFrame.CloseCode.MessageTooBig, "Max frame length has been exceeded.");
            }

            this._payloadLength = (int)_payloadLength;
         }

         if (this.opCode.isControlFrame()) {
            if (this._payloadLength > 125) {
               throw new NanoWSD.WebSocketException(NanoWSD.WebSocketFrame.CloseCode.ProtocolError, "Control frame with payload length > 125 bytes.");
            }

            if (this.opCode == NanoWSD.WebSocketFrame.OpCode.Close && this._payloadLength == 1) {
               throw new NanoWSD.WebSocketException(NanoWSD.WebSocketFrame.CloseCode.ProtocolError, "Received close frame with payload len 1.");
            }
         }

         if (masked) {
            this.maskingKey = new byte[4];
            int read = 0;

            while (read < this.maskingKey.length) {
               read += checkedRead(in.read(this.maskingKey, read, this.maskingKey.length - read));
            }
         }
      }

      public void setBinaryPayload(byte[] payload) {
         this.payload = payload;
         this._payloadLength = payload.length;
         this._payloadString = null;
      }

      public void setFin(boolean fin) {
         this.fin = fin;
      }

      public void setMaskingKey(byte[] maskingKey) {
         if (maskingKey != null && maskingKey.length != 4) {
            throw new IllegalArgumentException("MaskingKey " + Arrays.toString(maskingKey) + " hasn't length 4");
         } else {
            this.maskingKey = maskingKey;
         }
      }

      public void setOpCode(NanoWSD.WebSocketFrame.OpCode opcode) {
         this.opCode = opcode;
      }

      public void setTextPayload(String payload) throws CharacterCodingException {
         this.payload = text2Binary(payload);
         this._payloadLength = payload.length();
         this._payloadString = payload;
      }

      public void setUnmasked() {
         this.setMaskingKey(null);
      }

      public String toString() {
         StringBuilder sb = new StringBuilder("WS[");
         sb.append(this.getOpCode());
         sb.append(", ").append(this.isFin() ? "fin" : "inter");
         sb.append(", ").append(this.isMasked() ? "masked" : "unmasked");
         sb.append(", ").append(this.payloadToString());
         sb.append(']');
         return sb.toString();
      }

      public void write(OutputStream out) throws IOException {
         byte header = 0;
         if (this.fin) {
            header = (byte)(header | 128);
         }

         header = (byte)(header | this.opCode.getValue() & 15);
         out.write(header);
         this._payloadLength = this.getBinaryPayload().length;
         if (this._payloadLength <= 125) {
            out.write(this.isMasked() ? 128 | (byte)this._payloadLength : (byte)this._payloadLength);
         } else if (this._payloadLength <= 65535) {
            out.write(this.isMasked() ? 254 : 126);
            out.write(this._payloadLength >>> 8);
            out.write(this._payloadLength);
         } else {
            out.write(this.isMasked() ? 255 : 127);
            out.write(this._payloadLength >>> 56 & 0);
            out.write(this._payloadLength >>> 48 & 0);
            out.write(this._payloadLength >>> 40 & 0);
            out.write(this._payloadLength >>> 32 & 0);
            out.write(this._payloadLength >>> 24);
            out.write(this._payloadLength >>> 16);
            out.write(this._payloadLength >>> 8);
            out.write(this._payloadLength);
         }

         if (this.isMasked()) {
            out.write(this.maskingKey);

            for (int i = 0; i < this._payloadLength; i++) {
               out.write(this.getBinaryPayload()[i] ^ this.maskingKey[i % 4]);
            }
         } else {
            out.write(this.getBinaryPayload());
         }

         out.flush();
      }

      public static enum CloseCode {
         NormalClosure(1000),
         GoingAway(1001),
         ProtocolError(1002),
         UnsupportedData(1003),
         NoStatusRcvd(1005),
         AbnormalClosure(1006),
         InvalidFramePayloadData(1007),
         PolicyViolation(1008),
         MessageTooBig(1009),
         MandatoryExt(1010),
         InternalServerError(1011),
         TLSHandshake(1015);

         private final int code;

         public static NanoWSD.WebSocketFrame.CloseCode find(int value) {
            for (NanoWSD.WebSocketFrame.CloseCode code : values()) {
               if (code.getValue() == value) {
                  return code;
               }
            }

            return null;
         }

         private CloseCode(int code) {
            this.code = code;
         }

         public int getValue() {
            return this.code;
         }
      }

      public static class CloseFrame extends NanoWSD.WebSocketFrame {
         private NanoWSD.WebSocketFrame.CloseCode _closeCode;
         private String _closeReason;

         private static byte[] generatePayload(NanoWSD.WebSocketFrame.CloseCode code, String closeReason) throws CharacterCodingException {
            if (code != null) {
               byte[] reasonBytes = text2Binary(closeReason);
               byte[] payload = new byte[reasonBytes.length + 2];
               payload[0] = (byte)(code.getValue() >> 8 & 0xFF);
               payload[1] = (byte)(code.getValue() & 0xFF);
               System.arraycopy(reasonBytes, 0, payload, 2, reasonBytes.length);
               return payload;
            } else {
               return new byte[0];
            }
         }

         public CloseFrame(NanoWSD.WebSocketFrame.CloseCode code, String closeReason) throws CharacterCodingException {
            super(NanoWSD.WebSocketFrame.OpCode.Close, true, generatePayload(code, closeReason));
         }

         private CloseFrame(NanoWSD.WebSocketFrame wrap) throws CharacterCodingException {
            super(wrap);

            assert wrap.getOpCode() == NanoWSD.WebSocketFrame.OpCode.Close;

            if (wrap.getBinaryPayload().length >= 2) {
               this._closeCode = NanoWSD.WebSocketFrame.CloseCode.find((wrap.getBinaryPayload()[0] & 255) << 8 | wrap.getBinaryPayload()[1] & 255);
               this._closeReason = binary2Text(this.getBinaryPayload(), 2, this.getBinaryPayload().length - 2);
            }
         }

         public NanoWSD.WebSocketFrame.CloseCode getCloseCode() {
            return this._closeCode;
         }

         public String getCloseReason() {
            return this._closeReason;
         }
      }

      public static enum OpCode {
         Continuation(0),
         Text(1),
         Binary(2),
         Close(8),
         Ping(9),
         Pong(10);

         private final byte code;

         public static NanoWSD.WebSocketFrame.OpCode find(byte value) {
            for (NanoWSD.WebSocketFrame.OpCode opcode : values()) {
               if (opcode.getValue() == value) {
                  return opcode;
               }
            }

            return null;
         }

         private OpCode(int code) {
            this.code = (byte)code;
         }

         public byte getValue() {
            return this.code;
         }

         public boolean isControlFrame() {
            return this == Close || this == Ping || this == Pong;
         }
      }
   }
}
