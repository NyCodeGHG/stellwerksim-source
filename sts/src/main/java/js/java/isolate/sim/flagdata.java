package js.java.isolate.sim;

import java.util.ArrayList;
import java.util.IllegalFormatFlagsException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

public class flagdata implements Iterable<Integer> {
   private final LinkedList<flagdata.flagelem> fl = new LinkedList();
   private final LinkedList<flagdata.flagelem> staticfl = new LinkedList();

   public flagdata(String flags) throws IllegalFormatFlagsException {
      super();
      boolean inBracket = false;
      boolean needBracket = false;
      boolean inBracket2 = false;
      int needBracket2 = 0;
      StringBuffer param = null;
      flags = flags.toUpperCase().trim();
      flagdata.flagelem fe = null;

      for(int i = 0; i < flags.length(); ++i) {
         char f = flags.charAt(i);
         if (f >= 'A' && f <= 'Z' && !inBracket && !needBracket && !inBracket2) {
            fe = new flagdata.flagelem();
            fe.flag = f;
            fe.longflag = f + "";
            this.fl.addLast(fe);
            this.staticfl.add(fe);
            if (f != 'E' && f != 'F' && f != 'K') {
               needBracket = false;
            } else {
               needBracket = true;
            }

            needBracket2 = 0;
            if (f == 'W') {
               needBracket2 = 2;
            }
         } else if (f == '(') {
            if (!needBracket) {
               throw new IllegalFormatFlagsException("keine Klammerparameter erlaubt");
            }

            if (inBracket || inBracket2) {
               throw new IllegalFormatFlagsException("doppelte Klammern");
            }

            inBracket = true;
            needBracket = false;
         } else if (f == ')' && inBracket) {
            inBracket = false;
            needBracket = false;
            if (fe.data == 0) {
               throw new IllegalFormatFlagsException("()-Klammern ohne Inhalt");
            }
         } else if (f == '[') {
            if (inBracket || inBracket2) {
               throw new IllegalFormatFlagsException("doppelte Klammern");
            }

            inBracket2 = true;
            param = new StringBuffer();
         } else if (f == ']' && inBracket2) {
            inBracket2 = false;
            --needBracket2;
            if (param.length() <= 0) {
               throw new IllegalFormatFlagsException("[]-Klammern ohne Inhalt");
            }

            fe.addParam(param.toString().toLowerCase());
            param = null;
         } else if (fe != null && !inBracket && !inBracket2 && Character.isLetterOrDigit(f)) {
            fe.longflag = fe.longflag + f;
         } else if (Character.isDigit(f) && inBracket) {
            fe.data = fe.data * 10 + Integer.parseInt(f + "");
         } else {
            if (!Character.isLetterOrDigit(f) && f != ',' && f != '-' || !inBracket2) {
               throw new IllegalFormatFlagsException("unerlaubtes Zeichen: " + f);
            }

            param.append(f);
         }
      }

      if (needBracket) {
         throw new IllegalFormatFlagsException("()-Parameter fehlt");
      } else if (needBracket2 > 0) {
         throw new IllegalFormatFlagsException("[]-Parameter fehlt");
      } else if (inBracket || inBracket2) {
         throw new IllegalFormatFlagsException("Klammern nicht geschlossen");
      }
   }

   public flagdata() {
      this("", "", "");
   }

   public flagdata(String flags, String flagd, String flagparam) {
      super();
      flagdata.flagelem fe = null;
      StringTokenizer ft = new StringTokenizer(flagd, ",");
      String[] p = null;
      if (flagparam != null && !flagparam.isEmpty()) {
         p = flagparam.split(":");
      }

      for(int i = 0; i < flags.length(); ++i) {
         char f = flags.charAt(i);
         if (f >= 'A' && f <= 'Z') {
            fe = new flagdata.flagelem();
            fe.flag = f;
            fe.longflag = f + "";
            switch(f) {
               case 'E':
               case 'F':
               case 'K':
                  if (ft.hasMoreTokens()) {
                     try {
                        fe.data = Integer.parseInt(ft.nextToken());
                     } catch (NumberFormatException var10) {
                     }
                  }
            }

            if (p != null) {
               for(int j = 0; j < p.length; ++j) {
                  if (p[j].startsWith(f + "=")) {
                     fe.addParam(p[j].substring(2).toLowerCase());
                  }
               }
            }

            this.fl.addLast(fe);
            this.staticfl.add(fe);
         } else if (fe != null) {
            fe.longflag = fe.longflag + f;
         }
      }
   }

   public flagdata(flagdata old) {
      super();

      for(flagdata.flagelem f : old.fl) {
         this.fl.addLast(new flagdata.flagelem(f));
      }

      for(flagdata.flagelem f : old.staticfl) {
         this.staticfl.addLast(new flagdata.flagelem(f));
      }
   }

   public void addFlag(char f) {
      flagdata.flagelem fe = new flagdata.flagelem();
      fe.flag = f;
      fe.longflag = f + "";
      this.fl.addLast(fe);
      this.staticfl.add(fe);
   }

   public boolean hasFlag(char flag) {
      for(flagdata.flagelem f : this.fl) {
         if (f.flag == flag) {
            return true;
         }
      }

      return false;
   }

   public boolean hadFlag(char flag) {
      for(flagdata.flagelem f : this.staticfl) {
         if (f.flag == flag) {
            return true;
         }
      }

      return false;
   }

   public boolean hasLongFlag(String flag) {
      for(flagdata.flagelem f : this.staticfl) {
         if (f.longflag.equalsIgnoreCase(flag)) {
            return true;
         }
      }

      return false;
   }

   public String getLongFlag(char flag) {
      for(flagdata.flagelem f : this.staticfl) {
         if (f.flag == flag) {
            return f.longflag;
         }
      }

      return "";
   }

   public boolean replaceFlag(char oldflag, char newflag) {
      boolean ret = false;

      for(flagdata.flagelem f : this.fl) {
         if (f.flag == oldflag) {
            f.flag = newflag;
            ret = true;
         }
      }

      return ret;
   }

   public int dataOfFlag(char flag) {
      for(flagdata.flagelem f : this.fl) {
         if (f.flag == flag) {
            return f.data;
         }
      }

      return 0;
   }

   public ArrayList<String> paramsOfFlag(char flag) {
      for(flagdata.flagelem f : this.fl) {
         if (f.flag == flag) {
            return f.param;
         }
      }

      return null;
   }

   public boolean flagHasParam(char flag, String p) {
      p = p.toLowerCase();

      for(flagdata.flagelem f : this.fl) {
         if (f.flag == flag) {
            return f.param.contains(p);
         }
      }

      return false;
   }

   public boolean removeFlag(char flag) {
      boolean ret = false;
      Iterator<flagdata.flagelem> it = this.fl.iterator();

      while(it.hasNext()) {
         flagdata.flagelem f = (flagdata.flagelem)it.next();
         if (f.flag == flag) {
            it.remove();
            ret = true;
         }
      }

      return ret;
   }

   public void killFlag(char flag) {
      this.removeFlag(flag);
      Iterator<flagdata.flagelem> it = this.staticfl.iterator();

      while(it.hasNext()) {
         flagdata.flagelem f = (flagdata.flagelem)it.next();
         if (f.flag == flag) {
            it.remove();
         }
      }
   }

   public String toString() {
      StringBuilder r = new StringBuilder();

      for(flagdata.flagelem f : this.fl) {
         r.append(f.longflag);
         if (f.data > 0) {
            r.append('(');
            r.append(f.data);
            r.append(')');
         }

         if (f.param != null) {
            for(String p : f.param) {
               r.append('[');
               r.append(p);
               r.append(']');
            }
         }
      }

      return r.toString();
   }

   public String getFlags() {
      StringBuilder r = new StringBuilder();

      for(flagdata.flagelem f : this.fl) {
         r.append(f.longflag);
      }

      return r.toString();
   }

   public String getFlagdata() {
      StringBuilder r = new StringBuilder();

      for(flagdata.flagelem f : this.fl) {
         if (f.data > 0) {
            if (r.length() > 0) {
               r.append(',');
            }

            r.append(f.data);
         }
      }

      return r.toString();
   }

   public String getFlagparam() {
      StringBuilder r = new StringBuilder();

      for(flagdata.flagelem f : this.fl) {
         if (f.param != null) {
            for(String p : f.param) {
               if (r.length() > 0) {
                  r.append(':');
               }

               r.append(f.flag + "=" + p);
            }
         }
      }

      return r.toString();
   }

   public Iterator<Integer> iterator() {
      return new flagdata.fditerator(this.fl.iterator());
   }

   public Iterator<Character> flagiterator() {
      return new flagdata.flagiterator(this.fl.iterator());
   }

   private static class fditerator implements Iterator<Integer> {
      private Iterator<flagdata.flagelem> i;

      private fditerator(Iterator<flagdata.flagelem> iterator) {
         super();
         this.i = iterator;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public void remove() {
         this.i.remove();
      }

      public Integer next() {
         return ((flagdata.flagelem)this.i.next()).data;
      }
   }

   private static class flagelem {
      public char flag;
      public String longflag = "";
      public int data = 0;
      public ArrayList<String> param = null;

      flagelem() {
         super();
      }

      flagelem(flagdata.flagelem other) {
         super();
         this.flag = other.flag;
         this.longflag = other.longflag;
         this.data = other.data;
         if (other.param != null) {
            this.param = new ArrayList(other.param);
         }
      }

      public void addParam(String p) {
         if (this.param == null) {
            this.param = new ArrayList();
         }

         this.param.add(p);
      }
   }

   private static class flagiterator implements Iterator<Character> {
      private Iterator<flagdata.flagelem> i;

      private flagiterator(Iterator<flagdata.flagelem> iterator) {
         super();
         this.i = iterator;
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public void remove() {
         this.i.remove();
      }

      public Character next() {
         return ((flagdata.flagelem)this.i.next()).flag;
      }
   }
}
