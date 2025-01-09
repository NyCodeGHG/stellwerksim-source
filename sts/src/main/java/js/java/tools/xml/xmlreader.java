package js.java.tools.xml;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import js.java.tools.urlConnModifier;
import js.java.tools.gui.dataTransferDisplay.DataTransferDisplayInterface;
import js.java.tools.streams.CountInputStream;
import js.java.tools.streams.DataDisplayInputStream;
import js.java.tools.streams.UTF8DataOutputStream;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

public class xmlreader extends DefaultHandler {
   private WeakHashMap<String, xmllistener> listeners;
   private DataTransferDisplayInterface dtdc = null;
   private static final SAXParserFactory factory = SAXParserFactory.newInstance();
   protected long dataSize = 0L;
   private urlConnModifier urlmod = null;
   private LinkedList stack = null;

   public xmlreader() {
      this.listeners = new WeakHashMap();
   }

   public xmlreader(DataTransferDisplayInterface dtdc) {
      this();
      this.dtdc = dtdc;
   }

   public void registerTag(String tag, xmllistener l) {
      this.listeners.put(tag, l);
   }

   public void clearRegistrations() {
      this.listeners.clear();
   }

   public boolean updateData(String url) throws IOException {
      return this.updateData(url, null);
   }

   public long getDataSize() {
      return this.dataSize;
   }

   @Deprecated
   public void setUrlConnModifier(urlConnModifier u) {
      this.urlmod = u;
   }

   public boolean isCleanXML(String data) {
      int klc = 0;
      int slc = 0;
      boolean inkl = false;
      boolean otherc = false;

      for (int i = 0; i < data.length(); i++) {
         char c = data.charAt(i);
         switch (c) {
            case '/':
               if (inkl) {
                  klc--;
                  if (!otherc) {
                     slc++;
                  }
               }
               break;
            case '<':
               klc++;
               inkl = true;
               otherc = false;
               break;
            case '>':
               inkl = false;
               break;
            default:
               if (inkl) {
                  otherc = true;
               }
         }
      }

      return klc <= slc;
   }

   public boolean updateData(String url, StringBuffer senddata) throws IOException {
      this.dataSize = 0L;
      CountInputStream inputC;
      if (!url.startsWith("http://") && !url.startsWith("https://")) {
         inputC = new CountInputStream(this.getClass().getResourceAsStream(url));
      } else {
         URL u = new URL(url);
         URLConnection urlConn = u.openConnection();
         if (this.urlmod != null) {
            this.urlmod.modify(urlConn);
         }

         urlConn.setDoInput(true);
         if (senddata != null) {
            urlConn.setDoOutput(true);
         }

         urlConn.setUseCaches(false);
         urlConn.setRequestProperty("Accept-Encoding", "gzip");
         if (senddata != null) {
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            UTF8DataOutputStream printout = new UTF8DataOutputStream(urlConn.getOutputStream());
            Throwable str = null;

            try {
               String content = senddata.toString();
               printout.write(content);
               printout.flush();
            } catch (Throwable var32) {
               str = var32;
               throw var32;
            } finally {
               if (printout != null) {
                  if (str != null) {
                     try {
                        printout.close();
                     } catch (Throwable var31) {
                        str.addSuppressed(var31);
                     }
                  } else {
                     printout.close();
                  }
               }
            }
         }

         String enc = urlConn.getContentEncoding();
         if (enc != null && enc.compareTo("gzip") == 0) {
            inputC = new CountInputStream(new GZIPInputStream(urlConn.getInputStream()));
         } else {
            inputC = new CountInputStream(urlConn.getInputStream());
         }
      }

      FilterInputStream ip = inputC;
      if (this.dtdc != null) {
         ip = new DataDisplayInputStream(inputC, this.dtdc);
      }

      ip = new DataInputStream(ip);
      BufferedReader input = new BufferedReader(new InputStreamReader(ip, "UTF-8"));
      Throwable var41 = null;

      try {
         input.mark(50);
         String str = input.readLine();
         if (str != null) {
            if (!str.equals("<gleisdata_v3/>")) {
               input.reset();
            }

            try {
               SAXParser saxParser = factory.newSAXParser();
               saxParser.parse(new InputSource(input), this);
            } catch (Exception var33) {
               if (var33 instanceof SAXParseException) {
                  SAXParseException s = (SAXParseException)var33;
                  System.out.println("Parse Exception at line " + s.getLineNumber() + " column " + s.getColumnNumber());
                  System.out.println("ID: " + s.getPublicId());
               } else {
                  var33.printStackTrace();
                  System.out.println("Exception occured before char: " + inputC.getCount());
                  System.out.println("Dump: ###" + inputC.getLastRead() + "###");
               }

               Logger.getLogger(xmlreader.class.getName()).log(Level.SEVERE, "updateData / SAX", var33);
               throw new IOException(var33);
            }
         }

         this.dataSize = inputC.getCount();
      } catch (Throwable var35) {
         var41 = var35;
         throw var35;
      } finally {
         if (input != null) {
            if (var41 != null) {
               try {
                  input.close();
               } catch (Throwable var30) {
                  var41.addSuppressed(var30);
               }
            } else {
               input.close();
            }
         }
      }

      return true;
   }

   public void updateData(File f) throws IOException {
      this.dataSize = 0L;
      CountInputStream inputC = new CountInputStream(new FileInputStream(f));
      FilterInputStream ip = inputC;
      if (this.dtdc != null) {
         ip = new DataDisplayInputStream(inputC, this.dtdc);
      }

      ip = new DataInputStream(ip);
      BufferedReader input = new BufferedReader(new InputStreamReader(ip, "UTF-8"));
      Throwable var5 = null;

      try {
         try {
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(new InputSource(input), this);
         } catch (Exception var16) {
            if (var16 instanceof SAXParseException) {
               SAXParseException s = (SAXParseException)var16;
               System.out.println("Parse Exception at line " + s.getLineNumber() + " column " + s.getColumnNumber());
               System.out.println("ID: " + s.getPublicId());
            } else {
               var16.printStackTrace();
               System.out.println("Exception occured before char: " + inputC.getCount());
               System.out.println("Dump: ###" + inputC.getLastRead() + "###");
            }

            Logger.getLogger(xmlreader.class.getName()).log(Level.SEVERE, "updateData / SAX", var16);
            throw new IOException(var16);
         }

         this.dataSize = inputC.getCount();
      } catch (Throwable var17) {
         var5 = var17;
         throw var17;
      } finally {
         if (input != null) {
            if (var5 != null) {
               try {
                  input.close();
               } catch (Throwable var15) {
                  var5.addSuppressed(var15);
               }
            } else {
               input.close();
            }
         }
      }
   }

   public void updateDataByString(String data) throws IOException {
      ByteArrayInputStream input = new ByteArrayInputStream(data.getBytes("UTF-8"));
      Throwable var3 = null;

      try {
         try {
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(input, this);
         } catch (Exception var13) {
            throw new IOException(var13.getMessage());
         }
      } catch (Throwable var14) {
         var3 = var14;
         throw var14;
      } finally {
         if (input != null) {
            if (var3 != null) {
               try {
                  input.close();
               } catch (Throwable var12) {
                  var3.addSuppressed(var12);
               }
            } else {
               input.close();
            }
         }
      }
   }

   public void startDocument() throws SAXException {
      this.stack = new LinkedList();
   }

   public void endDocument() throws SAXException {
      if (this.stack.size() > 0) {
         throw new SAXException("not empty error");
      }
   }

   public void startElement(String namespaceURI, String lName, String qName, Attributes attrs) throws SAXException {
      String eName = lName;
      if ("".equals(lName)) {
         eName = qName;
      }

      this.stack.addLast(eName);
      this.stack.addLast(new AttributesImpl(attrs));
      this.stack.addLast(null);
      xmllistener l = (xmllistener)this.listeners.get(eName);
      if (l != null) {
         try {
            l.parseStartTag(eName, attrs);
         } catch (Exception var8) {
            Logger.getLogger(xmlreader.class.getName()).log(Level.SEVERE, "start tag: " + eName, var8);
         }
      }
   }

   public void endElement(String namespaceURI, String sName, String qName) throws SAXException {
      try {
         String lastpcdata = (String)this.stack.removeLast();
         Attributes lastattrib = (Attributes)this.stack.removeLast();
         String lasttag = (String)this.stack.removeLast();
         xmllistener l = (xmllistener)this.listeners.get(lasttag);
         if (l != null) {
            try {
               l.parseEndTag(lasttag, lastattrib, lastpcdata);
            } catch (Exception var9) {
               Logger.getLogger(xmlreader.class.getName()).log(Level.SEVERE, "end tag: " + lasttag, var9);
            }
         }
      } catch (Exception var10) {
         var10.printStackTrace();
         throw new SAXException("tag error", var10);
      }
   }

   public void characters(char[] buf, int offset, int len) throws SAXException {
      String s = (String)this.stack.removeLast();
      if (s == null) {
         s = new String(buf, offset, len);
      } else {
         s = s + new String(buf, offset, len);
      }

      this.stack.addLast(s);
   }
}
