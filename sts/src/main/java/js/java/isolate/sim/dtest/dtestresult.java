package js.java.isolate.sim.dtest;

import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;

public class dtestresult implements Comparable {
   public static final int RANK_INFO = 0;
   public static final int RANK_WARNING = 1;
   public static final int RANK_FAULT = 2;
   private String text;
   private int rank;
   private gleis gl = null;
   private LinkedList<gleis> markedGleis = null;
   private fahrstrasse fs = null;

   public dtestresult(int rank, String text) {
      super();
      this.text = text;
      this.rank = rank;
   }

   public dtestresult(int rank, String text, gleis gl) {
      this(rank, text);
      this.gl = gl;
   }

   public dtestresult(int rank, String text, LinkedList<gleis> m) {
      this(rank, text);
      this.markedGleis = m;
   }

   public dtestresult(int rank, String text, gleis gl, LinkedList<gleis> m) {
      this(rank, text, gl);
      this.markedGleis = m;
   }

   public dtestresult(int rank, String text, fahrstrasse f) {
      this(rank, text);
      this.fs = f;
   }

   public dtestresult(int rank, String text, Object... v) {
      this(rank, text);

      for(Object va : v) {
         if (va instanceof gleis) {
            this.gl = (gleis)va;
         } else if (va instanceof fahrstrasse) {
            this.fs = (fahrstrasse)va;
         } else if (va instanceof LinkedList) {
            this.markedGleis = (LinkedList)va;
         }
      }
   }

   public int getRank() {
      return this.rank;
   }

   public String getText() {
      return this.text;
   }

   public gleis getGleis() {
      return this.gl;
   }

   public boolean paintResultsInGleisbild(gleisbildModelSts glbModel) {
      glbModel.allOff();
      glbModel.setSelectedGleis(this.getGleis());
      glbModel.setFocus(this.getGleis());
      if (this.markedGleis != null) {
         for(gleis mgl : this.markedGleis) {
            glbModel.addMarkedGleis(mgl);
         }
      }

      if (this.fs != null) {
         glbModel.showFahrweg(this.fs);
      }

      return true;
   }

   public boolean equals(Object o) {
      if (!(o instanceof dtestresult)) {
         return false;
      } else {
         dtestresult t = (dtestresult)o;
         return t.rank == this.rank && this.text.equalsIgnoreCase(t.text) && this.gl == t.gl;
      }
   }

   public int hashCode() {
      int hash = 7;
      hash = 83 * hash + (this.text != null ? this.text.hashCode() : 0);
      hash = 83 * hash + this.rank;
      return 83 * hash + (this.gl != null ? this.gl.hashCode() : 0);
   }

   public int compareTo(Object o) {
      dtestresult t = (dtestresult)o;
      int r = 1;
      if (t.rank == this.rank) {
         r = this.text.compareToIgnoreCase(t.text);
         if (r == 0 && this.gl != null && t.gl != null) {
            r = this.gl.compareTo(t.gl);
            if (r == 0) {
               r = this.gl.compareToGleis(t.gl);
            }
         }
      } else if (t.rank < this.rank) {
         r = -1;
      } else {
         r = 1;
      }

      return r;
   }
}
