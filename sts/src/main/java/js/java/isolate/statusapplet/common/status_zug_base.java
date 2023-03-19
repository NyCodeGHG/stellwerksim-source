package js.java.isolate.statusapplet.common;

import java.text.DateFormat;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;

public abstract class status_zug_base {
   public int zid;
   public String name = null;
   public int verspaetung = 0;
   public int prevverspaetung = 0;
   public boolean verspaetungSeen = false;
   public int aid = 0;
   public int visibleaid = 0;
   public boolean visible = false;
   public boolean laststopdone = false;
   public char thematag = 'A';
   public LinkedList<szug_plan_base> plan = new LinkedList();
   public DateFormat sdf;
   public HashSet<Integer> refs = new HashSet();
   public HashSet<Integer> refed = new HashSet();
   public int status = 0;
   public static final int ST_NEW = 0;
   public static final int ST_NOTSEEN = 1;
   public static final int ST_SEEN = 2;
   public static final int ST_DELETE = 3;

   public status_zug_base() {
      super();
      this.sdf = DateFormat.getTimeInstance(3, Locale.GERMAN);
   }
}
