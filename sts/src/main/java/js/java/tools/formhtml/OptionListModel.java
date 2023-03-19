package js.java.tools.formhtml;

import java.io.Serializable;
import java.util.BitSet;
import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

class OptionListModel<E> extends DefaultListModel<E> implements ListSelectionModel, Serializable {
   private static final int MIN = -1;
   private static final int MAX = Integer.MAX_VALUE;
   private int selectionMode = 0;
   private int minIndex = Integer.MAX_VALUE;
   private int maxIndex = -1;
   private int anchorIndex = -1;
   private int leadIndex = -1;
   private int firstChangedIndex = Integer.MAX_VALUE;
   private int lastChangedIndex = -1;
   private boolean isAdjusting = false;
   private BitSet value = new BitSet(32);
   private BitSet initialValue = new BitSet(32);
   protected EventListenerList listenerList = new EventListenerList();
   protected boolean leadAnchorNotificationEnabled = true;

   OptionListModel() {
      super();
   }

   public int getMinSelectionIndex() {
      return this.isSelectionEmpty() ? -1 : this.minIndex;
   }

   public int getMaxSelectionIndex() {
      return this.maxIndex;
   }

   public boolean getValueIsAdjusting() {
      return this.isAdjusting;
   }

   public int getSelectionMode() {
      return this.selectionMode;
   }

   public void setSelectionMode(int selectionMode) {
      switch(selectionMode) {
         case 0:
         case 1:
         case 2:
            this.selectionMode = selectionMode;
            return;
         default:
            throw new IllegalArgumentException("invalid selectionMode");
      }
   }

   public boolean isSelectedIndex(int index) {
      return index >= this.minIndex && index <= this.maxIndex ? this.value.get(index) : false;
   }

   public boolean isSelectionEmpty() {
      return this.minIndex > this.maxIndex;
   }

   public void addListSelectionListener(ListSelectionListener l) {
      this.listenerList.add(ListSelectionListener.class, l);
   }

   public void removeListSelectionListener(ListSelectionListener l) {
      this.listenerList.remove(ListSelectionListener.class, l);
   }

   public ListSelectionListener[] getListSelectionListeners() {
      return (ListSelectionListener[])this.listenerList.getListeners(ListSelectionListener.class);
   }

   protected void fireValueChanged(boolean isAdjusting) {
      this.fireValueChanged(this.getMinSelectionIndex(), this.getMaxSelectionIndex(), isAdjusting);
   }

   protected void fireValueChanged(int firstIndex, int lastIndex) {
      this.fireValueChanged(firstIndex, lastIndex, this.getValueIsAdjusting());
   }

   protected void fireValueChanged(int firstIndex, int lastIndex, boolean isAdjusting) {
      Object[] listeners = this.listenerList.getListenerList();
      ListSelectionEvent e = null;

      for(int i = listeners.length - 2; i >= 0; i -= 2) {
         if (listeners[i] == ListSelectionListener.class) {
            if (e == null) {
               e = new ListSelectionEvent(this, firstIndex, lastIndex, isAdjusting);
            }

            ((ListSelectionListener)listeners[i + 1]).valueChanged(e);
         }
      }
   }

   private void fireValueChanged() {
      if (this.lastChangedIndex != -1) {
         int oldFirstChangedIndex = this.firstChangedIndex;
         int oldLastChangedIndex = this.lastChangedIndex;
         this.firstChangedIndex = Integer.MAX_VALUE;
         this.lastChangedIndex = -1;
         this.fireValueChanged(oldFirstChangedIndex, oldLastChangedIndex);
      }
   }

   private void markAsDirty(int r) {
      this.firstChangedIndex = Math.min(this.firstChangedIndex, r);
      this.lastChangedIndex = Math.max(this.lastChangedIndex, r);
   }

   private void set(int r) {
      if (!this.value.get(r)) {
         this.value.set(r);
         Option option = (Option)this.get(r);
         option.setSelection(true);
         this.markAsDirty(r);
         this.minIndex = Math.min(this.minIndex, r);
         this.maxIndex = Math.max(this.maxIndex, r);
      }
   }

   private void clear(int r) {
      if (this.value.get(r)) {
         this.value.clear(r);
         Option option = (Option)this.get(r);
         option.setSelection(false);
         this.markAsDirty(r);
         if (r == this.minIndex) {
            ++this.minIndex;

            while(this.minIndex <= this.maxIndex && !this.value.get(this.minIndex)) {
               ++this.minIndex;
            }
         }

         if (r == this.maxIndex) {
            --this.maxIndex;

            while(this.minIndex <= this.maxIndex && !this.value.get(this.maxIndex)) {
               --this.maxIndex;
            }
         }

         if (this.isSelectionEmpty()) {
            this.minIndex = Integer.MAX_VALUE;
            this.maxIndex = -1;
         }
      }
   }

   public void setLeadAnchorNotificationEnabled(boolean flag) {
      this.leadAnchorNotificationEnabled = flag;
   }

   public boolean isLeadAnchorNotificationEnabled() {
      return this.leadAnchorNotificationEnabled;
   }

   private void updateLeadAnchorIndices(int anchorIndex, int leadIndex) {
      if (this.leadAnchorNotificationEnabled) {
         if (this.anchorIndex != anchorIndex) {
            if (this.anchorIndex != -1) {
               this.markAsDirty(this.anchorIndex);
            }

            this.markAsDirty(anchorIndex);
         }

         if (this.leadIndex != leadIndex) {
            if (this.leadIndex != -1) {
               this.markAsDirty(this.leadIndex);
            }

            this.markAsDirty(leadIndex);
         }
      }

      this.anchorIndex = anchorIndex;
      this.leadIndex = leadIndex;
   }

   private boolean contains(int a, int b, int i) {
      return i >= a && i <= b;
   }

   private void changeSelection(int clearMin, int clearMax, int setMin, int setMax, boolean clearFirst) {
      for(int i = Math.min(setMin, clearMin); i <= Math.max(setMax, clearMax); ++i) {
         boolean shouldClear = this.contains(clearMin, clearMax, i);
         boolean shouldSet = this.contains(setMin, setMax, i);
         if (shouldSet && shouldClear) {
            if (clearFirst) {
               shouldClear = false;
            } else {
               shouldSet = false;
            }
         }

         if (shouldSet) {
            this.set(i);
         }

         if (shouldClear) {
            this.clear(i);
         }
      }

      this.fireValueChanged();
   }

   private void changeSelection(int clearMin, int clearMax, int setMin, int setMax) {
      this.changeSelection(clearMin, clearMax, setMin, setMax, true);
   }

   public void clearSelection() {
      this.removeSelectionInterval(this.minIndex, this.maxIndex);
   }

   public void setSelectionInterval(int index0, int index1) {
      if (index0 != -1 && index1 != -1) {
         if (this.getSelectionMode() == 0) {
            index0 = index1;
         }

         this.updateLeadAnchorIndices(index0, index1);
         int clearMin = this.minIndex;
         int clearMax = this.maxIndex;
         int setMin = Math.min(index0, index1);
         int setMax = Math.max(index0, index1);
         this.changeSelection(clearMin, clearMax, setMin, setMax);
      }
   }

   public void addSelectionInterval(int index0, int index1) {
      if (index0 != -1 && index1 != -1) {
         if (this.getSelectionMode() != 2) {
            this.setSelectionInterval(index0, index1);
         } else {
            this.updateLeadAnchorIndices(index0, index1);
            int clearMin = Integer.MAX_VALUE;
            int clearMax = -1;
            int setMin = Math.min(index0, index1);
            int setMax = Math.max(index0, index1);
            this.changeSelection(clearMin, clearMax, setMin, setMax);
         }
      }
   }

   public void removeSelectionInterval(int index0, int index1) {
      if (index0 != -1 && index1 != -1) {
         this.updateLeadAnchorIndices(index0, index1);
         int clearMin = Math.min(index0, index1);
         int clearMax = Math.max(index0, index1);
         int setMin = Integer.MAX_VALUE;
         int setMax = -1;
         this.changeSelection(clearMin, clearMax, setMin, setMax);
      }
   }

   private void setState(int index, boolean state) {
      if (state) {
         this.set(index);
      } else {
         this.clear(index);
      }
   }

   public void insertIndexInterval(int index, int length, boolean before) {
      int insMinIndex = before ? index : index + 1;
      int insMaxIndex = insMinIndex + length - 1;

      for(int i = this.maxIndex; i >= insMinIndex; --i) {
         this.setState(i + length, this.value.get(i));
      }

      boolean setInsertedValues = this.value.get(index);

      for(int i = insMinIndex; i <= insMaxIndex; ++i) {
         this.setState(i, setInsertedValues);
      }
   }

   public void removeIndexInterval(int index0, int index1) {
      int rmMinIndex = Math.min(index0, index1);
      int rmMaxIndex = Math.max(index0, index1);
      int gapLength = rmMaxIndex - rmMinIndex + 1;

      for(int i = rmMinIndex; i <= this.maxIndex; ++i) {
         this.setState(i, this.value.get(i + gapLength));
      }
   }

   public void setValueIsAdjusting(boolean isAdjusting) {
      if (isAdjusting != this.isAdjusting) {
         this.isAdjusting = isAdjusting;
         this.fireValueChanged(isAdjusting);
      }
   }

   public String toString() {
      String s = (this.getValueIsAdjusting() ? "~" : "=") + this.value.toString();
      return this.getClass().getName() + " " + Integer.toString(this.hashCode()) + " " + s;
   }

   public Object clone() throws CloneNotSupportedException {
      OptionListModel clone = (OptionListModel)super.clone();
      clone.value = (BitSet)this.value.clone();
      clone.listenerList = new EventListenerList();
      return clone;
   }

   public int getAnchorSelectionIndex() {
      return this.anchorIndex;
   }

   public int getLeadSelectionIndex() {
      return this.leadIndex;
   }

   public void setAnchorSelectionIndex(int anchorIndex) {
      this.anchorIndex = anchorIndex;
   }

   public void setLeadSelectionIndex(int leadIndex) {
      int anchorIndex = this.anchorIndex;
      if (this.getSelectionMode() == 0) {
         anchorIndex = leadIndex;
      }

      int oldMin = Math.min(this.anchorIndex, this.leadIndex);
      int oldMax = Math.max(this.anchorIndex, this.leadIndex);
      int newMin = Math.min(anchorIndex, leadIndex);
      int newMax = Math.max(anchorIndex, leadIndex);
      if (this.value.get(this.anchorIndex)) {
         this.changeSelection(oldMin, oldMax, newMin, newMax);
      } else {
         this.changeSelection(newMin, newMax, oldMin, oldMax, false);
      }

      this.anchorIndex = anchorIndex;
      this.leadIndex = leadIndex;
   }

   public void setInitialSelection(int i) {
      if (!this.initialValue.get(i)) {
         if (this.selectionMode == 0) {
            this.initialValue.and(new BitSet());
         }

         this.initialValue.set(i);
      }
   }

   public BitSet getInitialSelection() {
      return this.initialValue;
   }
}
