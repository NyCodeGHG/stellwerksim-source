package js.java.tools.gui.jsuggestfield;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
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

public class JSuggestField extends JTextField {
   private static final long serialVersionUID = 1756202080423312153L;
   private JDialog d;
   private Point location;
   private JList list;
   private Vector<String> data;
   private Vector<String> suggestions;
   private JSuggestField.InterruptableMatcher matcher;
   private Font busy;
   private Font regular;
   private String lastWord = "";
   private String lastChosenExistingVariable;
   private String hint;
   private LinkedList<ActionListener> listeners;
   private SuggestMatcher suggestMatcher = new ContainsMatcher();
   private boolean caseSensitive = false;

   public JSuggestField(Frame owner) {
      this.data = new Vector();
      this.suggestions = new Vector();
      this.listeners = new LinkedList();
      owner.addComponentListener(new ComponentListener() {
         public void componentHidden(ComponentEvent e) {
            JSuggestField.this.relocate();
         }

         public void componentMoved(ComponentEvent e) {
            JSuggestField.this.relocate();
         }

         public void componentResized(ComponentEvent e) {
            JSuggestField.this.relocate();
         }

         public void componentShown(ComponentEvent e) {
            JSuggestField.this.relocate();
         }
      });
      owner.addWindowListener(new WindowListener() {
         public void windowActivated(WindowEvent e) {
         }

         public void windowClosed(WindowEvent e) {
            JSuggestField.this.d.dispose();
         }

         public void windowClosing(WindowEvent e) {
            JSuggestField.this.d.dispose();
         }

         public void windowDeactivated(WindowEvent e) {
         }

         public void windowDeiconified(WindowEvent e) {
         }

         public void windowIconified(WindowEvent e) {
            JSuggestField.this.d.setVisible(false);
         }

         public void windowOpened(WindowEvent e) {
         }
      });
      this.addFocusListener(new FocusListener() {
         public void focusGained(FocusEvent e) {
            if (JSuggestField.this.getText().equals(JSuggestField.this.hint)) {
               JSuggestField.this.setText("");
            }

            JSuggestField.this.showSuggest();
         }

         public void focusLost(FocusEvent e) {
            JSuggestField.this.d.setVisible(false);
            if (JSuggestField.this.getText().equals("") && e.getOppositeComponent() != null && e.getOppositeComponent().getName() != null) {
               if (!e.getOppositeComponent().getName().equals("suggestFieldDropdownButton")) {
                  JSuggestField.this.setText(JSuggestField.this.hint);
               }
            } else if (JSuggestField.this.getText().equals("")) {
               JSuggestField.this.setText(JSuggestField.this.hint);
            }
         }
      });
      this.d = new JDialog(owner);
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
            if (this.selected == JSuggestField.this.list.getSelectedIndex()) {
               JSuggestField.this.setText(JSuggestField.this.list.getSelectedValue().toString());
               JSuggestField.this.lastChosenExistingVariable = JSuggestField.this.list.getSelectedValue().toString();
               JSuggestField.this.fireActionEvent();
               JSuggestField.this.d.setVisible(false);
            }

            this.selected = JSuggestField.this.list.getSelectedIndex();
         }
      });
      this.d.add(new JScrollPane(this.list, 20, 31));
      this.d.pack();
      this.addKeyListener(new KeyListener() {
         public void keyPressed(KeyEvent e) {
            JSuggestField.this.relocate();
         }

         public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == 27) {
               JSuggestField.this.d.setVisible(false);
            } else {
               if (e.getKeyCode() == 40) {
                  if (JSuggestField.this.d.isVisible()) {
                     JSuggestField.this.list.setSelectedIndex(JSuggestField.this.list.getSelectedIndex() + 1);
                     JSuggestField.this.list.ensureIndexIsVisible(JSuggestField.this.list.getSelectedIndex() + 1);
                     return;
                  }

                  JSuggestField.this.showSuggest();
               } else {
                  if (e.getKeyCode() == 38) {
                     JSuggestField.this.list.setSelectedIndex(JSuggestField.this.list.getSelectedIndex() - 1);
                     JSuggestField.this.list.ensureIndexIsVisible(JSuggestField.this.list.getSelectedIndex() - 1);
                     return;
                  }

                  if (e.getKeyCode() == 10 && JSuggestField.this.list.getSelectedIndex() != -1 && JSuggestField.this.suggestions.size() > 0) {
                     JSuggestField.this.setText(JSuggestField.this.list.getSelectedValue().toString());
                     JSuggestField.this.lastChosenExistingVariable = JSuggestField.this.list.getSelectedValue().toString();
                     JSuggestField.this.fireActionEvent();
                     JSuggestField.this.d.setVisible(false);
                     return;
                  }
               }

               JSuggestField.this.showSuggest();
            }
         }

         public void keyTyped(KeyEvent e) {
         }
      });
      this.regular = this.getFont();
      this.busy = new Font(this.getFont().getName(), 2, this.getFont().getSize());
   }

   public JSuggestField(Frame owner, Vector<String> data) {
      this(owner);
      this.setSuggestData(data);
   }

   public void addSelectionListener(ActionListener listener) {
      if (listener != null) {
         this.listeners.add(listener);
      }
   }

   private void fireActionEvent() {
      ActionEvent event = new ActionEvent(this, 0, this.getText());

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
         this.location = this.getLocationOnScreen();
         this.location.y = this.location.y + this.getHeight();
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
      if (!this.getText().toLowerCase().contains(this.lastWord.toLowerCase())) {
         this.suggestions.clear();
      }

      if (this.suggestions.isEmpty()) {
         this.suggestions.addAll(this.data);
      }

      if (this.matcher != null) {
         this.matcher.stop = true;
      }

      this.matcher = new JSuggestField.InterruptableMatcher();
      SwingUtilities.invokeLater(this.matcher);
      this.lastWord = this.getText();
      this.relocate();
   }

   private class InterruptableMatcher extends Thread {
      private volatile boolean stop;

      private InterruptableMatcher() {
      }

      public void run() {
         try {
            JSuggestField.this.setFont(JSuggestField.this.busy);
            Iterator<String> it = JSuggestField.this.suggestions.iterator();
            String word = JSuggestField.this.getText();

            while (it.hasNext()) {
               if (this.stop) {
                  return;
               }

               if (JSuggestField.this.caseSensitive) {
                  if (!JSuggestField.this.suggestMatcher.matches((String)it.next(), word)) {
                     it.remove();
                  }
               } else if (!JSuggestField.this.suggestMatcher.matches((String)it.next(), word.toLowerCase())) {
                  it.remove();
               }
            }

            JSuggestField.this.setFont(JSuggestField.this.regular);
            if (JSuggestField.this.suggestions.size() > 0) {
               JSuggestField.this.list.setListData(JSuggestField.this.suggestions);
               JSuggestField.this.list.setSelectedIndex(0);
               JSuggestField.this.list.ensureIndexIsVisible(0);
               JSuggestField.this.d.setVisible(true);
            } else {
               JSuggestField.this.d.setVisible(false);
            }
         } catch (Exception var3) {
         }
      }
   }
}
