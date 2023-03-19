package js.java.schaltungen.toplevelMessage;

import js.java.tools.gui.SwingTools;

public enum States {
   START {
      @Override
      void run(TopLevelMessage frame) {
         frame.xy(frame.totalWidth, 20);
         frame.setVisible(true);
         SwingTools.toFront(frame);
         frame.setState(MOVEIN);
      }
   },
   MOVEIN {
      @Override
      void run(TopLevelMessage frame) {
         int dx = frame.totalWidth - frame.getWidth();
         if (frame.x > dx) {
            frame.xy(frame.x - (frame.x - dx) / 2 - 1, frame.y);
         } else if (frame.delay == 0) {
            frame.setState(SHOWWAITING2);
         } else {
            frame.waiting = frame.delay;
            frame.setState(SHOWWAITING);
         }
      }
   },
   MOVEOUT {
      @Override
      void run(TopLevelMessage frame) {
         int dx = frame.totalWidth - frame.getWidth();
         if (frame.x >= frame.totalWidth) {
            frame.end();
         } else {
            frame.xy(frame.x + (frame.x - dx) / 2 + 1, frame.y);
         }
      }
   },
   SHOWWAITING {
      @Override
      void run(TopLevelMessage frame) {
         --frame.waiting;
         if (frame.waiting <= 0) {
            frame.setState(MOVEOUT);
         }
      }
   },
   SHOWWAITING2 {
      @Override
      void run(TopLevelMessage frame) {
      }
   };

   private States() {
   }

   abstract void run(TopLevelMessage var1);
}
