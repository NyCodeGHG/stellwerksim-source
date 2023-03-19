package js.java.isolate.sim.eventsys;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.JSpinner.NumberEditor;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelEventsys;
import js.java.isolate.sim.toolkit.MXbuttonGroup;
import js.java.isolate.sim.toolkit.menuBorder;
import js.java.tools.gui.layout.ColumnLayout;

public abstract class eventFactory extends JPanel {
   protected gleisbildModelEventsys glbModel;
   private final JPanel cardpanel;
   private final HashMap<String, eventFactory.cardData> cards = new HashMap();
   private final LinkedList<eventFactory.cardData> cardtitles = new LinkedList();
   private JTextField namefield;
   private final JRadioButton[] wahrscheinlichkeit = new JRadioButton[eventHaeufigkeiten.HAEUFIGKEITEN.values().length];
   private ButtonGroup wahrscheinlichkeitsgroup;
   private final HashMap<thema, JCheckBox> themaboxesON = new HashMap();
   private final HashMap<thema, JCheckBox> themaboxesOFF = new HashMap();
   private JSpinner gewichtSpinner;
   private SpinnerNumberModel gewichtModel;

   protected eventFactory() {
      super();
      this.cardpanel = new JPanel();
      this.cardpanel.setLayout(new CardLayout());
   }

   public abstract String getName();

   public abstract Class<? extends event> getEventTyp();

   public boolean serverEvent(eventContainer ev, gleisbildModelEventsys glb, String parameter) {
      return false;
   }

   protected abstract void initGui();

   public abstract String getDescription();

   public String getTyp() {
      return this.getEventTyp().getSimpleName();
   }

   public void setGleis(gleis gl) {
   }

   public boolean isRandom() {
      return true;
   }

   public boolean isTheme() {
      return true;
   }

   public boolean equals(Object o) {
      if (o instanceof eventFactory) {
         return this.getEventTyp() == ((eventFactory)o).getEventTyp();
      } else {
         return false;
      }
   }

   protected final void add(String card, String label, JComponent c, boolean above) {
      eventFactory.cardData cd = (eventFactory.cardData)this.cards.get(card);
      if (cd == null) {
         cd = new eventFactory.cardData();
         cd.name = card;
         cd.p = new JPanel();
         cd.p.setLayout(new GridBagLayout());
         cd.sc = new JScrollPane();
         cd.sc.setViewportView(cd.p);
         cd.sc.setBorder(null);
         this.cardpanel.add(cd.sc, card);
         this.cards.put(card, cd);
         this.cardtitles.add(cd);
         menuBorder m = new menuBorder(card);
         m.setFillBackground(false);
         cd.p.setBorder(m);
      }

      if (label != null) {
         if (cd.gy > 0) {
            JLabel l = new JLabel(" ");
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = cd.gy;
            gridBagConstraints.fill = 2;
            gridBagConstraints.anchor = 11;
            gridBagConstraints.weightx = 0.0;
            gridBagConstraints.gridwidth = 2;
            cd.p.add(l, gridBagConstraints);
            ++cd.gy;
         }

         JLabel l = new JLabel(label);
         l.setForeground(UIManager.getDefaults().getColor("TitledBorder.titleColor"));
         GridBagConstraints gridBagConstraints = new GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = cd.gy;
         gridBagConstraints.fill = 2;
         gridBagConstraints.anchor = above ? 11 : 10;
         gridBagConstraints.weightx = 0.0;
         gridBagConstraints.gridwidth = 1;
         gridBagConstraints.insets = new Insets(0, 0, 2, 4);
         cd.p.add(l, gridBagConstraints);
         if (above) {
            ++cd.gy;
         }
      }

      if (c != null) {
         GridBagConstraints gridBagConstraints = new GridBagConstraints();
         gridBagConstraints.gridx = above ? 0 : 1;
         gridBagConstraints.gridy = cd.gy;
         gridBagConstraints.fill = 2;
         gridBagConstraints.anchor = 11;
         gridBagConstraints.weightx = 1.0;
         gridBagConstraints.gridwidth = above ? 2 : 1;
         cd.p.add(c, gridBagConstraints);
         ++cd.gy;
      }
   }

   public final void init(gleisbildModelEventsys _my_gleisbild) {
      this.glbModel = _my_gleisbild;
   }

   public final void initGui(gleisbildModelEventsys _my_gleisbild) {
      this.glbModel = _my_gleisbild;
      this.setLayout(new GridBagLayout());
      JLabel l = new JLabel("<html><b>" + this.getName() + "</b>: " + this.getDescription() + "</html>");
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.gridwidth = 1;
      gridBagConstraints.ipady = 5;
      this.add(l, gridBagConstraints);
      JSeparator sep = new JSeparator(0);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.gridwidth = 1;
      this.add(sep, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weighty = 1.0;
      gridBagConstraints.gridwidth = 1;
      this.add(this.cardpanel, gridBagConstraints);
      this.namefield = new JTextField();
      this.namefield.setToolTipText("Ein beschreibender Name für den Erbauer.");
      this.add("Basisdaten", "Name", this.namefield, false);
      if (this.isRandom()) {
         JPanel p = new JPanel();
         p.setLayout(new ColumnLayout(2));
         this.wahrscheinlichkeitsgroup = new ButtonGroup();
         int i = 0;

         for(eventHaeufigkeiten.HAEUFIGKEITEN h : eventHaeufigkeiten.HAEUFIGKEITEN.values()) {
            this.wahrscheinlichkeit[i] = new JRadioButton(eventHaeufigkeiten.create(this.glbModel).get(h));
            this.wahrscheinlichkeit[i].setActionCommand(h.toString());
            this.wahrscheinlichkeit[i].setMargin(new Insets(0, 0, 0, 0));
            this.wahrscheinlichkeit[i].setFocusPainted(false);
            this.wahrscheinlichkeitsgroup.add(this.wahrscheinlichkeit[i]);
            if (i == 0) {
               this.wahrscheinlichkeit[i].setSelected(true);
            }

            p.add(this.wahrscheinlichkeit[i]);
            ++i;
         }

         this.add("Basisdaten", "Häufigkeit", p, false);
         this.gewichtModel = new SpinnerNumberModel(1, 1, 10, 1);
         this.gewichtSpinner = new JSpinner(this.gewichtModel);
         this.gewichtSpinner.setEditor(new NumberEditor(this.gewichtSpinner, "0"));
         this.gewichtSpinner
            .setToolTipText(
               "<html>Innerhalb einer Häufigkeit sind alle Störungen gleichberechtigt.<br>Die Gewichtung ändert die Chance für eine einzelne Störung.</html>"
            );
         this.add("Basisdaten", "Gewichtung", this.gewichtSpinner, false);
      }

      this.initGui();
      if (this.isTheme()) {
         JPanel p = new JPanel();
         p.setLayout(new BoxLayout(p, 1));
         p.setLayout(new GridBagLayout());
         int yy = 0;

         for(thema t : thema.themen) {
            MXbuttonGroup bg = new MXbuttonGroup();
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = yy;
            gridBagConstraints.weightx = 0.0;
            gridBagConstraints.gridwidth = 1;
            JCheckBox cb = new JCheckBox();
            cb.setEnabled(false);
            cb.setToolTipText("nur für dieses Thema");
            cb.setBackground(Color.GREEN);
            p.add(cb, gridBagConstraints);
            this.themaboxesON.put(t, cb);
            bg.add(cb);
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = yy;
            gridBagConstraints.weightx = 0.0;
            gridBagConstraints.gridwidth = 1;
            cb = new JCheckBox();
            cb.setEnabled(false);
            cb.setToolTipText("nicht bei diesem Thema");
            cb.setBackground(Color.RED);
            p.add(cb, gridBagConstraints);
            this.themaboxesOFF.put(t, cb);
            bg.add(cb);
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = yy;
            gridBagConstraints.weightx = 0.0;
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.anchor = 17;
            l = new JLabel(" " + t.guiname + " ");
            p.add(l, gridBagConstraints);
            ++yy;
         }

         gridBagConstraints = new GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = yy;
         gridBagConstraints.weightx = 1.0;
         gridBagConstraints.gridwidth = 3;
         gridBagConstraints.fill = 2;
         sep = new JSeparator(0);
         p.add(sep, gridBagConstraints);
         ++yy;
         gridBagConstraints = new GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = yy;
         gridBagConstraints.weightx = 1.0;
         gridBagConstraints.gridwidth = 3;
         gridBagConstraints.fill = 2;
         l = new JLabel("<html>Ausschluss hat Vorrang vor Einschluss.</html>");
         p.add(l, gridBagConstraints);
         ++yy;
         this.add("Themen", null, p, true);
      }

      for(eventFactory.cardData cd : this.cards.values()) {
         gridBagConstraints = new GridBagConstraints();
         gridBagConstraints.gridx = 0;
         gridBagConstraints.gridy = cd.gy;
         gridBagConstraints.weighty = 1.0;
         cd.p.add(Box.createVerticalGlue(), gridBagConstraints);
      }
   }

   public static final int random(int min, int max) {
      return (int)(Math.random() * (double)(max - min) + (double)min);
   }

   public Iterator getCards() {
      return this.cardtitles.iterator();
   }

   public void showCard(Object o) {
      if (o instanceof eventFactory.cardData) {
         eventFactory.cardData cd = (eventFactory.cardData)o;
         ((CardLayout)this.cardpanel.getLayout()).show(this.cardpanel, cd.name);
      }
   }

   public void showContainer(eventContainer ev, boolean editmode) {
      this.glbModel.clearMarkedGleis();
      this.namefield.setText(ev.getName());
      this.namefield.setEnabled(editmode);
      if (this.isRandom()) {
         eventHaeufigkeiten.HAEUFIGKEITEN v = eventHaeufigkeiten.fromString(ev.getValue("occurrence"));
         this.wahrscheinlichkeit[eventHaeufigkeiten.value2int(v)].setSelected(true);

         for(int i = 0; i < this.wahrscheinlichkeit.length; ++i) {
            this.wahrscheinlichkeit[i].setEnabled(editmode);
         }

         this.gewichtModel.setValue(ev.getIntValue("weight", 1));
         this.gewichtSpinner.setEnabled(editmode);
      }

      if (this.isTheme()) {
         Set<String> thoff = ev.getThemeList(true);
         Set<String> thon = ev.getThemeList(false);

         for(thema t : this.themaboxesOFF.keySet()) {
            JCheckBox cb = (JCheckBox)this.themaboxesOFF.get(t);
            cb.setEnabled(editmode);
            cb.setSelected(thoff.contains(t.name));
         }

         for(thema t : this.themaboxesON.keySet()) {
            JCheckBox cb = (JCheckBox)this.themaboxesON.get(t);
            cb.setEnabled(editmode);
            cb.setSelected(thon.contains(t.name));
         }
      }
   }

   public void readContainer(eventContainer ev) {
      ev.setFactory(this);
      ev.setName(this.namefield.getText());
      if (this.isRandom()) {
         eventHaeufigkeiten.HAEUFIGKEITEN v = eventHaeufigkeiten.fromString(this.wahrscheinlichkeitsgroup.getSelection().getActionCommand());
         ev.setValue("occurrence", v.toString());
         this.gewichtSpinner.getValue();
         ev.setLongValue("weight", (long)this.gewichtModel.getNumber().intValue());
      }

      if (this.isTheme()) {
         HashSet<String> thoff = new HashSet();
         HashSet<String> thon = new HashSet();

         for(thema t : this.themaboxesOFF.keySet()) {
            JCheckBox cb = (JCheckBox)this.themaboxesOFF.get(t);
            if (cb.isSelected()) {
               thoff.add(t.name);
            }
         }

         for(thema t : this.themaboxesON.keySet()) {
            JCheckBox cb = (JCheckBox)this.themaboxesON.get(t);
            if (cb.isSelected()) {
               thon.add(t.name);
            }
         }

         ev.setThemeList(thoff, true);
         ev.setThemeList(thon, false);
      }
   }

   public eventHaeufigkeiten.HAEUFIGKEITEN getOccurrence(eventContainer ev) {
      return eventHaeufigkeiten.fromString(ev.getValue("occurrence"));
   }

   public int getWeight(eventContainer ev) {
      return ev.getIntValue("weight", 1);
   }

   public boolean isIndependantEvent() {
      return false;
   }

   public boolean isStopFollowing() {
      return false;
   }

   class cardData {
      JPanel p;
      JScrollPane sc;
      int gy = 0;
      String name;

      cardData() {
         super();
      }

      public String toString() {
         return this.name;
      }
   }
}
