package js.java.tools.formhtml;

import java.net.URL;
import java.util.HashMap;
import javax.swing.ButtonGroup;
import javax.swing.DefaultButtonModel;
import javax.swing.JToggleButton.ToggleButtonModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.PlainDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.StyleSheet;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLDocument.HTMLReader.SpecialAction;
import javax.swing.text.html.HTMLDocument.HTMLReader.TagAction;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import js.java.tools.gui.BoundedPlainDocument;

public class HTMLDocument extends javax.swing.text.html.HTMLDocument {
   public HTMLDocument(StyleSheet styles) {
      super(styles);
   }

   public ParserCallback getReader(int pos) {
      Object desc = this.getProperty("stream");
      if (desc instanceof URL) {
         this.setBase((URL)desc);
      }

      return new HTMLDocument.HTMLReader(pos);
   }

   public class HTMLReader extends javax.swing.text.html.HTMLDocument.HTMLReader {
      boolean inTextArea = false;
      TextAreaDocument textAreaDocument = null;
      Option option;
      private HashMap<String, ButtonGroup> radioButtonGroupsMap;

      public HTMLReader(int offset) {
         super(HTMLDocument.this, offset);
         TagAction fa = new HTMLDocument.HTMLReader.FormAction();
         this.registerTag(Tag.INPUT, fa);
         this.registerTag(Tag.OPTION, fa);
         this.registerTag(Tag.SELECT, fa);
         this.registerTag(Tag.TEXTAREA, fa);
      }

      public void handleText(char[] data, int pos) {
         if (this.inTextArea) {
            this.textAreaContent(data);
         } else if (this.option != null) {
            this.option.setLabel(new String(data));
         } else {
            super.handleText(data, pos);
         }
      }

      public class FormAction extends SpecialAction {
         Object selectModel;
         int optionCount;

         public FormAction() {
            super(HTMLReader.this);
         }

         public void start(Tag t, MutableAttributeSet attr) {
            if (t == Tag.INPUT) {
               String type = (String)attr.getAttribute(Attribute.TYPE);
               if (type == null) {
                  type = "text";
                  attr.addAttribute(Attribute.TYPE, "text");
               }

               this.setModel(type, attr);
            } else if (t == Tag.TEXTAREA) {
               HTMLReader.this.inTextArea = true;
               HTMLReader.this.textAreaDocument = new TextAreaDocument();
               attr.addAttribute(StyleConstants.ModelAttribute, HTMLReader.this.textAreaDocument);
            } else if (t == Tag.SELECT) {
               int size = HTML.getIntegerAttributeValue(attr, Attribute.SIZE, 1);
               boolean multiple = attr.getAttribute(Attribute.MULTIPLE) != null;
               if (size <= 1 && !multiple) {
                  this.selectModel = new OptionComboBoxModel();
               } else {
                  OptionListModel<Option> m = new OptionListModel<>();
                  if (multiple) {
                     m.setSelectionMode(2);
                  }

                  this.selectModel = m;
               }

               attr.addAttribute(StyleConstants.ModelAttribute, this.selectModel);
            }

            if (t == Tag.OPTION) {
               HTMLReader.this.option = new Option(attr);
               if (this.selectModel instanceof OptionListModel) {
                  OptionListModel<Option> m = (OptionListModel)this.selectModel;
                  m.addElement(HTMLReader.this.option);
                  if (HTMLReader.this.option.isSelected()) {
                     m.addSelectionInterval(this.optionCount, this.optionCount);
                     m.setInitialSelection(this.optionCount);
                  }
               } else if (this.selectModel instanceof OptionComboBoxModel) {
                  OptionComboBoxModel<Option> m = (OptionComboBoxModel)this.selectModel;
                  m.addElement(HTMLReader.this.option);
                  if (HTMLReader.this.option.isSelected()) {
                     m.setSelectedItem(HTMLReader.this.option);
                     m.setInitialSelection(HTMLReader.this.option);
                  }
               }

               ++this.optionCount;
            } else {
               super.start(t, attr);
            }
         }

         public void end(Tag t) {
            if (t == Tag.OPTION) {
               HTMLReader.this.option = null;
            } else {
               if (t == Tag.SELECT) {
                  this.selectModel = null;
                  this.optionCount = 0;
               } else if (t == Tag.TEXTAREA) {
                  HTMLReader.this.inTextArea = false;
                  HTMLReader.this.textAreaDocument.storeInitialText();
               }

               super.end(t);
            }
         }

         void setModel(String type, MutableAttributeSet attr) {
            if (type.equals("submit") || type.equals("reset") || type.equals("image")) {
               attr.addAttribute(StyleConstants.ModelAttribute, new DefaultButtonModel());
            } else if (type.equals("text") || type.equals("password")) {
               int maxLength = HTML.getIntegerAttributeValue(attr, Attribute.MAXLENGTH, -1);
               Document doc;
               if (maxLength > 0) {
                  doc = new BoundedPlainDocument(maxLength);
               } else {
                  doc = new PlainDocument();
               }

               String value = (String)attr.getAttribute(Attribute.VALUE);

               try {
                  doc.insertString(0, value, null);
               } catch (BadLocationException var7) {
               }

               attr.addAttribute(StyleConstants.ModelAttribute, doc);
            } else if (type.equals("file")) {
               attr.addAttribute(StyleConstants.ModelAttribute, new PlainDocument());
            } else if (type.equals("checkbox") || type.equals("radio")) {
               ToggleButtonModel model = new ToggleButtonModel();
               if (type.equals("radio")) {
                  String name = (String)attr.getAttribute(Attribute.NAME);
                  if (HTMLReader.this.radioButtonGroupsMap == null) {
                     HTMLReader.this.radioButtonGroupsMap = new HashMap();
                  }

                  ButtonGroup radioButtonGroup = (ButtonGroup)HTMLReader.this.radioButtonGroupsMap.get(name);
                  if (radioButtonGroup == null) {
                     radioButtonGroup = new ButtonGroup();
                     HTMLReader.this.radioButtonGroupsMap.put(name, radioButtonGroup);
                  }

                  model.setGroup(radioButtonGroup);
               }

               boolean checked = attr.getAttribute(Attribute.CHECKED) != null;
               model.setSelected(checked);
               attr.addAttribute(StyleConstants.ModelAttribute, model);
            }
         }
      }
   }
}
