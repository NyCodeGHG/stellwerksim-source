package js.java.isolate.sim.eventsys.events;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventFactory;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.element_list;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.toolkit.ComboGleisRenderer;

public abstract class sperrungBaseFactory extends eventFactory {
   private JList glist;
   private DefaultListModel dmodel;
   private JButton addb;
   private JButton delb;
   private JTextArea dtext;
   private JComboBox gdetails;
   private JLabel dlabel;
   private boolean iseditmode = false;
   private HashMap<gleis, Object> gdetailsvalues = null;
   private final element_list allowedElements = new element_list(gleis.ELEMENT_EINFAHRT, gleis.ELEMENT_AUSFAHRT, gleis.ALLE_STRECKENSIGNALE, gleis.ALLE_WEICHEN);
   private gleis selectedgleis = null;

   @Override
   protected void initGui() {
      GridBagLayout b = new GridBagLayout();
      JPanel pan = new JPanel();
      pan.setLayout(b);
      JScrollPane sc = new JScrollPane();
      sc.setVerticalScrollBarPolicy(22);
      this.dmodel = new DefaultListModel();
      this.glist = new JList();
      this.glist.setCellRenderer(new ComboGleisRenderer());
      this.glist.setSelectionMode(1);
      this.glist.setVisibleRowCount(5);
      this.glist.setModel(this.dmodel);
      this.glist.setEnabled(false);
      this.glist.setToolTipText("<html>Gleiselement auswählen, dann hinzufügen.</html>");
      ListSelectionModel rowSM = this.glist.getSelectionModel();
      rowSM.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
               try {
                  sperrungBaseFactory.this.delb.setEnabled(false);
                  sperrungBaseFactory.this.gdetails.setEnabled(false);
                  gleis g = (gleis)sperrungBaseFactory.this.glist.getSelectedValue();
                  if (g != null) {
                     sperrungBaseFactory.this.selectedG(g);
                  }
               } catch (Exception var3) {
                  Logger.getLogger("stslogger").log(Level.SEVERE, "initGui", var3);
               }
            }
         }
      });
      sc.setViewportView(this.glist);
      GridBagConstraints c = new GridBagConstraints();
      c.gridx = 0;
      c.gridy = 0;
      c.gridheight = 3;
      c.gridwidth = 2;
      c.weightx = 1.0;
      c.weighty = 1.0;
      c.fill = 1;
      pan.add(sc, c);
      this.addb = new JButton("hinzufügen");
      this.addb
         .addActionListener(
            new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  gleis g = sperrungBaseFactory.this.glbModel.getSelectedGleis();
                  if (g != null
                     && g.getENR() > 0
                     && sperrungBaseFactory.this.allowedElements.matches(g.getElement())
                     && !sperrungBaseFactory.this.dmodel.contains(g)) {
                     sperrungBaseFactory.this.dmodel.addElement(g);
                     sperrungBaseFactory.this.glist.setSelectedValue(g, true);
                  }

                  sperrungBaseFactory.this.addb.setEnabled(false);
               }
            }
         );
      this.addb.setEnabled(false);
      c = new GridBagConstraints();
      c.gridx = 2;
      c.gridy = 0;
      c.gridheight = 1;
      c.gridwidth = 1;
      c.fill = 2;
      pan.add(this.addb, c);
      this.delb = new JButton("entfernen");
      this.delb.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            for (Object o : sperrungBaseFactory.this.glist.getSelectedValues()) {
               sperrungBaseFactory.this.dmodel.removeElement(o);
            }

            sperrungBaseFactory.this.delb.setEnabled(false);
         }
      });
      this.delb.setEnabled(false);
      c = new GridBagConstraints();
      c.gridx = 2;
      c.gridy = 1;
      c.gridheight = 1;
      c.gridwidth = 1;
      c.weighty = 1.0;
      c.anchor = 18;
      c.fill = 2;
      pan.add(this.delb, c);
      this.dlabel = new JLabel("Details");
      c = new GridBagConstraints();
      c.gridx = 0;
      c.gridy = 5;
      c.gridheight = 1;
      c.gridwidth = 1;
      c.anchor = 17;
      c.insets = new Insets(5, 2, 5, 2);
      pan.add(this.dlabel, c);
      this.gdetails = new JComboBox();
      this.gdetails.setRenderer(new ComboGleisRenderer());
      this.gdetails.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
            sperrungBaseFactory.this.selectedGdetails();
         }
      });
      c = new GridBagConstraints();
      c.gridx = 1;
      c.gridy = 5;
      c.gridheight = 1;
      c.gridwidth = 2;
      c.weightx = 1.0;
      c.insets = new Insets(5, 2, 5, 2);
      c.fill = 2;
      pan.add(this.gdetails, c);
      this.add("Gleise", null, pan, true);
      sc = new JScrollPane();
      this.dtext = new JTextArea();
      this.dtext.setEnabled(false);
      this.dtext.setLineWrap(true);
      this.dtext.setRows(3);
      sc.setViewportView(this.dtext);
      this.add("Text", "Meldung für den Spieler", sc, true);
   }

   private void selectedGdetails() {
      if (this.gdetailsvalues != null) {
         gleis g = this.glbModel.getSelectedGleis();
         if (g != null) {
            this.gdetailsvalues.put(g, this.gdetails.getSelectedItem());
         }
      }
   }

   private void selectedG(gleis g) {
      this.glbModel.setSelectedGleis(null);
      if (g.getElement() == gleis.ELEMENT_WEICHEOBEN || g.getElement() == gleis.ELEMENT_WEICHEUNTEN) {
         this.gdetails.setEnabled(this.iseditmode);
         this.gdetails.removeAllItems();
         this.gdetails.addItem(gleisElements.ST_WEICHE_GERADE.toString());
         this.gdetails.addItem(gleisElements.ST_WEICHE_ABZWEIG.toString());
         this.dlabel.setText("Grundstellung");
         this.gdetails.setSelectedItem(this.gdetailsvalues.get(g));
      } else if (g.getElement() == gleis.ELEMENT_EINFAHRT) {
         this.gdetails.setEnabled(this.iseditmode);
         this.gdetails.removeAllItems();
         Iterator<gleis> it = this.glbModel.findIterator(new Object[]{gleis.ELEMENT_EINFAHRT});

         while (it.hasNext()) {
            gleis gl = (gleis)it.next();
            if (gl != g) {
               this.gdetails.addItem(gl);
            }
         }

         this.dlabel.setText("Alternativeinfahrt");
         if (this.gdetailsvalues.get(g) != null) {
            this.gdetails.setSelectedItem(this.gdetailsvalues.get(g));
         } else {
            this.gdetails.setSelectedIndex(-1);
         }
      } else {
         this.gdetails.removeAllItems();
         this.dlabel.setText("Details");
      }

      this.glbModel.setSelectedGleis(g);
      this.glbModel.setFocus(g);
      this.delb.setEnabled(this.iseditmode);
   }

   @Override
   public void setGleis(gleis gl) {
      this.selectedgleis = gl;
      this.addb.setEnabled(this.iseditmode && this.selectedgleis != null && this.allowedElements.matches(this.selectedgleis.getElement()));
   }

   @Override
   public void showContainer(eventContainer ev, boolean editmode) {
      super.showContainer(ev, editmode);
      this.gdetailsvalues = new HashMap();
      this.iseditmode = editmode;
      this.dmodel.clear();
      this.glist.setEnabled(true);
      this.glbModel.clearMarkedGleis();
      this.addb.setEnabled(editmode && this.selectedgleis != null && this.selectedgleis.typRequiresENR());
      this.delb.setEnabled(editmode && this.glist.getSelectedIndex() >= 0);

      for (gleis g : ev.getGleisList()) {
         if (g.getElement() == gleis.ELEMENT_EINFAHRT
            || g.getElement() == gleis.ELEMENT_AUSFAHRT
            || g.getElement() == gleis.ELEMENT_SIGNAL
            || g.getElement() == gleis.ELEMENT_WEICHEOBEN
            || g.getElement() == gleis.ELEMENT_WEICHEUNTEN) {
            this.dmodel.addElement(g);
            this.glbModel.addMarkedGleis(g);
            if (g.getElement() == gleis.ELEMENT_WEICHEOBEN || g.getElement() == gleis.ELEMENT_WEICHEUNTEN) {
               String d = ev.getValue(g, "details");
               this.gdetailsvalues.put(g, d);
            } else if (g.getElement() == gleis.ELEMENT_EINFAHRT) {
               try {
                  String d = ev.getValue(g, "details");
                  if (d != null) {
                     gleis eg = this.glbModel.findFirst(new Object[]{Integer.parseInt(d), gleis.ELEMENT_EINFAHRT});
                     this.gdetailsvalues.put(g, eg);
                  }
               } catch (Exception var7) {
                  Logger.getLogger("stslogger").log(Level.SEVERE, "showContainer AID " + this.glbModel.getAid(), var7);
               }
            }
         }
      }

      this.dtext.setEnabled(editmode);
      this.dtext.setText(ev.getValue("text"));
      this.gdetails.setEnabled(false);
   }

   @Override
   public void readContainer(eventContainer ev) {
      super.readContainer(ev);
      ev.setGleis(null);
      HashSet<gleis> hg = new HashSet();
      Enumeration e = this.dmodel.elements();

      while (e.hasMoreElements()) {
         gleis g = (gleis)e.nextElement();
         hg.add(g);
         if (g.getElement() == gleis.ELEMENT_WEICHEOBEN || g.getElement() == gleis.ELEMENT_WEICHEUNTEN) {
            String d = (String)this.gdetailsvalues.get(g);
            if (d != null) {
               ev.setValue(g, "details", d);
            }
         } else if (g.getElement() == gleis.ELEMENT_EINFAHRT) {
            gleis d = (gleis)this.gdetailsvalues.get(g);
            if (d != null) {
               ev.setValue(g, "details", d.getENR() + "");
            }
         }
      }

      ev.setGleisList(hg);
      ev.setValue("text", this.dtext.getText());
   }
}
