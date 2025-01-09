package js.java.tools.gui.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.TableCellRenderer;

public class MultiColumnTableUI extends BasicTableUI {
   private CMap map;

   public MultiColumnTableUI(CMap m) {
      this.map = m;
   }

   public void paint(Graphics g, JComponent c) {
      Rectangle r = g.getClipBounds();
      int firstRow = this.table.rowAtPoint(new Point(0, r.y));
      int lastRow = this.table.rowAtPoint(new Point(0, r.y + r.height));
      if (lastRow < 0) {
         lastRow = this.table.getRowCount() - 1;
      }

      for (int i = firstRow; i <= lastRow; i++) {
         this.paintRow(i, g);
      }
   }

   private void paintRow(int row, Graphics g) {
      Rectangle r = g.getClipBounds();

      for (int i = 0; i < this.table.getColumnCount(); i++) {
         Rectangle r1 = this.table.getCellRect(row, i, true);
         if (r1.intersects(r)) {
            int sk = this.map.visibleCell(row, i);

            for (int j = 1; j < this.map.span(row, sk); j++) {
               Rectangle r2 = this.table.getCellRect(row, i + j, true);
               r1 = (Rectangle)r1.createUnion(r2);
            }

            this.paintCell(row, sk, g, r1);
            i += this.map.span(row, sk) - 1;
         }
      }
   }

   private void paintCell(int row, int column, Graphics g, Rectangle area) {
      int verticalMargin = this.table.getRowMargin();
      int horizontalMargin = this.table.getColumnModel().getColumnMargin();
      Color c = g.getColor();
      g.setColor(this.table.getGridColor());
      if (this.table.getShowHorizontalLines()) {
         g.drawLine(area.x, area.y + area.height - 1, area.x + area.width - 1, area.y + area.height - 1);
      }

      if (this.table.getShowVerticalLines()) {
         g.drawLine(area.x, area.y, area.x, area.y + area.height - 1);
      }

      g.setColor(c);
      area.setBounds(area.x + horizontalMargin / 2, area.y + verticalMargin / 2, area.width - horizontalMargin, area.height - verticalMargin);
      if (this.table.isEditing() && this.table.getEditingRow() == row && this.table.getEditingColumn() == column) {
         Component component = this.table.getEditorComponent();
         component.setBounds(area);
         component.validate();
      } else {
         TableCellRenderer renderer = this.table.getCellRenderer(row, column);
         Component component = this.table.prepareRenderer(renderer, row, column);
         if (component != null) {
            if (component.getParent() == null) {
               this.rendererPane.add(component);
            }

            this.rendererPane.paintComponent(g, component, this.table, area.x, area.y, area.width, area.height, true);
         }
      }
   }
}
