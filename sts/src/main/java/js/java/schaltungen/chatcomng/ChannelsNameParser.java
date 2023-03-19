package js.java.schaltungen.chatcomng;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class ChannelsNameParser implements Iterable<ChannelsNameParser.ChannelName> {
   private final ChannelsNameParser.ChannelName[] channels;

   public ChannelsNameParser(String channelsStr, int defaultPrio) {
      super();
      String[] splitted = channelsStr.split(";");
      this.channels = new ChannelsNameParser.ChannelName[splitted.length];
      int i = 0;

      for(String cc : splitted) {
         String[] s = cc.split(":");
         String n = extractChannelName(s);
         String t = extractChannelTitle(s);
         int prio = extractChannelPrio(s, defaultPrio);
         this.channels[i] = new ChannelsNameParser.ChannelName(n, t, prio);
         ++i;
      }
   }

   private static String extractChannelName(String[] s) {
      return s[0].trim();
   }

   private static String extractChannelTitle(String[] s) {
      return s.length >= 2 ? s[1].trim() : s[0].trim().substring(1);
   }

   private static int extractChannelPrio(String[] s, int defaultPrio) {
      return s.length >= 3 ? Integer.parseInt(s[3]) : defaultPrio;
   }

   public Set<ChannelsNameParser.ChannelName> asSet() {
      TreeSet<ChannelsNameParser.ChannelName> ret = new TreeSet();

      for(ChannelsNameParser.ChannelName c : this.channels) {
         ret.add(c);
      }

      return ret;
   }

   public Iterator<ChannelsNameParser.ChannelName> iterator() {
      return new Iterator<ChannelsNameParser.ChannelName>() {
         int pos = -1;

         public boolean hasNext() {
            return ++this.pos < ChannelsNameParser.this.channels.length;
         }

         public ChannelsNameParser.ChannelName next() {
            return ChannelsNameParser.this.channels[this.pos];
         }
      };
   }

   public static class ChannelName implements Comparable<ChannelsNameParser.ChannelName> {
      public final String name;
      public final String title;
      public final int prio;
      public String customdata = "";

      ChannelName(String name, String title, int prio) {
         super();
         this.name = name.toLowerCase();
         this.title = title;
         this.prio = prio;
      }

      public int compareTo(ChannelsNameParser.ChannelName o) {
         if (this.prio != o.prio) {
            return this.prio - o.prio;
         } else {
            int r = this.title.compareToIgnoreCase(o.title);
            if (r == 0) {
               r = this.name.compareToIgnoreCase(o.name);
            }

            return r;
         }
      }

      public boolean equals(Object o) {
         if (o instanceof String) {
            return this.name.equalsIgnoreCase((String)o);
         } else if (!(o instanceof ChannelsNameParser.ChannelName)) {
            return false;
         } else {
            return this.name.equalsIgnoreCase(((ChannelsNameParser.ChannelName)o).name)
               && this.title.equalsIgnoreCase(((ChannelsNameParser.ChannelName)o).title);
         }
      }

      public int hashCode() {
         int hash = 7;
         hash = 67 * hash + Objects.hashCode(this.name);
         return 67 * hash + Objects.hashCode(this.title);
      }

      public String toString() {
         return this.title;
      }
   }
}
