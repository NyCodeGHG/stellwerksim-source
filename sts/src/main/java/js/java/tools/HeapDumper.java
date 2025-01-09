package js.java.tools;

import com.sun.management.HotSpotDiagnosticMXBean;
import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;

public class HeapDumper {
   private static final String HOTSPOT_BEAN_NAME = "com.sun.management:type=HotSpotDiagnostic";
   private static volatile HotSpotDiagnosticMXBean hotspotMBean;

   public static void dumpHeap(String fileName, boolean live) {
      initHotspotMBean();

      try {
         hotspotMBean.dumpHeap(fileName, live);
      } catch (RuntimeException var3) {
         throw var3;
      } catch (Exception var4) {
         throw new RuntimeException(var4);
      }
   }

   private static void initHotspotMBean() {
      if (hotspotMBean == null) {
         synchronized (HeapDumper.class) {
            if (hotspotMBean == null) {
               hotspotMBean = getHotspotMBean();
            }
         }
      }
   }

   private static HotSpotDiagnosticMXBean getHotspotMBean() {
      try {
         MBeanServer server = ManagementFactory.getPlatformMBeanServer();
         return (HotSpotDiagnosticMXBean)ManagementFactory.newPlatformMXBeanProxy(
            server, "com.sun.management:type=HotSpotDiagnostic", HotSpotDiagnosticMXBean.class
         );
      } catch (RuntimeException var2) {
         throw var2;
      } catch (Exception var3) {
         throw new RuntimeException(var3);
      }
   }
}
