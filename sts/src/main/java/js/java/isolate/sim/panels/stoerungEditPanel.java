package js.java.isolate.sim.panels;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.eventsys.anrufbueevent;
import js.java.isolate.sim.eventsys.bueevent;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventFactory;
import js.java.isolate.sim.eventsys.signalevent;
import js.java.isolate.sim.eventsys.weicheevent;
import js.java.isolate.sim.eventsys.events.bahnueberganganruf_factory;
import js.java.isolate.sim.eventsys.events.displayausfall_factory;
import js.java.isolate.sim.eventsys.events.fsspeicherstoerung_factory;
import js.java.isolate.sim.eventsys.events.randomsignalstoerung_factory;
import js.java.isolate.sim.eventsys.events.randomweichestoerung_factory;
import js.java.isolate.sim.eventsys.events.relaisgruppestoerung_factory;
import js.java.isolate.sim.eventsys.events.sicherungausfall_factory;
import js.java.isolate.sim.eventsys.events.signalausfall_factory;
import js.java.isolate.sim.eventsys.events.signalled_factory;
import js.java.isolate.sim.eventsys.events.signalmeldung_factory;
import js.java.isolate.sim.eventsys.events.signalstoerung_factory;
import js.java.isolate.sim.eventsys.events.sperreelemente_factory;
import js.java.isolate.sim.eventsys.events.sperreelementeaufzeit_factory;
import js.java.isolate.sim.eventsys.events.stellwerkausfall_factory;
import js.java.isolate.sim.eventsys.events.weichenausfall_factory;
import js.java.isolate.sim.eventsys.events.weichenfsstoerung_factory;
import js.java.isolate.sim.eventsys.events.zugAbfahrtStoerung_factory;
import js.java.isolate.sim.eventsys.events.zugFluegelStoerung_factory;
import js.java.isolate.sim.eventsys.events.zugGruenStoerung_factory;
import js.java.isolate.sim.eventsys.events.zugKuppelStoerung_factory;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.element;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.gecWorker.GecSelectEvent;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;
import js.java.isolate.sim.panels.actionevents.stoerungSelectedEvent;
import js.java.isolate.sim.toolkit.eventMenuRenderer;
import js.java.isolate.sim.toolkit.menuBorder;
import js.java.isolate.sim.toolkit.specialEntry;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.tools.actions.AbstractEvent;
import js.java.tools.gui.ViewTooltips;
import js.java.tools.gui.layout.CompactLayout;

public class stoerungEditPanel extends basePanel implements SessionClose {
   TreeMap<String, Box> ef_groups = new TreeMap();
   private final stoerungEditPanel.ef_item[] stlist = new stoerungEditPanel.ef_item[]{
      new stoerungEditPanel.ef_item(new randomsignalstoerung_factory(), "Gleis unspezifisch"),
      new stoerungEditPanel.ef_item(new randomweichestoerung_factory(), "Gleis unspezifisch"),
      new stoerungEditPanel.ef_item(new signalstoerung_factory(), "Gleis"),
      new stoerungEditPanel.ef_item(new signalmeldung_factory(), "Gleis"),
      new stoerungEditPanel.ef_item(new signalausfall_factory(), "Gleis"),
      new stoerungEditPanel.ef_item(new signalled_factory(), "Gleis"),
      new stoerungEditPanel.ef_item(new weichenausfall_factory(), "Gleis"),
      new stoerungEditPanel.ef_item(new bahnueberganganruf_factory(), "Gleis - Ereignis"),
      new stoerungEditPanel.ef_item(new sperreelemente_factory(), "Mehrfachgleis"),
      new stoerungEditPanel.ef_item(new sperreelementeaufzeit_factory(), "Mehrfachgleis"),
      new stoerungEditPanel.ef_item(new weichenfsstoerung_factory(), "Stellwerksweit"),
      new stoerungEditPanel.ef_item(new fsspeicherstoerung_factory(), "Stellwerksweit"),
      new stoerungEditPanel.ef_item(new relaisgruppestoerung_factory(), "Stellwerksweit"),
      new stoerungEditPanel.ef_item(new stellwerkausfall_factory(), "Stellwerksweit"),
      new stoerungEditPanel.ef_item(new sicherungausfall_factory(), "Stellwerksweit"),
      new stoerungEditPanel.ef_item(new displayausfall_factory(), "Stellwerksweit"),
      new stoerungEditPanel.ef_item(new zugAbfahrtStoerung_factory(), "Zug"),
      new stoerungEditPanel.ef_item(new zugKuppelStoerung_factory(), "Zug"),
      new stoerungEditPanel.ef_item(new zugFluegelStoerung_factory(), "Zug"),
      new stoerungEditPanel.ef_item(new zugGruenStoerung_factory(), "Zug")
   };
   private JToggleButton[] stbuts;
   private boolean editMode = false;
   private eventContainer lastec = null;
   private final DefaultListModel cmodel = new DefaultListModel();
   private boolean firstVisit = true;
   private eventFactory selectedF = null;
   private JList cardList;
   private JPanel dataPanel;
   private JPanel eventList;
   private ButtonGroup eventListBG;
   private JScrollPane eventScrollPane;
   private JScrollPane parameterPane;
   private JScrollPane typPane;

   public stoerungEditPanel(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
      this.initComponents();
      ViewTooltips.register(this.cardList);
      this.initMyComponents();
      e.registerListener(10, this);
   }

   @Override
   public void close() {
      this.ef_groups.clear();
      this.lastec = null;
      this.cmodel.clear();
   }

   private void init() {
      if (this.firstVisit) {
         for(stoerungEditPanel.ef_item f : this.stlist) {
            f.ef.initGui(this.glbControl.getModel());
         }

         this.firstVisit = false;
      }
   }

   @Override
   public void action(AbstractEvent e) {
      if (!this.firstVisit) {
         try {
            if (e instanceof GecSelectEvent) {
               if (this.editMode) {
                  this.glbControl.setFocus(null);
                  this.selectedTypFilter();
                  if (this.selectedF != null) {
                     this.selectedF.setGleis(this.glbControl.getSelectedGleis());
                     this.glbControl.setFocus(this.glbControl.getSelectedGleis());
                  }
               } else {
                  this.eventListBG.clearSelection();
               }
            } else if (e instanceof stoerungSelectedEvent) {
               stoerungSelectedEvent se = (stoerungSelectedEvent)e;
               eventContainer ev = se.getEvent();
               this.lastec = ev;
               this.editMode = se.isSelected();
               this.selectedTypFilter();
               if (ev != null) {
                  if (se.isUpdate()) {
                     this.getSelectedE(ev);
                  } else {
                     gleis gl = ev.getGleis();
                     this.glbControl.setSelectedGleis(gl);
                     this.glbControl.setFocus(gl);
                     this.selectedTypFilter();
                  }

                  if (ev.getFactory() != null) {
                     for(int i = 0; i < this.stlist.length; ++i) {
                        if (this.stlist[i].ef.getClass() == ev.getFactory().getClass()) {
                           this.stbuts[i].setSelected(true);
                           this.eventTypSelected(i);
                           this.setSelectedE(ev);
                           break;
                        }
                     }
                  } else {
                     this.selectedF = null;
                     this.eventListBG.clearSelection();
                     this.updateMenu();
                  }
               } else {
                  this.parameterPane.setViewportView(null);
                  this.selectedF = null;
                  this.eventListBG.clearSelection();
               }

               this.glbControl.repaint();
            }
         } catch (Exception var5) {
            System.out.println("Caught ex: " + var5.getMessage());
            Logger.getLogger("stslogger").log(Level.SEVERE, "Caught", var5);
         }
      }
   }

   @Override
   public void shown(String n, gecBase gec) {
      this.init();
      this.updateMenu();
      gec.addChangeListener(this);
   }

   private void initMyComponents() {
      this.stbuts = new JToggleButton[this.stlist.length];
      int i = 0;

      for(stoerungEditPanel.ef_item f : this.stlist) {
         Box pan;
         if (this.ef_groups.containsKey(f.group)) {
            pan = (Box)this.ef_groups.get(f.group);
         } else {
            pan = new Box(1);
            this.ef_groups.put(f.group, pan);
            menuBorder b = new menuBorder(f.group);
            b.setFillBackground(false);
            pan.setBorder(b);
         }

         JToggleButton b = new JRadioButton(f.ef.getName());
         this.eventListBG.add(b);
         this.stbuts[i] = b;
         b.setActionCommand(i + "");
         b.setEnabled(false);
         b.setToolTipText(f.ef.getDescription());
         b.setFocusPainted(false);
         b.setMargin(new Insets(0, 0, 0, 0));
         ++i;
         b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               int a = Integer.parseInt(e.getActionCommand());
               stoerungEditPanel.this.getSelectedE(stoerungEditPanel.this.lastec);
               stoerungEditPanel.this.eventTypSelected(a);
               stoerungEditPanel.this.setSelectedE(stoerungEditPanel.this.lastec);
            }
         });
         int addpos = -1;

         for(int c = 0; c < pan.getComponentCount(); ++c) {
            JToggleButton bb = (JToggleButton)pan.getComponent(c);
            if (bb.getText().compareToIgnoreCase(b.getText()) > 0) {
               addpos = c;
               break;
            }
         }

         if (addpos >= 0) {
            pan.add(b, addpos);
         } else {
            pan.add(b);
         }
      }

      for(Box pan : this.ef_groups.values()) {
         this.eventList.add(pan);
      }

      this.cardList.setModel(this.cmodel);
      this.updateMenu();
   }

   private void updateMenu() {
      this.cmodel.clear();
      this.cmodel.addElement(new specialEntry("Störungstyp"));
      if (this.selectedF != null) {
         Iterator it = this.selectedF.getCards();

         while(it.hasNext()) {
            Object o = it.next();
            this.cmodel.addElement(o);
         }

         this.cardList.setSelectedIndex(1);
      } else {
         this.cardList.setSelectedIndex(0);
      }
   }

   private void selectedTyp(eventContainer ev) {
      for(int i = 0; i < this.stlist.length; ++i) {
         if (ev.getFactory().getClass() == this.stlist[i].ef.getClass()) {
            this.stbuts[i].setSelected(true);
            this.updateMenu();
            break;
         }
      }
   }

   private void selectedTypFilter() {
      gleis gl = this.glbControl.getSelectedGleis();
      element e = gleis.ELEMENT_LEER;
      if (gl != null) {
         e = gl.getElement();
      }

      boolean searchEna = false;

      for(int i = 0; i < this.stlist.length; ++i) {
         boolean ena = false;
         if (signalevent.class.isAssignableFrom(this.stlist[i].ef.getEventTyp())) {
            if (e == gleis.ELEMENT_SIGNAL) {
               ena = true;
            }
         } else if (weicheevent.class.isAssignableFrom(this.stlist[i].ef.getEventTyp())) {
            if (e == gleis.ELEMENT_WEICHEOBEN || e == gleis.ELEMENT_WEICHEUNTEN) {
               ena = true;
            }
         } else if (bueevent.class.isAssignableFrom(this.stlist[i].ef.getEventTyp())) {
            if (e == gleis.ELEMENT_BAHNÜBERGANG || e == gleis.ELEMENT_ANRUFÜBERGANG) {
               ena = true;
            }
         } else if (anrufbueevent.class.isAssignableFrom(this.stlist[i].ef.getEventTyp())) {
            if (e == gleis.ELEMENT_ANRUFÜBERGANG) {
               ena = true;
            }
         } else {
            ena = true;
         }

         this.stbuts[i].setEnabled(ena && this.editMode);
         if (this.editMode && !ena && this.stbuts[i].isSelected()) {
            searchEna = true;
         }
      }

      if (searchEna) {
         for(int i = 2; i < this.stlist.length; ++i) {
            if (this.stbuts[i].isEnabled()) {
               this.stbuts[i].setSelected(true);
               this.getSelectedE(this.lastec);
               this.eventTypSelected(i);
               this.setSelectedE(this.lastec);
               this.cardList.setSelectedIndex(0);
               break;
            }
         }
      }
   }

   private void eventTypSelected(int a) {
      eventFactory oldSelected = this.selectedF;
      this.selectedF = this.stlist[a].ef;
      this.parameterPane.setViewportView(this.selectedF);
      Dimension d1 = this.selectedF.getMinimumSize();
      Dimension d2 = this.parameterPane.getViewport().getSize();
      d1.width = d2.width;
      d1.height = d2.height;
      this.selectedF.setPreferredSize(d1);
      this.selectedF.revalidate();
      if (oldSelected != this.selectedF) {
         this.updateMenu();
      }
   }

   private void setSelectedE(eventContainer ev) {
      if (this.selectedF != null && ev != null) {
         this.selectedF.showContainer(ev, this.editMode);
      }
   }

   private void getSelectedE(eventContainer ev) {
      if (this.selectedF != null && ev != null) {
         ev.setGleis(this.glbControl.getSelectedGleis());
         this.selectedF.readContainer(ev);
      }
   }

   private void initComponents() {
      this.eventListBG = new ButtonGroup();
      this.eventScrollPane = new JScrollPane();
      this.cardList = new JList();
      this.dataPanel = new JPanel();
      this.typPane = new JScrollPane();
      this.eventList = new JPanel();
      this.parameterPane = new JScrollPane();
      this.setBorder(BorderFactory.createTitledBorder("Störung bearbeiten"));
      this.setLayout(new BorderLayout(5, 0));
      this.eventScrollPane.setBorder(null);
      this.cardList.setSelectionMode(0);
      this.cardList.setCellRenderer(new eventMenuRenderer());
      this.cardList.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent evt) {
            stoerungEditPanel.this.cardListValueChanged(evt);
         }
      });
      this.eventScrollPane.setViewportView(this.cardList);
      this.add(this.eventScrollPane, "West");
      this.dataPanel.setLayout(new CardLayout());
      this.typPane.setBorder(null);
      this.eventList.setLayout(null);
      this.eventList.setLayout(new CompactLayout(2));
      this.typPane.setViewportView(this.eventList);
      this.dataPanel.add(this.typPane, "typ");
      this.parameterPane.setBorder(null);
      this.dataPanel.add(this.parameterPane, "parameter");
      this.add(this.dataPanel, "Center");
   }

   private void cardListValueChanged(ListSelectionEvent evt) {
      if (this.cardList.getSelectedIndex() == 0) {
         ((CardLayout)this.dataPanel.getLayout()).show(this.dataPanel, "typ");
      } else {
         ((CardLayout)this.dataPanel.getLayout()).show(this.dataPanel, "parameter");
         Object s = this.cardList.getSelectedValue();
         if (this.selectedF != null) {
            Dimension d1 = this.selectedF.getMinimumSize();
            Dimension d2 = this.parameterPane.getViewport().getSize();
            d1.width = d2.width;
            d1.height = d2.height;
            this.selectedF.setPreferredSize(d1);
            this.selectedF.revalidate();
            this.selectedF.invalidate();
            this.selectedF.showCard(s);
         }
      }
   }

   private class ef_item {
      eventFactory ef;
      String group;

      ef_item(eventFactory _ef, String _group) {
         super();
         this.ef = _ef;
         this.group = _group;
      }
   }

   private class togglesort implements Comparable {
      JToggleButton b;

      togglesort(JToggleButton _b) {
         super();
         this.b = _b;
      }

      public int compareTo(Object o) {
         stoerungEditPanel.togglesort b2 = (stoerungEditPanel.togglesort)o;
         return this.b.getText().compareToIgnoreCase(b2.b.getText());
      }
   }
}
