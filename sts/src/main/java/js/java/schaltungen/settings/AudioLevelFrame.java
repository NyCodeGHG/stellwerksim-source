package js.java.schaltungen.settings;

import de.deltaga.eb.EventBusService;
import de.deltaga.eb.EventHandler;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import js.java.schaltungen.UserContextMini;
import js.java.schaltungen.audio.AudioPlayer;
import js.java.schaltungen.audio.AudioSettings;
import js.java.schaltungen.audio.AudioSettingsChangedEvent;

public class AudioLevelFrame extends JPanel {
   private final UserContextMini uc;
   private final AudioSettings asettings;
   private final LinkedList<AudioLevelPanel> items = new LinkedList();
   private JPanel contentPanel;

   AudioLevelFrame(UserContextMini uc) {
      this.uc = uc;
      this.initComponents();
      this.asettings = uc.getAudioSettings();
      EventBusService.getInstance().subscribe(this);
      this.addItem("Chat Ton", this.asettings.playChatSettings(), AudioPlayer.SAMPLES.CHAT);
      this.addItem("Bü Ton", this.asettings.playBÜSettings(), AudioPlayer.SAMPLES.TABLE);
      this.addItem("Akzeptor Ton", this.asettings.playÜGSettings(), AudioPlayer.SAMPLES.TABLE);
      this.addItem("Zug Einfahrt Ton", this.asettings.playZugSettings(), AudioPlayer.SAMPLES.TABLE);
      this.addItem("Funk Ton", this.asettings.playMessageSettings(), AudioPlayer.SAMPLES.MELDUNG);
      this.addItem("Zählwerk Ton", this.asettings.playCounterSettings(), AudioPlayer.SAMPLES.COUNTER);
   }

   private void addItem(String title, AudioSettings.SoundSettings asetting, AudioPlayer.SAMPLES sample) {
      AudioLevelPanel alp = new AudioLevelPanel(title, asetting, sample);
      this.contentPanel.add(alp);
      this.items.add(alp);
   }

   @EventHandler(
      weak = true
   )
   public void checkAudio(AudioSettingsChangedEvent event) {
      for (AudioLevelPanel alp : this.items) {
         alp.update();
      }
   }

   private void initComponents() {
      this.contentPanel = new JPanel();
      this.setLayout(new BorderLayout());
      this.contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
      this.contentPanel.setLayout(new GridLayout(0, 1));
      this.add(this.contentPanel, "Center");
   }

   private void formWindowClosed(WindowEvent evt) {
   }
}
