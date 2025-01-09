package js.java.isolate.sim.sim.funk;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.border.LineBorder;
import js.java.isolate.sim.sim.zugUndPlanPanel;
import js.java.isolate.sim.sim.fahrplanRenderer.zugRenderer;
import js.java.isolate.sim.zug.zug;
import js.java.tools.gui.layout.WrapLayout;

public class funkMenu extends JPanel implements Scrollable {
   private final zug z;
   private final zugUndPlanPanel.funkAdapter fa;
   private int colFlip = 0;
   private static final Color[] cols = new Color[]{new Color(255, 255, 255), new Color(255, 255, 240)};

   public funkMenu(zugUndPlanPanel.funkAdapter fa, zug z, int unterzugAZid) {
      this.z = z;
      this.fa = fa;
      this.initComponents();
      this.setLayout(new BoxLayout(this, 3));
      if (z != null) {
         this.showFunk(new funk_zugGleisaenderung(fa, z, unterzugAZid));
         this.showFunk(new funk_zugWeiterfahren(fa, z, unterzugAZid));
      }

      this.showFunk(new funk_zugWarten(fa, z, unterzugAZid));
      if (z != null) {
         this.showFunk(new funk_zugGeschwindigkeit(fa, z, unterzugAZid));
         this.showFunk(new funk_zugRichtungAendern(fa, z, unterzugAZid));
         this.showFunk(new funk_zugPosition(fa, z, unterzugAZid));
      }

      this.showFunk(new funk_hotline(fa, z, unterzugAZid));
      this.showFunk(new funk_reparaturFortschritt(fa));
      this.showFunk(new funk_gleisansage(fa));
   }

   public void close() {
      this.fa.close();
   }

   public zug getZug() {
      return this.z;
   }

   private void showFunk(funkAuftragBase befehl) {
      JPanel o = new JPanel();
      o.setLayout(new BefehlsLayout());
      o.setBackground(cols[this.colFlip]);
      o.setBorder(BorderFactory.createTitledBorder(new LineBorder(o.getBackground().darker()), befehl.getTitel()));
      int commands = 0;
      JPanel p = new JPanel();
      p.setOpaque(false);
      p.setLayout(new WrapLayout(0));

      for (funkAuftragBase.funkValueItem fvi : befehl.getValues()) {
         funkMenu.fButton b = new funkMenu.fButton(befehl, fvi);
         if (fvi.iconName != null) {
            try {
               b.setHorizontalTextPosition(10);
               b.setIcon(new ImageIcon(zugRenderer.class.getResource(fvi.iconName + ".png")));
            } catch (Exception var9) {
               var9.printStackTrace();
            }
         }

         p.add(b);
         commands++;
      }

      o.add(p);
      if (commands > 0) {
         this.colFlip = 1 - this.colFlip;
         this.add(o);
      }
   }

   public void updateLayout() {
      for (int i = 0; i < this.getComponentCount(); i++) {
         this.getComponent(i).doLayout();
      }

      this.revalidate();
   }

   public Dimension getPreferredScrollableViewportSize() {
      return this.getPreferredSize();
   }

   public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
      return 10;
   }

   public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
      return visibleRect.height / 2;
   }

   public boolean getScrollableTracksViewportWidth() {
      return true;
   }

   public boolean getScrollableTracksViewportHeight() {
      return false;
   }

   private void initComponents() {
      this.setBackground(new Color(255, 255, 255));
      this.setLayout(null);
   }

   private class fButton extends JButton implements ActionListener {
      private final funkAuftragBase.funkValueItem fvi;
      private final funkAuftragBase befehl;

      fButton(funkAuftragBase befehl, funkAuftragBase.funkValueItem fvi) {
         super(fvi.text);
         this.befehl = befehl;
         this.fvi = fvi;
         this.setMargin(new Insets(3, 5, 3, 5));
         this.setFocusable(false);
         this.setFocusPainted(false);
         this.addActionListener(this);
      }

      public void actionPerformed(ActionEvent e) {
         funkMenu.this.close();
         this.befehl.selected(this.fvi);
      }
   }
}
