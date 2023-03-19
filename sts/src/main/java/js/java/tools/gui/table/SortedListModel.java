package js.java.tools.gui.table;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class SortedListModel extends AbstractListModel {
   private List<SortedListModel.SortedListEntry> sortedModel;
   private ListModel unsortedModel;
   private Comparator comparator;
   private SortedListModel.SortOrder sortOrder;

   private SortedListModel() {
      super();
   }

   public SortedListModel(ListModel model) {
      this(model, SortedListModel.SortOrder.ASCENDING, null);
   }

   public SortedListModel(ListModel model, SortedListModel.SortOrder sortOrder) {
      this(model, sortOrder, null);
   }

   public SortedListModel(ListModel model, SortedListModel.SortOrder sortOrder, Comparator comp) {
      super();
      this.unsortedModel = model;
      this.unsortedModel.addListDataListener(new ListDataListener() {
         public void intervalAdded(ListDataEvent e) {
            SortedListModel.this.unsortedIntervalAdded(e);
         }

         public void intervalRemoved(ListDataEvent e) {
            SortedListModel.this.unsortedIntervalRemoved(e);
         }

         public void contentsChanged(ListDataEvent e) {
            SortedListModel.this.unsortedContentsChanged(e);
         }
      });
      this.sortOrder = sortOrder;
      if (comp != null) {
         this.comparator = comp;
      } else {
         this.comparator = new SortedTableModel.DefaultComparator();
      }

      this.initSortedList();
      Collections.sort(this.sortedModel);
   }

   private void initSortedList() {
      int size = this.unsortedModel.getSize();
      this.sortedModel = new ArrayList(size);

      for(int x = 0; x < size; ++x) {
         SortedListModel.SortedListEntry entry = new SortedListModel.SortedListEntry(x);
         int insertionPoint = this.findInsertionPoint(entry);
         this.sortedModel.add(insertionPoint, entry);
      }
   }

   public Object getElementAt(int index) throws IndexOutOfBoundsException {
      int modelIndex = this.toUnsortedModelIndex(index);
      return this.unsortedModel.getElementAt(modelIndex);
   }

   public int getSize() {
      return this.sortedModel.size();
   }

   public int toUnsortedModelIndex(int index) throws IndexOutOfBoundsException {
      int modelIndex = -1;
      SortedListModel.SortedListEntry entry = (SortedListModel.SortedListEntry)this.sortedModel.get(index);
      return entry.getIndex();
   }

   public int[] toUnsortedModelIndices(int[] sortedSelectedIndices) {
      int[] unsortedSelectedIndices = new int[sortedSelectedIndices.length];
      int x = 0;

      for(int sortedIndex : sortedSelectedIndices) {
         unsortedSelectedIndices[x++] = this.toUnsortedModelIndex(sortedIndex);
      }

      Arrays.sort(unsortedSelectedIndices);
      return unsortedSelectedIndices;
   }

   public int toSortedModelIndex(int unsortedIndex) {
      int sortedIndex = -1;
      int x = -1;

      for(SortedListModel.SortedListEntry entry : this.sortedModel) {
         ++x;
         if (entry.getIndex() == unsortedIndex) {
            sortedIndex = x;
            break;
         }
      }

      return sortedIndex;
   }

   public int[] toSortedModelIndices(int[] unsortedModelIndices) {
      int[] sortedModelIndices = new int[unsortedModelIndices.length];
      int x = 0;

      for(int unsortedIndex : unsortedModelIndices) {
         sortedModelIndices[x++] = this.toSortedModelIndex(unsortedIndex);
      }

      Arrays.sort(sortedModelIndices);
      return sortedModelIndices;
   }

   private void resetModelData() {
      int index = 0;

      for(SortedListModel.SortedListEntry entry : this.sortedModel) {
         entry.setIndex(index++);
      }
   }

   public void setComparator(Comparator comp) {
      if (comp == null) {
         this.sortOrder = SortedListModel.SortOrder.UNORDERED;
         this.comparator = Collator.getInstance();
         this.resetModelData();
      } else {
         this.comparator = comp;
         Collections.sort(this.sortedModel);
      }

      this.fireContentsChanged(0, 0, this.sortedModel.size() - 1);
   }

   public void setSortOrder(SortedListModel.SortOrder sortOrder) {
      if (this.sortOrder != sortOrder) {
         this.sortOrder = sortOrder;
         if (sortOrder == SortedListModel.SortOrder.UNORDERED) {
            this.resetModelData();
         } else {
            Collections.sort(this.sortedModel);
         }

         this.fireContentsChanged(0, 0, this.sortedModel.size() - 1);
      }
   }

   private void unsortedIntervalAdded(ListDataEvent e) {
      int begin = e.getIndex0();
      int end = e.getIndex1();
      int nElementsAdded = end - begin + 1;

      for(SortedListModel.SortedListEntry entry : this.sortedModel) {
         int index = entry.getIndex();
         if (index >= begin) {
            entry.setIndex(index + nElementsAdded);
         }
      }

      for(int x = begin; x <= end; ++x) {
         SortedListModel.SortedListEntry newEntry = new SortedListModel.SortedListEntry(x);
         int insertionPoint = this.findInsertionPoint(newEntry);
         this.sortedModel.add(insertionPoint, newEntry);
         this.fireIntervalAdded(1, insertionPoint, insertionPoint);
      }
   }

   private void unsortedIntervalRemoved(ListDataEvent e) {
      int begin = e.getIndex0();
      int end = e.getIndex1();
      int nElementsRemoved = end - begin + 1;
      int sortedSize = this.sortedModel.size();
      boolean[] bElementRemoved = new boolean[sortedSize];

      for(int x = sortedSize - 1; x >= 0; --x) {
         SortedListModel.SortedListEntry entry = (SortedListModel.SortedListEntry)this.sortedModel.get(x);
         int index = entry.getIndex();
         if (index > end) {
            entry.setIndex(index - nElementsRemoved);
         } else if (index >= begin) {
            this.sortedModel.remove(x);
            bElementRemoved[x] = true;
         }
      }

      for(int x = bElementRemoved.length - 1; x >= 0; --x) {
         if (bElementRemoved[x]) {
            this.fireIntervalRemoved(2, x, x);
         }
      }
   }

   private void unsortedContentsChanged(ListDataEvent e) {
      if (this.unsortedModel.getSize() != this.sortedModel.size()) {
         this.initSortedList();
      }

      Collections.sort(this.sortedModel);
      this.fireContentsChanged(0, 0, this.sortedModel.size() - 1);
   }

   private int findInsertionPoint(SortedListModel.SortedListEntry entry) {
      int insertionPoint = this.sortedModel.size();
      if (this.sortOrder != SortedListModel.SortOrder.UNORDERED) {
         insertionPoint = Collections.binarySearch(this.sortedModel, entry);
         if (insertionPoint < 0) {
            insertionPoint = -(insertionPoint + 1);
         }
      }

      return insertionPoint;
   }

   public static enum SortOrder {
      UNORDERED,
      ASCENDING,
      DESCENDING;
   }

   class SortedListEntry implements Comparable {
      private int index;

      private SortedListEntry() {
         super();
      }

      SortedListEntry(int index) {
         super();
         this.index = index;
      }

      public int getIndex() {
         return this.index;
      }

      public void setIndex(int index) {
         this.index = index;
      }

      public int compareTo(Object o) {
         Object thisElement = SortedListModel.this.unsortedModel.getElementAt(this.index);
         SortedListModel.SortedListEntry thatEntry = (SortedListModel.SortedListEntry)o;
         Object thatElement = SortedListModel.this.unsortedModel.getElementAt(thatEntry.getIndex());
         if (SortedListModel.this.comparator instanceof Collator) {
            thisElement = thisElement.toString();
            thatElement = thatElement.toString();
         }

         int comparison = SortedListModel.this.comparator.compare(thisElement, thatElement);
         if (SortedListModel.this.sortOrder == SortedListModel.SortOrder.DESCENDING) {
            comparison = -comparison;
         }

         return comparison;
      }
   }
}
