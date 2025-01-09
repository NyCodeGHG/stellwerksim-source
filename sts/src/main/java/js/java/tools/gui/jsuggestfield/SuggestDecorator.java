package js.java.tools.gui.jsuggestfield;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class SuggestDecorator {
   private static final long serialVersionUID = 1756202080423312153L;
   private JDialog d;
   private Point location;
   private JList list;
   private Vector<String> data;
   private Vector<String> suggestions;
   private SuggestDecorator.InterruptableMatcher matcher;
   private Font busy;
   private Font regular;
   private String lastWord = "";
   private String lastChosenExistingVariable;
   private String hint;
   private LinkedList<ActionListener> listeners;
   private SuggestMatcher suggestMatcher = new ContainsMatcher();
   private boolean caseSensitive = false;
   private final JTextField textfield;

   public SuggestDecorator(Window owner, JTextField textfield) {
      this.textfield = textfield;
      this.data = new Vector();
      this.suggestions = new Vector();
      this.listeners = new LinkedList();
      owner.addComponentListener(new ComponentListener() {
         public void componentHidden(ComponentEvent e) {
            SuggestDecorator.this.relocate();
         }

         public void componentMoved(ComponentEvent e) {
            SuggestDecorator.this.relocate();
         }

         public void componentResized(ComponentEvent e) {
            SuggestDecorator.this.relocate();
         }

         public void componentShown(ComponentEvent e) {
            SuggestDecorator.this.relocate();
         }
      });
      owner.addWindowListener(new WindowListener() {
         public void windowActivated(WindowEvent e) {
         }

         public void windowClosed(WindowEvent e) {
            SuggestDecorator.this.d.dispose();
         }

         public void windowClosing(WindowEvent e) {
            SuggestDecorator.this.d.dispose();
         }

         public void windowDeactivated(WindowEvent e) {
         }

         public void windowDeiconified(WindowEvent e) {
         }

         public void windowIconified(WindowEvent e) {
            SuggestDecorator.this.d.setVisible(false);
         }

         public void windowOpened(WindowEvent e) {
         }
      });
      this.d = new JDialog(owner);
      this.init();
   }

   public SuggestDecorator(Window owner, JTextField textfield, Vector<String> data) {
      this(owner, textfield);
      this.setSuggestData(data);
   }

   private void init() {
      this.textfield.addFocusListener(new FocusListener() {
         public void focusGained(FocusEvent e) {
            if (SuggestDecorator.this.textfield.getText().equals(SuggestDecorator.this.hint)) {
               SuggestDecorator.this.textfield.setText("");
            }

            SuggestDecorator.this.showSuggest();
         }

         public void focusLost(FocusEvent e) {
            SuggestDecorator.this.d.setVisible(false);
            if (SuggestDecorator.this.textfield.getText().isEmpty() && e.getOppositeComponent() != null && e.getOppositeComponent().getName() != null) {
               if (!e.getOppositeComponent().getName().equals("suggestFieldDropdownButton")) {
                  SuggestDecorator.this.textfield.setText(SuggestDecorator.this.hint);
               }
            } else if (SuggestDecorator.this.textfield.getText().isEmpty()) {
               SuggestDecorator.this.textfield.setText(SuggestDecorator.this.hint);
            }
         }
      });
      this.d.setUndecorated(true);
      this.d.setFocusableWindowState(false);
      this.d.setFocusable(false);
      this.list = new JList();
      this.list.addMouseListener(new MouseListener() {
         private int selected;

         public void mouseClicked(MouseEvent e) {
         }

         public void mouseEntered(MouseEvent e) {
         }

         public void mouseExited(MouseEvent e) {
         }

         public void mousePressed(MouseEvent e) {
         }

         public void mouseReleased(MouseEvent e) {
            if (this.selected == SuggestDecorator.this.list.getSelectedIndex()) {
               SuggestDecorator.this.textfield.setText(SuggestDecorator.this.list.getSelectedValue().toString());
               SuggestDecorator.this.lastChosenExistingVariable = SuggestDecorator.this.list.getSelectedValue().toString();
               SuggestDecorator.this.fireActionEvent();
               SuggestDecorator.this.d.setVisible(false);
            }

            this.selected = SuggestDecorator.this.list.getSelectedIndex();
         }
      });
      this.d.add(new JScrollPane(this.list, 20, 31));
      this.d.pack();
      this.textfield.addKeyListener(new KeyListener() {
         public void keyPressed(KeyEvent e) {
            SuggestDecorator.this.relocate();
         }

         public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == 27) {
               SuggestDecorator.this.d.setVisible(false);
            } else {
               if (e.getKeyCode() == 40) {
                  if (SuggestDecorator.this.d.isVisible()) {
                     if (SuggestDecorator.this.list.getModel().getSize() > 0) {
                        SuggestDecorator.this.list.setSelectedIndex(SuggestDecorator.this.list.getSelectedIndex() + 1);
                        SuggestDecorator.this.list.ensureIndexIsVisible(SuggestDecorator.this.list.getSelectedIndex() + 1);
                     }

                     return;
                  }

                  SuggestDecorator.this.showSuggest();
               } else {
                  if (e.getKeyCode() == 38) {
                     if (SuggestDecorator.this.list.getModel().getSize() > 0) {
                        SuggestDecorator.this.list.setSelectedIndex(SuggestDecorator.this.list.getSelectedIndex() - 1);
                        SuggestDecorator.this.list.ensureIndexIsVisible(SuggestDecorator.this.list.getSelectedIndex() - 1);
                     }

                     return;
                  }

                  if (e.getKeyCode() == 10 && SuggestDecorator.this.list.getSelectedIndex() != -1 && SuggestDecorator.this.suggestions.size() > 0) {
                     SuggestDecorator.this.textfield.setText(SuggestDecorator.this.list.getSelectedValue().toString());
                     SuggestDecorator.this.lastChosenExistingVariable = SuggestDecorator.this.list.getSelectedValue().toString();
                     SuggestDecorator.this.fireActionEvent();
                     SuggestDecorator.this.d.setVisible(false);
                     return;
                  }
               }

               SuggestDecorator.this.showSuggest();
            }
         }

         public void keyTyped(KeyEvent e) {
         }
      });
      this.regular = this.textfield.getFont();
      this.busy = new Font(this.textfield.getFont().getName(), 2, this.textfield.getFont().getSize());
   }

   public void addSelectionListener(ActionListener listener) {
      if (listener != null) {
         this.listeners.add(listener);
      }
   }

   private void fireActionEvent() {
      ActionEvent event = new ActionEvent(this, 0, this.textfield.getText());

      for (ActionListener listener : this.listeners) {
         listener.actionPerformed(event);
      }
   }

   public String getHint() {
      return this.hint;
   }

   public String getLastChosenExistingVariable() {
      return this.lastChosenExistingVariable;
   }

   public Vector<String> getSuggestData() {
      return (Vector<String>)this.data.clone();
   }

   public void hideSuggest() {
      this.d.setVisible(false);
   }

   public boolean isCaseSensitive() {
      return this.caseSensitive;
   }

   public boolean isSuggestVisible() {
      return this.d.isVisible();
   }

   private void relocate() {
      try {
         this.location = this.textfield.getLocationOnScreen();
         this.location.y = this.location.y + this.textfield.getHeight();
         this.d.setLocation(this.location);
      } catch (IllegalComponentStateException var2) {
      }
   }

   public void removeSelectionListener(ActionListener listener) {
      this.listeners.remove(listener);
   }

   public void setCaseSensitive(boolean caseSensitive) {
      this.caseSensitive = caseSensitive;
   }

   public void setHint(String hint) {
      this.hint = hint;
   }

   public void setMaximumSuggestSize(Dimension size) {
      this.d.setMaximumSize(size);
   }

   public void setMinimumSuggestSize(Dimension size) {
      this.d.setMinimumSize(size);
   }

   public void setPreferredSuggestSize(Dimension size) {
      this.d.setPreferredSize(size);
   }

   public boolean setSuggestData(Vector<String> data) {
      if (data == null) {
         return false;
      } else {
         this.data = data;
         this.list.setListData(data);
         return true;
      }
   }

   public void setSuggestMatcher(SuggestMatcher suggestMatcher) {
      this.suggestMatcher = suggestMatcher;
   }

   public void showSuggest() {
      if (!this.textfield.getText().toLowerCase().contains(this.lastWord.toLowerCase())) {
         this.suggestions.clear();
      }

      if (this.suggestions.isEmpty()) {
         this.suggestions.addAll(this.data);
      }

      if (this.matcher != null) {
         this.matcher.stop = true;
      }

      this.matcher = new SuggestDecorator.InterruptableMatcher();
      SwingUtilities.invokeLater(this.matcher);
      this.lastWord = this.textfield.getText();
      this.relocate();
   }

   private class InterruptableMatcher extends Thread {
      private volatile boolean stop;

      private InterruptableMatcher() {
      }

      public void run() {
         try {
            SuggestDecorator.this.textfield.setFont(SuggestDecorator.this.busy);
            Iterator<String> it = SuggestDecorator.this.suggestions.iterator();
            String word = SuggestDecorator.this.textfield.getText();

            while (it.hasNext()) {
               if (this.stop) {
                  return;
               }

               if (SuggestDecorator.this.caseSensitive) {
                  if (!SuggestDecorator.this.suggestMatcher.matches((String)it.next(), word)) {
                     it.remove();
                  }
               } else if (!SuggestDecorator.this.suggestMatcher.matches((String)it.next(), word.toLowerCase())) {
                  it.remove();
               }
            }

            SuggestDecorator.this.textfield.setFont(SuggestDecorator.this.regular);
            if (SuggestDecorator.this.suggestions.size() > 0) {
               SuggestDecorator.this.list.setListData(SuggestDecorator.this.suggestions);
               SuggestDecorator.this.list.setSelectedIndex(0);
               SuggestDecorator.this.list.ensureIndexIsVisible(0);
               SuggestDecorator.this.d.setVisible(true);
            } else {
               SuggestDecorator.this.d.setVisible(false);
            }
         } catch (Exception var3) {
         }
      }
   }
}
