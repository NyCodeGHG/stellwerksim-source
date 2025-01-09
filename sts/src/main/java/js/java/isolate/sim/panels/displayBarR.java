package js.java.isolate.sim.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.UIManager;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisTypContainer;
import js.java.isolate.sim.gleis.displayBar.connector;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.gecWorker.GecSelectEvent;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;
import js.java.isolate.sim.panels.actionevents.displaySelectedEvent;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.tools.actions.AbstractEvent;
import js.java.tools.gui.SmoothViewport;

public class displayBarR extends basePanel implements SessionClose {
   private final TreeSet<displayBarR.gleisLine> trigger = new TreeSet();
   private gleis currentDisplay = null;
   private boolean shown = false;
   private JTextField displayNameField;
   private JCheckBox jCheckBox1;
   private JCheckBox jCheckBox2;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JLabel jLabel4;
   private JPanel jPanel1;
   private JPanel triggerHeaderPanel;
   private JPanel triggerPanel;
   private JScrollPane triggerScrollPane;

   @Override
   public void close() {
      this.trigger.clear();
   }

   public displayBarR(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
      this.initComponents();
      this.triggerScrollPane.setViewport(new SmoothViewport(this.triggerPanel));
      this.triggerPanel.setLayout(new GridLayout(0, 5) {
         public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
               Insets insets = parent.getInsets();
               int ncomponents = parent.getComponentCount();
               int nrows = this.getRows();
               int ncols = this.getColumns();
               boolean ltr = parent.getComponentOrientation().isLeftToRight();
               if (ncomponents != 0) {
                  if (nrows > 0) {
                     ncols = (ncomponents + nrows - 1) / nrows;
                  } else {
                     nrows = (ncomponents + ncols - 1) / ncols;
                  }

                  int w = parent.getWidth() - (insets.left + insets.right);
                  int h = parent.getHeight() - (insets.top + insets.bottom);
                  w = (w - (ncols - 1) * this.getHgap()) / ncols;
                  h = (h - (nrows - 1) * this.getVgap()) / nrows;
                  if (ltr) {
                     int c = 0;

                     for (int x = insets.left; c < ncols; x += w + this.getHgap()) {
                        int r = 0;

                        for (int y = insets.top; r < nrows; y += h + this.getVgap()) {
                           int i = r * ncols + c;
                           if (i < ncomponents) {
                              Dimension d = parent.getComponent(i).getPreferredSize();
                              int dx = x;
                              int dy = y;
                              int dw = w;
                              int dh = h;
                              if (c > 2 && d.width < w) {
                                 dx = x + (w - d.width) / 2;
                                 dw = d.width;
                              }

                              if (c > 2 && d.height < h) {
                                 dy = y + (h - d.height) / 2;
                                 dh = d.height;
                              }

                              parent.getComponent(i).setBounds(dx, dy, dw, dh);
                              if (c == 0) {
                                 displayBarR.this.triggerHeaderPanel.getComponent(c).setBounds(dx, dy, w, h);
                              }
                           }

                           r++;
                        }

                        c++;
                     }
                  } else {
                     super.layoutContainer(parent);
                  }
               }
            }
         }
      });
      this.triggerHeaderPanel.setLayout(new GridLayout(0, 5) {
         public Dimension preferredLayoutSize(Container parent) {
            Dimension d1 = super.preferredLayoutSize(parent);
            Dimension d = displayBarR.this.triggerPanel.getLayout().preferredLayoutSize(displayBarR.this.triggerPanel);
            d.height = d1.height;
            return d;
         }

         public Dimension minimumLayoutSize(Container parent) {
            Dimension d1 = super.minimumLayoutSize(parent);
            Dimension d = displayBarR.this.triggerPanel.getLayout().minimumLayoutSize(displayBarR.this.triggerPanel);
            d.height = d1.height;
            return d;
         }
      });
      this.addHeaderLabel("SW-Wert", this.triggerHeaderPanel);
      this.addHeaderLabel("Element", this.triggerHeaderPanel);
      this.addHeaderLabel("Koordinaten", this.triggerHeaderPanel);
      this.addHeaderLabel("verbunden", this.triggerHeaderPanel);
      this.addHeaderLabel("FS Display", this.triggerHeaderPanel);
      this.triggerScrollPane.setColumnHeaderView(this.triggerHeaderPanel);
      e.registerListener(10, this);
   }

   @Override
   public void action(AbstractEvent e) {
      if (e instanceof displaySelectedEvent) {
         displaySelectedEvent dse = (displaySelectedEvent)e;
         this.glbControl.getModel().allOff();
         this.currentDisplay = dse.getDisplay();
         LinkedList<connector> l = dse.getSelected();
         if (this.currentDisplay != null && l != null) {
            this.displayNameField.setText(this.currentDisplay.getSWWert());

            for (displayBarR.gleisLine gl : this.trigger) {
               connector d = null;

               for (connector c : l) {
                  try {
                     if (c.getSWwert().equalsIgnoreCase(gl.gl.getSWWert())) {
                        d = c;
                        gl.element.scrollRectToVisible(new Rectangle(0, 0, gl.element.getWidth(), gl.element.getHeight()));
                        break;
                     }
                  } catch (NullPointerException var11) {
                  }
               }

               gl.setConnector(d);
               gl.setEnabled(!this.glbControl.getModel().getDisplayBar().isLegacy());
            }
         } else {
            this.displayNameField.setText("");

            for (displayBarR.gleisLine gl : this.trigger) {
               gl.setConnector(null);
               gl.setEnabled(false);
            }
         }
      } else if (e instanceof GecSelectEvent && this.shown) {
         try {
            String sw = this.glbControl.getSelectedGleis().getSWWert();

            for (displayBarR.gleisLine gl : this.trigger) {
               if (gl.gl.getSWWert().equals(sw)) {
                  gl.element.scrollRectToVisible(new Rectangle(0, 0, gl.element.getWidth(), gl.element.getHeight()));
                  gl.britzel();
                  break;
               }
            }
         } catch (NullPointerException var10) {
         }
      }
   }

   @Override
   public void shown(String n, gecBase gec) {
      this.shown = true;
      this.currentDisplay = null;
      this.displayNameField.setText("");
      this.glbControl.getModel().allOff();
      this.updateTriggerList();
      gec.addChangeListener(this);
   }

   @Override
   public void hidden(gecBase gec) {
      this.shown = false;
      this.glbControl.getModel().allOff();
   }

   private void addHeaderLabel(String text, JPanel p) {
      JLabel hlab = new JLabel();
      hlab.setBackground(UIManager.getDefaults().getColor("Table.focusCellBackground"));
      hlab.setHorizontalAlignment(0);
      hlab.setText(text);
      hlab.setBorder(BorderFactory.createBevelBorder(0));
      hlab.setOpaque(true);
      p.add(hlab);
   }

   private void updateOthers(displayBarR.gleisLine othergl) {
      for (displayBarR.gleisLine gl : this.trigger) {
         if (gl != othergl && othergl.swwert.getText().equalsIgnoreCase(gl.swwert.getText())) {
            gl.setConnector(othergl.set);
         }
      }
   }

   private void updateTriggerList() {
      this.triggerPanel.removeAll();
      this.trigger.clear();

      for (gleis gl : this.glbControl.getModel()) {
         if (gl.isDisplayTriggerSelectable()) {
            this.trigger.add(new displayBarR.gleisLine(gl));
         }
      }

      for (displayBarR.gleisLine glx : this.trigger) {
         glx.setEnabled(false);
         glx.add(this.triggerPanel);
      }

      this.triggerScrollPane.revalidate();
      this.triggerPanel.doLayout();
   }

   private void initComponents() {
      this.triggerHeaderPanel = new JPanel();
      this.triggerScrollPane = new JScrollPane();
      this.triggerPanel = new JPanel();
      this.jLabel1 = new JLabel();
      this.jLabel2 = new JLabel();
      this.jLabel3 = new JLabel();
      this.jCheckBox1 = new JCheckBox();
      this.jCheckBox2 = new JCheckBox();
      this.jPanel1 = new JPanel();
      this.jLabel4 = new JLabel();
      this.displayNameField = new JTextField();
      this.triggerHeaderPanel.setFocusable(false);
      this.triggerHeaderPanel.setMinimumSize(new Dimension(100, 20));
      this.triggerHeaderPanel.setLayout(null);
      this.setBorder(BorderFactory.createTitledBorder("Verdrahtung"));
      this.setLayout(new BorderLayout());
      this.triggerPanel.setLayout(new GridLayout(0, 5));
      this.jLabel1.setText("Bahnsteig");
      this.triggerPanel.add(this.jLabel1);
      this.jLabel2.setHorizontalAlignment(0);
      this.jLabel2.setText("40/68");
      this.triggerPanel.add(this.jLabel2);
      this.jLabel3.setText("1");
      this.triggerPanel.add(this.jLabel3);
      this.triggerPanel.add(this.jCheckBox1);
      this.triggerPanel.add(this.jCheckBox2);
      this.triggerScrollPane.setViewportView(this.triggerPanel);
      this.add(this.triggerScrollPane, "Center");
      this.jPanel1.setLayout(new BoxLayout(this.jPanel1, 2));
      this.jLabel4.setForeground(SystemColor.windowBorder);
      this.jLabel4.setText("verdrahte Auslöser mit Display ");
      this.jPanel1.add(this.jLabel4);
      this.displayNameField.setEditable(false);
      this.jPanel1.add(this.displayNameField);
      this.add(this.jPanel1, "North");
   }

   private class gleisLine implements Comparable, ActionListener {
      gleis gl;
      JLabel element;
      JLabel coords;
      JLabel swwert;
      JCheckBox connected;
      JCheckBox fsconnected = null;
      JLabel fsconnectedPlaceholder = null;
      connector set = null;
      final Timer fxTimer = new Timer(800, this);

      gleisLine(gleis g) {
         this.gl = g;
         this.element = new JLabel(gleisTypContainer.getInstance().getTypElementName(this.gl));
         this.element.setHorizontalAlignment(0);
         this.element.setOpaque(true);
         this.coords = new JLabel(this.gl.getCol() + "/" + this.gl.getRow());
         this.coords.setHorizontalAlignment(0);
         this.coords.setOpaque(true);
         this.swwert = new JLabel(this.gl.getSWWert());
         this.swwert.setHorizontalAlignment(0);
         this.swwert.setOpaque(true);
         this.connected = new JCheckBox();
         this.connected.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
               gleisLine.this.connected();
            }
         });
         if (this.gl.isDisplayFStrigger()) {
            this.fsconnected = new JCheckBox();
            this.fsconnected.setEnabled(this.gl.isDisplayFStrigger());
            this.fsconnected.addItemListener(new ItemListener() {
               public void itemStateChanged(ItemEvent evt) {
                  gleisLine.this.FSconnected();
               }
            });
         } else {
            this.fsconnectedPlaceholder = new JLabel();
         }

         this.fxTimer.setRepeats(false);
      }

      public void britzel() {
         Color bg = UIManager.getDefaults().getColor("Label.background");
         Color fg = UIManager.getDefaults().getColor("Label.foreground");
         this.swwert.setBackground(fg);
         this.swwert.setForeground(bg);
         this.fxTimer.restart();
      }

      public void actionPerformed(ActionEvent e) {
         this.updateBG();
      }

      private void updateBG() {
         Color bg = UIManager.getDefaults().getColor("Label.background");
         Color fg = UIManager.getDefaults().getColor("Label.foreground");
         if (this.set != null) {
            bg = UIManager.getDefaults().getColor("info");
            fg = UIManager.getDefaults().getColor("infoText");
         }

         this.element.setBackground(bg);
         this.element.setForeground(fg);
         this.coords.setBackground(bg);
         this.coords.setForeground(fg);
         this.swwert.setBackground(bg);
         this.swwert.setForeground(fg);
      }

      private void connected() {
         if (this.connected.isSelected() && this.set == null) {
            this.set = displayBarR.this.glbControl
               .getModel()
               .getDisplayBar()
               .addEntry(displayBarR.this.currentDisplay, this.gl, this.fsconnected != null ? this.fsconnected.isSelected() : false);
            if (this.set == null) {
               this.connected.setSelected(false);
            }

            displayBarR.this.updateOthers(this);
         } else if (!this.connected.isSelected() && this.set != null) {
            displayBarR.this.glbControl.getModel().getDisplayBar().delEntry(this.set);
            this.set = null;
            displayBarR.this.updateOthers(this);
         }

         if (this.fsconnected != null) {
            this.fsconnected.setEnabled(this.set != null);
         }

         this.updateGleisbildMark();
         this.updateBG();
      }

      private void FSconnected() {
         if (this.set != null && this.fsconnected != null) {
            this.set.setFSconnector(this.fsconnected.isSelected());
         }
      }

      void add(JPanel p) {
         p.add(this.swwert);
         p.add(this.element);
         p.add(this.coords);
         p.add(this.connected);
         if (this.fsconnected != null) {
            p.add(this.fsconnected);
         } else {
            p.add(this.fsconnectedPlaceholder);
         }
      }

      void setEnabled(boolean e) {
         this.element.setEnabled(e);
         this.coords.setEnabled(e);
         this.swwert.setEnabled(e);
         e &= !this.gl.getSWWert().isEmpty();
         this.connected.setEnabled(e);
         if (this.fsconnected != null) {
            this.fsconnected.setEnabled(e && this.set != null);
         }
      }

      void setConnector(connector c) {
         this.set = c;
         this.connected.setSelected(c != null);
         this.updateGleisbildMark();
         if (this.fsconnected != null) {
            this.fsconnected.setEnabled(c != null);
            this.fsconnected.setSelected(c != null && c.isFSconnector());
         }
      }

      void updateGleisbildMark() {
         if (this.connected.isSelected()) {
            displayBarR.this.glbControl.getModel().addMarkedGleis(this.gl);
         } else {
            displayBarR.this.glbControl.getModel().removeMarkedGleis(this.gl);
         }
      }

      public boolean equals(Object o) {
         displayBarR.gleisLine glne = (displayBarR.gleisLine)o;
         return this.gl.sameGleis(glne.gl);
      }

      public int hashCode() {
         int hash = 5;
         return 23 * hash + (this.gl != null ? this.gl.hashCode() : 0);
      }

      public int compareTo(Object o) {
         displayBarR.gleisLine glne = (displayBarR.gleisLine)o;
         int r = this.gl.getSWWert().compareToIgnoreCase(glne.gl.getSWWert());
         if (r == 0) {
            r = this.gl.compareToGleis(glne.gl);
         }

         return r;
      }
   }
}
