package js.java.tools;

import de.deltaga.serial.Base64;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BinaryStore {
   @BinaryStore.StoreElement(
      storeName = "VERS"
   )
   public long version;
   protected static final int TYPE_STRING = 1;
   protected static final int TYPE_LONG = 2;
   protected static final int TYPE_BOOLEAN = 3;

   protected BinaryStore(long version) {
      this.version = version;
   }

   public final String toBase64() {
      return toBase64(this);
   }

   public final void fromBase64(String pcdata) {
      fromBase64(this, pcdata);
   }

   public static String toBase64(BinaryStore ge) {
      ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
      OutputStream b64os = new Base64.OutputStream(baos1, 9);
      DataOutputStream os2 = new DataOutputStream(b64os);
      ge.writeExternal(os2);
      return baos1.toString();
   }

   public static void fromBase64(BinaryStore ge, String pcdata) {
      byte[] objBytes = Base64.decode(pcdata);
      ByteArrayInputStream bais1 = new ByteArrayInputStream(objBytes);
      DataInputStream is2 = new DataInputStream(bais1);
      ge.readExternal(is2);
   }

   private String readName(DataInputStream in) throws IOException {
      boolean inName = false;

      while (!inName) {
         int b = in.readUnsignedByte();
         if (b == 255) {
            inName = true;
         }

         if (b == 0 && inName) {
            return "";
         }
      }

      return in.readUTF();
   }

   protected final String readValueString(DataInputStream in) throws IOException {
      boolean inName = false;

      while (!inName) {
         int b = in.readUnsignedByte();
         if (b == 254) {
            inName = true;
         }

         if (b == 0 && inName) {
            return "";
         }
      }

      return in.readUTF();
   }

   protected final long readValueLong(DataInputStream in) throws IOException {
      boolean inName = false;

      while (!inName) {
         int b = in.readUnsignedByte();
         if (b == 254) {
            inName = true;
         }

         if (b == 0 && inName) {
            return 0L;
         }
      }

      return in.readLong();
   }

   protected final boolean readValueBoolean(DataInputStream in) throws IOException {
      boolean inName = false;

      while (!inName) {
         int b = in.readUnsignedByte();
         if (b == 254) {
            inName = true;
         }

         if (b == 0 && inName) {
            return false;
         }
      }

      return in.readBoolean();
   }

   protected final int readType(DataInputStream in) throws IOException {
      return in.readUnsignedByte();
   }

   protected final void writeData(DataOutputStream out, String name, String value) throws IOException {
      out.writeByte(255);
      out.writeUTF(name);
      out.writeByte(1);
      out.writeByte(254);
      out.writeUTF(value);
   }

   protected final void writeData(DataOutputStream out, String name, long value) throws IOException {
      out.writeByte(255);
      out.writeUTF(name);
      out.writeByte(2);
      out.writeByte(254);
      out.writeLong(value);
   }

   protected final void writeData(DataOutputStream out, String name, boolean value) throws IOException {
      out.writeByte(255);
      out.writeUTF(name);
      out.writeByte(3);
      out.writeByte(254);
      out.writeBoolean(value);
   }

   protected void handleReadValue(DataInputStream in, String name) throws IOException {
      for (Class obj = this.getClass(); !obj.equals(Object.class); obj = obj.getSuperclass()) {
         Field[] fs = obj.getDeclaredFields();

         for (Field f : fs) {
            try {
               BinaryStore.StoreElement se = (BinaryStore.StoreElement)f.getAnnotation(BinaryStore.StoreElement.class);
               if (se != null && se.storeName().equals(name)) {
                  if (f.getType() == boolean.class) {
                     if (this.readType(in) == 3) {
                        f.setBoolean(this, this.readValueBoolean(in));
                     }
                  } else if (f.getType() == long.class) {
                     if (this.readType(in) == 2) {
                        f.setLong(this, this.readValueLong(in));
                     }
                  } else if (f.getType() == int.class) {
                     if (this.readType(in) == 2) {
                        f.setInt(this, (int)this.readValueLong(in));
                     }
                  } else if (f.getType() == String.class && this.readType(in) == 1) {
                     f.set(this, this.readValueString(in));
                  }
               }
            } catch (IllegalAccessException | IllegalArgumentException var10) {
               Logger.getLogger(BinaryStore.class.getName()).log(Level.SEVERE, "readExternal", var10);
            }
         }
      }
   }

   private void readExternal(DataInputStream in) {
      try {
         while (true) {
            String name = this.readName(in);
            this.handleReadValue(in, name);
         }
      } catch (IOException var3) {
      }
   }

   protected void writeExternal(DataOutputStream out) {
      try {
         for (Class obj = this.getClass(); !obj.equals(Object.class); obj = obj.getSuperclass()) {
            Field[] fs = obj.getDeclaredFields();

            for (Field f : fs) {
               try {
                  BinaryStore.StoreElement se = (BinaryStore.StoreElement)f.getAnnotation(BinaryStore.StoreElement.class);
                  if (se != null) {
                     BinaryStore.DeprecatedElement de = (BinaryStore.DeprecatedElement)f.getAnnotation(BinaryStore.DeprecatedElement.class);
                     if (de == null) {
                        if (f.getType() == boolean.class) {
                           this.writeData(out, se.storeName(), f.getBoolean(this));
                        } else if (f.getType() == long.class) {
                           this.writeData(out, se.storeName(), f.getLong(this));
                        } else if (f.getType() == int.class) {
                           this.writeData(out, se.storeName(), (long)f.getInt(this));
                        } else if (f.getType() == String.class) {
                           this.writeData(out, se.storeName(), (String)f.get(this));
                        }
                     }
                  }
               } catch (IllegalAccessException | IllegalArgumentException var10) {
                  Logger.getLogger(BinaryStore.class.getName()).log(Level.SEVERE, "writeExternal", var10);
               }
            }
         }

         out.writeByte(0);
         out.writeByte(0);
      } catch (IOException var11) {
      }
   }

   public String toString() {
      StringBuilder ret = new StringBuilder();

      for (Class obj = this.getClass(); !obj.equals(Object.class); obj = obj.getSuperclass()) {
         Field[] fs = obj.getDeclaredFields();

         for (Field f : fs) {
            try {
               BinaryStore.StoreElement se = (BinaryStore.StoreElement)f.getAnnotation(BinaryStore.StoreElement.class);
               if (se != null) {
                  BinaryStore.DeprecatedElement de = (BinaryStore.DeprecatedElement)f.getAnnotation(BinaryStore.DeprecatedElement.class);
                  if (de == null) {
                     f.setAccessible(true);
                     ret.append(f.getName()).append("(").append(se.storeName()).append(")=");
                     if (f.getType() == boolean.class) {
                        ret.append(f.getBoolean(this));
                     } else if (f.getType() == long.class) {
                        ret.append(f.getLong(this));
                     } else if (f.getType() == int.class) {
                        ret.append(f.getInt(this));
                     } else if (f.getType() == String.class) {
                        ret.append(f.get(this));
                     }

                     ret.append(',');
                  }
               }
            } catch (IllegalAccessException | IllegalArgumentException var10) {
               Logger.getLogger(BinaryStore.class.getName()).log(Level.SEVERE, "writeExternal", var10);
            }
         }
      }

      return ret.toString();
   }

   @Retention(RetentionPolicy.RUNTIME)
   public @interface DeprecatedElement {
   }

   @Retention(RetentionPolicy.RUNTIME)
   public @interface StoreElement {
      String storeName();
   }
}
