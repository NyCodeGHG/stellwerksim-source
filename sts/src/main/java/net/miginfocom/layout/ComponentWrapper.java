package net.miginfocom.layout;

public interface ComponentWrapper {
   int TYPE_UNSET = -1;
   int TYPE_UNKNOWN = 0;
   int TYPE_CONTAINER = 1;
   int TYPE_LABEL = 2;
   int TYPE_TEXT_FIELD = 3;
   int TYPE_TEXT_AREA = 4;
   int TYPE_BUTTON = 5;
   int TYPE_LIST = 6;
   int TYPE_TABLE = 7;
   int TYPE_SCROLL_PANE = 8;
   int TYPE_IMAGE = 9;
   int TYPE_PANEL = 10;
   int TYPE_COMBO_BOX = 11;
   int TYPE_SLIDER = 12;
   int TYPE_SPINNER = 13;
   int TYPE_PROGRESS_BAR = 14;
   int TYPE_TREE = 15;
   int TYPE_CHECK_BOX = 16;
   int TYPE_SCROLL_BAR = 17;
   int TYPE_SEPARATOR = 18;

   Object getComponent();

   int getX();

   int getY();

   int getWidth();

   int getHeight();

   int getScreenLocationX();

   int getScreenLocationY();

   int getMinimumWidth(int var1);

   int getMinimumHeight(int var1);

   int getPreferredWidth(int var1);

   int getPreferredHeight(int var1);

   int getMaximumWidth(int var1);

   int getMaximumHeight(int var1);

   void setBounds(int var1, int var2, int var3, int var4);

   boolean isVisible();

   int getBaseline(int var1, int var2);

   boolean hasBaseline();

   ContainerWrapper getParent();

   float getPixelUnitFactor(boolean var1);

   int getHorizontalScreenDPI();

   int getVerticalScreenDPI();

   int getScreenWidth();

   int getScreenHeight();

   String getLinkId();

   int getLayoutHashCode();

   int[] getVisualPadding();

   void paintDebugOutline();

   int getComponetType(boolean var1);
}
