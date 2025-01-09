package js.java.isolate.fahrplaneditor;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.text.MaskFormatter;
import javax.swing.text.PlainDocument;
import js.java.isolate.sim.flagdata;
import js.java.tools.TextHelper;
import js.java.tools.balloontip.BalloonTip;
import js.java.tools.fx.BumpFilter;
import js.java.tools.fx.GlowFilter;
import js.java.tools.fx.GrayFilter;
import js.java.tools.gui.DisableInputLayerUI;
import js.java.tools.gui.warningPopup.IconPopupButton;
import js.java.tools.xml.xmllistener;
import js.java.tools.xml.xmlreader;
import org.xml.sax.Attributes;

class bahnhofHalt extends JPanel implements xmllistener, Comparable {
   public static boolean keysupport = true;
   private fahrplaneditor my_main;
   private bahnhof my_parent;
   private JButton delButton;
   private JButton a2zButton;
   private JComboBox gleisCB;
   private JComboBox einfahrtCB;
   private JComboBox ausfahrtCB;
   private JFormattedTextField ankunft;
   private JFormattedTextField abfahrt;
   private JTextField flags;
   private JTextField hinweis;
   private markerBox[] tmarker = new markerBox[26];
   private Color fcolor;
   private Color warncolor = Color.RED;
   private BalloonTip balloonTip = null;
   private IconPopupButton gleisWarn;
   private IconPopupButton einfahrtWarn;
   private IconPopupButton ausfahrtWarn;
   private IconPopupButton ankunftWarn;
   private IconPopupButton abfahrtWarn;
   private IconPopupButton flagsWarn;
   private IconPopupButton[] markerWarn = new IconPopupButton[26];
   private JLabel savedMarker;
   private DisableInputLayerUI ui;
   private JLayer<JPanel> layer;
   private boolean runningFlagCheck = false;
   private boolean zidvalid = false;
   private boolean zidCheckRunning = false;

   void cleanSaveStatus() {
      this.savedMarker.setIcon(null);
   }

   void postValidate() {
      this.gleisWarn.clearWarning();
      this.einfahrtWarn.clearWarning();
      this.ausfahrtWarn.clearWarning();
      this.ankunftWarn.clearWarning();
      this.abfahrtWarn.clearWarning();
      this.flagsWarn.clearWarning();
   }

   void validate(LinkedList<dataFailures> l) {
      dataFailures ausf = null;
      dataFailures einf = null;
      if (l != null) {
         if (this.gleisCB.getSelectedItem() == null) {
            dataFailures f = new dataFailures(this.gleisWarn, "Gleis nicht gesetzt");
            l.add(f);
         }

         String f = this.flags.getText();

         try {
            flagdata fd = new flagdata(f);
            if ((fd.hasFlag('E') || fd.hasFlag('K')) && (this.ausfahrtCB.getSelectedItem() == null || ((enritem)this.ausfahrtCB.getSelectedItem()).enr != 0)) {
               ausf = new dataFailures(this.ausfahrtWarn, "E/K-Flag mit falscher Ausfahrt");
               l.add(ausf);
            }

            if (fd.hasFlag('D')) {
               if (this.getAn() == 0) {
                  this.ankunft.setText(this.abfahrt.getText());
               } else if (this.getAb() == 0) {
                  this.abfahrt.setText(this.ankunft.getText());
               }
            }
         } catch (Exception var23) {
         }

         if (this.ausfahrtCB.getSelectedItem() == null) {
            ausf = new dataFailures(this.ausfahrtWarn, "Keine Ausfahrt");
            l.add(ausf);
         }

         if (this.einfahrtCB.getSelectedItem() == null) {
            einf = new dataFailures(this.einfahrtWarn, "Keine Einfahrt");
            l.add(einf);
         }
      }

      boolean notallmarkers = false;

      label382:
      for (int i = 0; i < this.tmarker.length; i++) {
         char c = (char)(65 + i);
         this.markerWarn[i].clearWarning();
         dataFailures markerf = null;
         boolean v = this.tmarker[i].isSelected();
         notallmarkers |= !v;
         enritem ausitem = (enritem)this.ausfahrtCB.getSelectedItem();
         enritem einitem = (enritem)this.einfahrtCB.getSelectedItem();
         if (v) {
            boolean justadded = false;
            if (this.my_parent.ausfahrtenTM.containsKey(c) && !((enritem)this.my_parent.ausfahrtenTM.get(c)).equals(ausitem)) {
               if (l != null) {
                  ausf = new dataFailures(this.ausfahrtWarn, "Ausfahrt passt nicht zu vorheriger Definition und Themenmarkern");
                  l.add(ausf);
                  ausf.addSolution(
                     new einausSolver(
                        "Ausfahrt auf " + c + " anpassen: " + ((enritem)this.my_parent.ausfahrtenTM.get(c)).text,
                        this.ausfahrtCB,
                        (enritem)this.my_parent.ausfahrtenTM.get(c)
                     )
                  );
                  markerf = new dataFailures(this.markerWarn[i], "Ausfahrt passt nicht zu vorheriger Definition und Themenmarkern");
                  l.add(markerf);
                  markerf.addSolution(new markerSolver("Marker entfernen", this.tmarker[i]));
               }
            } else if (ausitem != null) {
               this.my_parent.ausfahrtenTM.put(c, ausitem);
            }

            if (this.my_parent.einfahrtenTM.containsKey(c) && !((enritem)this.my_parent.einfahrtenTM.get(c)).equals(einitem)) {
               if (l != null) {
                  einf = new dataFailures(this.einfahrtWarn, "Einfahrt passt nicht zu vorheriger Definition und Themenmarkern");
                  l.add(einf);
                  einf.addSolution(
                     new einausSolver(
                        "Einfahrt auf " + c + " anpassen: " + ((enritem)this.my_parent.einfahrtenTM.get(c)).text,
                        this.einfahrtCB,
                        (enritem)this.my_parent.einfahrtenTM.get(c)
                     )
                  );
                  markerf = new dataFailures(this.markerWarn[i], "Einfahrt passt nicht zu vorheriger Definition und Themenmarkern");
                  l.add(markerf);
                  markerf.addSolution(new markerSolver("Marker entfernen", this.tmarker[i]));
               }
            } else if (einitem != null) {
               if (this.my_parent.einfahrtenTM.containsKey(c) && einitem.enr == 0 && this.my_main.getBhfs().getFirst() != this.my_parent && l != null) {
                  einf = new dataFailures(this.einfahrtWarn, "Einfahrt mitten im Fahrplan fehlt");
                  l.add(einf);
               }

               this.my_parent.einfahrtenTM.put(c, einitem);
            }
         }

         if (l != null && v) {
            int an = this.getAn();
            int ab = this.getAb();
            boolean meseen = false;

            for (bahnhof b : this.my_main.getBhfs()) {
               if (b != this.my_parent) {
                  int min = b.getMinAn(i);
                  int max = b.getMaxAb(i);
                  if (an > min && an < max) {
                     dataFailures f = new dataFailures(
                        this.ankunftWarn, "Ankunft innerhalb Zeitfenster von " + b.getAidData().getName() + " (" + min + "-" + max + ")"
                     );
                     l.add(f);
                  }

                  if (ab > min && ab < max) {
                     dataFailures f = new dataFailures(
                        this.abfahrtWarn, "Abfahrt innerhalb Zeitfenster von " + b.getAidData().getName() + " (" + min + "-" + max + ")"
                     );
                     l.add(f);
                  }

                  if (an < max && ab > max || ab < max && an > max) {
                     dataFailures f = new dataFailures(
                        this.ankunftWarn, "Ankunft oder Abfahrt Zeitkonflikt mit " + b.getAidData().getName() + " (" + min + "-" + max + ")"
                     );
                     l.add(f);
                     f = new dataFailures(this.abfahrtWarn, "Ankunft oder Abfahrt Zeitkonflikt mit " + b.getAidData().getName() + " (" + min + "-" + max + ")");
                     l.add(f);
                  }
               } else {
                  meseen = true;
               }
            }

            boolean hasEKF = false;

            try {
               String flag = this.flags.getText();
               flagdata fdx = new flagdata(flag);
               hasEKF = fdx.hasFlag('E') || fdx.hasFlag('K') || fdx.hasFlag('F');
            } catch (Exception var22) {
            }

            Iterator var63 = this.my_parent.getHalte().iterator();

            while (true) {
               bahnhofHalt bx;
               while (true) {
                  if (!var63.hasNext()) {
                     continue label382;
                  }

                  bx = (bahnhofHalt)var63.next();
                  if (bx != this && bx.hasMarker(i) && bx.gleisCB.getSelectedItem() != null) {
                     if (!hasEKF) {
                        break;
                     }

                     boolean BhasEKF = false;

                     try {
                        String flag = bx.flags.getText();
                        flagdata fdx = new flagdata(flag);
                        BhasEKF = fdx.hasFlag('E') || fdx.hasFlag('K') || fdx.hasFlag('F');
                     } catch (Exception var21) {
                     }

                     if (!BhasEKF) {
                        break;
                     }
                  }
               }

               if (an >= bx.getAn() && an <= bx.getAb()) {
                  dataFailures f = new dataFailures(this.ankunftWarn, "Ankunft Zeitkonflikt Gleis " + bx.gleisCB.getSelectedItem().toString());
                  l.add(f);
               }

               if (ab >= bx.getAn() && ab <= bx.getAb()) {
                  dataFailures f = new dataFailures(this.abfahrtWarn, "Abfahrt Zeitkonflikt Gleis " + bx.gleisCB.getSelectedItem().toString());
                  l.add(f);
               }
            }
         }
      }

      boolean justaddedx = false;

      try {
         FPEaidData a = this.my_parent.getAidData();
         String flag = this.flags.getText();
         flagdata fdx = new flagdata(flag);
         if ((fdx.hasFlag('E') || fdx.hasFlag('K') || fdx.hasFlag('F')) && notallmarkers && l != null) {
            ausf = new dataFailures(this.flagsWarn, "E/K/F nur mit allen Themenmarkern");
            l.add(ausf);
            ausf.addSolution(new tmSolver("Alle Marker setzen", this.tmarker));
         }

         if (!fdx.hasFlag('E') && !fdx.hasFlag('K')) {
            if ((this.ausfahrtCB.getSelectedItem() == null || ((enritem)this.ausfahrtCB.getSelectedItem()).enr == 0)
               && this.my_parent.ausfahrtenTM.containsKey('A')
               && ((enritem)this.my_parent.ausfahrtenTM.get('A')).enr != 0) {
               ausf = new dataFailures(this.ausfahrtWarn, "Keine Ausfahrt aber kein E/K-Flag");
               l.add(ausf);
            }
         } else {
            if (l != null) {
               if (this.ausfahrtCB.getSelectedItem() == null || ((enritem)this.ausfahrtCB.getSelectedItem()).enr != 0) {
                  ausf = new dataFailures(this.ausfahrtWarn, "E/K-Flag mit falscher Ausfahrt");
                  l.add(ausf);
                  ausf.addSolution(new einausSolver("Ausfahrt anpassen auf: " + a.getENR0().text, this.ausfahrtCB, a.getENR0()));
               }
            } else {
               for (int i = 0; i < this.tmarker.length; i++) {
                  char cx = (char)(65 + i);
                  this.my_parent.ausfahrtenTM.put(cx, a.getENR0());
               }
            }

            for (int i = 0; i < this.tmarker.length; i++) {
               char cx = (char)(65 + i);
               this.my_main.hasEKTM.put(cx, this.my_parent);
            }

            justaddedx = true;
         }

         if (l != null
            && (
               fdx.hasFlag('E') && (fdx.hasFlag('K') || fdx.hasFlag('F'))
                  || fdx.hasFlag('K') && (fdx.hasFlag('E') || fdx.hasFlag('F'))
                  || fdx.hasFlag('F') && (fdx.hasFlag('K') || fdx.hasFlag('E'))
            )) {
            ausf = new dataFailures(this.flagsWarn, "E/K/F nur einmal pro Halt");
            l.add(ausf);
         }

         if (fdx.hasFlag('E')) {
            if (l != null && this.my_main.hadEKF.containsKey(fdx.getLongFlag('E')) && this.my_main.hadEKF.get(fdx.getLongFlag('E')) != this) {
               ausf = new dataFailures(this.flagsWarn, "Flag " + fdx.getLongFlag('E') + " nur 1x pro Fahrplan erlaubt");
               l.add(ausf);
            }

            if (l == null) {
               this.my_main.hadEKF.put(fdx.getLongFlag('E'), this);
            }
         }

         if (fdx.hasFlag('K')) {
            if (l != null && this.my_main.hadEKF.containsKey(fdx.getLongFlag('K')) && this.my_main.hadEKF.get(fdx.getLongFlag('K')) != this) {
               ausf = new dataFailures(this.flagsWarn, "Flag " + fdx.getLongFlag('K') + " nur 1x pro Fahrplan erlaubt");
               l.add(ausf);
            }

            if (l == null) {
               this.my_main.hadEKF.put(fdx.getLongFlag('K'), this);
            }
         }
      } catch (Exception var20) {
         if (l != null) {
            ausf = new dataFailures(this.flagsWarn, "Fehler im Syntax: " + var20.getMessage());
            l.add(ausf);
         }
      }

      if (this.my_main.hasEKTM.containsKey('A') && !justaddedx && l != null) {
         dataFailures f = new dataFailures(this.gleisWarn, "Fahrplan trotz E/K-Flag");
         l.add(f);
      }

      if (l != null) {
         int an = this.getAn();
         int ab = this.getAb();
         if (ab < an) {
            dataFailures f = new dataFailures(this.ankunftWarn, "Abfahrt vor Ankunft");
            l.add(f);
            f = new dataFailures(this.abfahrtWarn, "Abfahrt vor Ankunft");
            l.add(f);
         }

         if (an % 100 > 59 || an % 100 < 0) {
            dataFailures f = new dataFailures(this.ankunftWarn, "Minute nicht zwischen 0 - 59");
            l.add(f);
         }

         if (ab % 100 > 59 || ab % 100 < 0) {
            dataFailures f = new dataFailures(this.abfahrtWarn, "Minute nicht zwischen 0 - 59");
            l.add(f);
         }

         if (an / 100 > 23 || an / 100 < 0) {
            dataFailures f = new dataFailures(this.ankunftWarn, "Stunde nicht zwischen 0 - 23");
            l.add(f);
         }

         if (ab / 100 > 23 || ab / 100 < 0) {
            dataFailures f = new dataFailures(this.abfahrtWarn, "Stunde nicht zwischen 0 - 23");
            l.add(f);
         }
      }
   }

   public boolean hasMarker(int i) {
      return this.tmarker[i].isSelected();
   }

   public boolean hasEK() {
      String f = this.flags.getText();

      try {
         flagdata fd = new flagdata(f);
         return fd.hasFlag('E') || fd.hasFlag('K');
      } catch (Exception var3) {
         return false;
      }
   }

   dataFailures createAusfahrtFailure(String text) {
      return new dataFailures(this.ausfahrtWarn, text);
   }

   private int getAs100Time(JFormattedTextField f) {
      try {
         String a = f.getText();
         if (a.charAt(2) == ':') {
            return Integer.parseInt(a.substring(0, 2)) * 100 + Integer.parseInt(a.substring(3, 5));
         }
      } catch (Exception var3) {
      }

      return 0;
   }

   private int getAsMinutesTime(JFormattedTextField f) {
      try {
         String a = f.getText();
         if (a.charAt(2) == ':') {
            return Integer.parseInt(a.substring(0, 2)) * 60 + Integer.parseInt(a.substring(3, 5));
         }
      } catch (Exception var3) {
      }

      return 0;
   }

   private void setFromMinutesTime(JFormattedTextField f, int min) {
      f.setText(String.format("%02d:%02d", min / 60, min % 60));
   }

   public int getAn() {
      return this.getAs100Time(this.ankunft);
   }

   public int getAb() {
      return this.getAs100Time(this.abfahrt);
   }

   public int getAusfahrtEnr() {
      enritem ausitem = (enritem)this.ausfahrtCB.getSelectedItem();
      return ausitem != null ? ausitem.enr : 0;
   }

   public void shiftMinutes(int min) {
      int anmin = this.getAsMinutesTime(this.ankunft);
      int abmin = this.getAsMinutesTime(this.abfahrt);
      anmin += min;
      abmin += min;
      this.setFromMinutesTime(this.ankunft, anmin);
      this.setFromMinutesTime(this.abfahrt, abmin);
   }

   private void warnHandler(IconPopupButton b) {
      solutionInterface e = (solutionInterface)b.getLastSelected();
      if (e != null) {
         e.solve();
         this.my_parent.entriesChanged(this, false);
      }
   }

   private IconPopupButton newWarn() {
      IconPopupButton b = new IconPopupButton();
      b.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            bahnhofHalt.this.warnHandler((IconPopupButton)e.getSource());
         }
      });
      b.setSolvingEnabled(this.my_parent.isErlaubt());
      this.add(b);
      return b;
   }

   bahnhofHalt(fahrplaneditor m, bahnhof parent) {
      this.my_main = m;
      this.my_parent = parent;
      this.setLayout(new planLayoutManager());
      this.delButton = new JButton();
      this.delButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/clear16.png")));
      this.delButton.setMargin(new Insets(1, 1, 1, 1));
      this.delButton.setFocusPainted(false);
      this.delButton.setFocusable(false);
      this.delButton.setEnabled(false);
      this.delButton.setToolTipText("diesen Halt löschen");
      this.delButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            bahnhofHalt.this.action_delButton();
         }
      });
      this.add(this.delButton);
      this.savedMarker = new JLabel();
      this.add(this.savedMarker);
      FPEaidData a = this.my_parent.getAidData();
      this.gleisWarn = this.newWarn();
      this.gleisCB = new JComboBox();
      this.gleisCB.setFocusable(keysupport);

      for (gleisData gleis : a.getGleise()) {
         this.gleisCB.addItem(gleis);
      }

      this.gleisCB.setSelectedItem(null);
      this.gleisCB.setEnabled(this.my_parent.isErlaubt());
      this.gleisCB.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
            bahnhofHalt.this.action_element();
            bahnhofHalt.this.ankunft.setCaretPosition(0);
            if (!bahnhofHalt.keysupport) {
               bahnhofHalt.this.ankunft.requestFocusInWindow();
            }
         }
      });
      this.add(this.gleisCB);
      this.ankunftWarn = this.newWarn();
      this.ankunft = new JFormattedTextField(this.createFormatter("##:##"));
      this.ankunft.setFont(this.my_main.font_mono);
      this.ankunft.setText("00:00");
      this.ankunft.setEnabled(this.my_parent.isErlaubt());
      this.ankunft.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            bahnhofHalt.this.action_element();
            bahnhofHalt.this.abfahrt.setCaretPosition(0);
            bahnhofHalt.this.abfahrt.requestFocusInWindow();
         }
      });
      this.ankunft.addKeyListener(new KeyListener() {
         public void keyTyped(KeyEvent e) {
            bahnhofHalt.this.action_element();
            if (bahnhofHalt.this.ankunft.getCaretPosition() == 4) {
               bahnhofHalt.this.abfahrt.setCaretPosition(0);
               bahnhofHalt.this.abfahrt.requestFocusInWindow();
            }
         }

         public void keyPressed(KeyEvent e) {
         }

         public void keyReleased(KeyEvent e) {
         }
      });
      this.add(this.ankunft);
      this.abfahrtWarn = this.newWarn();
      this.abfahrt = new JFormattedTextField(this.createFormatter("##:##"));
      this.abfahrt.setFont(this.my_main.font_mono);
      this.abfahrt.setText("00:00");
      this.abfahrt.setEnabled(this.my_parent.isErlaubt());
      this.abfahrt.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            bahnhofHalt.this.action_element();
            bahnhofHalt.this.flags.requestFocusInWindow();
         }
      });
      this.abfahrt.addKeyListener(new KeyListener() {
         public void keyTyped(KeyEvent e) {
            bahnhofHalt.this.action_element();
            if (bahnhofHalt.this.abfahrt.getCaretPosition() == 4) {
               if (bahnhofHalt.keysupport) {
                  bahnhofHalt.this.einfahrtCB.requestFocusInWindow();
               } else {
                  bahnhofHalt.this.flags.requestFocusInWindow();
               }
            }
         }

         public void keyPressed(KeyEvent e) {
         }

         public void keyReleased(KeyEvent e) {
         }
      });
      this.add(this.abfahrt);
      this.einfahrtWarn = this.newWarn();
      this.einfahrtCB = new JComboBox();
      this.einfahrtCB.setFocusable(keysupport);

      for (enritem e : a.getEinfahrten()) {
         this.einfahrtCB.addItem(e);
      }

      this.einfahrtCB.setSelectedItem(null);
      this.einfahrtCB.setEnabled(this.my_parent.isErlaubt());
      this.einfahrtCB.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
            bahnhofHalt.this.action_element();
            if (!bahnhofHalt.keysupport) {
               bahnhofHalt.this.flags.requestFocusInWindow();
            }
         }
      });
      this.add(this.einfahrtCB);
      this.ausfahrtWarn = this.newWarn();
      this.ausfahrtCB = new JComboBox();
      this.ausfahrtCB.setFocusable(keysupport);

      for (enritem e : a.getAusfahrten()) {
         this.ausfahrtCB.addItem(e);
      }

      this.ausfahrtCB.setSelectedItem(null);
      this.ausfahrtCB.setEnabled(this.my_parent.isErlaubt());
      this.ausfahrtCB.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
            bahnhofHalt.this.action_element();
            if (!bahnhofHalt.keysupport) {
               bahnhofHalt.this.flags.requestFocusInWindow();
            }
         }
      });
      this.add(this.ausfahrtCB);
      this.flagsWarn = this.newWarn();
      this.flags = new JTextField();
      this.fcolor = this.flags.getForeground();
      this.flags.setEnabled(this.my_parent.isErlaubt());
      this.flags.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            bahnhofHalt.this.action_flagfocus(true);
            bahnhofHalt.this.hinweis.requestFocusInWindow();
         }
      });
      this.flags.addKeyListener(new KeyListener() {
         public void keyTyped(KeyEvent e) {
         }

         public void keyPressed(KeyEvent e) {
            bahnhofHalt.this.hideBalloon();
         }

         public void keyReleased(KeyEvent e) {
            bahnhofHalt.this.action_flagelement();
         }
      });
      this.flags.addFocusListener(new FocusListener() {
         public void focusGained(FocusEvent e) {
            bahnhofHalt.this.action_flagfocus(false);
         }

         public void focusLost(FocusEvent e) {
            bahnhofHalt.this.action_flagfocus(true);
         }
      });
      PlainDocument pd = new PlainDocument();
      this.flags.setDocument(pd);
      pd.setDocumentFilter(new FlagDocumentFilter());
      this.add(this.flags);
      this.hinweis = new JTextField();
      this.hinweis.setEnabled(this.my_parent.isErlaubt());
      this.hinweis.addKeyListener(new KeyListener() {
         public void keyTyped(KeyEvent e) {
            bahnhofHalt.this.action_element();
         }

         public void keyPressed(KeyEvent e) {
         }

         public void keyReleased(KeyEvent e) {
         }
      });
      this.add(this.hinweis);
      TMbuttonGroup bg = new TMbuttonGroup();

      for (int i = 0; i < this.tmarker.length; i++) {
         this.markerWarn[i] = this.newWarn();
         this.tmarker[i] = new markerBox();
         this.tmarker[i].setFocusPainted(false);
         this.tmarker[i].setFocusable(false);
         this.tmarker[i].setBorder(null);
         this.tmarker[i].setToolTipText((char)(65 + i) + "");
         this.tmarker[i].setSelected(true);
         this.tmarker[i].setEnabled(this.my_parent.isErlaubt());
         this.tmarker[i].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               bahnhofHalt.this.action_elementTM();
            }
         });
         this.add(this.tmarker[i]);
         bg.add(this.tmarker[i]);
      }

      this.a2zButton = new JButton();
      this.a2zButton.setText("A-Z");
      this.a2zButton.setMargin(new Insets(1, 1, 1, 1));
      this.a2zButton.setFocusPainted(false);
      this.a2zButton.setFocusable(false);
      this.a2zButton.setEnabled(this.my_parent.isErlaubt());
      this.a2zButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            bahnhofHalt.this.action_a2zButton();
         }
      });
      this.add(this.a2zButton);
      this.ui = new DisableInputLayerUI();
      this.layer = new JLayer(this, this.ui);
   }

   bahnhofHalt(fahrplaneditor m, bahnhof b, planLine p) {
      this(m, b);
      this.delButton.setEnabled(this.my_parent.isErlaubt());
      FPEaidData a = this.my_parent.getAidData();
      boolean foundgl = false;

      for (gleisData gleis : a.getGleise()) {
         if (gleis.name.equalsIgnoreCase(p.gleis)) {
            this.gleisCB.setSelectedItem(gleis);
            this.gleisCB.setToolTipText(gleis.name);
            foundgl = true;
            break;
         }
      }

      if (!foundgl) {
         this.gleisCB.setToolTipText("Unbekanntes Gleis: " + p.gleis);
      }

      enritem ausitem = null;
      enritem einitem = null;

      for (enritem e : a.getEinfahrten()) {
         if (e.enr == p.ein_enr) {
            this.einfahrtCB.setSelectedItem(e);
            break;
         }
      }

      for (enritem ex : a.getAusfahrten()) {
         if (ex.enr == p.aus_enr) {
            this.ausfahrtCB.setSelectedItem(ex);
            break;
         }
      }

      this.ankunft.setText(p.an);
      this.abfahrt.setText(p.ab);
      this.hinweis.setText(p.hinweise);
      this.flags.setText(p.flags.toString());

      for (int i = 0; i < this.tmarker.length; i++) {
         boolean v = p.thematag.indexOf((char)(65 + i)) >= 0;
         this.tmarker[i].setSelected(v);
         char var12 = (char)(65 + i);
      }
   }

   private void hideBalloon() {
      if (this.balloonTip != null) {
         this.balloonTip.setVisible(false);
      }
   }

   private void showBalloon(String txt) {
      if (this.balloonTip == null) {
         this.balloonTip = new BalloonTip(this.flags);
      }

      this.balloonTip.setText(txt);
      this.balloonTip.setVisible(true);
   }

   private void action_element() {
      this.delButton.setEnabled(this.my_parent.isErlaubt());
      if (this.ausfahrtCB.getSelectedItem() != null) {
         this.ausfahrtCB.setToolTipText(this.ausfahrtCB.getSelectedItem().toString());
      } else {
         try {
            this.ausfahrtCB.setSelectedItem(((bahnhofHalt)this.my_parent.getHalte().getFirst()).ausfahrtCB.getSelectedItem());
         } catch (Exception var3) {
         }
      }

      if (this.einfahrtCB.getSelectedItem() != null) {
         this.einfahrtCB.setToolTipText(this.einfahrtCB.getSelectedItem().toString());
      } else {
         try {
            this.einfahrtCB.setSelectedItem(((bahnhofHalt)this.my_parent.getHalte().getFirst()).einfahrtCB.getSelectedItem());
         } catch (Exception var2) {
         }
      }

      this.my_parent.entriesChanged(this, false);
   }

   private void action_elementTM() {
      boolean isa = false;

      for (int i = 1; i < this.tmarker.length; i++) {
         isa = isa || this.tmarker[i].isSelected();
      }

      if (isa) {
         this.action_element();
      }

      this.my_main.filterMarkers();
   }

   private void action_flagelement() {
      this.action_element();
      String f = this.flags.getText();

      try {
         new flagdata(f);
         this.flags.setForeground(this.fcolor);
      } catch (Exception var3) {
         this.flags.setForeground(this.warncolor);
         this.showBalloon("Fehler im Syntax: " + var3.getMessage());
      }
   }

   private void action_flagfocus(boolean p) {
      if (!this.runningFlagCheck) {
         if (p) {
            this.runningFlagCheck = true;
            String f = this.flags.getText();

            try {
               final flagdata fd = new flagdata(f);
               this.flags.setForeground(this.fcolor);
               this.flags.setText(fd.toString());
               int zid = 0;
               if (fd.hasFlag('E')) {
                  zid = fd.dataOfFlag('E');
               } else if (fd.hasFlag('F')) {
                  zid = fd.dataOfFlag('F');
               } else if (fd.hasFlag('K')) {
                  zid = fd.dataOfFlag('K');
               }

               if (zid > 0) {
                  final int z = zid;
                  SwingWorker w = new SwingWorker() {
                     int _zid = z;

                     protected Object doInBackground() throws Exception {
                        bahnhofHalt.this.validateZid(fd, this._zid);
                        return null;
                     }

                     protected void done() {
                        bahnhofHalt.this.runningFlagCheck = false;

                        try {
                           if (!bahnhofHalt.this.zidvalid) {
                              bahnhofHalt.this.showBalloon("Diese ZID ist nicht verfügbar.");
                              bahnhofHalt.this.flags.setForeground(bahnhofHalt.this.warncolor);
                           }
                        } catch (Exception var2) {
                        }
                     }
                  };
                  w.execute();
               }
            } catch (Exception var7) {
               this.flags.setForeground(this.warncolor);
               this.showBalloon("Fehler im Syntax: " + var7.getMessage());
            }
         } else {
            this.hideBalloon();
         }
      }
   }

   private void action_delButton() {
      this.delButton.setEnabled(false);
      this.my_parent.entriesChanged(this, true);
   }

   private void action_a2zButton() {
      boolean oneUnselected = false;

      for (markerBox tmarker1 : this.tmarker) {
         oneUnselected |= !tmarker1.isSelected();
      }

      for (markerBox tmarker1 : this.tmarker) {
         tmarker1.setSelected(oneUnselected);
      }

      this.action_element();
   }

   private void validateZid(flagdata fd, int zid) {
      if (!this.zidCheckRunning) {
         this.zidCheckRunning = true;
         System.out.println("validate Z" + zid);
         String u = this.my_main.getZugDataUrl(zid, this.my_parent.getAidData(), fd);
         xmlreader xmlr = new xmlreader();
         xmlr.registerTag("zugcheck", this);
         xmlr.registerTag("zug", this);
         this.zidvalid = false;

         try {
            xmlr.updateData(u, new StringBuffer());
         } catch (IOException var6) {
            System.out.println("Ex: " + var6.getMessage());
            var6.printStackTrace();
         }

         this.zidCheckRunning = false;
      }
   }

   private MaskFormatter createFormatter(String s) {
      MaskFormatter formatter = null;

      try {
         formatter = new MaskFormatter(s);
      } catch (ParseException var4) {
         System.err.println("formatter is bad: " + var4.getMessage());
         System.exit(-1);
      }

      return formatter;
   }

   public void removed() {
      this.hideBalloon();
   }

   public void saveData(StringBuffer data, int counter) {
      data.append(TextHelper.urlEncode("azid[" + counter + "]"));
      data.append('=');
      data.append(TextHelper.urlEncode(counter + ""));
      data.append('&');
      data.append(TextHelper.urlEncode("gleis[" + counter + "]"));
      data.append('=');
      data.append(TextHelper.urlEncode(this.gleisCB.getSelectedItem().toString()));
      data.append('&');
      data.append(TextHelper.urlEncode("an[" + counter + "]"));
      data.append('=');
      data.append(TextHelper.urlEncode(this.ankunft.getText()));
      data.append('&');
      data.append(TextHelper.urlEncode("ab[" + counter + "]"));
      data.append('=');
      data.append(TextHelper.urlEncode(this.abfahrt.getText()));
      data.append('&');
      flagdata d = new flagdata(this.flags.getText());
      data.append(TextHelper.urlEncode("flags[" + counter + "]"));
      data.append('=');
      data.append(TextHelper.urlEncode(d.getFlags()));
      data.append('&');
      data.append(TextHelper.urlEncode("flagdata[" + counter + "]"));
      data.append('=');
      data.append(TextHelper.urlEncode(d.getFlagdata()));
      data.append('&');
      data.append(TextHelper.urlEncode("flagparam[" + counter + "]"));
      data.append('=');
      data.append(TextHelper.urlEncode(d.getFlagparam()));
      data.append('&');
      data.append(TextHelper.urlEncode("ein_enr[" + counter + "]"));
      data.append('=');
      data.append(TextHelper.urlEncode(((enritem)this.einfahrtCB.getSelectedItem()).enr + ""));
      data.append('&');
      data.append(TextHelper.urlEncode("aus_enr[" + counter + "]"));
      data.append('=');
      data.append(TextHelper.urlEncode(((enritem)this.ausfahrtCB.getSelectedItem()).enr + ""));
      data.append('&');
      data.append(TextHelper.urlEncode("hinweis[" + counter + "]"));
      data.append('=');
      data.append(TextHelper.urlEncode(this.hinweis.getText()));
      data.append('&');
      String thematag = "";

      for (int i = 0; i < this.tmarker.length; i++) {
         if (this.tmarker[i].isSelected()) {
            char c = (char)(65 + i);
            thematag = thematag + c;
         }
      }

      data.append(TextHelper.urlEncode("thematag[" + counter + "]"));
      data.append('=');
      data.append(TextHelper.urlEncode(thematag));
      data.append('&');

      try {
         this.savedMarker.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/accept16.png")));
      } catch (Exception var7) {
      }
   }

   public String toString() {
      return this.flags.getText();
   }

   public void parseStartTag(String tag, Attributes attrs) {
      if (!tag.equalsIgnoreCase("zug") && tag.equalsIgnoreCase("zugcheck")) {
         this.zidvalid = attrs.getValue("result").equalsIgnoreCase("true");
         System.out.println("R: " + attrs.getValue("result") + "::" + this.zidvalid);
      }
   }

   public void parseEndTag(String tag, Attributes attrs, String pcdata) {
   }

   public int compareTo(Object o) {
      bahnhofHalt b = (bahnhofHalt)o;
      int r = this.getAn() - b.getAn();
      if (r == 0) {
         r = this.flags.getText().compareToIgnoreCase(b.flags.getText());
      }

      if (r == 0) {
         r = this.getAb() - b.getAb();
      }

      if (r == 0) {
         for (int i = 0; i < this.tmarker.length; i++) {
            if (this.tmarker[i].isSelected() != b.tmarker[i].isSelected()) {
               if (this.tmarker[i].isSelected()) {
                  r = -1;
               } else {
                  r = 1;
               }
               break;
            }
         }
      }

      if (r == 0) {
         r = -1;
      }

      return r;
   }

   public void fadeOff(boolean off) {
      this.blurOff(off);
   }

   public void fade2Off(boolean off) {
      this.greyOff(off);
   }

   public void fade3Off(boolean off) {
      this.lockOff(off);
   }

   private void greyOff(boolean off) {
      if (off) {
         this.ui.setLockedEffects(new GrayFilter());
      }

      this.ui.setLocked(off);
   }

   private void lockOff(boolean off) {
      if (off) {
         GlowFilter f = new GlowFilter();
         f.setAmount(0.01F);
         this.ui.setLockedEffects(f);
      }

      this.ui.setLocked(off);
   }

   private void blurOff(boolean off) {
      if (off) {
         this.ui.setLockedEffects(new BumpFilter());
      }

      this.ui.setLocked(off);
   }

   public JLayer getThis() {
      return this.layer;
   }
}
