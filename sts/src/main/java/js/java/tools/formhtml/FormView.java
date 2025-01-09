package js.java.tools.formhtml;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.net.URLEncoder;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractListModel;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultButtonModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.JToggleButton.ToggleButtonModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.ComponentView;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.PlainDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;

public class FormView extends ComponentView implements ActionListener {
   @Deprecated
   public static final String SUBMIT = new String("Submit Query");
   @Deprecated
   public static final String RESET = new String("Reset");
   static final String PostDataProperty = "javax.swing.JEditorPane.postdata";
   private short maxIsPreferred;

   public FormView(Element elem) {
      super(elem);
   }

   protected Component createComponent() {
      AttributeSet attr = this.getElement().getAttributes();
      Tag t = (Tag)attr.getAttribute(StyleConstants.NameAttribute);
      JComponent c = null;
      Object model = attr.getAttribute(StyleConstants.ModelAttribute);
      this.removeStaleListenerForModel(model);
      if (t == Tag.INPUT) {
         c = this.createInputComponent(attr, model);
      } else if (t == Tag.SELECT) {
         if (model instanceof OptionListModel) {
            JList list = new JList((ListModel)model);
            int size = HTML.getIntegerAttributeValue(attr, Attribute.SIZE, 1);
            list.setVisibleRowCount(size);
            list.setSelectionModel((ListSelectionModel)model);
            c = new JScrollPane(list);
         } else {
            c = new JComboBox((ComboBoxModel)model);
            this.maxIsPreferred = 3;
         }
      } else if (t == Tag.TEXTAREA) {
         JTextArea area = new JTextArea((Document)model);
         int rows = HTML.getIntegerAttributeValue(attr, Attribute.ROWS, 1);
         area.setRows(rows);
         int cols = HTML.getIntegerAttributeValue(attr, Attribute.COLS, 20);
         this.maxIsPreferred = 3;
         area.setColumns(cols);
         c = new JScrollPane(area, 22, 32);
      }

      if (c != null) {
         c.setAlignmentY(1.0F);
      }

      return c;
   }

   private JComponent createInputComponent(AttributeSet attr, Object model) {
      JComponent c = null;
      String type = (String)attr.getAttribute(Attribute.TYPE);
      if (type.equals("submit") || type.equals("reset")) {
         String value = (String)attr.getAttribute(Attribute.VALUE);
         if (value == null) {
            if (type.equals("submit")) {
               value = UIManager.getString("FormView.submitButtonText");
            } else {
               value = UIManager.getString("FormView.resetButtonText");
            }
         }

         JButton button = new JButton(value);
         if (model != null) {
            button.setModel((ButtonModel)model);
            button.addActionListener(this);
         }

         c = button;
         this.maxIsPreferred = 3;
      } else if (type.equals("checkbox")) {
         c = new JCheckBox();
         c.setOpaque(false);
         if (model != null) {
            ((JCheckBox)c).setModel((ToggleButtonModel)model);
         }

         this.maxIsPreferred = 3;
      } else if (type.equals("radio")) {
         c = new JRadioButton();
         c.setOpaque(false);
         if (model != null) {
            ((JRadioButton)c).setModel((ToggleButtonModel)model);
         }

         this.maxIsPreferred = 3;
      } else if (type.equals("text")) {
         int size = HTML.getIntegerAttributeValue(attr, Attribute.SIZE, -1);
         JTextField field;
         if (size > 0) {
            field = new JTextField();
            field.setColumns(size);
         } else {
            field = new JTextField();
            field.setColumns(20);
         }

         c = field;
         if (model != null) {
            field.setDocument((Document)model);
         }

         field.addActionListener(this);
         this.maxIsPreferred = 3;
      } else if (type.equals("password")) {
         JPasswordField fieldx = new JPasswordField();
         c = fieldx;
         if (model != null) {
            fieldx.setDocument((Document)model);
         }

         int sizex = HTML.getIntegerAttributeValue(attr, Attribute.SIZE, -1);
         fieldx.setColumns(sizex > 0 ? sizex : 20);
         fieldx.addActionListener(this);
         this.maxIsPreferred = 3;
      }

      return c;
   }

   private void removeStaleListenerForModel(Object model) {
      if (model instanceof DefaultButtonModel) {
         DefaultButtonModel buttonModel = (DefaultButtonModel)model;
         String listenerClass = "javax.swing.AbstractButton$Handler";

         for (ActionListener listener : buttonModel.getActionListeners()) {
            if (listenerClass.equals(listener.getClass().getName())) {
               buttonModel.removeActionListener(listener);
            }
         }

         for (ChangeListener listenerx : buttonModel.getChangeListeners()) {
            if (listenerClass.equals(listenerx.getClass().getName())) {
               buttonModel.removeChangeListener(listenerx);
            }
         }

         for (ItemListener listenerxx : buttonModel.getItemListeners()) {
            if (listenerClass.equals(listenerxx.getClass().getName())) {
               buttonModel.removeItemListener(listenerxx);
            }
         }
      } else if (model instanceof AbstractListModel) {
         AbstractListModel listModel = (AbstractListModel)model;
         String listenerClass1 = "javax.swing.plaf.basic.BasicListUI$Handler";
         String listenerClass2 = "javax.swing.plaf.basic.BasicComboBoxUI$Handler";

         for (ListDataListener listenerxxx : listModel.getListDataListeners()) {
            if (listenerClass1.equals(listenerxxx.getClass().getName()) || listenerClass2.equals(listenerxxx.getClass().getName())) {
               listModel.removeListDataListener(listenerxxx);
            }
         }
      } else if (model instanceof AbstractDocument) {
         String listenerClass1 = "javax.swing.plaf.basic.BasicTextUI$UpdateHandler";
         String listenerClass2 = "javax.swing.text.DefaultCaret$Handler";
         AbstractDocument docModel = (AbstractDocument)model;

         for (DocumentListener listenerxxxx : docModel.getDocumentListeners()) {
            if (listenerClass1.equals(listenerxxxx.getClass().getName()) || listenerClass2.equals(listenerxxxx.getClass().getName())) {
               docModel.removeDocumentListener(listenerxxxx);
            }
         }
      }
   }

   public float getMaximumSpan(int axis) {
      switch (axis) {
         case 0:
            if ((this.maxIsPreferred & 1) == 1) {
               super.getMaximumSpan(axis);
               return this.getPreferredSpan(axis);
            }

            return super.getMaximumSpan(axis);
         case 1:
            if ((this.maxIsPreferred & 2) == 2) {
               super.getMaximumSpan(axis);
               return this.getPreferredSpan(axis);
            }

            return super.getMaximumSpan(axis);
         default:
            return super.getMaximumSpan(axis);
      }
   }

   public void actionPerformed(ActionEvent evt) {
      Element element = this.getElement();
      StringBuilder dataBuffer = new StringBuilder();
      javax.swing.text.html.HTMLDocument doc = (javax.swing.text.html.HTMLDocument)this.getDocument();
      AttributeSet attr = element.getAttributes();
      String type = (String)attr.getAttribute(Attribute.TYPE);
      if (type.equals("submit")) {
         this.getFormData(dataBuffer);
         this.submitData(dataBuffer.toString());
      } else if (type.equals("reset")) {
         this.resetForm();
      }
   }

   public void submitData(String data) {
   }

   private void storePostData(javax.swing.text.html.HTMLDocument doc, String target, String data) {
      String propName = "javax.swing.JEditorPane.postdata";
      doc.putProperty(propName, data);
   }

   private Element getFormElement() {
      for (Element elem = this.getElement(); elem != null; elem = elem.getParentElement()) {
         if (elem.getAttributes().getAttribute(StyleConstants.NameAttribute) == Tag.FORM) {
            return elem;
         }
      }

      return null;
   }

   public void getFormData(StringBuilder buffer) {
      Element formE = this.getFormElement();
      if (formE != null) {
         ElementIterator it = new ElementIterator(formE);

         Element next;
         while ((next = it.next()) != null) {
            if (this.isControl(next)) {
               String type = (String)next.getAttributes().getAttribute(Attribute.TYPE);
               if ((type == null || !type.equals("submit") || next == this.getElement()) && (type == null || !type.equals("image"))) {
                  this.loadElementDataIntoBuffer(next, buffer);
               }
            }
         }
      }
   }

   private void loadElementDataIntoBuffer(Element elem, StringBuilder buffer) {
      AttributeSet attr = elem.getAttributes();
      String name = (String)attr.getAttribute(Attribute.NAME);
      if (name != null) {
         String value = null;
         Tag tag = (Tag)elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
         if (tag == Tag.INPUT) {
            value = this.getInputElementData(attr);
         } else if (tag == Tag.TEXTAREA) {
            value = this.getTextAreaData(attr);
         } else if (tag == Tag.SELECT) {
            this.loadSelectData(attr, buffer);
         }

         if (name != null && value != null) {
            this.appendBuffer(buffer, name, value);
         }
      }
   }

   private String getInputElementData(AttributeSet attr) {
      Object model = attr.getAttribute(StyleConstants.ModelAttribute);
      String type = (String)attr.getAttribute(Attribute.TYPE);
      String value = null;
      if (type.equals("text") || type.equals("password")) {
         Document doc = (Document)model;

         try {
            value = doc.getText(0, doc.getLength());
         } catch (BadLocationException var7) {
            value = null;
         }
      } else if (type.equals("submit") || type.equals("hidden")) {
         value = (String)attr.getAttribute(Attribute.VALUE);
         if (value == null) {
            value = "";
         }
      } else if (type.equals("radio") || type.equals("checkbox")) {
         ButtonModel m = (ButtonModel)model;
         if (m.isSelected()) {
            value = (String)attr.getAttribute(Attribute.VALUE);
            if (value == null) {
               value = "on";
            }
         }
      }

      return value;
   }

   private void setInputElementData(AttributeSet attr, String value) {
      Object model = attr.getAttribute(StyleConstants.ModelAttribute);
      String type = (String)attr.getAttribute(Attribute.TYPE);
      if (type.equals("text") || type.equals("password")) {
         Document doc = (Document)model;

         try {
            doc.remove(0, doc.getLength());
            doc.insertString(0, value, null);
         } catch (BadLocationException var7) {
         }
      } else if (type.equals("radio") || type.equals("checkbox")) {
         ButtonModel m = (ButtonModel)model;
         String pvalue = (String)attr.getAttribute(Attribute.VALUE);
         if (pvalue == null) {
            pvalue = "on";
         }

         m.setSelected(pvalue.equalsIgnoreCase(value));
      }
   }

   private String getTextAreaData(AttributeSet attr) {
      Document doc = (Document)attr.getAttribute(StyleConstants.ModelAttribute);

      try {
         return doc.getText(0, doc.getLength());
      } catch (BadLocationException var4) {
         return null;
      }
   }

   private void setTextAreaData(AttributeSet attr, String value) {
      Document doc = (Document)attr.getAttribute(StyleConstants.ModelAttribute);

      try {
         doc.remove(0, doc.getLength());
         doc.insertString(0, value, null);
      } catch (BadLocationException var5) {
      }
   }

   private void loadSelectData(AttributeSet attr, StringBuilder buffer) {
      String name = (String)attr.getAttribute(Attribute.NAME);
      if (name != null) {
         Object m = attr.getAttribute(StyleConstants.ModelAttribute);
         if (m instanceof OptionListModel) {
            OptionListModel<Option> model = (OptionListModel<Option>)m;

            for (int i = 0; i < model.getSize(); i++) {
               if (model.isSelectedIndex(i)) {
                  Option option = (Option)model.getElementAt(i);
                  this.appendBuffer(buffer, name, option.getValue());
               }
            }
         } else if (m instanceof ComboBoxModel) {
            ComboBoxModel model = (ComboBoxModel)m;
            Option option = (Option)model.getSelectedItem();
            if (option != null) {
               this.appendBuffer(buffer, name, option.getValue());
            }
         }
      }
   }

   private void setSelectData(AttributeSet attr, String value) {
      Object m = attr.getAttribute(StyleConstants.ModelAttribute);
      if (m instanceof OptionListModel) {
         OptionListModel<Option> model = (OptionListModel<Option>)m;

         for (int i = 0; i < model.getSize(); i++) {
            Option option = (Option)model.getElementAt(i);
            if (option.getValue().equalsIgnoreCase(value)) {
               model.setSelectionInterval(i, i);
               break;
            }
         }
      } else if (m instanceof ComboBoxModel) {
         ComboBoxModel model = (ComboBoxModel)m;

         for (int ix = 0; ix < model.getSize(); ix++) {
            Option option = (Option)model.getElementAt(ix);
            if (option.getValue().equalsIgnoreCase(value)) {
               model.setSelectedItem(option);
               break;
            }
         }
      }
   }

   private String getSelectData(AttributeSet attr) {
      String name = (String)attr.getAttribute(Attribute.NAME);
      if (name == null) {
         return null;
      } else {
         Object m = attr.getAttribute(StyleConstants.ModelAttribute);
         if (m instanceof OptionListModel) {
            OptionListModel<Option> model = (OptionListModel<Option>)m;

            for (int i = 0; i < model.getSize(); i++) {
               if (model.isSelectedIndex(i)) {
                  Option option = (Option)model.getElementAt(i);
                  return option.getValue();
               }
            }
         } else if (m instanceof ComboBoxModel) {
            ComboBoxModel model = (ComboBoxModel)m;
            Option option = (Option)model.getSelectedItem();
            if (option != null) {
               return option.getValue();
            }
         }

         return null;
      }
   }

   private void appendBuffer(StringBuilder buffer, String name, String value) {
      if (buffer.length() > 0) {
         buffer.append('&');
      }

      String encodedName = URLEncoder.encode(name);
      buffer.append(encodedName);
      buffer.append('=');
      String encodedValue = URLEncoder.encode(value);
      buffer.append(encodedValue);
   }

   private boolean isControl(Element elem) {
      return elem.isLeaf();
   }

   void resetForm() {
      Element parent = this.getFormElement();
      if (parent != null) {
         ElementIterator it = new ElementIterator(parent);

         Element next;
         while ((next = it.next()) != null) {
            if (this.isControl(next)) {
               AttributeSet elemAttr = next.getAttributes();
               Object m = elemAttr.getAttribute(StyleConstants.ModelAttribute);
               if (m instanceof TextAreaDocument) {
                  TextAreaDocument doc = (TextAreaDocument)m;
                  doc.reset();
               } else if (m instanceof PlainDocument) {
                  try {
                     PlainDocument doc = (PlainDocument)m;
                     doc.remove(0, doc.getLength());
                     if (matchNameAttribute(elemAttr, Tag.INPUT)) {
                        String value = (String)elemAttr.getAttribute(Attribute.VALUE);
                        if (value != null) {
                           doc.insertString(0, value, null);
                        }
                     }
                  } catch (BadLocationException var10) {
                  }
               } else if (!(m instanceof OptionListModel)) {
                  if (m instanceof OptionComboBoxModel) {
                     OptionComboBoxModel model = (OptionComboBoxModel)m;
                     Option option = model.getInitialSelection();
                     if (option != null) {
                        model.setSelectedItem(option);
                     }
                  } else if (m instanceof ToggleButtonModel) {
                     boolean checked = (String)elemAttr.getAttribute(Attribute.CHECKED) != null;
                     ToggleButtonModel model = (ToggleButtonModel)m;
                     model.setSelected(checked);
                  }
               } else {
                  OptionListModel model = (OptionListModel)m;
                  int size = model.getSize();

                  for (int i = 0; i < size; i++) {
                     model.removeIndexInterval(i, i);
                  }

                  BitSet selectionRange = model.getInitialSelection();

                  for (int i = 0; i < selectionRange.size(); i++) {
                     if (selectionRange.get(i)) {
                        model.addSelectionInterval(i, i);
                     }
                  }
               }
            }
         }
      }
   }

   static boolean matchNameAttribute(AttributeSet attr, Tag tag) {
      Object o = attr.getAttribute(StyleConstants.NameAttribute);
      if (o instanceof Tag) {
         Tag name = (Tag)o;
         if (name == tag) {
            return true;
         }
      }

      return false;
   }

   public String getName() {
      Element elem = this.getElement();
      AttributeSet attr = elem.getAttributes();
      return (String)attr.getAttribute(Attribute.NAME);
   }

   public String getData() {
      Element elem = this.getElement();
      AttributeSet attr = elem.getAttributes();
      String value = null;
      Tag tag = (Tag)elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
      if (tag == Tag.INPUT) {
         value = this.getInputElementData(attr);
      } else if (tag == Tag.TEXTAREA) {
         value = this.getTextAreaData(attr);
      } else if (tag == Tag.SELECT) {
         StringBuilder buffer = new StringBuilder();
         this.loadSelectData(attr, buffer);
         value = buffer.toString();
      }

      return value;
   }

   private void setValue(Element elem, String value) {
      AttributeSet attr = elem.getAttributes();
      Tag tag = (Tag)elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
      if (tag == Tag.INPUT) {
         this.setInputElementData(attr, value);
      } else if (tag == Tag.TEXTAREA) {
         this.setTextAreaData(attr, value);
      } else if (tag == Tag.SELECT) {
         this.setSelectData(attr, value);
      }
   }

   public void setValues(Map<String, String> values) {
      Element formE = this.getFormElement();
      if (formE != null) {
         ElementIterator it = new ElementIterator(formE);

         Element next;
         while ((next = it.next()) != null) {
            if (this.isControl(next)) {
               AttributeSet attr = next.getAttributes();
               String name = (String)attr.getAttribute(Attribute.NAME);
               if (name != null) {
                  String v = (String)values.get(name);
                  if (v != null) {
                     this.setValue(next, v);
                  }
               }
            }
         }
      }
   }

   private String getValue(Element elem) {
      AttributeSet attr = elem.getAttributes();
      String value = null;
      Tag tag = (Tag)elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
      if (tag == Tag.INPUT) {
         value = this.getInputElementData(attr);
      } else if (tag == Tag.TEXTAREA) {
         value = this.getTextAreaData(attr);
      } else if (tag == Tag.SELECT) {
         value = this.getSelectData(attr);
      }

      return value;
   }

   public Map<String, String> getValues() {
      HashMap<String, String> ret = new HashMap();
      Element formE = this.getFormElement();
      if (formE != null) {
         ElementIterator it = new ElementIterator(formE);

         Element next;
         while ((next = it.next()) != null) {
            if (this.isControl(next)) {
               AttributeSet attr = next.getAttributes();
               String name = (String)attr.getAttribute(Attribute.NAME);
               String v = this.getValue(next);
               if (name != null && v != null) {
                  ret.put(name, v);
               }
            }
         }
      }

      return ret;
   }
}
