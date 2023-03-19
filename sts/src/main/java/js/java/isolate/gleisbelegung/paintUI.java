package js.java.isolate.gleisbelegung;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.plaf.LayerUI;
import js.java.tools.gui.GraphicTools;

class paintUI extends LayerUI<JComponent> {
   private final ArrayList<Shape> shapeList = new ArrayList();

   paintUI() {
      super();
   }

   public void paint(Graphics g, JComponent l) {
      super.paint(g, l);
      Graphics2D g2 = (Graphics2D)g;
      GraphicTools.enableGfxAA(g2);
      Color r = Color.RED;
      Color in = new Color(255, 255, 0, 85);
      g2.setStroke(new BasicStroke(3.0F));

      for(Shape shape : this.shapeList) {
         g2.setColor(in);
         g2.fill(shape);
         g2.setColor(r);
         g2.draw(shape);
      }

      g2.setStroke(new BasicStroke(1.0F));
   }

   public void add(Shape e) {
      this.shapeList.add(e);
   }

   public void add(ArrayList<Shape> e) {
      e.stream().forEach(s -> this.add(s));
   }

   public void clear() {
      this.shapeList.clear();
   }
}
