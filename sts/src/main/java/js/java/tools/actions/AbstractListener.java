package js.java.tools.actions;

import java.util.EventListener;

public interface AbstractListener<T extends AbstractEvent> extends EventListener {
   void action(T var1);
}
