package js.java.tools.security;

import java.security.AccessControlException;
import java.security.AccessController;
import java.security.BasicPermission;

public class IndividualPermission extends BasicPermission {
   public IndividualPermission(String name) {
      super(name);
   }

   public static boolean checkPermission(String name) {
      try {
         AccessController.checkPermission(new IndividualPermission(name));

         try {
            AccessController.checkPermission(new IndividualPermission(name + ".undef"));
         } catch (AccessControlException var2) {
            return true;
         }
      } catch (AccessControlException var3) {
      }

      return false;
   }
}
