package org.relayirc.chatengine;

import java.util.EventObject;
import org.relayirc.util.Debug;

public class ChannelEvent extends EventObject {
   private String _originNick = null;
   private String _originNick2 = null;
   private String _originAddress = null;
   private String _subjectNick = null;
   private String _subjectNick2 = null;
   private String _subjectAddress = null;
   private String _newNick = null;
   private Object _value = null;

   public ChannelEvent(Channel src) {
      super(src);
      Debug.println("ChannelEvent(" + src + ")");
   }

   public ChannelEvent(Channel src, Object value) {
      super(src);
      this._value = value;
      Debug.println("ChannelEvent(" + src + "," + value + ")");
   }

   public ChannelEvent(Channel src, String originNick, String originAddress, Object value) {
      super(src);
      this._originNick = originNick;
      if (!originNick.startsWith("@") && !originNick.startsWith("+") && !originNick.startsWith("%")) {
         this._originNick2 = originNick;
      } else {
         this._originNick2 = originNick.substring(1);
      }

      this._originAddress = originAddress;
      this._value = value;
      Debug.println("ChannelEvent(" + src + "," + originNick + "," + value + ")");
   }

   public ChannelEvent(Channel src, String originNick, String originAddress, String subjectNick, String subjectAddress, Object value) {
      this(src, originNick, originAddress, value);
      this._subjectNick = subjectNick;
      if (!subjectNick.startsWith("@") && !subjectNick.startsWith("+") && !subjectNick.startsWith("%")) {
         this._subjectNick2 = subjectNick;
      } else {
         this._subjectNick2 = subjectNick.substring(1);
      }

      this._subjectAddress = subjectAddress;
      Debug.println("ChannelEvent(" + src + "," + originNick + "," + subjectNick + "," + value + ")");
   }

   public String getOriginNick() {
      return this._originNick;
   }

   public String getOriginNick2() {
      return this._originNick2;
   }

   public String getOriginAddress() {
      return this._originAddress;
   }

   public String getSubjectNick() {
      return this._subjectNick;
   }

   public String getSubjectNick2() {
      return this._subjectNick2;
   }

   public String getSubjectAddress() {
      return this._subjectAddress;
   }

   public Object getValue() {
      return this._value;
   }

   public Channel getChannel() {
      return (Channel)this.getSource();
   }

   public String getChannelName() {
      return ((Channel)this.getSource()).getName();
   }
}
