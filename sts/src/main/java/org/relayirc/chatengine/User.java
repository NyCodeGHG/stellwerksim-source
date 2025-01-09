package org.relayirc.chatengine;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.NotActiveException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Date;

public class User implements IChatObject, Serializable {
   static final long serialVersionUID = 7267124067453694537L;
   private String _nick = "";
   private String _altNick = "";
   private String _desc = "";
   private String _userName = "Unknown";
   private String _fullName = "Unknown";
   private String _hostName = "Unknown";
   private String _serverName = "Unknown";
   private String _serverDesc = "Unknown";
   private String _channels = "Unknown";
   private boolean _isOnline = false;
   private int _idleTime = 0;
   private Date _signonTime = new Date();
   private Date _updateTime = new Date();
   private transient PropertyChangeSupport _propChangeSupport = null;

   public User(String nick) {
      this(nick, "Unknown", "Unknown", "Unknown");
   }

   public User(String nick, String altNick, String userName, String fullName) {
      this._nick = nick;
      this._altNick = altNick;
      this._userName = userName;
      this._fullName = fullName;
      this._updateTime = new Date();
      this._propChangeSupport = new PropertyChangeSupport(this);
   }

   public boolean equals(Object obj) {
      return obj instanceof User && ((User)obj).getNick().equals(this.getNick());
   }

   public void onUpdateComplete() {
      this.setUpdateTime(new Date());
      this._propChangeSupport.firePropertyChange("updated", null, null);
   }

   public void addPropertyChangeListener(PropertyChangeListener listener) {
      this._propChangeSupport.addPropertyChangeListener(listener);
   }

   public void removePropertyChangeListener(PropertyChangeListener listener) {
      this._propChangeSupport.removePropertyChangeListener(listener);
   }

   @Override
   public String getDescription() {
      return this._desc;
   }

   @Override
   public void setDescription(String desc) {
      this._desc = desc;
   }

   public String getAltNick() {
      return this._altNick;
   }

   public void setAltNick(String altnick) {
      this._altNick = altnick;
   }

   public String getNick() {
      return this._nick;
   }

   public void setNick(String nick) {
      this._nick = nick;
   }

   public String getName() {
      return this._nick;
   }

   public void setName(String nick) {
      this._nick = nick;
   }

   public String getUserName() {
      return this._userName;
   }

   public void setUserName(String userName) {
      this._userName = userName;
   }

   public String getFullName() {
      return this._fullName;
   }

   public void setFullName(String fullName) {
      this._fullName = fullName;
   }

   public String getHostName() {
      return this._hostName;
   }

   public void setHostName(String hostName) {
      this._hostName = hostName;
   }

   public String getServerName() {
      return this._serverName;
   }

   public void setServerName(String serverName) {
      this._serverName = serverName;
   }

   public String getServerDesc() {
      return this._serverDesc;
   }

   public void setServerDesc(String serverDesc) {
      this._serverDesc = serverDesc;
   }

   public String getChannels() {
      return this._channels;
   }

   public void setChannels(String channels) {
      this._channels = channels;
   }

   public boolean isOnline() {
      return this._isOnline;
   }

   public void setOnline(boolean isOnline) {
      this._isOnline = isOnline;
   }

   public int getIdleTime() {
      return this._idleTime;
   }

   public void setIdleTime(int idleTime) {
      this._idleTime = idleTime;
   }

   public Date getSignonTime() {
      return this._signonTime;
   }

   public void setSignonTime(Date signonTime) {
      this._signonTime = signonTime;
   }

   public Date getUpdateTime() {
      return this._updateTime;
   }

   public void setUpdateTime(Date updateTime) {
      this._updateTime = updateTime;
   }

   public String toString() {
      return this._nick;
   }

   public String toDescription() {
      StringBuilder sb = new StringBuilder(500);
      sb.append("WHOIS Information for ").append(this.getNick()).append('\n');
      sb.append("As of ").append(this.getUpdateTime()).append('\n');
      sb.append("").append('\n');
      sb.append("Nick: ").append(this.getNick()).append('\n');
      sb.append("Address: ").append(this.getUserName()).append('@').append(this.getHostName()).append('\n');
      sb.append("Server: ").append(this.getServerName()).append(" (").append(this.getServerDesc()).append(')').append('\n');
      sb.append('\n');
      sb.append("Channels: ").append(this.getChannels()).append('\n');
      sb.append('\n');
      if (this._isOnline) {
         sb.append("Seconds Idle: ").append(this.getIdleTime()).append('\n');
         sb.append("On since: ").append(this.getSignonTime()).append('\n');
      } else {
         sb.append("User is currently OFFLINE").append('\n');
      }

      return sb.toString();
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      try {
         in.defaultReadObject();
      } catch (NotActiveException var3) {
         var3.printStackTrace();
      }

      this._propChangeSupport = new PropertyChangeSupport(this);
   }
}
