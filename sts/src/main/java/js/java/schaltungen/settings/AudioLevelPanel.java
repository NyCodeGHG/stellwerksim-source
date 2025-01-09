package js.java.schaltungen.settings;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import js.java.schaltungen.audio.AudioPlayer;
import js.java.schaltungen.audio.AudioSettings;

public class AudioLevelPanel extends JPanel {
   private final String name;
   private final AudioSettings.SoundSettings asetting;
   private final AudioPlayer aplayer;
   private final AudioPlayer playPlayer;
   private boolean opening;
   private JButton defaultButton;
   private JCheckBox enabledCB;
   private JSlider gainSlider;
   private JButton playButton;

   public AudioLevelPanel(String name, AudioSettings.SoundSettings asetting, AudioPlayer.SAMPLES sample) {
      this.name = name;
      this.asetting = asetting;
      this.initComponents();
      this.aplayer = new AudioPlayer(AudioPlayer.SAMPLES.CHAT);
      this.playPlayer = new AudioPlayer(sample);
      this.opening = true;
      this.update();
      this.opening = false;
   }

   void update() {
      if (!this.gainSlider.getValueIsAdjusting()) {
         this.gainSlider.setValue((int)this.asetting.getGain());
      }

      this.enabledCB.setSelected(this.asetting.isEnabled());
   }

   private void initComponents() {
      this.enabledCB = new JCheckBox();
      this.gainSlider = new JSlider();
      this.defaultButton = new JButton();
      this.playButton = new JButton();
      this.setBorder(BorderFactory.createTitledBorder(this.name));
      this.setLayout(new GridBagLayout());
      this.enabledCB.setText("hörbar");
      this.enabledCB.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            AudioLevelPanel.this.enabledCBActionPerformed(evt);
         }
      });
      this.add(this.enabledCB, new GridBagConstraints());
      this.gainSlider.setMajorTickSpacing(10);
      this.gainSlider.setMaximum(0);
      this.gainSlider.setMinimum(-40);
      this.gainSlider.setMinorTickSpacing(5);
      this.gainSlider.setPaintLabels(true);
      this.gainSlider.setPaintTicks(true);
      this.gainSlider.addChangeListener(new ChangeListener() {
         public void stateChanged(ChangeEvent evt) {
            AudioLevelPanel.this.gainSliderStateChanged(evt);
         }
      });
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.add(this.gainSlider, gridBagConstraints);
      this.defaultButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/schaltungen/p/default_24x24.png")));
      this.defaultButton.setToolTipText("Default Lautstärke");
      this.defaultButton.setMargin(new Insets(2, 2, 2, 2));
      this.defaultButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            AudioLevelPanel.this.defaultButtonActionPerformed(evt);
         }
      });
      this.add(this.defaultButton, new GridBagConstraints());
      this.playButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/arrow_right.png")));
      this.playButton.setToolTipText("Abspielen");
      this.playButton.setMargin(new Insets(2, 2, 2, 2));
      this.playButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            AudioLevelPanel.this.playButtonActionPerformed(evt);
         }
      });
      this.add(this.playButton, new GridBagConstraints());
   }

   private void enabledCBActionPerformed(ActionEvent evt) {
      this.asetting.setEnabled(this.enabledCB.isSelected());
   }

   private void gainSliderStateChanged(ChangeEvent evt) {
      if (!this.opening) {
         this.aplayer.setGain((float)this.gainSlider.getValue());
         this.aplayer.play();
         if (!this.gainSlider.getValueIsAdjusting()) {
            this.asetting.setGain((float)this.gainSlider.getValue());
            this.playButtonActionPerformed(null);
         }
      }
   }

   private void defaultButtonActionPerformed(ActionEvent evt) {
      this.asetting.setGain(this.asetting.getDefaultGain());
      this.asetting.setEnabled(true);
   }

   private void playButtonActionPerformed(ActionEvent evt) {
      this.playPlayer.setGain((float)this.gainSlider.getValue());
      this.playPlayer.play();
   }
}
