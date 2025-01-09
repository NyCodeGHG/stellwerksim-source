package com.ezware.common;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class SwingBean {
   private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

   public void addPropertyListener(PropertyChangeListener pl) {
      this.propertyChangeSupport.addPropertyChangeListener(pl);
   }

   public void addPropertyListener(String propertyName, PropertyChangeListener pl) {
      this.propertyChangeSupport.addPropertyChangeListener(propertyName, pl);
   }

   public void removePropertyListener(PropertyChangeListener pl) {
      this.propertyChangeSupport.removePropertyChangeListener(pl);
   }

   public void removePropertyListener(String propertyName, PropertyChangeListener pl) {
      this.propertyChangeSupport.removePropertyChangeListener(propertyName, pl);
   }

   protected <T> boolean firePropertyChange(String propertyName, T oldValue, T newValue) {
      boolean result = false;
      if (result = !this.sameValues(oldValue, newValue)) {
         this.propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
      }

      return result;
   }

   private <T> boolean sameValues(T a, T b) {
      return a == b || a != null && b != null && a.equals(b);
   }
}
