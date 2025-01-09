package net.miginfocom.layout;

import java.beans.Beans;
import java.beans.ExceptionListener;
import java.beans.Introspector;
import java.beans.PersistenceDelegate;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.util.IdentityHashMap;
import java.util.TreeSet;
import java.util.WeakHashMap;

public final class LayoutUtil {
   public static final int INF = 2097051;
   static final int NOT_SET = -2147471302;
   public static final int MIN = 0;
   public static final int PREF = 1;
   public static final int MAX = 2;
   private static volatile WeakHashMap<Object, String> CR_MAP = null;
   private static volatile WeakHashMap<Object, Boolean> DT_MAP = null;
   private static int eSz = 0;
   private static int globalDebugMillis = 0;
   public static final boolean HAS_BEANS = hasBeans();
   private static ByteArrayOutputStream writeOutputStream = null;
   private static byte[] readBuf = null;
   private static final IdentityHashMap<Object, Object> SER_MAP = new IdentityHashMap(2);

   private static boolean hasBeans() {
      try {
         LayoutUtil.class.getClassLoader().loadClass("java.beans.Beans");
         return true;
      } catch (ClassNotFoundException var1) {
         return false;
      }
   }

   private LayoutUtil() {
   }

   public static String getVersion() {
      return "4.0";
   }

   public static int getGlobalDebugMillis() {
      return globalDebugMillis;
   }

   public static void setGlobalDebugMillis(int millis) {
      globalDebugMillis = millis;
   }

   public static void setDesignTime(ContainerWrapper cw, boolean b) {
      if (DT_MAP == null) {
         DT_MAP = new WeakHashMap();
      }

      DT_MAP.put(cw != null ? cw.getComponent() : null, b);
   }

   public static boolean isDesignTime(ContainerWrapper cw) {
      if (DT_MAP == null) {
         return HAS_BEANS && Beans.isDesignTime();
      } else {
         if (cw != null && !DT_MAP.containsKey(cw.getComponent())) {
            cw = null;
         }

         Boolean b = (Boolean)DT_MAP.get(cw != null ? cw.getComponent() : null);
         return b != null && b;
      }
   }

   public static int getDesignTimeEmptySize() {
      return eSz;
   }

   public static void setDesignTimeEmptySize(int pixels) {
      eSz = pixels;
   }

   static void putCCString(Object con, String s) {
      if (s != null && con != null && isDesignTime(null)) {
         if (CR_MAP == null) {
            CR_MAP = new WeakHashMap(64);
         }

         CR_MAP.put(con, s);
      }
   }

   static synchronized void setDelegate(Class c, PersistenceDelegate del) {
      try {
         Introspector.getBeanInfo(c, 3).getBeanDescriptor().setValue("persistenceDelegate", del);
      } catch (Exception var3) {
      }
   }

   static String getCCString(Object con) {
      return CR_MAP != null ? (String)CR_MAP.get(con) : null;
   }

   static void throwCC() {
      throw new IllegalStateException("setStoreConstraintData(true) must be set for strings to be saved.");
   }

   static int[] calculateSerial(int[][] sizes, ResizeConstraint[] resConstr, Float[] defPushWeights, int startSizeType, int bounds) {
      float[] lengths = new float[sizes.length];
      float usedLength = 0.0F;

      for (int i = 0; i < sizes.length; i++) {
         if (sizes[i] != null) {
            float len = sizes[i][startSizeType] != -2147471302 ? (float)sizes[i][startSizeType] : 0.0F;
            int newSizeBounded = getBrokenBoundary(len, sizes[i][0], sizes[i][2]);
            if (newSizeBounded != -2147471302) {
               len = (float)newSizeBounded;
            }

            usedLength += len;
            lengths[i] = len;
         }
      }

      int useLengthI = Math.round(usedLength);
      if (useLengthI != bounds && resConstr != null) {
         boolean isGrow = useLengthI < bounds;
         TreeSet<Integer> prioList = new TreeSet();

         for (int ix = 0; ix < sizes.length; ix++) {
            ResizeConstraint resC = (ResizeConstraint)getIndexSafe(resConstr, ix);
            if (resC != null) {
               prioList.add(isGrow ? resC.growPrio : resC.shrinkPrio);
            }
         }

         Integer[] prioIntegers = (Integer[])prioList.toArray(new Integer[prioList.size()]);

         for (int force = 0; force <= (isGrow && defPushWeights != null ? 1 : 0); force++) {
            for (int pr = prioIntegers.length - 1; pr >= 0; pr--) {
               int curPrio = prioIntegers[pr];
               float totWeight = 0.0F;
               Float[] resizeWeight = new Float[sizes.length];

               for (int ixx = 0; ixx < sizes.length; ixx++) {
                  if (sizes[ixx] != null) {
                     ResizeConstraint resC = (ResizeConstraint)getIndexSafe(resConstr, ixx);
                     if (resC != null) {
                        int prio = isGrow ? resC.growPrio : resC.shrinkPrio;
                        if (curPrio == prio) {
                           if (!isGrow) {
                              resizeWeight[ixx] = resC.shrink;
                           } else {
                              resizeWeight[ixx] = force != 0 && resC.grow == null
                                 ? defPushWeights[ixx < defPushWeights.length ? ixx : defPushWeights.length - 1]
                                 : resC.grow;
                           }

                           if (resizeWeight[ixx] != null) {
                              totWeight += resizeWeight[ixx];
                           }
                        }
                     }
                  }
               }

               if (totWeight > 0.0F) {
                  while (true) {
                     float toChange = (float)bounds - usedLength;
                     boolean hit = false;
                     float changedWeight = 0.0F;

                     for (int ixxx = 0; ixxx < sizes.length && totWeight > 1.0E-4F; ixxx++) {
                        Float weight = resizeWeight[ixxx];
                        if (weight != null) {
                           float sizeDelta = toChange * weight / totWeight;
                           float newSize = lengths[ixxx] + sizeDelta;
                           if (sizes[ixxx] != null) {
                              int newSizeBounded = getBrokenBoundary(newSize, sizes[ixxx][0], sizes[ixxx][2]);
                              if (newSizeBounded != -2147471302) {
                                 resizeWeight[ixxx] = null;
                                 hit = true;
                                 changedWeight += weight;
                                 newSize = (float)newSizeBounded;
                                 sizeDelta = newSize - lengths[ixxx];
                              }
                           }

                           lengths[ixxx] = newSize;
                           usedLength += sizeDelta;
                        }
                     }

                     totWeight -= changedWeight;
                     if (!hit) {
                        break;
                     }
                  }
               }
            }
         }
      }

      return roundSizes(lengths);
   }

   static Object getIndexSafe(Object[] arr, int ix) {
      return arr != null ? arr[ix < arr.length ? ix : arr.length - 1] : null;
   }

   private static int getBrokenBoundary(float sz, int lower, int upper) {
      if (lower != -2147471302) {
         if (sz < (float)lower) {
            return lower;
         }
      } else if (sz < 0.0F) {
         return 0;
      }

      return upper != -2147471302 && sz > (float)upper ? upper : -2147471302;
   }

   static int sum(int[] terms, int start, int len) {
      int s = 0;
      int i = start;

      for (int iSz = start + len; i < iSz; i++) {
         s += terms[i];
      }

      return s;
   }

   static int sum(int[] terms) {
      return sum(terms, 0, terms.length);
   }

   public static int getSizeSafe(int[] sizes, int sizeType) {
      if (sizes != null && sizes[sizeType] != -2147471302) {
         return sizes[sizeType];
      } else {
         return sizeType == 2 ? 2097051 : 0;
      }
   }

   static BoundSize derive(BoundSize bs, UnitValue min, UnitValue pref, UnitValue max) {
      return bs != null && !bs.isUnset()
         ? new BoundSize(min != null ? min : bs.getMin(), pref != null ? pref : bs.getPreferred(), max != null ? max : bs.getMax(), bs.getGapPush(), null)
         : new BoundSize(min, pref, max, null);
   }

   public static boolean isLeftToRight(LC lc, ContainerWrapper container) {
      return lc != null && lc.getLeftToRight() != null ? lc.getLeftToRight() : container == null || container.isLeftToRight();
   }

   static int[] roundSizes(float[] sizes) {
      int[] retInts = new int[sizes.length];
      float posD = 0.0F;

      for (int i = 0; i < retInts.length; i++) {
         int posI = (int)(posD + 0.5F);
         posD += sizes[i];
         retInts[i] = (int)(posD + 0.5F) - posI;
      }

      return retInts;
   }

   static boolean equals(Object o1, Object o2) {
      return o1 == o2 || o1 != null && o2 != null && o1.equals(o2);
   }

   static UnitValue getInsets(LC lc, int side, boolean getDefault) {
      UnitValue[] i = lc.getInsets();
      return i != null && i[side] != null ? i[side] : (getDefault ? PlatformDefaults.getPanelInsets(side) : UnitValue.ZERO);
   }

   static void writeXMLObject(OutputStream os, Object o, ExceptionListener listener) {
      ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(LayoutUtil.class.getClassLoader());
      XMLEncoder encoder = new XMLEncoder(os);
      Throwable var5 = null;

      try {
         if (listener != null) {
            encoder.setExceptionListener(listener);
         }

         encoder.writeObject(o);
      } catch (Throwable var14) {
         var5 = var14;
         throw var14;
      } finally {
         if (encoder != null) {
            if (var5 != null) {
               try {
                  encoder.close();
               } catch (Throwable var13) {
                  var5.addSuppressed(var13);
               }
            } else {
               encoder.close();
            }
         }
      }

      Thread.currentThread().setContextClassLoader(oldClassLoader);
   }

   public static synchronized void writeAsXML(ObjectOutput out, Object o) throws IOException {
      if (writeOutputStream == null) {
         writeOutputStream = new ByteArrayOutputStream(16384);
      }

      writeOutputStream.reset();
      writeXMLObject(writeOutputStream, o, new ExceptionListener() {
         public void exceptionThrown(Exception e) {
            e.printStackTrace();
         }
      });
      byte[] buf = writeOutputStream.toByteArray();
      out.writeInt(buf.length);
      out.write(buf);
   }

   public static synchronized Object readAsXML(ObjectInput in) throws IOException {
      if (readBuf == null) {
         readBuf = new byte[16384];
      }

      Thread cThread = Thread.currentThread();
      ClassLoader oldCL = null;

      try {
         oldCL = cThread.getContextClassLoader();
         cThread.setContextClassLoader(LayoutUtil.class.getClassLoader());
      } catch (SecurityException var6) {
      }

      Object o = null;

      try {
         int length = in.readInt();
         if (length > readBuf.length) {
            readBuf = new byte[length];
         }

         in.readFully(readBuf, 0, length);
         o = new XMLDecoder(new ByteArrayInputStream(readBuf, 0, length)).readObject();
      } catch (EOFException var5) {
      }

      if (oldCL != null) {
         cThread.setContextClassLoader(oldCL);
      }

      return o;
   }

   public static void setSerializedObject(Object caller, Object o) {
      synchronized (SER_MAP) {
         SER_MAP.put(caller, o);
      }
   }

   public static Object getSerializedObject(Object caller) {
      synchronized (SER_MAP) {
         return SER_MAP.remove(caller);
      }
   }
}
