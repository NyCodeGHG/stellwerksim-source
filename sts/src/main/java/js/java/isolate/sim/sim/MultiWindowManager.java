package js.java.isolate.sim.sim;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;

public class MultiWindowManager implements ItemListener, ActionListener {
   private final JRadioButtonMenuItem winLayout1;
   private final JRadioButtonMenuItem winLayout2;
   private final JRadioButtonMenuItem winLayoutMulti;
   private final JRadioButtonMenuItem winLayoutMulti2;
   private final stellwerksim_main my_main;
   private final JSplitPane mainSplitPane;
   private final JPanel controlPanel;
   private final zugUndPlanPanel fahrplanPanel;
   private final MultiWindowManager.StateChange SINGLE = new MultiWindowManager.State_Single();
   private final MultiWindowManager.StateChange TWO = new MultiWindowManager.State_Two();
   private final MultiWindowManager.StateChange MULTI = new MultiWindowManager.State_Multi();
   private final MultiWindowManager.StateChange MULTI2 = new MultiWindowManager.State_Multi2();
   private MultiWindowManager.StateChange currentState = this.SINGLE;

   MultiWindowManager(
      stellwerksim_main m,
      JSplitPane mainSplitPane,
      JPanel controlPanel,
      zugUndPlanPanel fahrplanPanel,
      JRadioButtonMenuItem winLayout1,
      JRadioButtonMenuItem winLayout2,
      JRadioButtonMenuItem winLayoutMulti,
      JRadioButtonMenuItem winLayoutMulti2
   ) {
      this.my_main = m;
      this.winLayout1 = winLayout1;
      this.winLayout2 = winLayout2;
      this.winLayoutMulti = winLayoutMulti;
      this.winLayoutMulti2 = winLayoutMulti2;
      this.mainSplitPane = mainSplitPane;
      this.controlPanel = controlPanel;
      this.fahrplanPanel = fahrplanPanel;
      winLayout1.addItemListener(this);
      winLayout2.addItemListener(this);
      winLayoutMulti.addItemListener(this);
      winLayoutMulti2.addItemListener(this);
   }

   public void itemStateChanged(ItemEvent e) {
      if (e.getStateChange() == 1) {
         MultiWindowManager.StateChange newState = null;
         if (this.winLayout1.isSelected()) {
            newState = this.SINGLE;
         } else if (this.winLayout2.isSelected()) {
            newState = this.TWO;
         } else if (this.winLayoutMulti.isSelected()) {
            newState = this.MULTI;
         } else if (this.winLayoutMulti2.isSelected()) {
            newState = this.MULTI2;
         }

         if (newState != this.currentState) {
            MultiWindowManager.StateChange oldState = this.currentState;
            this.currentState = newState;
            oldState.unset();
            this.currentState.set();
         }
      }
   }

   public void close() {
      this.currentState.unset();
   }

   public void actionPerformed(ActionEvent e) {
      this.winLayout1.setSelected(true);
   }

   private interface StateChange {
      void set();

      void unset();
   }

   private class State_Multi implements MultiWindowManager.StateChange {
      private List<externalPanel> windows = null;

      private State_Multi() {
      }

      protected boolean asDialog() {
         return true;
      }

      @Override
      public void set() {
         this.windows = MultiWindowManager.this.fahrplanPanel.extractSplit(MultiWindowManager.this, MultiWindowManager.this.mainSplitPane, this.asDialog());
         int h = MultiWindowManager.this.my_main.getHeight() / this.windows.size();
         int y = MultiWindowManager.this.my_main.getY();

         for (externalPanel ep : this.windows) {
            ep.setWindowPosition(y, h);
            y += h;
            ep.createStateSaver();
         }
      }

      @Override
      public void unset() {
         MultiWindowManager.this.fahrplanPanel.restoreSplit(this.windows);
         this.windows = null;
      }
   }

   private class State_Multi2 extends MultiWindowManager.State_Multi {
      private State_Multi2() {
      }

      @Override
      protected boolean asDialog() {
         return false;
      }
   }

   private class State_Single implements MultiWindowManager.StateChange {
      private State_Single() {
      }

      @Override
      public void set() {
         MultiWindowManager.this.mainSplitPane.setBottomComponent(MultiWindowManager.this.controlPanel);
      }

      @Override
      public void unset() {
         MultiWindowManager.this.mainSplitPane.remove(MultiWindowManager.this.controlPanel);
      }
   }

   private class State_Two implements MultiWindowManager.StateChange {
      private externalPanel eCONTROL = null;

      private State_Two() {
      }

      @Override
      public void set() {
         this.eCONTROL = new externalPanel(MultiWindowManager.this.my_main, MultiWindowManager.this, "Steuerung", false);
         this.eCONTROL.setPanel(MultiWindowManager.this.controlPanel, 500, 300);
         this.eCONTROL.createStateSaver();
         MultiWindowManager.this.fahrplanPanel.bind(this.eCONTROL);
      }

      @Override
      public void unset() {
         this.eCONTROL.rmPanel();
         MultiWindowManager.this.mainSplitPane.setBottomComponent(MultiWindowManager.this.controlPanel);
         this.eCONTROL = null;
      }
   }
}
