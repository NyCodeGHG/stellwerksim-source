package js.java.isolate.sim.eventsys.events;

import java.util.EnumSet;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventFactory;

public abstract class zugStoerungBaseFactory extends eventFactory {
   private JTextArea dtext = null;
   private JTextField zname = null;
   private JTextField bname = null;
   private JSpinner num = null;
   private JSpinner anteil = null;
   private JCheckBox silent = null;
   private EnumSet<zugStoerungBaseFactory.USEDFIELDS> usedFields;

   protected zugStoerungBaseFactory(EnumSet<zugStoerungBaseFactory.USEDFIELDS> a) {
      super();
      this.usedFields = a;
   }

   @Override
   protected void initGui() {
      if (this.usedFields.contains(zugStoerungBaseFactory.USEDFIELDS.ZUGNAME)) {
         this.zname = new JTextField();
         this.zname.setEnabled(false);
         this.zname.setToolTipText("Anfangsname der/des Zugs, oder leer; max 35 Zeichen!");
         this.add("Details", "Zugname beginnt mit (opt)", this.zname, false);
      }

      if (this.usedFields.contains(zugStoerungBaseFactory.USEDFIELDS.ZUGANTEIL)) {
         this.anteil = new JSpinner(new SpinnerNumberModel(100, 1, 100, 1));
         this.anteil.setToolTipText("auf wieviel Prozent die Störung gestreut wird - tritt dann beim 1. auf");
         this.add("Details", "Anteil (%)", this.anteil, false);
      }

      if (this.usedFields.contains(zugStoerungBaseFactory.USEDFIELDS.BAHNSTEIG)) {
         this.bname = new JTextField();
         this.bname.setEnabled(false);
         this.bname.setToolTipText("Anfangsname der/des Bahnsteigs, oder leer; z.Z. max 35 Zeichen!");
         this.add("Details", "Bahnsteigname beginnt mit (opt)", this.bname, false);
      }

      if (this.usedFields.contains(zugStoerungBaseFactory.USEDFIELDS.DAUER)) {
         this.num = new JSpinner(new SpinnerNumberModel(5, 1, 60, 1));
         this.num.setToolTipText("verzögert Abfahrt um Minuten");
         this.add("Details", "Dauer (Minuten)", this.num, false);
      }

      if (this.usedFields.contains(zugStoerungBaseFactory.USEDFIELDS.SILENT)) {
         this.silent = new JCheckBox("keine Meldung zeigen wenn Störung auftritt");
         this.add("Details", "", this.silent, false);
      }

      if (this.usedFields.contains(zugStoerungBaseFactory.USEDFIELDS.TEXT)) {
         JScrollPane sc = new JScrollPane();
         this.dtext = new JTextArea();
         this.dtext.setEnabled(false);
         this.dtext.setLineWrap(true);
         this.dtext.setRows(3);
         this.dtext.setToolTipText("z.Z. max 35 Zeichen!");
         sc.setViewportView(this.dtext);
         this.add("Text", "Meldung für den Spieler", sc, true);
      }
   }

   @Override
   public void showContainer(eventContainer ev, boolean editmode) {
      super.showContainer(ev, editmode);
      if (this.zname != null) {
         this.zname.setEnabled(editmode);
         this.zname.setText(ev.getValue("zugname"));
      }

      if (this.bname != null) {
         this.bname.setEnabled(editmode);
         this.bname.setText(ev.getValue("bahnsteigname"));
      }

      if (this.num != null) {
         this.num.setEnabled(editmode);
         this.num.setValue(Math.max(ev.getIntValue("dauer", 2), 1));
      }

      if (this.anteil != null) {
         this.anteil.setEnabled(editmode);
         this.anteil.setValue(Math.max(ev.getIntValue("anteil", 100), 1));
      }

      if (this.silent != null) {
         this.silent.setEnabled(editmode);
         this.silent.setSelected(ev.getBoolValue("silent", false));
      }

      if (this.dtext != null) {
         this.dtext.setEnabled(editmode);
         this.dtext.setText(ev.getValue("text"));
      }
   }

   @Override
   public void readContainer(eventContainer ev) {
      super.readContainer(ev);
      if (this.anteil != null) {
         ev.setIntValue("anteil", Math.max(this.anteil.getValue(), 1));
      } else {
         ev.rmValue("anteil");
      }

      if (this.num != null) {
         ev.setIntValue("dauer", Math.max(this.num.getValue(), 1));
      } else {
         ev.rmValue("dauer");
      }

      if (this.zname != null) {
         ev.setValue("zugname", this.zname.getText());
      } else {
         ev.rmValue("zugname");
      }

      if (this.bname != null) {
         ev.setValue("bahnsteigname", this.bname.getText());
      } else {
         ev.rmValue("bahnsteigname");
      }

      if (this.silent != null) {
         ev.setValue("silent", this.silent.isSelected());
      } else {
         ev.rmValue("silent");
      }

      if (this.dtext != null) {
         ev.setValue("text", this.dtext.getText());
      } else {
         ev.rmValue("text");
      }
   }

   protected static enum USEDFIELDS {
      ZUGNAME,
      ZUGANTEIL,
      BAHNSTEIG,
      DAUER,
      TEXT,
      SILENT;
   }
}
