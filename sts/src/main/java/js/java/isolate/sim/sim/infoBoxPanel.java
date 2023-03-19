package js.java.isolate.sim.sim;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import js.java.isolate.sim.toolkit.HyperlinkCaller;
import js.java.tools.gui.GraphicTools;
import js.java.tools.gui.HTMLEditorKitCustomCss;
import js.java.tools.gui.SmoothViewport;
import js.java.tools.gui.SimpleToggleButton.FlashingSimpleButton;
import js.java.tools.gui.layout.SimpleOneColumnLayout;

public class infoBoxPanel extends JPanel implements ChangeListener {
   private static final Color BGCOLOR = Color.WHITE;
   private infoBoxPanel.iconButton upButton;
   private infoBoxPanel.iconButton messageButton;
   private JScrollPane messagesScroller;
   private JViewport messagesViewport;
   private JPanel messagesPanel;
   private final ImageIcon newmessageIcon = new ImageIcon(this.getClass().getResource("/js/java/tools/resources/new-message16.png"));
   private final ImageIcon flagIcon = new ImageIcon(this.getClass().getResource("/js/java/tools/resources/flag16.png"));
   private final ImageIcon greyFlagIcon = GraphicTools.toGray(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/flag16.png")));
   private infoBoxPanel.message lastaddedMessage = null;
   private int lastaddedHeight = 0;
   private int lastaddedY = 0;
   private JPanel chatPanel;
   private JPanel mainPanel;
   private JPanel northPanel;
   private JPanel southPanel;

   public infoBoxPanel() {
      super();
      this.initComponents();
      this.messagesViewport();
   }

   private void messagesViewport() {
      this.messagesScroller = new JScrollPane();
      this.messagesPanel = new infoBoxPanel.MessagePanel();
      this.messagesViewport = new SmoothViewport(this.messagesPanel);
      this.messagesViewport.addChangeListener(this);
      this.messagesViewport.setBackground(BGCOLOR);
      this.messagesScroller.setVerticalScrollBarPolicy(22);
      this.messagesScroller.setHorizontalScrollBarPolicy(31);
      this.messagesScroller.setViewport(this.messagesViewport);
      this.mainPanel.add(this.chatPanel, infoBoxPanel.CARDNAME.CHAT.getName());
      this.mainPanel.add(this.messagesScroller, infoBoxPanel.CARDNAME.MESSAGE.getName());
      this.upButton = new infoBoxPanel.iconButton("/js/java/tools/resources/arrow_up8.png", false);
      this.upButton.setDestinationColor(new Color(204, 68, 68));
      this.upButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            infoBoxPanel.this.showCard(infoBoxPanel.CARDNAME.MESSAGE);
            int nComp = infoBoxPanel.this.messagesPanel.getComponentCount();

            for(int i = 0; i < nComp; ++i) {
               try {
                  infoBoxPanel.message m = (infoBoxPanel.message)infoBoxPanel.this.messagesPanel.getComponent(i);
                  if (m.isUnseen()) {
                     m.scrollRectToVisible(new Rectangle(m.getWidth(), m.getHeight() + 10));
                     break;
                  }
               } catch (ClassCastException var5) {
               }
            }
         }
      });
      this.northPanel.add(this.upButton, "Center");
      this.messageButton = new infoBoxPanel.iconButton("/js/java/tools/resources/message8.png", true);
      this.messageButton.setToolTipText("Meldungen zeigen");
      this.messageButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (infoBoxPanel.this.messageButton.isSelected()) {
               infoBoxPanel.this.showCard(infoBoxPanel.CARDNAME.MESSAGE);
            } else {
               infoBoxPanel.this.showCard(infoBoxPanel.CARDNAME.CHAT);
            }
         }
      });
      this.northPanel.add(this.messageButton, "East");
   }

   public void set2ndPanel(JComponent c) {
      this.chatPanel.removeAll();
      this.chatPanel.add(c, "Center");
      this.chatPanel.repaint();
   }

   public void stateChanged(ChangeEvent e) {
      if (this.lastaddedMessage != null && this.lastaddedHeight < this.messagesPanel.getHeight()) {
         int by = this.messagesPanel.getHeight() - this.lastaddedHeight;
         if (this.messagesPanel.getY() == this.lastaddedY - by && this.lastaddedMessage.getPreferredSize().height == this.lastaddedMessage.getHeight()) {
            this.lastaddedMessage = null;
         } else {
            this.messagesPanel.setLocation(0, this.lastaddedY - by);
         }
      }

      this.checkVisibilityState();
   }

   private void checkVisibilityState() {
      boolean unread = false;
      int nComp = this.messagesPanel.getComponentCount();

      for(int i = 0; i < nComp; ++i) {
         try {
            infoBoxPanel.message m = (infoBoxPanel.message)this.messagesPanel.getComponent(i);
            m.computeVisiblity();
            unread |= m.isUnseen();
         } catch (ClassCastException var5) {
         }
      }

      this.setFlashing(unread, false);
   }

   private void checkReadState() {
      boolean unread = false;
      int nComp = this.messagesPanel.getComponentCount();

      for(int i = 0; i < nComp; ++i) {
         try {
            infoBoxPanel.message m = (infoBoxPanel.message)this.messagesPanel.getComponent(i);
            unread |= m.isUnseen();
         } catch (ClassCastException var5) {
         }
      }

      this.setFlashing(unread, false);
   }

   private void setFlashing(boolean f, boolean force) {
      if (force || this.upButton.isFlashing() != f) {
         this.upButton.setFlashing(f);
      }

      this.checkFlashEnable();
   }

   private void checkFlashEnable() {
      this.upButton.setEnabled(this.upButton.isFlashing() || !this.messageButton.isSelected());
   }

   private void setCSS(JEditorPane box) {
      StyleSheet css = ((HTMLEditorKit)box.getEditorKit()).getStyleSheet();
      css.addRule("body { margin: 0; font-family: Dialog, sans-serif; font-size: 8px; font-style: normal; }");
      css.addRule("ul { list-style-type: square; }");
      css.addRule("li { padding-bottom: 3px; font-size: 9px; }");
   }

   private void showCard(infoBoxPanel.CARDNAME name) {
      ((CardLayout)this.mainPanel.getLayout()).show(this.mainPanel, name.getName());
      this.messageButton.setSelected(name == infoBoxPanel.CARDNAME.MESSAGE);
      this.checkVisibilityState();
   }

   private boolean isMessagePanelVisible() {
      return this.messageButton.isSelected();
   }

   public void setText(final String t, final TEXTTYPE type, final Object reference, final HyperlinkCaller caller, final String simutime) {
      if (SwingUtilities.isEventDispatchThread()) {
         this.setText_awt(t, type, reference, caller, simutime);
      } else {
         try {
            SwingUtilities.invokeAndWait(new Runnable() {
               public void run() {
                  infoBoxPanel.this.setText_awt(t, type, reference, caller, simutime);
               }
            });
         } catch (InvocationTargetException | InterruptedException var7) {
         }
      }
   }

   private void setText_awt(String t, TEXTTYPE type, Object reference, HyperlinkCaller caller, String simutime) {
      t = "<html><body>" + t + " </body></html>";
      if (this.isOverwriter(type)) {
         this.dropMessage(type, reference);
      }

      this.lastaddedMessage = null;
      infoBoxPanel.message msg = new infoBoxPanel.message(t, type, reference, caller, simutime);
      this.messagesPanel.add(msg);
      this.messagesPanel.revalidate();
      this.messagesPanel.repaint();
      this.setFlashing(true, true);
      if (this.isDirectShow(type)) {
         this.showCard(infoBoxPanel.CARDNAME.MESSAGE);
         msg.scrollRectToVisible(new Rectangle(msg.getWidth(), msg.getHeight() + 10));
      } else {
         boolean maynotshow = true;
         if (this.messagesPanel.getY() > -5) {
            maynotshow = false;
            int nComp = this.messagesPanel.getComponentCount();

            for(int i = 0; i < nComp; ++i) {
               try {
                  infoBoxPanel.message m = (infoBoxPanel.message)this.messagesPanel.getComponent(i);
                  if (m != msg) {
                     maynotshow |= m.isUnread();
                     if (maynotshow) {
                        break;
                     }
                  }
               } catch (ClassCastException var11) {
               }
            }
         }

         if (maynotshow) {
            this.lastaddedMessage = msg;
            this.lastaddedHeight = this.messagesPanel.getHeight();
            this.lastaddedY = this.messagesPanel.getY();
         } else {
            msg.scrollRectToVisible(new Rectangle(msg.getWidth(), msg.getHeight() + 10));
         }
      }
   }

   private void dropMessageFinally(infoBoxPanel.message m) {
      SwingUtilities.invokeLater(new infoBoxPanel.DropRunnable(m));
   }

   public void finishText(final Object reference) {
      if (SwingUtilities.isEventDispatchThread()) {
         this.finishText_awt(reference);
      } else {
         try {
            SwingUtilities.invokeAndWait(new Runnable() {
               public void run() {
                  infoBoxPanel.this.finishText_awt(reference);
               }
            });
         } catch (InvocationTargetException | InterruptedException var3) {
         }
      }
   }

   private void finishText_awt(Object reference) {
      boolean foundFirst = false;
      int nComp = this.messagesPanel.getComponentCount();

      for(int i = nComp - 1; i >= 0; --i) {
         try {
            infoBoxPanel.message m = (infoBoxPanel.message)this.messagesPanel.getComponent(i);
            if (m.reference == reference) {
               if (foundFirst) {
                  m.drop();
               } else {
                  m.dropDelayed();
               }

               foundFirst = true;
            }
         } catch (ClassCastException var6) {
         }
      }
   }

   private void dropMessage(TEXTTYPE t, Object reference) {
      int nComp = this.messagesPanel.getComponentCount();

      for(int i = 0; i < nComp; ++i) {
         try {
            infoBoxPanel.message m = (infoBoxPanel.message)this.messagesPanel.getComponent(i);
            if (m.reference == reference && m.type == t) {
               m.drop();
            }
         } catch (ClassCastException var6) {
         }
      }
   }

   private boolean isDirectShow(TEXTTYPE t) {
      return t == TEXTTYPE.REPLY;
   }

   private boolean isOverwriter(TEXTTYPE t) {
      return t == TEXTTYPE.STANRUF || t == TEXTTYPE.ANRUF || t == TEXTTYPE.REPLY;
   }

   private void initComponents() {
      this.chatPanel = new JPanel();
      this.mainPanel = new JPanel();
      this.northPanel = new JPanel();
      this.southPanel = new JPanel();
      this.chatPanel.setLayout(new BorderLayout());
      this.setLayout(new BorderLayout());
      this.mainPanel.setLayout(new CardLayout());
      this.add(this.mainPanel, "Center");
      this.northPanel.setLayout(new BorderLayout());
      this.add(this.northPanel, "North");
      this.southPanel.setLayout(new BorderLayout());
      this.add(this.southPanel, "South");
   }

   private static enum CARDNAME {
      CHAT("chat"),
      MESSAGE("messages");

      private String cardname;

      private CARDNAME(String n) {
         this.cardname = n;
      }

      String getName() {
         return this.cardname;
      }
   }

   private class DropRunnable implements Runnable {
      private final infoBoxPanel.message m;

      DropRunnable(infoBoxPanel.message m) {
         super();
         this.m = m;
      }

      public void run() {
         infoBoxPanel.this.messagesPanel.remove(this.m);
         infoBoxPanel.this.messagesPanel.revalidate();
         infoBoxPanel.this.messagesPanel.repaint();
         this.m.close();
      }
   }

   private static class MessagePanel extends JPanel implements Scrollable {
      MessagePanel() {
         super();
         SimpleOneColumnLayout l = new SimpleOneColumnLayout();
         l.setBottomUpDirection(true);
         this.setLayout(l);
         this.setBackground(infoBoxPanel.BGCOLOR);
      }

      public Dimension getPreferredScrollableViewportSize() {
         Dimension d = this.getPreferredSize();
         JViewport port = (JViewport)this.getParent();
         return new Dimension(port.getWidth(), d.height);
      }

      public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
         JViewport port = (JViewport)this.getParent();
         return port.getHeight() / 4;
      }

      public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
         JViewport port = (JViewport)this.getParent();
         return port.getHeight() / 4;
      }

      public boolean getScrollableTracksViewportWidth() {
         return true;
      }

      public boolean getScrollableTracksViewportHeight() {
         return false;
      }
   }

   private static enum READINGSTATE {
      UNREAD,
      TIMER_SEEN,
      TIMER_READ,
      READ;
   }

   private static class iconButton extends FlashingSimpleButton {
      iconButton(String iconname, boolean toggle) {
         super();
         if (!toggle) {
            this.setModel(new DefaultButtonModel());
         }

         this.setFocusPainted(false);
         this.setFocusable(false);
         this.setIcon(new ImageIcon(this.getClass().getResource(iconname)));
      }

      protected void paintContent(Graphics2D g2, int x, int y, int w, int h, Color fgcol, Color bgcol, boolean raised) {
         Icon icn = this.getIcon();
         if (icn != null) {
            if (!this.isEnabled()) {
               icn = this.toGray(icn);
            }

            int yy = y + (h - icn.getIconHeight()) / 2;
            int xx = x + (w - icn.getIconWidth()) / 2;
            Graphics2D gicon = (Graphics2D)g2.create(xx, yy, w - 5, h - 5);
            icn.paintIcon(this, gicon, 0, 0);
         }
      }
   }

   private class message extends JPanel implements HyperlinkListener, ActionListener, MouseListener {
      private final String text;
      private final TEXTTYPE type;
      private final Object reference;
      private HyperlinkCaller caller;
      private final JEditorPane output;
      private final boolean isHyperlink;
      private final Rectangle compute_r = new Rectangle();
      private boolean completelyVisible = false;
      private infoBoxPanel.READINGSTATE readstate = infoBoxPanel.READINGSTATE.UNREAD;
      private int fadeHeight = 0;
      private boolean dropMe = false;
      private final Timer seenTimer = new Timer(1500, this);
      private final Timer readTimer = new Timer(60000, this);
      private final Timer fadeTimer = new Timer(100, new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (message.this.dropMe) {
               infoBoxPanel.this.dropMessageFinally(message.this);
               message.this.fadeTimer.stop();
            } else {
               message.this.fadeHeight = message.this.fadeHeight + (message.this.prefHeight - message.this.fadeHeight) / 2 + 1;
               infoBoxPanel.this.messagesPanel.revalidate();
               infoBoxPanel.this.messagesPanel.repaint();
            }
         }
      });
      private Timer dropDelayedTimer = null;
      private final ActionListener dropAction = new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            message.this.drop();
         }
      };
      private BufferedImage img = null;
      private int prefHeight = 0;
      private static final int arcWidth = 20;
      private static final int arcHeight = 20;
      private final Color disabledColor = new Color(238, 238, 238);
      private final Color enabledColor = new Color(238, 238, 0);
      private boolean rolledOver = false;
      private boolean marked = false;
      private final Font titleFont = new Font("Dialog", 2, 9);

      message(String t, TEXTTYPE type, Object reference, HyperlinkCaller caller, String simutime) {
         super();
         this.text = simutime;
         this.type = type;
         this.reference = reference;
         if (t.contains("a href")) {
            this.caller = caller;
            this.isHyperlink = true;
         } else if (t.contains("b href")) {
            this.caller = caller;
            this.isHyperlink = false;
            t = t.replaceAll("b href", "a href");
         } else {
            this.caller = null;
            this.isHyperlink = false;
         }

         this.seenTimer.setRepeats(false);
         this.readTimer.setRepeats(false);
         this.setLayout(new BorderLayout());
         this.setBackground(infoBoxPanel.BGCOLOR);
         this.output = new JEditorPane();
         this.output.setOpaque(false);
         this.output.setEditable(false);
         this.output.setContentType("text/html");
         this.output.setEditorKit(new HTMLEditorKitCustomCss());
         this.output.setForeground(Color.BLACK);
         infoBoxPanel.this.setCSS(this.output);
         this.add(this.output, "Center");
         if (this.caller != null) {
            this.output.addHyperlinkListener(this);
         }

         this.output.setText(t);
         this.addMouseListener(this);
         this.output.addMouseListener(this);
         if (type == TEXTTYPE.CHAT) {
            this.dropDelayedTimer = new Timer(1800000, this.dropAction);
            this.dropDelayedTimer.setRepeats(false);
            this.dropDelayedTimer.start();
         }
      }

      public void close() {
         SwingUtilities.invokeLater(() -> {
            this.seenTimer.stop();
            this.readTimer.stop();
            this.fadeTimer.stop();
         });
      }

      public void hyperlinkUpdate(HyperlinkEvent e) {
         if (e.getEventType() == EventType.ACTIVATED) {
            String d = e.getDescription();
            if (this.caller != null) {
               this.caller.clicked(d);
               this.caller = null;
               this.drop();
               infoBoxPanel.this.checkVisibilityState();
            }
         }
      }

      public Insets getInsets() {
         return new Insets(15, 4, 1, 1);
      }

      public boolean isHyperlink() {
         return this.isHyperlink;
      }

      public Dimension getPreferredSize() {
         Dimension d = super.getPreferredSize();
         d.height -= this.fadeHeight;
         if (d.height <= 0) {
            d.height = 0;
            this.dropMe = true;
         }

         return d;
      }

      public void paint(Graphics g) {
         if (this.img == null) {
            super.paint(g);
         } else {
            g.drawImage(this.img, 0, 0, null);
            int t = this.fadeHeight * 255 / this.prefHeight;
            g.setColor(new Color(infoBoxPanel.BGCOLOR.getRed(), infoBoxPanel.BGCOLOR.getGreen(), infoBoxPanel.BGCOLOR.getBlue(), Math.min(255, t)));
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
         }
      }

      public void paintComponent(Graphics g) {
         super.paintComponent(g);
         Color col;
         if (this.readstate == infoBoxPanel.READINGSTATE.READ && !this.marked && !this.isHyperlink) {
            col = this.disabledColor;
         } else {
            col = this.enabledColor;
         }

         if (this.rolledOver) {
            col = col.brighter();
         }

         g.setColor(this.getBackground());
         g.fillRect(0, 0, this.getWidth(), this.getHeight());
         g.setColor(col);
         GraphicTools.enableGfxAA((Graphics2D)g);
         GraphicTools.enableTextAA((Graphics2D)g);
         g.fillRoundRect(1, 1, this.getWidth() - 2, this.getHeight() - 2, 20, 20);
         g.setColor(col.darker());
         g.drawLine(2, 14, this.getWidth() - 3, 14);
         g.setColor(col.brighter());
         g.drawLine(2, 15, this.getWidth() - 3, 15);
         g.setFont(this.titleFont);
         g.setColor(Color.BLACK);
         g.drawString(this.text, 10, 12);
         int iw = infoBoxPanel.this.newmessageIcon.getIconWidth();
         int ih = infoBoxPanel.this.newmessageIcon.getIconHeight();
         int ix = this.getWidth() - iw - 10;
         int iy = 2;
         g.setColor(col);
         g.draw3DRect(ix - 2, iy - 1, iw + 3, ih + 2, true);
         ImageIcon ic = null;
         if (this.marked || this.isHyperlink) {
            ic = infoBoxPanel.this.flagIcon;
         } else if (this.isUnseen()) {
            ic = infoBoxPanel.this.newmessageIcon;
         } else {
            ic = infoBoxPanel.this.greyFlagIcon;
         }

         if (ic != null) {
            ic.paintIcon(this, g, ix, iy);
         }
      }

      public void dropDelayed() {
         if (this.readstate == infoBoxPanel.READINGSTATE.READ) {
            this.drop();
         } else {
            this.dropDelayedTimer = new Timer(120000, this.dropAction);
            this.dropDelayedTimer.setRepeats(false);
            this.dropDelayedTimer.start();
         }
      }

      public void drop() {
         if (!infoBoxPanel.this.isMessagePanelVisible() || this.getWidth() <= 0 || this.getHeight() <= 0) {
            infoBoxPanel.this.dropMessageFinally(this);
         } else if (this.img == null) {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();

            BufferedImage i;
            try {
               i = gc.createCompatibleImage(this.getWidth(), this.getHeight(), 1);
            } catch (OutOfMemoryError var6) {
               Logger.getLogger("stslogger").log(Level.SEVERE, "Out of memory", var6);
               infoBoxPanel.this.dropMessageFinally(this);
               return;
            }

            Graphics2D g2 = i.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            this.paintAll(g2);
            g2.dispose();
            this.img = i;
            this.prefHeight = this.getHeight();
            this.fadeTimer.start();
         }
      }

      private void updateVisibility() {
         if (infoBoxPanel.this.isMessagePanelVisible()) {
            this.computeVisibleRect(this.compute_r);
            this.completelyVisible = this.compute_r.width == this.getWidth() && this.compute_r.height >= this.getHeight() - 10;
         } else {
            this.completelyVisible = false;
         }
      }

      void computeVisiblity() {
         if (this.readstate == infoBoxPanel.READINGSTATE.UNREAD || this.readstate == infoBoxPanel.READINGSTATE.TIMER_SEEN) {
            boolean lastCV = this.completelyVisible;
            this.updateVisibility();
            if (this.completelyVisible && lastCV != this.completelyVisible) {
               this.readstate = infoBoxPanel.READINGSTATE.TIMER_SEEN;
               this.seenTimer.start();
            } else if (!this.completelyVisible && lastCV != this.completelyVisible) {
               this.readstate = infoBoxPanel.READINGSTATE.UNREAD;
               this.seenTimer.stop();
            }
         }
      }

      public boolean isUnread() {
         return this.readstate != infoBoxPanel.READINGSTATE.READ || this.isHyperlink;
      }

      public boolean isUnseen() {
         return this.readstate == infoBoxPanel.READINGSTATE.UNREAD || this.readstate == infoBoxPanel.READINGSTATE.TIMER_SEEN || this.isHyperlink;
      }

      public void actionPerformed(ActionEvent e) {
         if (this.readstate == infoBoxPanel.READINGSTATE.TIMER_SEEN) {
            this.updateVisibility();
            if (this.completelyVisible) {
               this.readstate = infoBoxPanel.READINGSTATE.TIMER_READ;
               this.readTimer.start();
               infoBoxPanel.this.checkReadState();
               this.repaint();
            } else {
               this.readstate = infoBoxPanel.READINGSTATE.UNREAD;
            }
         } else if (this.readstate == infoBoxPanel.READINGSTATE.TIMER_READ) {
            this.readstate = infoBoxPanel.READINGSTATE.READ;
            this.repaint();
            infoBoxPanel.this.checkReadState();
         }
      }

      public void mouseClicked(MouseEvent e) {
         int iw = infoBoxPanel.this.newmessageIcon.getIconWidth();
         int ih = infoBoxPanel.this.newmessageIcon.getIconHeight();
         int ix = this.getWidth() - iw - 10;
         int iy = 2;
         if (e.getX() >= ix && e.getX() <= ix + iw && e.getY() >= iy && e.getY() <= iy + ih) {
            this.marked = !this.marked;
            this.repaint();
         }

         if (this.readstate != infoBoxPanel.READINGSTATE.READ) {
            this.readstate = infoBoxPanel.READINGSTATE.READ;
            this.repaint();
            this.readTimer.stop();
            this.seenTimer.stop();
            infoBoxPanel.this.checkReadState();
         }
      }

      public void mousePressed(MouseEvent e) {
      }

      public void mouseReleased(MouseEvent e) {
      }

      public void mouseEntered(MouseEvent e) {
         if (this.isUnread() && this.completelyVisible) {
            this.rolledOver = true;
            this.repaint();
         }
      }

      public void mouseExited(MouseEvent e) {
         this.rolledOver = false;
         this.repaint();
      }
   }
}
