package net.miginfocom.layout;

public interface ContainerWrapper extends ComponentWrapper {
   ComponentWrapper[] getComponents();

   int getComponentCount();

   Object getLayout();

   boolean isLeftToRight();

   void paintDebugCell(int var1, int var2, int var3, int var4);
}
