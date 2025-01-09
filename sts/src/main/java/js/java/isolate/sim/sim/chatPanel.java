package js.java.isolate.sim.sim;

import de.deltaga.eb.EventHandler;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import js.java.schaltungen.UserContext;
import js.java.schaltungen.UserContextMini;
import js.java.schaltungen.chatcomng.ChannelsNameParser;
import js.java.schaltungen.chatcomng.ChatShowChannelEvent;
import js.java.schaltungen.chatcomng.ConnectedChannelsEvent;
import js.java.schaltungen.chatcomng.IrcChannel;
import js.java.schaltungen.chatcomng.IrcLine;
import js.java.schaltungen.chatcomng.UserChatMessageEvent;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.tools.gui.HTMLEditorKitCustomCss;

public class chatPanel extends chatPanelBase implements SessionClose {
   private final UserContext uc;
   private final LinkedList<String> text = new LinkedList();
   private final ArrayList lineLinks = new ArrayList();
   private final HyperlinkListener linkListener = new HyperlinkListener() {
      public void hyperlinkUpdate(HyperlinkEvent e) {
         if (e.getEventType() == EventType.ACTIVATED) {
            int idx = Integer.parseInt(e.getDescription());
            Object evt = chatPanel.this.lineLinks.get(idx);
            chatPanel.this.uc.busPublish(evt);
         }
      }
   };
   private final HashMap<String, chatPanel.FilterElement> filter = new HashMap();
   private final TreeSet<String> knownChannels = new TreeSet();
   private final HashSet<String> filterChannels = new HashSet();
   private JTextPane ircTextPane;
   private JScrollPane outputPane;
   private JPopupMenu popupMenu;
   private JTabbedPane tabPanel;

   public chatPanel(UserContext uc) {
      this.uc = uc;
      this.initComponents();

      for (ChannelsNameParser.ChannelName cc : new ChannelsNameParser(uc.getParameter(UserContextMini.DATATYPE.MIXFILTERCHANNELS), 20)) {
         this.filterChannels.add(cc.name);
      }

      uc.busSubscribe(this);
      this.buildStyle(this.ircTextPane);
      uc.addCloseObject(this);
   }

   private void buildStyle(JTextPane pane) {
      DefaultCaret caret = (DefaultCaret)pane.getCaret();
      caret.setUpdatePolicy(2);
      pane.addHyperlinkListener(this.linkListener);
      StyleSheet css = ((HTMLEditorKit)pane.getEditorKit()).getStyleSheet();
      css.addRule("body { margin: 0; font-family: Dialog, sans-serif; font-size: 10px; font-style: normal; }");
   }

   @Override
   public void close() {
      this.lineLinks.clear();
      this.filter.clear();
      this.knownChannels.clear();
   }

   @EventHandler
   public void newChannel(ConnectedChannelsEvent ch) {
      SwingUtilities.invokeLater(() -> this.newChannelAwt(ch));
   }

   private void newChannelAwt(ConnectedChannelsEvent ch) {
      this.knownChannels.clear();
      this.popupMenu.removeAll();
      TreeSet<IrcChannel> channelsSorted = new TreeSet(new Comparator<IrcChannel>() {
         public int compare(IrcChannel o1, IrcChannel o2) {
            return o1.channel.compareTo(o2.channel);
         }
      });
      channelsSorted.addAll(ch.channels);

      for (final IrcChannel i : channelsSorted) {
         if (i.userChannel) {
            this.knownChannels.add(i.channel.title);
            final JCheckBoxMenuItem menu = new JCheckBoxMenuItem(i.channel.title);
            menu.setSelected(this.filter.containsKey(i.channel.title));
            menu.addItemListener(new ItemListener() {
               public void itemStateChanged(ItemEvent ex) {
                  chatPanel.this.changeFilter(menu.isSelected(), i.channel.name, i.channel.title);
               }
            });
            this.popupMenu.add(menu);
         }
      }

      Iterator<Entry<String, chatPanel.FilterElement>> it = this.filter.entrySet().iterator();

      while (it.hasNext()) {
         Entry<String, chatPanel.FilterElement> e = (Entry<String, chatPanel.FilterElement>)it.next();
         if (!this.knownChannels.contains(e.getKey())) {
            this.tabPanel.remove((Component)e.getValue());
            it.remove();
         }
      }
   }

   @EventHandler
   public void ircMessage(IrcLine event) {
      if (!event.statusMessage) {
         SwingUtilities.invokeLater(() -> {
            if (event.channel == null || !this.filterChannels.contains(event.channel)) {
               String line = "";
               if (event.clickEvent != null) {
                  this.lineLinks.add(event.clickEvent);
                  int idx = this.lineLinks.size() - 1;
                  line = "<span style='font-size:8px;'><b>[<a href='" + idx + "'>" + event.channeltitel + "</a>]</b></span> ";
               }

               line = line + event.line;
               this.text.add(line);
            }

            while (this.text.size() > 100) {
               this.text.removeFirst();
            }

            this.ircTextPane.setText("<html>" + (String)this.text.stream().collect(Collectors.joining()) + "</html>");
            if (event.channel != null) {
               chatPanel.FilterElement f = (chatPanel.FilterElement)this.filter.get(event.channel);
               if (f != null) {
                  f.append(event.line);
                  if (this.tabPanel.getSelectedComponent() != f) {
                     int tidx = this.tabPanel.indexOfComponent(f);
                     this.tabPanel.setForegroundAt(tidx, Color.RED);
                  }
               }
            }
         });
      }
   }

   private void changeFilter(boolean selected, String name, String title) {
      if (selected) {
         chatPanel.FilterElement f = (chatPanel.FilterElement)this.filter.get(name);
         if (f == null) {
            f = new chatPanel.FilterElement(name);
            this.filter.put(name, f);
            this.tabPanel.add(f, title);
         }

         this.tabPanel.setSelectedComponent(f);
      } else {
         chatPanel.FilterElement f = (chatPanel.FilterElement)this.filter.get(name);
         if (f != null) {
            this.tabPanel.remove(f);
            this.filter.remove(name);
         }
      }
   }

   private void initComponents() {
      this.popupMenu = new JPopupMenu();
      this.tabPanel = new JTabbedPane();
      this.outputPane = new JScrollPane();
      this.ircTextPane = new JTextPane();
      this.setBorder(BorderFactory.createTitledBorder(null, "Chat Meldungen", 0, 0, new Font("Tahoma", 0, 9)));
      this.setDoubleBuffered(false);
      this.setLayout(new BorderLayout());
      this.tabPanel.setTabLayoutPolicy(1);
      this.tabPanel.setTabPlacement(3);
      this.tabPanel
         .setToolTipText("<html>\n<b>Rechte Maustaste</b>: Kanalfilter<br>\n<b>Doppelklick linke Maustaste auf Tab</b>: Kanal im Chat Fenster\n</html>\n");
      this.tabPanel.setComponentPopupMenu(this.popupMenu);
      this.tabPanel.setFocusable(false);
      this.tabPanel.addChangeListener(new ChangeListener() {
         public void stateChanged(ChangeEvent evt) {
            chatPanel.this.tabPanelStateChanged(evt);
         }
      });
      this.tabPanel.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent evt) {
            chatPanel.this.tabPanelMouseClicked(evt);
         }
      });
      this.ircTextPane.setEditable(false);
      this.ircTextPane.setBackground(UIManager.getDefaults().getColor("FormattedTextField.inactiveBackground"));
      this.ircTextPane.setContentType("text/html");
      this.ircTextPane.setEditorKit(new HTMLEditorKitCustomCss());
      this.ircTextPane
         .setText("<html>\n<b>Rechte Maustaste</b>: Kanalfilter<br>\n<b>Doppelklick linke Maustaste auf Tab</b>: Kanal im Chat Fenster\n</html>\n");
      this.ircTextPane.setComponentPopupMenu(this.popupMenu);
      this.outputPane.setViewportView(this.ircTextPane);
      this.tabPanel.addTab("Mix", this.outputPane);
      this.add(this.tabPanel, "Center");
   }

   private void tabPanelStateChanged(ChangeEvent evt) {
      this.tabPanel.setForegroundAt(this.tabPanel.getSelectedIndex(), Color.BLACK);
   }

   private void tabPanelMouseClicked(MouseEvent evt) {
      if (evt.getClickCount() >= 2) {
         Component c = this.tabPanel.getSelectedComponent();
         if (c instanceof chatPanel.FilterElement) {
            chatPanel.FilterElement f = (chatPanel.FilterElement)c;
            this.uc.busPublish(new ChatShowChannelEvent(f.channel));
         }
      }
   }

   private class FilterElement extends JPanel implements ActionListener {
      final LinkedList<String> text = new LinkedList();
      final JScrollPane scroller = new JScrollPane();
      final JTextPane textPane = new JTextPane();
      final JTextField textField = new JTextField() {
         private static final String HINT = "Hier Chat-Text für Kanal eingeben, RETURN zum Senden";

         public void paint(Graphics g) {
            super.paint(g);
            if (this.getText().length() == 0) {
               int h = this.getHeight();
               ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
               Insets ins = this.getInsets();
               FontMetrics fm = g.getFontMetrics();
               int c0 = this.getBackground().getRGB();
               int c1 = this.getForeground().getRGB();
               int m = -16843010;
               int c2 = ((c0 & m) >>> 1) + ((c1 & m) >>> 1);
               g.setColor(new Color(c2, true));
               g.drawString("Hier Chat-Text für Kanal eingeben, RETURN zum Senden", ins.left + 5, h / 2 + fm.getAscent() / 2 - 2);
            }
         }
      };
      final String channel;

      FilterElement(String channel) {
         this.channel = channel;
         this.setLayout(new BorderLayout());
         this.scroller.setViewportView(this.textPane);
         this.scroller.setDoubleBuffered(false);
         this.textPane.setComponentPopupMenu(chatPanel.this.popupMenu);
         this.textPane.setEditable(false);
         this.textPane.setBackground(UIManager.getDefaults().getColor("FormattedTextField.inactiveBackground"));
         this.textPane.setContentType("text/html");
         this.textPane.setEditorKit(new HTMLEditorKitCustomCss());
         chatPanel.this.buildStyle(this.textPane);
         this.add(this.scroller, "Center");
         this.add(this.textField, "South");
         this.textField.addActionListener(this);
      }

      void append(String str) {
         this.text.add(str);

         while (this.text.size() > 100) {
            this.text.removeFirst();
         }

         this.textPane.setText("<html>" + (String)this.text.stream().collect(Collectors.joining()) + "</html>");
      }

      public void actionPerformed(ActionEvent e) {
         String msg = this.textField.getText().trim();
         this.textField.setText("");
         if (!msg.isEmpty()) {
            chatPanel.this.uc.busPublish(new UserChatMessageEvent(this.channel, msg));
         }
      }
   }
}
