package js.java.isolate.sim.inserts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.colorSystem.gleisColor;
import js.java.isolate.sim.gleis.gleisElements.element;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.gleisbildModel;
import js.java.isolate.sim.inserts.inserttoken.inserttoken;
import js.java.isolate.sim.inserts.inserttoken.newlinetoken;
import js.java.isolate.sim.panels.actionevents.insertPanelPreviewUpdateEvent;
import js.java.isolate.sim.toolkit.ComboGleisRenderer;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.tools.ColorText;
import js.java.tools.gui.BoundedPlainDocument;
import js.java.tools.gui.NumberTextField;
import js.java.tools.gui.layout.AutoMultiColumnLayout;
import js.java.tools.gui.layout.SimpleOneColumnLayout;
import js.java.tools.gui.layout.ThinkingPanel;
import js.java.tools.gui.layout.AutoMultiColumnLayout.MaxWidth;
import js.java.tools.gui.renderer.ComboColorRenderer;

public abstract class insert extends JPanel implements SessionClose {
   private static final String PANELPATH = insert.class.getPackage().getName();
   protected final GleisAdapter my_main;
   protected final gleisbildModel glbModel;
   protected LinkedList<inserttoken> layout = null;
   protected final HashMap<String, String> storage = new HashMap();
   protected final gleisbildModel subModel;
   private int width = 0;
   private int height = 0;
   private int gy = 0;
   private int viewWidth = 0;

   public static Object loadClass(String type) {
      return loadClass(PANELPATH, type);
   }

   private static Object loadClass(String base, String type) {
      try {
         Class c = Class.forName(base + "." + type);
         return c.newInstance();
      } catch (IllegalAccessException | InstantiationException | ClassNotFoundException var5) {
         Logger.getLogger("stslogger").log(Level.SEVERE, "Caught", var5);
         return null;
      }
   }

   public insert(GleisAdapter m, gleisbildModel glb) {
      this.my_main = m;
      this.glbModel = glb;
      this.initComponents();
      this.layout = new LinkedList();
      this.subModel = new gleisbildModel(this.my_main);
   }

   @Override
   public void close() {
      this.subModel.close();
      this.layout.clear();
   }

   protected void setLayout(LinkedList<inserttoken> l) {
      this.layout = l;
      this.calcSize();
      this.initlist(true);
      this.subModel.gl_simpleresize(this.width + 3, this.height + 3);
      this.subModel.clear();
      this.paint(this.subModel, 1, 1, true);
      this.initlist(true);
   }

   protected void refreshPreview() {
      this.my_main.interPanelCom(new insertPanelPreviewUpdateEvent(this));
   }

   protected void add(String label, JComponent c) {
      JPanel p = new JPanel();
      p.setLayout(new SimpleOneColumnLayout());
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = this.gy;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.insets = new Insets(2, 0, 0, 0);
      this.add(p, gridBagConstraints);
      this.gy++;
      JLabel l = new JLabel(label);
      l.setForeground(UIManager.getDefaults().getColor("TitledBorder.titleColor"));
      p.add(l);
      p.add(c);
   }

   private void addInput(String label, final String name, final JTextField nt) {
      nt.addActionListener(new ActionListener() {
         String n = name;

         public void actionPerformed(ActionEvent e) {
            insert.this.action(this.n, (JComponent)e.getSource());
         }
      });
      nt.addFocusListener(new FocusListener() {
         String n = name;

         public void focusGained(FocusEvent e) {
         }

         public void focusLost(FocusEvent e) {
            insert.this.focuslost(this.n, (JComponent)e.getSource());
         }
      });
      nt.getDocument().addDocumentListener(new DocumentListener() {
         JTextField tf = nt;
         String n = name;

         public void insertUpdate(DocumentEvent e) {
            insert.this.textchange(this.n, this.tf);
         }

         public void removeUpdate(DocumentEvent e) {
            insert.this.textchange(this.n, this.tf);
         }

         public void changedUpdate(DocumentEvent e) {
            insert.this.textchange(this.n, this.tf);
         }
      });
      this.add(label, nt);
   }

   private void calcSize() {
      this.width = 1;
      this.height = 0;
      int w = 0;

      for (inserttoken t : this.layout) {
         if (t instanceof newlinetoken) {
            this.height++;
            this.width = Math.max(this.width, w);
            w = 0;
         } else if (t.isElement()) {
            w++;
         }
      }

      this.height++;
      this.width = Math.max(this.width, w);
   }

   protected final NumberTextField addIntInput(String label, String name, int def) {
      NumberTextField n = new NumberTextField();
      n.setColumns(4);
      n.setInt(def);
      this.addInput(label, name, n);
      return n;
   }

   protected final JTextField addTextInput(String label, String name, String def) {
      JTextField n = new JTextField();
      n.setDocument(new BoundedPlainDocument(30));
      n.setColumns(40);
      n.setText(def);
      this.addInput(label, name, n);
      return n;
   }

   protected final JCheckBox addBoolInput(String label, final String name, boolean def) {
      JCheckBox n = new JCheckBox();
      n.setSelected(def);
      n.setText(label);
      n.addActionListener(new ActionListener() {
         String n = name;

         public void actionPerformed(ActionEvent e) {
            insert.this.action(this.n, (JComponent)e.getSource());
         }
      });
      this.add("", n);
      return n;
   }

   protected final JToggleButton[] addMXInput(String label, int selected, String... names) {
      ButtonGroup bg2 = new ButtonGroup();
      if (names.length % 2 != 0) {
         return null;
      } else {
         JToggleButton[] ret = new JToggleButton[names.length / 2];
         JPanel pan = new ThinkingPanel();
         pan.setLayout(new AutoMultiColumnLayout(new MaxWidth()));

         for (int i = 0; i < names.length; i += 2) {
            String blabel = names[i];
            final String key = names[i + 1];
            JToggleButton r = new JRadioButton(blabel);
            bg2.add(r);
            r.setSelected(selected == i / 2);
            r.setFocusable(false);
            r.addItemListener(new ItemListener() {
               String n = key;

               public void itemStateChanged(ItemEvent e) {
                  insert.this.action(this.n, (JComponent)e.getSource());
               }
            });
            pan.add(r);
            ret[i / 2] = r;
         }

         this.add(label, pan);
         return ret;
      }
   }

   protected final JToggleButton[] addMXInput(String label, int selected, List<String> names) {
      ButtonGroup bg2 = new ButtonGroup();
      if (names.size() % 2 != 0) {
         return null;
      } else {
         JToggleButton[] ret = new JToggleButton[names.size() / 2];
         JPanel pan = new JPanel();
         pan.setLayout(new AutoMultiColumnLayout(new MaxWidth()));

         for (int i = 0; i < names.size(); i += 2) {
            String blabel = (String)names.get(i);
            final String key = (String)names.get(i);
            JToggleButton r = new JRadioButton(blabel);
            bg2.add(r);
            r.setSelected(selected == i / 2);
            r.setFocusable(false);
            r.addItemListener(new ItemListener() {
               String n = key;

               public void itemStateChanged(ItemEvent e) {
                  insert.this.action(this.n, (JComponent)e.getSource());
               }
            });
            pan.add(r);
            ret[i / 2] = r;
         }

         this.add(label, pan);
         return ret;
      }
   }

   protected final JComboBox addENRInput(String label, final String name, element element) {
      JComboBox n = new JComboBox();
      n.setRenderer(new ComboGleisRenderer());
      n.setEditable(false);
      TreeMap<Integer, gleis> allenrs = new TreeMap();
      Iterator<gleis> it = this.glbModel.findIterator(element);

      while (it.hasNext()) {
         gleis gl = (gleis)it.next();
         allenrs.put(gl.getENR(), gl);
      }

      for (Integer i : allenrs.keySet()) {
         n.addItem(allenrs.get(i));
      }

      n.addItemListener(new ItemListener() {
         String n = name;

         public void itemStateChanged(ItemEvent e) {
            insert.this.action(this.n, (JComponent)e.getSource());
         }
      });
      this.add(label, n);
      return n;
   }

   protected final JComboBox addENRInput(String label, final String name, LinkedList<gleis> lg) {
      JComboBox n = new JComboBox();
      n.setRenderer(new ComboGleisRenderer());
      n.setEditable(false);
      TreeMap<Integer, gleis> allenrs = new TreeMap();

      for (gleis gl : lg) {
         if (gl.typRequiresENR() && gl.getENR() > 0) {
            allenrs.put(gl.getENR(), gl);
         }
      }

      for (Integer i : allenrs.keySet()) {
         n.addItem(allenrs.get(i));
      }

      n.addItemListener(new ItemListener() {
         String n = name;

         public void itemStateChanged(ItemEvent e) {
            insert.this.action(this.n, (JComponent)e.getSource());
         }
      });
      this.add(label, n);
      return n;
   }

   protected final JComboBox addColorInput(String label, final String name, String def) {
      JComboBox n = new JComboBox();
      n.setRenderer(new ComboColorRenderer());
      n.setEditable(false);
      TreeMap<String, Color> cols = gleisColor.getInstance().getBGcolors();

      for (String k : cols.keySet()) {
         ColorText c = new ColorText(k, (Color)cols.get(k));
         n.addItem(c);
         if (k.equalsIgnoreCase(def)) {
            n.setSelectedItem(c);
         }
      }

      n.addItemListener(new ItemListener() {
         String n = name;

         public void itemStateChanged(ItemEvent e) {
            insert.this.action(this.n, (JComponent)e.getSource());
         }
      });
      this.add(label, n);
      return n;
   }

   protected boolean isleftright(gleisbildModel glb, int x) {
      return x > glb.getGleisWidth() / 2;
   }

   public abstract String getName();

   public abstract String getVersion();

   protected abstract void initVariables(boolean var1);

   protected void initInterface() {
   }

   public final void initInsert() {
      this.removeAll();
      this.initInterface();
      JSeparator sep = new JSeparator(0);
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = this.gy;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.gridwidth = 2;
      this.add(sep, gridBagConstraints);
      this.gy++;
      this.refreshPreview();
   }

   protected int getXOffset() {
      return 0;
   }

   protected int getYOffset() {
      return 0;
   }

   protected void action(String name, JComponent source) {
   }

   protected void focuslost(String name, JComponent source) {
   }

   protected void textchange(String name, JComponent source) {
   }

   public gleisbildModel getModel() {
      return this.subModel;
   }

   public void paint(gleisbildModel glb, int x, int y) {
      this.initlist(this.isleftright(glb, x));
      this.paint(glb, x, y, false);
      glb.repaint();
   }

   private void initlist(boolean leftright) {
      this.initVariables(leftright);

      for (inserttoken t : this.layout) {
         t.init();
      }
   }

   private void paint(gleisbildModel glb, int x, int y, boolean demo) {
      int xm = 1;
      if (this.isleftright(glb, x)) {
         xm = -1;
      }

      if (!demo) {
         x -= this.getXOffset() * xm;
         y -= this.getYOffset();
      }

      int cx = x;
      int cy = y;

      for (inserttoken t : this.layout) {
         if (t instanceof newlinetoken) {
            cx = x;
            cy++;
         } else {
            gleis gl = glb.getXY(cx, cy);

            try {
               t.work(gl, this.storage, demo, xm == 1);
            } catch (Exception var12) {
               this.my_main.showStatus("Exception: " + var12.getMessage(), 4);
               Logger.getLogger("stslogger").log(Level.SEVERE, "Caught", var12);
            }

            if (t.isElement()) {
               cx += xm;
            }

            if (t.isVisible()) {
               glb.changeC(1);
            }
         }
      }
   }

   public LinkedList<Point> getCoords(gleisbildModel glb, int x, int y) {
      LinkedList<Point> lp = new LinkedList();
      int xm = 1;
      if (this.isleftright(glb, x)) {
         xm = -1;
      }

      x -= this.getXOffset() * xm;
      y -= this.getYOffset();
      int cx = x;
      int cy = y;

      for (inserttoken t : this.layout) {
         if (t instanceof newlinetoken) {
            cx = x;
            cy++;
         } else {
            if (t.isVisible()) {
               lp.add(new Point(cx, cy));
            }

            if (t.isElement()) {
               cx += xm;
            }
         }
      }

      return lp;
   }

   public int getGleisWidth() {
      return this.width;
   }

   public int getGleisHeight() {
      return this.height;
   }

   public String toString() {
      return this.getName();
   }

   public static gleisElements.RICHTUNG calcRichtung(gleisElements.RICHTUNG r, boolean leftright) {
      if (leftright) {
         return r;
      } else if (r == gleisElements.RICHTUNG.right) {
         return gleisElements.RICHTUNG.left;
      } else {
         return r == gleisElements.RICHTUNG.left ? gleisElements.RICHTUNG.right : r;
      }
   }

   public void setViewWidth(int w) {
      this.viewWidth = w;
   }

   public Dimension getPreferredSize() {
      Dimension d = super.getPreferredSize();
      if (this.viewWidth > 0) {
         d.width = Math.min(d.width, this.viewWidth);
      }

      return d;
   }

   private void initComponents() {
      this.setLayout(new GridBagLayout());
   }
}
