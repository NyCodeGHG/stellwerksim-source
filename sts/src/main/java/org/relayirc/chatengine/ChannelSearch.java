package org.relayirc.chatengine;

import java.util.Vector;

public class ChannelSearch {
   private Server _server = null;
   private String _name = null;
   private int _minUsers = Integer.MIN_VALUE;
   private int _maxUsers = Integer.MAX_VALUE;
   private Vector _results = new Vector();
   private boolean _complete = false;
   private Vector _listeners = new Vector();

   public ChannelSearch(Server server) {
      super();
      this._server = server;
   }

   public String getName() {
      return this._name;
   }

   public void setName(String name) {
      this._name = name;
   }

   public int getMinUsers() {
      return this._minUsers;
   }

   public void setMinUsers(int min) {
      this._minUsers = min;
   }

   public int getMaxUsers() {
      return this._maxUsers;
   }

   public void setMaxUsers(int min) {
      this._maxUsers = min;
   }

   void setComplete(boolean complete) {
      this._complete = complete;
   }

   public boolean isComplete() {
      return this._complete;
   }

   public int getChannelCount() {
      return this._results == null ? -1 : this._results.size();
   }

   public Channel getChannelAt(int index) {
      return this._results == null ? null : (Channel)this._results.elementAt(index);
   }

   public void start() {
      this._results = new Vector();
      this._server.startChannelSearch(this);
   }

   public void addChannelSearchListener(ChannelSearchListener listener) {
      this._listeners.addElement(listener);
   }

   public void removeChannelSearchListener(ChannelSearchListener listener) {
      this._listeners.removeElement(listener);
   }

   void processChannel(Channel chan) {
      if (chan.getUserCount() > this._minUsers && chan.getUserCount() < this._maxUsers) {
         this._results.addElement(chan);

         for(int i = 0; i < this._listeners.size(); ++i) {
            ((ChannelSearchListener)this._listeners.elementAt(i)).searchFound(chan);
         }
      }
   }

   void searchStarted(int channels) {
      for(int i = 0; i < this._listeners.size(); ++i) {
         ((ChannelSearchListener)this._listeners.elementAt(i)).searchStarted(channels);
      }
   }

   void searchEnded() {
      for(int i = 0; i < this._listeners.size(); ++i) {
         ((ChannelSearchListener)this._listeners.elementAt(i)).searchEnded();
      }
   }
}
