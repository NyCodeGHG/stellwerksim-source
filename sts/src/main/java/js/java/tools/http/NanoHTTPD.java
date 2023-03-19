package js.java.tools.http;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public abstract class NanoHTTPD {
   private static final String CONTENT_REGEX = "[ |\t]*([^/^ ^;^,]+/[^ ^;^,]+)";
   private static final Pattern MIME_PATTERN = Pattern.compile("[ |\t]*([^/^ ^;^,]+/[^ ^;^,]+)", 2);
   private static final String CHARSET_REGEX = "[ |\t]*(charset)[ |\t]*=[ |\t]*['|\"]?([^\"^'^;^,]*)['|\"]?";
   private static final Pattern CHARSET_PATTERN = Pattern.compile("[ |\t]*(charset)[ |\t]*=[ |\t]*['|\"]?([^\"^'^;^,]*)['|\"]?", 2);
   private static final String BOUNDARY_REGEX = "[ |\t]*(boundary)[ |\t]*=[ |\t]*['|\"]?([^\"^'^;^,]*)['|\"]?";
   private static final Pattern BOUNDARY_PATTERN = Pattern.compile("[ |\t]*(boundary)[ |\t]*=[ |\t]*['|\"]?([^\"^'^;^,]*)['|\"]?", 2);
   private static final String CONTENT_DISPOSITION_REGEX = "([ |\t]*Content-Disposition[ |\t]*:)(.*)";
   private static final Pattern CONTENT_DISPOSITION_PATTERN = Pattern.compile("([ |\t]*Content-Disposition[ |\t]*:)(.*)", 2);
   private static final String CONTENT_TYPE_REGEX = "([ |\t]*content-type[ |\t]*:)(.*)";
   private static final Pattern CONTENT_TYPE_PATTERN = Pattern.compile("([ |\t]*content-type[ |\t]*:)(.*)", 2);
   private static final String CONTENT_DISPOSITION_ATTRIBUTE_REGEX = "[ |\t]*([a-zA-Z]*)[ |\t]*=[ |\t]*['|\"]([^\"^']*)['|\"]";
   private static final Pattern CONTENT_DISPOSITION_ATTRIBUTE_PATTERN = Pattern.compile("[ |\t]*([a-zA-Z]*)[ |\t]*=[ |\t]*['|\"]([^\"^']*)['|\"]");
   public static final int SOCKET_READ_TIMEOUT = 5000;
   public static final String MIME_PLAINTEXT = "text/plain";
   public static final String MIME_HTML = "text/html";
   private static final String QUERY_STRING_PARAMETER = "NanoHttpd.QUERY_STRING";
   private static final Logger LOG = Logger.getLogger(NanoHTTPD.class.getName());
   protected static Map<String, String> MIME_TYPES;
   private final String hostname;
   private final int myPort;
   private volatile ServerSocket myServerSocket;
   private NanoHTTPD.ServerSocketFactory serverSocketFactory = new NanoHTTPD.DefaultServerSocketFactory();
   private Thread myThread;
   protected NanoHTTPD.AsyncRunner asyncRunner;
   private NanoHTTPD.TempFileManagerFactory tempFileManagerFactory;

   public static Map<String, String> mimeTypes() {
      if (MIME_TYPES == null) {
         MIME_TYPES = new HashMap();
         loadMimeTypes(MIME_TYPES, "META-INF/nanohttpd/default-mimetypes.properties");
         loadMimeTypes(MIME_TYPES, "META-INF/nanohttpd/mimetypes.properties");
         if (MIME_TYPES.isEmpty()) {
            LOG.log(Level.WARNING, "no mime types found in the classpath! please provide mimetypes.properties");
         }
      }

      return MIME_TYPES;
   }

   private static void loadMimeTypes(Map<String, String> result, String resourceName) {
      Properties properties;
      try {
         for(Enumeration<URL> resources = NanoHTTPD.class.getClassLoader().getResources(resourceName); resources.hasMoreElements(); result.putAll(properties)) {
            URL url = (URL)resources.nextElement();
            properties = new Properties();
            InputStream stream = null;

            try {
               stream = url.openStream();
               properties.load(url.openStream());
            } catch (IOException var11) {
               LOG.log(Level.SEVERE, "could not load mimetypes from " + url, var11);
            } finally {
               safeClose(stream);
            }
         }
      } catch (IOException var13) {
         LOG.log(Level.INFO, "no mime types available at " + resourceName);
      }
   }

   public static SSLServerSocketFactory makeSSLSocketFactory(KeyStore loadedKeyStore, KeyManager[] keyManagers) throws IOException {
      SSLServerSocketFactory res = null;

      try {
         TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
         trustManagerFactory.init(loadedKeyStore);
         SSLContext ctx = SSLContext.getInstance("TLS");
         ctx.init(keyManagers, trustManagerFactory.getTrustManagers(), null);
         return ctx.getServerSocketFactory();
      } catch (Exception var5) {
         throw new IOException(var5.getMessage());
      }
   }

   public static SSLServerSocketFactory makeSSLSocketFactory(KeyStore loadedKeyStore, KeyManagerFactory loadedKeyFactory) throws IOException {
      try {
         return makeSSLSocketFactory(loadedKeyStore, loadedKeyFactory.getKeyManagers());
      } catch (Exception var3) {
         throw new IOException(var3.getMessage());
      }
   }

   public static SSLServerSocketFactory makeSSLSocketFactory(String keyAndTrustStoreClasspathPath, char[] passphrase) throws IOException {
      try {
         KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
         InputStream keystoreStream = NanoHTTPD.class.getResourceAsStream(keyAndTrustStoreClasspathPath);
         if (keystoreStream == null) {
            throw new IOException("Unable to load keystore from classpath: " + keyAndTrustStoreClasspathPath);
         } else {
            keystore.load(keystoreStream, passphrase);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keystore, passphrase);
            return makeSSLSocketFactory(keystore, keyManagerFactory);
         }
      } catch (Exception var5) {
         throw new IOException(var5.getMessage());
      }
   }

   public static String getMimeTypeForFile(String uri) {
      int dot = uri.lastIndexOf(46);
      String mime = null;
      if (dot >= 0) {
         mime = (String)mimeTypes().get(uri.substring(dot + 1).toLowerCase());
      }

      return mime == null ? "application/octet-stream" : mime;
   }

   private static final void safeClose(Object closeable) {
      try {
         if (closeable != null) {
            if (closeable instanceof Closeable) {
               ((Closeable)closeable).close();
            } else if (closeable instanceof Socket) {
               ((Socket)closeable).close();
            } else {
               if (!(closeable instanceof ServerSocket)) {
                  throw new IllegalArgumentException("Unknown object to close");
               }

               ((ServerSocket)closeable).close();
            }
         }
      } catch (IOException var2) {
         LOG.log(Level.SEVERE, "Could not close", var2);
      }
   }

   public NanoHTTPD(int port) {
      this(null, port);
   }

   public NanoHTTPD(String hostname, int port) {
      super();
      this.hostname = hostname;
      this.myPort = port;
      this.setTempFileManagerFactory(new NanoHTTPD.DefaultTempFileManagerFactory());
      this.setAsyncRunner(new NanoHTTPD.DefaultAsyncRunner());
   }

   public synchronized void closeAllConnections() {
      this.stop();
   }

   protected NanoHTTPD.ClientHandler createClientHandler(Socket finalAccept, InputStream inputStream) {
      return new NanoHTTPD.ClientHandler(inputStream, finalAccept);
   }

   protected NanoHTTPD.ServerRunnable createServerRunnable(int timeout) {
      return new NanoHTTPD.ServerRunnable(timeout);
   }

   protected static Map<String, List<String>> decodeParameters(Map<String, String> parms) {
      return decodeParameters((String)parms.get("NanoHttpd.QUERY_STRING"));
   }

   protected static Map<String, List<String>> decodeParameters(String queryString) {
      Map<String, List<String>> parms = new HashMap();
      if (queryString != null) {
         StringTokenizer st = new StringTokenizer(queryString, "&");

         while(st.hasMoreTokens()) {
            String e = st.nextToken();
            int sep = e.indexOf(61);
            String propertyName = sep >= 0 ? decodePercent(e.substring(0, sep)).trim() : decodePercent(e).trim();
            if (!parms.containsKey(propertyName)) {
               parms.put(propertyName, new ArrayList());
            }

            String propertyValue = sep >= 0 ? decodePercent(e.substring(sep + 1)) : null;
            if (propertyValue != null) {
               ((List)parms.get(propertyName)).add(propertyValue);
            }
         }
      }

      return parms;
   }

   protected static String decodePercent(String str) {
      String decoded = null;

      try {
         decoded = URLDecoder.decode(str, "UTF8");
      } catch (UnsupportedEncodingException var3) {
         LOG.log(Level.WARNING, "Encoding not supported, ignored", var3);
      }

      return decoded;
   }

   protected boolean useGzipWhenAccepted(NanoHTTPD.Response r) {
      return r.getMimeType() != null && r.getMimeType().toLowerCase().contains("text/");
   }

   public final int getListeningPort() {
      return this.myServerSocket == null ? -1 : this.myServerSocket.getLocalPort();
   }

   public final boolean isAlive() {
      return this.wasStarted() && !this.myServerSocket.isClosed() && this.myThread.isAlive();
   }

   public NanoHTTPD.ServerSocketFactory getServerSocketFactory() {
      return this.serverSocketFactory;
   }

   public void setServerSocketFactory(NanoHTTPD.ServerSocketFactory serverSocketFactory) {
      this.serverSocketFactory = serverSocketFactory;
   }

   public String getHostname() {
      return this.hostname;
   }

   public NanoHTTPD.TempFileManagerFactory getTempFileManagerFactory() {
      return this.tempFileManagerFactory;
   }

   public void makeSecure(SSLServerSocketFactory sslServerSocketFactory, String[] sslProtocols) {
      this.serverSocketFactory = new NanoHTTPD.SecureServerSocketFactory(sslServerSocketFactory, sslProtocols);
   }

   public static NanoHTTPD.Response newChunkedResponse(NanoHTTPD.Response.IStatus status, String mimeType, InputStream data) {
      return new NanoHTTPD.Response(status, mimeType, data, -1L);
   }

   public static NanoHTTPD.Response newFixedLengthResponse(NanoHTTPD.Response.IStatus status, String mimeType, InputStream data, long totalBytes) {
      return new NanoHTTPD.Response(status, mimeType, data, totalBytes);
   }

   public static NanoHTTPD.Response newFixedLengthResponse(NanoHTTPD.Response.IStatus status, String mimeType, String txt) {
      if (txt == null) {
         return newFixedLengthResponse(status, mimeType, new ByteArrayInputStream(new byte[0]), 0L);
      } else {
         byte[] bytes;
         try {
            bytes = txt.getBytes("UTF-8");
         } catch (UnsupportedEncodingException var5) {
            LOG.log(Level.SEVERE, "encoding problem, responding nothing", var5);
            bytes = new byte[0];
         }

         return newFixedLengthResponse(status, mimeType, new ByteArrayInputStream(bytes), (long)bytes.length);
      }
   }

   public static NanoHTTPD.Response newFixedLengthResponse(String msg) {
      return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/html", msg);
   }

   public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
      Map<String, String> files = new HashMap();
      NanoHTTPD.Method method = session.getMethod();
      if (NanoHTTPD.Method.PUT.equals(method) || NanoHTTPD.Method.POST.equals(method)) {
         try {
            session.parseBody(files);
         } catch (IOException var5) {
            return newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "text/plain", "SERVER INTERNAL ERROR: IOException: " + var5.getMessage());
         } catch (NanoHTTPD.ResponseException var6) {
            return newFixedLengthResponse(var6.getStatus(), "text/plain", var6.getMessage());
         }
      }

      Map<String, String> parms = session.getParms();
      parms.put("NanoHttpd.QUERY_STRING", session.getQueryParameterString());
      return this.serve(session.getUri(), method, session.getHeaders(), parms, files);
   }

   @Deprecated
   public NanoHTTPD.Response serve(String uri, NanoHTTPD.Method method, Map<String, String> headers, Map<String, String> parms, Map<String, String> files) {
      return newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "text/plain", "Not Found");
   }

   public void setAsyncRunner(NanoHTTPD.AsyncRunner asyncRunner) {
      this.asyncRunner = asyncRunner;
   }

   public void setTempFileManagerFactory(NanoHTTPD.TempFileManagerFactory tempFileManagerFactory) {
      this.tempFileManagerFactory = tempFileManagerFactory;
   }

   public void start() throws IOException {
      this.start(5000);
   }

   public void start(int timeout) throws IOException {
      this.start(timeout, true);
   }

   public void start(int timeout, boolean daemon) throws IOException {
      this.myServerSocket = this.getServerSocketFactory().create();
      this.myServerSocket.setReuseAddress(true);
      NanoHTTPD.ServerRunnable serverRunnable = this.createServerRunnable(timeout);
      this.myThread = new Thread(serverRunnable);
      this.myThread.setDaemon(daemon);
      this.myThread.setName("NanoHttpd Main Listener");
      this.myThread.start();

      while(!serverRunnable.hasBinded && serverRunnable.bindException == null) {
         try {
            Thread.sleep(10L);
         } catch (Throwable var5) {
         }
      }

      if (serverRunnable.bindException != null) {
         throw serverRunnable.bindException;
      }
   }

   public void stop() {
      try {
         safeClose(this.myServerSocket);
         this.asyncRunner.closeAll();
         if (this.myThread != null) {
            this.myThread.join();
         }
      } catch (Exception var2) {
         LOG.log(Level.SEVERE, "Could not stop all connections", var2);
      }
   }

   public final boolean wasStarted() {
      return this.myServerSocket != null && this.myThread != null;
   }

   public interface AsyncRunner {
      void closeAll();

      void closed(NanoHTTPD.ClientHandler var1);

      void exec(NanoHTTPD.ClientHandler var1);
   }

   public class ClientHandler implements Runnable {
      private final InputStream inputStream;
      private final Socket acceptSocket;

      private ClientHandler(InputStream inputStream, Socket acceptSocket) {
         super();
         this.inputStream = inputStream;
         this.acceptSocket = acceptSocket;
      }

      public void close() {
         NanoHTTPD.safeClose(this.inputStream);
         NanoHTTPD.safeClose(this.acceptSocket);
      }

      public void run() {
         OutputStream outputStream = null;

         try {
            outputStream = this.acceptSocket.getOutputStream();
            NanoHTTPD.TempFileManager tempFileManager = NanoHTTPD.this.tempFileManagerFactory.create();
            NanoHTTPD.HTTPSession session = NanoHTTPD.this.new HTTPSession(tempFileManager, this.inputStream, outputStream, this.acceptSocket.getInetAddress());

            while(!this.acceptSocket.isClosed()) {
               session.execute();
            }
         } catch (Exception var7) {
            if ((!(var7 instanceof SocketException) || !"NanoHttpd Shutdown".equals(var7.getMessage())) && !(var7 instanceof SocketTimeoutException)) {
               NanoHTTPD.LOG.log(Level.SEVERE, "Communication with the client broken, or an bug in the handler code", var7);
            }
         } finally {
            NanoHTTPD.safeClose(outputStream);
            NanoHTTPD.safeClose(this.inputStream);
            NanoHTTPD.safeClose(this.acceptSocket);
            NanoHTTPD.this.asyncRunner.closed(this);
         }
      }
   }

   public static class Cookie {
      private final String n;
      private final String v;
      private final String e;

      public static String getHTTPTime(int days) {
         Calendar calendar = Calendar.getInstance();
         SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
         dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
         calendar.add(5, days);
         return dateFormat.format(calendar.getTime());
      }

      public Cookie(String name, String value) {
         this(name, value, 30);
      }

      public Cookie(String name, String value, int numDays) {
         super();
         this.n = name;
         this.v = value;
         this.e = getHTTPTime(numDays);
      }

      public Cookie(String name, String value, String expires) {
         super();
         this.n = name;
         this.v = value;
         this.e = expires;
      }

      public String getHTTPHeader() {
         String fmt = "%s=%s; expires=%s";
         return String.format(fmt, this.n, this.v, this.e);
      }
   }

   public class CookieHandler implements Iterable<String> {
      private final HashMap<String, String> cookies = new HashMap();
      private final ArrayList<NanoHTTPD.Cookie> queue = new ArrayList();

      public CookieHandler(Map<String, String> httpHeaders) {
         super();
         String raw = (String)httpHeaders.get("cookie");
         if (raw != null) {
            String[] tokens = raw.split(";");

            for(String token : tokens) {
               String[] data = token.trim().split("=");
               if (data.length == 2) {
                  this.cookies.put(data[0], data[1]);
               }
            }
         }
      }

      public void delete(String name) {
         this.set(name, "-delete-", -30);
      }

      public Iterator<String> iterator() {
         return this.cookies.keySet().iterator();
      }

      public String read(String name) {
         return (String)this.cookies.get(name);
      }

      public void set(NanoHTTPD.Cookie cookie) {
         this.queue.add(cookie);
      }

      public void set(String name, String value, int expires) {
         this.queue.add(new NanoHTTPD.Cookie(name, value, NanoHTTPD.Cookie.getHTTPTime(expires)));
      }

      public void unloadQueue(NanoHTTPD.Response response) {
         for(NanoHTTPD.Cookie cookie : this.queue) {
            response.addHeader("Set-Cookie", cookie.getHTTPHeader());
         }
      }
   }

   public static class DefaultAsyncRunner implements NanoHTTPD.AsyncRunner {
      private long requestCount;
      private final List<NanoHTTPD.ClientHandler> running = Collections.synchronizedList(new ArrayList());

      public DefaultAsyncRunner() {
         super();
      }

      public List<NanoHTTPD.ClientHandler> getRunning() {
         return this.running;
      }

      @Override
      public void closeAll() {
         for(NanoHTTPD.ClientHandler clientHandler : new ArrayList(this.running)) {
            clientHandler.close();
         }
      }

      @Override
      public void closed(NanoHTTPD.ClientHandler clientHandler) {
         this.running.remove(clientHandler);
      }

      @Override
      public void exec(NanoHTTPD.ClientHandler clientHandler) {
         ++this.requestCount;
         Thread t = new Thread(clientHandler);
         t.setDaemon(true);
         t.setName("NanoHttpd Request Processor (#" + this.requestCount + ")");
         this.running.add(clientHandler);
         t.start();
      }
   }

   public static class DefaultServerSocketFactory implements NanoHTTPD.ServerSocketFactory {
      public DefaultServerSocketFactory() {
         super();
      }

      @Override
      public ServerSocket create() throws IOException {
         return new ServerSocket();
      }
   }

   public static class DefaultTempFile implements NanoHTTPD.TempFile {
      private final File file;
      private final OutputStream fstream;

      public DefaultTempFile(File tempdir) throws IOException {
         super();
         this.file = File.createTempFile("NanoHTTPD-", "", tempdir);
         this.fstream = new FileOutputStream(this.file);
      }

      @Override
      public void delete() throws Exception {
         NanoHTTPD.safeClose(this.fstream);
         if (!this.file.delete()) {
            throw new Exception("could not delete temporary file");
         }
      }

      @Override
      public String getName() {
         return this.file.getAbsolutePath();
      }

      @Override
      public OutputStream open() throws Exception {
         return this.fstream;
      }
   }

   public static class DefaultTempFileManager implements NanoHTTPD.TempFileManager {
      private final File tmpdir = new File(System.getProperty("java.io.tmpdir"));
      private final List<NanoHTTPD.TempFile> tempFiles;

      public DefaultTempFileManager() {
         super();
         if (!this.tmpdir.exists()) {
            this.tmpdir.mkdirs();
         }

         this.tempFiles = new ArrayList();
      }

      @Override
      public void clear() {
         for(NanoHTTPD.TempFile file : this.tempFiles) {
            try {
               file.delete();
            } catch (Exception var4) {
               NanoHTTPD.LOG.log(Level.WARNING, "could not delete file ", var4);
            }
         }

         this.tempFiles.clear();
      }

      @Override
      public NanoHTTPD.TempFile createTempFile(String filename_hint) throws Exception {
         NanoHTTPD.DefaultTempFile tempFile = new NanoHTTPD.DefaultTempFile(this.tmpdir);
         this.tempFiles.add(tempFile);
         return tempFile;
      }
   }

   private class DefaultTempFileManagerFactory implements NanoHTTPD.TempFileManagerFactory {
      private DefaultTempFileManagerFactory() {
         super();
      }

      @Override
      public NanoHTTPD.TempFileManager create() {
         return new NanoHTTPD.DefaultTempFileManager();
      }
   }

   protected class HTTPSession implements NanoHTTPD.IHTTPSession {
      private static final int REQUEST_BUFFER_LEN = 512;
      private static final int MEMORY_STORE_LIMIT = 1024;
      public static final int BUFSIZE = 8192;
      public static final int MAX_HEADER_SIZE = 1024;
      private final NanoHTTPD.TempFileManager tempFileManager;
      private final OutputStream outputStream;
      private final BufferedInputStream inputStream;
      private int splitbyte;
      private int rlen;
      private String uri;
      private NanoHTTPD.Method method;
      private Map<String, String> parms;
      private Map<String, String> headers;
      private NanoHTTPD.CookieHandler cookies;
      private String queryParameterString;
      private String remoteIp;
      private String remoteHostname;
      private String protocolVersion;

      public HTTPSession(NanoHTTPD.TempFileManager tempFileManager, InputStream inputStream, OutputStream outputStream) {
         super();
         this.tempFileManager = tempFileManager;
         this.inputStream = new BufferedInputStream(inputStream, 8192);
         this.outputStream = outputStream;
      }

      public HTTPSession(NanoHTTPD.TempFileManager tempFileManager, InputStream inputStream, OutputStream outputStream, InetAddress inetAddress) {
         super();
         this.tempFileManager = tempFileManager;
         this.inputStream = new BufferedInputStream(inputStream, 8192);
         this.outputStream = outputStream;
         this.remoteIp = !inetAddress.isLoopbackAddress() && !inetAddress.isAnyLocalAddress() ? inetAddress.getHostAddress().toString() : "127.0.0.1";
         this.remoteHostname = !inetAddress.isLoopbackAddress() && !inetAddress.isAnyLocalAddress() ? inetAddress.getHostName().toString() : "localhost";
         this.headers = new HashMap();
      }

      private void decodeHeader(BufferedReader in, Map<String, String> pre, Map<String, String> parms, Map<String, String> headers) throws NanoHTTPD.ResponseException {
         try {
            String inLine = in.readLine();
            if (inLine != null) {
               StringTokenizer st = new StringTokenizer(inLine);
               if (!st.hasMoreTokens()) {
                  throw new NanoHTTPD.ResponseException(NanoHTTPD.Response.Status.BAD_REQUEST, "BAD REQUEST: Syntax error. Usage: GET /example/file.html");
               } else {
                  pre.put("method", st.nextToken());
                  if (!st.hasMoreTokens()) {
                     throw new NanoHTTPD.ResponseException(NanoHTTPD.Response.Status.BAD_REQUEST, "BAD REQUEST: Missing URI. Usage: GET /example/file.html");
                  } else {
                     String uri = st.nextToken();
                     int qmi = uri.indexOf(63);
                     if (qmi >= 0) {
                        this.decodeParms(uri.substring(qmi + 1), parms);
                        uri = NanoHTTPD.decodePercent(uri.substring(0, qmi));
                     } else {
                        uri = NanoHTTPD.decodePercent(uri);
                     }

                     if (st.hasMoreTokens()) {
                        this.protocolVersion = st.nextToken();
                     } else {
                        this.protocolVersion = "HTTP/1.1";
                        NanoHTTPD.LOG.log(Level.FINE, "no protocol version specified, strange. Assuming HTTP/1.1.");
                     }

                     for(String line = in.readLine(); line != null && !line.trim().isEmpty(); line = in.readLine()) {
                        int p = line.indexOf(58);
                        if (p >= 0) {
                           headers.put(line.substring(0, p).trim().toLowerCase(Locale.US), line.substring(p + 1).trim());
                        }
                     }

                     pre.put("uri", uri);
                  }
               }
            }
         } catch (IOException var11) {
            throw new NanoHTTPD.ResponseException(NanoHTTPD.Response.Status.INTERNAL_ERROR, "SERVER INTERNAL ERROR: IOException: " + var11.getMessage(), var11);
         }
      }

      private void decodeMultipartFormData(String boundary, String encoding, ByteBuffer fbuf, Map<String, String> parms, Map<String, String> files) throws NanoHTTPD.ResponseException {
         try {
            int[] boundary_idxs = this.getBoundaryPositions(fbuf, boundary.getBytes());
            if (boundary_idxs.length < 2) {
               throw new NanoHTTPD.ResponseException(
                  NanoHTTPD.Response.Status.BAD_REQUEST, "BAD REQUEST: Content type is multipart/form-data but contains less than two boundary strings."
               );
            } else {
               byte[] part_header_buff = new byte[1024];

               for(int bi = 0; bi < boundary_idxs.length - 1; ++bi) {
                  fbuf.position(boundary_idxs[bi]);
                  int len = fbuf.remaining() < 1024 ? fbuf.remaining() : 1024;
                  fbuf.get(part_header_buff, 0, len);
                  BufferedReader in = new BufferedReader(
                     new InputStreamReader(new ByteArrayInputStream(part_header_buff, 0, len), Charset.forName(encoding)), len
                  );
                  int headerLines = 0;
                  String mpline = in.readLine();
                  ++headerLines;
                  if (mpline == null || !mpline.contains(boundary)) {
                     throw new NanoHTTPD.ResponseException(
                        NanoHTTPD.Response.Status.BAD_REQUEST, "BAD REQUEST: Content type is multipart/form-data but chunk does not start with boundary."
                     );
                  }

                  String part_name = null;
                  String file_name = null;
                  String content_type = null;
                  mpline = in.readLine();
                  ++headerLines;

                  while(mpline != null && mpline.trim().length() > 0) {
                     Matcher matcher = NanoHTTPD.CONTENT_DISPOSITION_PATTERN.matcher(mpline);
                     if (matcher.matches()) {
                        String attributeString = matcher.group(2);
                        matcher = NanoHTTPD.CONTENT_DISPOSITION_ATTRIBUTE_PATTERN.matcher(attributeString);

                        while(matcher.find()) {
                           String key = matcher.group(1);
                           if ("name".equalsIgnoreCase(key)) {
                              part_name = matcher.group(2);
                           } else if ("filename".equalsIgnoreCase(key)) {
                              file_name = matcher.group(2);
                           }
                        }
                     }

                     matcher = NanoHTTPD.CONTENT_TYPE_PATTERN.matcher(mpline);
                     if (matcher.matches()) {
                        content_type = matcher.group(2).trim();
                     }

                     mpline = in.readLine();
                     ++headerLines;
                  }

                  int part_header_len = 0;

                  while(headerLines-- > 0) {
                     part_header_len = this.scipOverNewLine(part_header_buff, part_header_len);
                  }

                  if (part_header_len >= len - 4) {
                     throw new NanoHTTPD.ResponseException(NanoHTTPD.Response.Status.INTERNAL_ERROR, "Multipart header size exceeds MAX_HEADER_SIZE.");
                  }

                  int part_data_start = boundary_idxs[bi] + part_header_len;
                  int part_data_end = boundary_idxs[bi + 1] - 4;
                  fbuf.position(part_data_start);
                  if (content_type == null) {
                     byte[] data_bytes = new byte[part_data_end - part_data_start];
                     fbuf.get(data_bytes);
                     parms.put(part_name, new String(data_bytes, encoding));
                  } else {
                     String path = this.saveTmpFile(fbuf, part_data_start, part_data_end - part_data_start, file_name);
                     if (!files.containsKey(part_name)) {
                        files.put(part_name, path);
                     } else {
                        int count = 2;

                        while(files.containsKey(part_name + count)) {
                           ++count;
                        }

                        files.put(part_name + count, path);
                     }

                     parms.put(part_name, file_name);
                  }
               }
            }
         } catch (NanoHTTPD.ResponseException var21) {
            throw var21;
         } catch (Exception var22) {
            throw new NanoHTTPD.ResponseException(NanoHTTPD.Response.Status.INTERNAL_ERROR, var22.toString());
         }
      }

      private int scipOverNewLine(byte[] part_header_buff, int index) {
         while(part_header_buff[index] != 10) {
            ++index;
         }

         return index + 1;
      }

      private void decodeParms(String parms, Map<String, String> p) {
         if (parms == null) {
            this.queryParameterString = "";
         } else {
            this.queryParameterString = parms;
            StringTokenizer st = new StringTokenizer(parms, "&");

            while(st.hasMoreTokens()) {
               String e = st.nextToken();
               int sep = e.indexOf(61);
               if (sep >= 0) {
                  p.put(NanoHTTPD.decodePercent(e.substring(0, sep)).trim(), NanoHTTPD.decodePercent(e.substring(sep + 1)));
               } else {
                  p.put(NanoHTTPD.decodePercent(e).trim(), "");
               }
            }
         }
      }

      @Override
      public void execute() throws IOException {
         NanoHTTPD.Response r = null;

         try {
            byte[] buf = new byte[8192];
            this.splitbyte = 0;
            this.rlen = 0;
            int read = -1;
            this.inputStream.mark(8192);

            try {
               read = this.inputStream.read(buf, 0, 8192);
            } catch (SSLException var18) {
               throw var18;
            } catch (IOException var19) {
               NanoHTTPD.safeClose(this.inputStream);
               NanoHTTPD.safeClose(this.outputStream);
               throw new SocketException("NanoHttpd Shutdown");
            }

            if (read == -1) {
               NanoHTTPD.safeClose(this.inputStream);
               NanoHTTPD.safeClose(this.outputStream);
               throw new SocketException("NanoHttpd Shutdown");
            }

            while(read > 0) {
               this.rlen += read;
               this.splitbyte = this.findHeaderEnd(buf, this.rlen);
               if (this.splitbyte > 0) {
                  break;
               }

               read = this.inputStream.read(buf, this.rlen, 8192 - this.rlen);
            }

            if (this.splitbyte < this.rlen) {
               this.inputStream.reset();
               this.inputStream.skip((long)this.splitbyte);
            }

            this.parms = new HashMap();
            if (null == this.headers) {
               this.headers = new HashMap();
            } else {
               this.headers.clear();
            }

            BufferedReader hin = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buf, 0, this.rlen)));
            Map<String, String> pre = new HashMap();
            this.decodeHeader(hin, pre, this.parms, this.headers);
            if (null != this.remoteIp) {
               this.headers.put("remote-addr", this.remoteIp);
               this.headers.put("http-client-ip", this.remoteIp);
            }

            this.method = NanoHTTPD.Method.lookup((String)pre.get("method"));
            if (this.method == null) {
               throw new NanoHTTPD.ResponseException(NanoHTTPD.Response.Status.BAD_REQUEST, "BAD REQUEST: Syntax error.");
            }

            this.uri = (String)pre.get("uri");
            this.cookies = NanoHTTPD.this.new CookieHandler(this.headers);
            String connection = (String)this.headers.get("connection");
            boolean keepAlive = "HTTP/1.1".equals(this.protocolVersion) && (connection == null || !connection.matches("(?i).*close.*"));
            r = NanoHTTPD.this.serve(this);
            if (r == null) {
               throw new NanoHTTPD.ResponseException(NanoHTTPD.Response.Status.INTERNAL_ERROR, "SERVER INTERNAL ERROR: Serve() returned a null response.");
            }

            String acceptEncoding = (String)this.headers.get("accept-encoding");
            this.cookies.unloadQueue(r);
            r.setRequestMethod(this.method);
            r.setGzipEncoding(NanoHTTPD.this.useGzipWhenAccepted(r) && acceptEncoding != null && acceptEncoding.contains("gzip"));
            r.setKeepAlive(keepAlive);
            r.send(this.outputStream);
            if (!keepAlive || r.isCloseConnection()) {
               throw new SocketException("NanoHttpd Shutdown");
            }
         } catch (SocketException var20) {
            throw var20;
         } catch (SocketTimeoutException var21) {
            throw var21;
         } catch (SSLException var22) {
            NanoHTTPD.Response resp = NanoHTTPD.newFixedLengthResponse(
               NanoHTTPD.Response.Status.INTERNAL_ERROR, "text/plain", "SSL PROTOCOL FAILURE: " + var22.getMessage()
            );
            resp.send(this.outputStream);
            NanoHTTPD.safeClose(this.outputStream);
         } catch (IOException var23) {
            NanoHTTPD.Response respx = NanoHTTPD.newFixedLengthResponse(
               NanoHTTPD.Response.Status.INTERNAL_ERROR, "text/plain", "SERVER INTERNAL ERROR: IOException: " + var23.getMessage()
            );
            respx.send(this.outputStream);
            NanoHTTPD.safeClose(this.outputStream);
         } catch (NanoHTTPD.ResponseException var24) {
            NanoHTTPD.Response respxx = NanoHTTPD.newFixedLengthResponse(var24.getStatus(), "text/plain", var24.getMessage());
            respxx.send(this.outputStream);
            NanoHTTPD.safeClose(this.outputStream);
         } finally {
            NanoHTTPD.safeClose(r);
            this.tempFileManager.clear();
         }
      }

      private int findHeaderEnd(byte[] buf, int rlen) {
         for(int splitbyte = 0; splitbyte + 1 < rlen; ++splitbyte) {
            if (buf[splitbyte] == 13 && buf[splitbyte + 1] == 10 && splitbyte + 3 < rlen && buf[splitbyte + 2] == 13 && buf[splitbyte + 3] == 10) {
               return splitbyte + 4;
            }

            if (buf[splitbyte] == 10 && buf[splitbyte + 1] == 10) {
               return splitbyte + 2;
            }
         }

         return 0;
      }

      private int[] getBoundaryPositions(ByteBuffer b, byte[] boundary) {
         int[] res = new int[0];
         if (b.remaining() < boundary.length) {
            return res;
         } else {
            int search_window_pos = 0;
            byte[] search_window = new byte[4096 + boundary.length];
            int first_fill = b.remaining() < search_window.length ? b.remaining() : search_window.length;
            b.get(search_window, 0, first_fill);
            int new_bytes = first_fill - boundary.length;

            do {
               for(int j = 0; j < new_bytes; ++j) {
                  for(int i = 0; i < boundary.length && search_window[j + i] == boundary[i]; ++i) {
                     if (i == boundary.length - 1) {
                        int[] new_res = new int[res.length + 1];
                        System.arraycopy(res, 0, new_res, 0, res.length);
                        new_res[res.length] = search_window_pos + j;
                        res = new_res;
                     }
                  }
               }

               search_window_pos += new_bytes;
               System.arraycopy(search_window, search_window.length - boundary.length, search_window, 0, boundary.length);
               int var11 = search_window.length - boundary.length;
               new_bytes = b.remaining() < var11 ? b.remaining() : var11;
               b.get(search_window, boundary.length, new_bytes);
            } while(new_bytes > 0);

            return res;
         }
      }

      @Override
      public NanoHTTPD.CookieHandler getCookies() {
         return this.cookies;
      }

      @Override
      public final Map<String, String> getHeaders() {
         return this.headers;
      }

      @Override
      public final InputStream getInputStream() {
         return this.inputStream;
      }

      @Override
      public final NanoHTTPD.Method getMethod() {
         return this.method;
      }

      @Override
      public final Map<String, String> getParms() {
         return this.parms;
      }

      @Override
      public String getQueryParameterString() {
         return this.queryParameterString;
      }

      private RandomAccessFile getTmpBucket() {
         try {
            NanoHTTPD.TempFile tempFile = this.tempFileManager.createTempFile(null);
            return new RandomAccessFile(tempFile.getName(), "rw");
         } catch (Exception var2) {
            throw new Error(var2);
         }
      }

      @Override
      public final String getUri() {
         return this.uri;
      }

      public long getBodySize() {
         if (this.headers.containsKey("content-length")) {
            return Long.parseLong((String)this.headers.get("content-length"));
         } else {
            return this.splitbyte < this.rlen ? (long)(this.rlen - this.splitbyte) : 0L;
         }
      }

      @Override
      public void parseBody(Map<String, String> files) throws IOException, NanoHTTPD.ResponseException {
         RandomAccessFile randomAccessFile = null;

         try {
            long size = this.getBodySize();
            ByteArrayOutputStream baos = null;
            DataOutput request_data_output = null;
            if (size < 1024L) {
               baos = new ByteArrayOutputStream();
               request_data_output = new DataOutputStream(baos);
            } else {
               randomAccessFile = this.getTmpBucket();
               request_data_output = randomAccessFile;
            }

            byte[] buf = new byte[512];

            while(this.rlen >= 0 && size > 0L) {
               this.rlen = this.inputStream.read(buf, 0, (int)Math.min(size, 512L));
               size -= (long)this.rlen;
               if (this.rlen > 0) {
                  request_data_output.write(buf, 0, this.rlen);
               }
            }

            ByteBuffer fbuf = null;
            if (baos != null) {
               fbuf = ByteBuffer.wrap(baos.toByteArray(), 0, baos.size());
            } else {
               fbuf = randomAccessFile.getChannel().map(MapMode.READ_ONLY, 0L, randomAccessFile.length());
               randomAccessFile.seek(0L);
            }

            if (NanoHTTPD.Method.POST.equals(this.method)) {
               String contentType = "";
               String encoding = "UTF-8";
               String contentTypeHeader = (String)this.headers.get("content-type");
               if (contentTypeHeader != null) {
                  contentType = this.getDetailFromContentHeader(contentTypeHeader, NanoHTTPD.MIME_PATTERN, "", 1);
                  encoding = this.getDetailFromContentHeader(contentTypeHeader, NanoHTTPD.CHARSET_PATTERN, "US-ASCII", 2);
               }

               if ("multipart/form-data".equalsIgnoreCase(contentType)) {
                  String boundary = this.getDetailFromContentHeader(contentTypeHeader, NanoHTTPD.BOUNDARY_PATTERN, null, 2);
                  if (boundary == null) {
                     throw new NanoHTTPD.ResponseException(
                        NanoHTTPD.Response.Status.BAD_REQUEST,
                        "BAD REQUEST: Content type is multipart/form-data but boundary missing. Usage: GET /example/file.html"
                     );
                  }

                  this.decodeMultipartFormData(boundary, encoding, fbuf, this.parms, files);
               } else {
                  byte[] postBytes = new byte[fbuf.remaining()];
                  fbuf.get(postBytes);
                  String postLine = new String(postBytes, encoding).trim();
                  if ("application/x-www-form-urlencoded".equalsIgnoreCase(contentType)) {
                     this.decodeParms(postLine, this.parms);
                  } else if (postLine.length() != 0) {
                     files.put("postData", postLine);
                  }
               }
            } else if (NanoHTTPD.Method.PUT.equals(this.method)) {
               files.put("content", this.saveTmpFile(fbuf, 0, fbuf.limit(), null));
            }
         } finally {
            NanoHTTPD.safeClose(randomAccessFile);
         }
      }

      private String getDetailFromContentHeader(String contentTypeHeader, Pattern pattern, String defaultValue, int group) {
         Matcher matcher = pattern.matcher(contentTypeHeader);
         return matcher.find() ? matcher.group(group) : defaultValue;
      }

      private String saveTmpFile(ByteBuffer b, int offset, int len, String filename_hint) {
         String path = "";
         if (len > 0) {
            FileOutputStream fileOutputStream = null;

            try {
               NanoHTTPD.TempFile tempFile = this.tempFileManager.createTempFile(filename_hint);
               ByteBuffer src = b.duplicate();
               fileOutputStream = new FileOutputStream(tempFile.getName());
               FileChannel dest = fileOutputStream.getChannel();
               src.position(offset).limit(offset + len);
               dest.write(src.slice());
               path = tempFile.getName();
            } catch (Exception var13) {
               throw new Error(var13);
            } finally {
               NanoHTTPD.safeClose(fileOutputStream);
            }
         }

         return path;
      }

      @Override
      public String getRemoteIpAddress() {
         return this.remoteIp;
      }

      @Override
      public String getRemoteHostName() {
         return this.remoteHostname;
      }
   }

   public interface IHTTPSession {
      void execute() throws IOException;

      NanoHTTPD.CookieHandler getCookies();

      Map<String, String> getHeaders();

      InputStream getInputStream();

      NanoHTTPD.Method getMethod();

      Map<String, String> getParms();

      String getQueryParameterString();

      String getUri();

      void parseBody(Map<String, String> var1) throws IOException, NanoHTTPD.ResponseException;

      String getRemoteIpAddress();

      String getRemoteHostName();
   }

   public static enum Method {
      GET,
      PUT,
      POST,
      DELETE,
      HEAD,
      OPTIONS,
      TRACE,
      CONNECT,
      PATCH;

      static NanoHTTPD.Method lookup(String method) {
         for(NanoHTTPD.Method m : values()) {
            if (m.toString().equalsIgnoreCase(method)) {
               return m;
            }
         }

         return null;
      }
   }

   public static class Response implements Closeable {
      private NanoHTTPD.Response.IStatus status;
      private String mimeType;
      private InputStream data;
      private long contentLength;
      private final Map<String, String> header = new HashMap<String, String>() {
         public String put(String key, String value) {
            Response.this.lowerCaseHeader.put(key == null ? key : key.toLowerCase(), value);
            return (String)super.put(key, value);
         }
      };
      private final Map<String, String> lowerCaseHeader = new HashMap();
      private NanoHTTPD.Method requestMethod;
      private boolean chunkedTransfer;
      private boolean encodeAsGzip;
      private boolean keepAlive;

      protected Response(NanoHTTPD.Response.IStatus status, String mimeType, InputStream data, long totalBytes) {
         super();
         this.status = status;
         this.mimeType = mimeType;
         if (data == null) {
            this.data = new ByteArrayInputStream(new byte[0]);
            this.contentLength = 0L;
         } else {
            this.data = data;
            this.contentLength = totalBytes;
         }

         this.chunkedTransfer = this.contentLength < 0L;
         this.keepAlive = true;
      }

      public void close() throws IOException {
         if (this.data != null) {
            this.data.close();
         }
      }

      public void addHeader(String name, String value) {
         this.header.put(name, value);
      }

      public void closeConnection(boolean close) {
         if (close) {
            this.header.put("connection", "close");
         } else {
            this.header.remove("connection");
         }
      }

      public boolean isCloseConnection() {
         return "close".equals(this.getHeader("connection"));
      }

      public InputStream getData() {
         return this.data;
      }

      public String getHeader(String name) {
         return (String)this.lowerCaseHeader.get(name.toLowerCase());
      }

      public String getMimeType() {
         return this.mimeType;
      }

      public NanoHTTPD.Method getRequestMethod() {
         return this.requestMethod;
      }

      public NanoHTTPD.Response.IStatus getStatus() {
         return this.status;
      }

      public void setGzipEncoding(boolean encodeAsGzip) {
         this.encodeAsGzip = encodeAsGzip;
      }

      public void setKeepAlive(boolean useKeepAlive) {
         this.keepAlive = useKeepAlive;
      }

      protected void send(OutputStream outputStream) {
         SimpleDateFormat gmtFrmt = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
         gmtFrmt.setTimeZone(TimeZone.getTimeZone("GMT"));

         try {
            if (this.status == null) {
               throw new Error("sendResponse(): Status can't be null.");
            }

            PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8")), false);
            pw.append("HTTP/1.1 ").append(this.status.getDescription()).append(" \r\n");
            if (this.mimeType != null) {
               this.printHeader(pw, "Content-Type", this.mimeType);
            }

            if (this.getHeader("date") == null) {
               this.printHeader(pw, "Date", gmtFrmt.format(new Date()));
            }

            for(Entry<String, String> entry : this.header.entrySet()) {
               this.printHeader(pw, (String)entry.getKey(), (String)entry.getValue());
            }

            if (this.getHeader("connection") == null) {
               this.printHeader(pw, "Connection", this.keepAlive ? "keep-alive" : "close");
            }

            if (this.getHeader("content-length") != null) {
               this.encodeAsGzip = false;
            }

            if (this.encodeAsGzip) {
               this.printHeader(pw, "Content-Encoding", "gzip");
               this.setChunkedTransfer(true);
            }

            long pending = this.data != null ? this.contentLength : 0L;
            if (this.requestMethod != NanoHTTPD.Method.HEAD && this.chunkedTransfer) {
               this.printHeader(pw, "Transfer-Encoding", "chunked");
            } else if (!this.encodeAsGzip) {
               pending = this.sendContentLengthHeaderIfNotAlreadyPresent(pw, pending);
            }

            pw.append("\r\n");
            pw.flush();
            this.sendBodyWithCorrectTransferAndEncoding(outputStream, pending);
            outputStream.flush();
            NanoHTTPD.safeClose(this.data);
         } catch (IOException var6) {
            NanoHTTPD.LOG.log(Level.SEVERE, "Could not send response to the client", var6);
         }
      }

      protected void printHeader(PrintWriter pw, String key, String value) {
         pw.append(key).append(": ").append(value).append("\r\n");
      }

      protected long sendContentLengthHeaderIfNotAlreadyPresent(PrintWriter pw, long defaultSize) {
         String contentLengthString = this.getHeader("content-length");
         long size = defaultSize;
         if (contentLengthString != null) {
            try {
               size = Long.parseLong(contentLengthString);
            } catch (NumberFormatException var8) {
               NanoHTTPD.LOG.severe("content-length was no number " + contentLengthString);
            }
         }

         pw.print("Content-Length: " + size + "\r\n");
         return size;
      }

      private void sendBodyWithCorrectTransferAndEncoding(OutputStream outputStream, long pending) throws IOException {
         if (this.requestMethod != NanoHTTPD.Method.HEAD && this.chunkedTransfer) {
            NanoHTTPD.Response.ChunkedOutputStream chunkedOutputStream = new NanoHTTPD.Response.ChunkedOutputStream(outputStream);
            this.sendBodyWithCorrectEncoding(chunkedOutputStream, -1L);
            chunkedOutputStream.finish();
         } else {
            this.sendBodyWithCorrectEncoding(outputStream, pending);
         }
      }

      private void sendBodyWithCorrectEncoding(OutputStream outputStream, long pending) throws IOException {
         if (this.encodeAsGzip) {
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
            this.sendBody(gzipOutputStream, -1L);
            gzipOutputStream.finish();
         } else {
            this.sendBody(outputStream, pending);
         }
      }

      private void sendBody(OutputStream outputStream, long pending) throws IOException {
         long BUFFER_SIZE = 16384L;
         byte[] buff = new byte[(int)BUFFER_SIZE];
         boolean sendEverything = pending == -1L;

         while(pending > 0L || sendEverything) {
            long bytesToRead = sendEverything ? BUFFER_SIZE : Math.min(pending, BUFFER_SIZE);
            int read = this.data.read(buff, 0, (int)bytesToRead);
            if (read <= 0) {
               break;
            }

            outputStream.write(buff, 0, read);
            if (!sendEverything) {
               pending -= (long)read;
            }
         }
      }

      public void setChunkedTransfer(boolean chunkedTransfer) {
         this.chunkedTransfer = chunkedTransfer;
      }

      public void setData(InputStream data) {
         this.data = data;
      }

      public void setMimeType(String mimeType) {
         this.mimeType = mimeType;
      }

      public void setRequestMethod(NanoHTTPD.Method requestMethod) {
         this.requestMethod = requestMethod;
      }

      public void setStatus(NanoHTTPD.Response.IStatus status) {
         this.status = status;
      }

      private static class ChunkedOutputStream extends FilterOutputStream {
         public ChunkedOutputStream(OutputStream out) {
            super(out);
         }

         public void write(int b) throws IOException {
            byte[] data = new byte[]{(byte)b};
            this.write(data, 0, 1);
         }

         public void write(byte[] b) throws IOException {
            this.write(b, 0, b.length);
         }

         public void write(byte[] b, int off, int len) throws IOException {
            if (len != 0) {
               this.out.write(String.format("%x\r\n", len).getBytes());
               this.out.write(b, off, len);
               this.out.write("\r\n".getBytes());
            }
         }

         public void finish() throws IOException {
            this.out.write("0\r\n\r\n".getBytes());
         }
      }

      public interface IStatus {
         String getDescription();

         int getRequestStatus();
      }

      public static enum Status implements NanoHTTPD.Response.IStatus {
         SWITCH_PROTOCOL(101, "Switching Protocols"),
         OK(200, "OK"),
         CREATED(201, "Created"),
         ACCEPTED(202, "Accepted"),
         NO_CONTENT(204, "No Content"),
         PARTIAL_CONTENT(206, "Partial Content"),
         REDIRECT(301, "Moved Permanently"),
         NOT_MODIFIED(304, "Not Modified"),
         BAD_REQUEST(400, "Bad Request"),
         UNAUTHORIZED(401, "Unauthorized"),
         FORBIDDEN(403, "Forbidden"),
         NOT_FOUND(404, "Not Found"),
         METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
         NOT_ACCEPTABLE(406, "Not Acceptable"),
         REQUEST_TIMEOUT(408, "Request Timeout"),
         CONFLICT(409, "Conflict"),
         RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"),
         INTERNAL_ERROR(500, "Internal Server Error"),
         NOT_IMPLEMENTED(501, "Not Implemented"),
         UNSUPPORTED_HTTP_VERSION(505, "HTTP Version Not Supported");

         private final int requestStatus;
         private final String description;

         private Status(int requestStatus, String description) {
            this.requestStatus = requestStatus;
            this.description = description;
         }

         @Override
         public String getDescription() {
            return "" + this.requestStatus + " " + this.description;
         }

         @Override
         public int getRequestStatus() {
            return this.requestStatus;
         }
      }
   }

   public static final class ResponseException extends Exception {
      private static final long serialVersionUID = 6569838532917408380L;
      private final NanoHTTPD.Response.Status status;

      public ResponseException(NanoHTTPD.Response.Status status, String message) {
         super(message);
         this.status = status;
      }

      public ResponseException(NanoHTTPD.Response.Status status, String message, Exception e) {
         super(message, e);
         this.status = status;
      }

      public NanoHTTPD.Response.Status getStatus() {
         return this.status;
      }
   }

   public static class SecureServerSocketFactory implements NanoHTTPD.ServerSocketFactory {
      private SSLServerSocketFactory sslServerSocketFactory;
      private String[] sslProtocols;

      public SecureServerSocketFactory(SSLServerSocketFactory sslServerSocketFactory, String[] sslProtocols) {
         super();
         this.sslServerSocketFactory = sslServerSocketFactory;
         this.sslProtocols = sslProtocols;
      }

      @Override
      public ServerSocket create() throws IOException {
         SSLServerSocket ss = null;
         ss = (SSLServerSocket)this.sslServerSocketFactory.createServerSocket();
         if (this.sslProtocols != null) {
            ss.setEnabledProtocols(this.sslProtocols);
         } else {
            ss.setEnabledProtocols(ss.getSupportedProtocols());
         }

         ss.setUseClientMode(false);
         ss.setWantClientAuth(false);
         ss.setNeedClientAuth(false);
         return ss;
      }
   }

   public class ServerRunnable implements Runnable {
      private final int timeout;
      private IOException bindException;
      private boolean hasBinded = false;

      private ServerRunnable(int timeout) {
         super();
         this.timeout = timeout;
      }

      public void run() {
         try {
            NanoHTTPD.this.myServerSocket
               .bind(
                  NanoHTTPD.this.hostname != null
                     ? new InetSocketAddress(NanoHTTPD.this.hostname, NanoHTTPD.this.myPort)
                     : new InetSocketAddress(NanoHTTPD.this.myPort)
               );
            this.hasBinded = true;
         } catch (IOException var4) {
            this.bindException = var4;
            return;
         }

         do {
            try {
               Socket finalAccept = NanoHTTPD.this.myServerSocket.accept();
               if (this.timeout > 0) {
                  finalAccept.setSoTimeout(this.timeout);
               }

               InputStream inputStream = finalAccept.getInputStream();
               NanoHTTPD.this.asyncRunner.exec(NanoHTTPD.this.createClientHandler(finalAccept, inputStream));
            } catch (IOException var3) {
               NanoHTTPD.LOG.log(Level.FINE, "Communication with the client broken", var3);
            }
         } while(!NanoHTTPD.this.myServerSocket.isClosed());
      }
   }

   public interface ServerSocketFactory {
      ServerSocket create() throws IOException;
   }

   public interface TempFile {
      void delete() throws Exception;

      String getName();

      OutputStream open() throws Exception;
   }

   public interface TempFileManager {
      void clear();

      NanoHTTPD.TempFile createTempFile(String var1) throws Exception;
   }

   public interface TempFileManagerFactory {
      NanoHTTPD.TempFileManager create();
   }
}
