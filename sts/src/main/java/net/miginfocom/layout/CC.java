package net.miginfocom.layout;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.util.ArrayList;

public final class CC implements Externalizable {
   private static final BoundSize DEF_GAP = BoundSize.NULL_SIZE;
   static final String[] DOCK_SIDES = new String[]{"north", "west", "south", "east"};
   private int dock = -1;
   private UnitValue[] pos = null;
   private UnitValue[] padding = null;
   private Boolean flowX = null;
   private int skip = 0;
   private int split = 1;
   private int spanX = 1;
   private int spanY = 1;
   private int cellX = -1;
   private int cellY = 0;
   private String tag = null;
   private String id = null;
   private int hideMode = -1;
   private DimConstraint hor = new DimConstraint();
   private DimConstraint ver = new DimConstraint();
   private BoundSize newline = null;
   private BoundSize wrap = null;
   private boolean boundsInGrid = true;
   private boolean external = false;
   private Float pushX = null;
   private Float pushY = null;
   private static final String[] EMPTY_ARR = new String[0];
   private transient String[] linkTargets = null;

   String[] getLinkTargets() {
      if (this.linkTargets == null) {
         ArrayList<String> targets = new ArrayList(2);
         if (this.pos != null) {
            for (int i = 0; i < this.pos.length; i++) {
               this.addLinkTargetIDs(targets, this.pos[i]);
            }
         }

         this.linkTargets = targets.isEmpty() ? EMPTY_ARR : (String[])targets.toArray(new String[targets.size()]);
      }

      return this.linkTargets;
   }

   private void addLinkTargetIDs(ArrayList<String> targets, UnitValue uv) {
      if (uv != null) {
         String linkId = uv.getLinkTargetId();
         if (linkId != null) {
            targets.add(linkId);
         } else {
            for (int i = uv.getSubUnitCount() - 1; i >= 0; i--) {
               UnitValue subUv = uv.getSubUnitValue(i);
               if (subUv.isLinkedDeep()) {
                  this.addLinkTargetIDs(targets, subUv);
               }
            }
         }
      }
   }

   public final CC endGroupX(String s) {
      this.hor.setEndGroup(s);
      return this;
   }

   public final CC sizeGroupX(String s) {
      this.hor.setSizeGroup(s);
      return this;
   }

   public final CC minWidth(String size) {
      this.hor.setSize(LayoutUtil.derive(this.hor.getSize(), ConstraintParser.parseUnitValue(size, true), null, null));
      return this;
   }

   public final CC width(String size) {
      this.hor.setSize(ConstraintParser.parseBoundSize(size, false, true));
      return this;
   }

   public final CC maxWidth(String size) {
      this.hor.setSize(LayoutUtil.derive(this.hor.getSize(), null, null, ConstraintParser.parseUnitValue(size, true)));
      return this;
   }

   public final CC gapX(String before, String after) {
      if (before != null) {
         this.hor.setGapBefore(ConstraintParser.parseBoundSize(before, true, true));
      }

      if (after != null) {
         this.hor.setGapAfter(ConstraintParser.parseBoundSize(after, true, true));
      }

      return this;
   }

   public final CC alignX(String align) {
      this.hor.setAlign(ConstraintParser.parseUnitValueOrAlign(align, true, null));
      return this;
   }

   public final CC growPrioX(int p) {
      this.hor.setGrowPriority(p);
      return this;
   }

   public final CC growPrio(int... widthHeight) {
      switch (widthHeight.length) {
         case 2:
            this.growPrioY(widthHeight[1]);
         case 1:
            this.growPrioX(widthHeight[0]);
            return this;
         default:
            throw new IllegalArgumentException("Illegal argument count: " + widthHeight.length);
      }
   }

   public final CC growX() {
      this.hor.setGrow(ResizeConstraint.WEIGHT_100);
      return this;
   }

   public final CC growX(float w) {
      this.hor.setGrow(new Float(w));
      return this;
   }

   public final CC grow(float... widthHeight) {
      switch (widthHeight.length) {
         case 2:
            this.growY(widthHeight[1]);
         case 1:
            this.growX(widthHeight[0]);
            return this;
         default:
            throw new IllegalArgumentException("Illegal argument count: " + widthHeight.length);
      }
   }

   public final CC shrinkPrioX(int p) {
      this.hor.setShrinkPriority(p);
      return this;
   }

   public final CC shrinkPrio(int... widthHeight) {
      switch (widthHeight.length) {
         case 2:
            this.shrinkPrioY(widthHeight[1]);
         case 1:
            this.shrinkPrioX(widthHeight[0]);
            return this;
         default:
            throw new IllegalArgumentException("Illegal argument count: " + widthHeight.length);
      }
   }

   public final CC shrinkX(float w) {
      this.hor.setShrink(new Float(w));
      return this;
   }

   public final CC shrink(float... widthHeight) {
      switch (widthHeight.length) {
         case 2:
            this.shrinkY(widthHeight[1]);
         case 1:
            this.shrinkX(widthHeight[0]);
            return this;
         default:
            throw new IllegalArgumentException("Illegal argument count: " + widthHeight.length);
      }
   }

   public final CC endGroupY(String s) {
      this.ver.setEndGroup(s);
      return this;
   }

   public final CC endGroup(String... xy) {
      switch (xy.length) {
         case 2:
            this.endGroupY(xy[1]);
         case 1:
            this.endGroupX(xy[0]);
            return this;
         default:
            throw new IllegalArgumentException("Illegal argument count: " + xy.length);
      }
   }

   public final CC sizeGroupY(String s) {
      this.ver.setSizeGroup(s);
      return this;
   }

   public final CC sizeGroup(String... xy) {
      switch (xy.length) {
         case 2:
            this.sizeGroupY(xy[1]);
         case 1:
            this.sizeGroupX(xy[0]);
            return this;
         default:
            throw new IllegalArgumentException("Illegal argument count: " + xy.length);
      }
   }

   public final CC minHeight(String size) {
      this.ver.setSize(LayoutUtil.derive(this.ver.getSize(), ConstraintParser.parseUnitValue(size, false), null, null));
      return this;
   }

   public final CC height(String size) {
      this.ver.setSize(ConstraintParser.parseBoundSize(size, false, false));
      return this;
   }

   public final CC maxHeight(String size) {
      this.ver.setSize(LayoutUtil.derive(this.ver.getSize(), null, null, ConstraintParser.parseUnitValue(size, false)));
      return this;
   }

   public final CC gapY(String before, String after) {
      if (before != null) {
         this.ver.setGapBefore(ConstraintParser.parseBoundSize(before, true, false));
      }

      if (after != null) {
         this.ver.setGapAfter(ConstraintParser.parseBoundSize(after, true, false));
      }

      return this;
   }

   public final CC alignY(String align) {
      this.ver.setAlign(ConstraintParser.parseUnitValueOrAlign(align, false, null));
      return this;
   }

   public final CC growPrioY(int p) {
      this.ver.setGrowPriority(p);
      return this;
   }

   public final CC growY() {
      this.ver.setGrow(ResizeConstraint.WEIGHT_100);
      return this;
   }

   public final CC growY(Float w) {
      this.ver.setGrow(w);
      return this;
   }

   public final CC shrinkPrioY(int p) {
      this.ver.setShrinkPriority(p);
      return this;
   }

   public final CC shrinkY(float w) {
      this.ver.setShrink(new Float(w));
      return this;
   }

   public final CC hideMode(int mode) {
      this.setHideMode(mode);
      return this;
   }

   public final CC id(String s) {
      this.setId(s);
      return this;
   }

   public final CC tag(String tag) {
      this.setTag(tag);
      return this;
   }

   public final CC cell(int... colRowWidthHeight) {
      switch (colRowWidthHeight.length) {
         case 4:
            this.setSpanY(colRowWidthHeight[3]);
         case 3:
            this.setSpanX(colRowWidthHeight[2]);
         case 2:
            this.setCellY(colRowWidthHeight[1]);
         case 1:
            this.setCellX(colRowWidthHeight[0]);
            return this;
         default:
            throw new IllegalArgumentException("Illegal argument count: " + colRowWidthHeight.length);
      }
   }

   public final CC span(int... cells) {
      if (cells != null && cells.length != 0) {
         if (cells.length == 1) {
            this.setSpanX(cells[0]);
            this.setSpanY(1);
         } else {
            this.setSpanX(cells[0]);
            this.setSpanY(cells[1]);
         }
      } else {
         this.setSpanX(2097051);
         this.setSpanY(1);
      }

      return this;
   }

   public final CC gap(String... args) {
      switch (args.length) {
         case 4:
            this.gapBottom(args[3]);
         case 3:
            this.gapTop(args[2]);
         case 2:
            this.gapRight(args[1]);
         case 1:
            this.gapLeft(args[0]);
            return this;
         default:
            throw new IllegalArgumentException("Illegal argument count: " + args.length);
      }
   }

   public final CC gapBefore(String boundsSize) {
      this.hor.setGapBefore(ConstraintParser.parseBoundSize(boundsSize, true, true));
      return this;
   }

   public final CC gapAfter(String boundsSize) {
      this.hor.setGapAfter(ConstraintParser.parseBoundSize(boundsSize, true, true));
      return this;
   }

   public final CC gapTop(String boundsSize) {
      this.ver.setGapBefore(ConstraintParser.parseBoundSize(boundsSize, true, false));
      return this;
   }

   public final CC gapLeft(String boundsSize) {
      this.hor.setGapBefore(ConstraintParser.parseBoundSize(boundsSize, true, true));
      return this;
   }

   public final CC gapBottom(String boundsSize) {
      this.ver.setGapAfter(ConstraintParser.parseBoundSize(boundsSize, true, false));
      return this;
   }

   public final CC gapRight(String boundsSize) {
      this.hor.setGapAfter(ConstraintParser.parseBoundSize(boundsSize, true, true));
      return this;
   }

   public final CC spanY() {
      return this.spanY(2097051);
   }

   public final CC spanY(int cells) {
      this.setSpanY(cells);
      return this;
   }

   public final CC spanX() {
      return this.spanX(2097051);
   }

   public final CC spanX(int cells) {
      this.setSpanX(cells);
      return this;
   }

   public final CC push() {
      return this.pushX().pushY();
   }

   public final CC push(Float weightX, Float weightY) {
      return this.pushX(weightX).pushY(weightY);
   }

   public final CC pushY() {
      return this.pushY(ResizeConstraint.WEIGHT_100);
   }

   public final CC pushY(Float weight) {
      this.setPushY(weight);
      return this;
   }

   public final CC pushX() {
      return this.pushX(ResizeConstraint.WEIGHT_100);
   }

   public final CC pushX(Float weight) {
      this.setPushX(weight);
      return this;
   }

   public final CC split(int parts) {
      this.setSplit(parts);
      return this;
   }

   public final CC split() {
      this.setSplit(2097051);
      return this;
   }

   public final CC skip(int cells) {
      this.setSkip(cells);
      return this;
   }

   public final CC skip() {
      this.setSkip(1);
      return this;
   }

   public final CC external() {
      this.setExternal(true);
      return this;
   }

   public final CC flowX() {
      this.setFlowX(Boolean.TRUE);
      return this;
   }

   public final CC flowY() {
      this.setFlowX(Boolean.FALSE);
      return this;
   }

   public final CC grow() {
      this.growX();
      this.growY();
      return this;
   }

   public final CC newline() {
      this.setNewline(true);
      return this;
   }

   public final CC newline(String gapSize) {
      BoundSize bs = ConstraintParser.parseBoundSize(gapSize, true, this.flowX != null && !this.flowX);
      if (bs != null) {
         this.setNewlineGapSize(bs);
      } else {
         this.setNewline(true);
      }

      return this;
   }

   public final CC wrap() {
      this.setWrap(true);
      return this;
   }

   public final CC wrap(String gapSize) {
      BoundSize bs = ConstraintParser.parseBoundSize(gapSize, true, this.flowX != null && !this.flowX);
      if (bs != null) {
         this.setWrapGapSize(bs);
      } else {
         this.setWrap(true);
      }

      return this;
   }

   public final CC dockNorth() {
      this.setDockSide(0);
      return this;
   }

   public final CC dockWest() {
      this.setDockSide(1);
      return this;
   }

   public final CC dockSouth() {
      this.setDockSide(2);
      return this;
   }

   public final CC dockEast() {
      this.setDockSide(3);
      return this;
   }

   public final CC x(String x) {
      return this.corrPos(x, 0);
   }

   public final CC y(String y) {
      return this.corrPos(y, 1);
   }

   public final CC x2(String x2) {
      return this.corrPos(x2, 2);
   }

   public final CC y2(String y2) {
      return this.corrPos(y2, 3);
   }

   private CC corrPos(String uv, int ix) {
      UnitValue[] b = this.getPos();
      if (b == null) {
         b = new UnitValue[4];
      }

      b[ix] = ConstraintParser.parseUnitValue(uv, ix % 2 == 0);
      this.setPos(b);
      this.setBoundsInGrid(true);
      return this;
   }

   public final CC pos(String x, String y) {
      UnitValue[] b = this.getPos();
      if (b == null) {
         b = new UnitValue[4];
      }

      b[0] = ConstraintParser.parseUnitValue(x, true);
      b[1] = ConstraintParser.parseUnitValue(y, false);
      this.setPos(b);
      this.setBoundsInGrid(false);
      return this;
   }

   public final CC pos(String x, String y, String x2, String y2) {
      this.setPos(
         new UnitValue[]{
            ConstraintParser.parseUnitValue(x, true),
            ConstraintParser.parseUnitValue(y, false),
            ConstraintParser.parseUnitValue(x2, true),
            ConstraintParser.parseUnitValue(y2, false)
         }
      );
      this.setBoundsInGrid(false);
      return this;
   }

   public final CC pad(int top, int left, int bottom, int right) {
      this.setPadding(new UnitValue[]{new UnitValue((float)top), new UnitValue((float)left), new UnitValue((float)bottom), new UnitValue((float)right)});
      return this;
   }

   public final CC pad(String pad) {
      this.setPadding(pad != null ? ConstraintParser.parseInsets(pad, false) : null);
      return this;
   }

   public DimConstraint getHorizontal() {
      return this.hor;
   }

   public void setHorizontal(DimConstraint h) {
      this.hor = h != null ? h : new DimConstraint();
   }

   public DimConstraint getVertical() {
      return this.ver;
   }

   public void setVertical(DimConstraint v) {
      this.ver = v != null ? v : new DimConstraint();
   }

   public DimConstraint getDimConstraint(boolean isHor) {
      return isHor ? this.hor : this.ver;
   }

   public UnitValue[] getPos() {
      return this.pos != null ? new UnitValue[]{this.pos[0], this.pos[1], this.pos[2], this.pos[3]} : null;
   }

   public void setPos(UnitValue[] pos) {
      this.pos = pos != null ? new UnitValue[]{pos[0], pos[1], pos[2], pos[3]} : null;
      this.linkTargets = null;
   }

   public boolean isBoundsInGrid() {
      return this.boundsInGrid;
   }

   void setBoundsInGrid(boolean b) {
      this.boundsInGrid = b;
   }

   public int getCellX() {
      return this.cellX;
   }

   public void setCellX(int x) {
      this.cellX = x;
   }

   public int getCellY() {
      return this.cellX < 0 ? -1 : this.cellY;
   }

   public void setCellY(int y) {
      if (y < 0) {
         this.cellX = -1;
      }

      this.cellY = y < 0 ? 0 : y;
   }

   public int getDockSide() {
      return this.dock;
   }

   public void setDockSide(int side) {
      if (side >= -1 && side <= 3) {
         this.dock = side;
      } else {
         throw new IllegalArgumentException("Illegal dock side: " + side);
      }
   }

   public boolean isExternal() {
      return this.external;
   }

   public void setExternal(boolean b) {
      this.external = b;
   }

   public Boolean getFlowX() {
      return this.flowX;
   }

   public void setFlowX(Boolean b) {
      this.flowX = b;
   }

   public int getHideMode() {
      return this.hideMode;
   }

   public void setHideMode(int mode) {
      if (mode >= -1 && mode <= 3) {
         this.hideMode = mode;
      } else {
         throw new IllegalArgumentException("Wrong hideMode: " + mode);
      }
   }

   public String getId() {
      return this.id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public UnitValue[] getPadding() {
      return this.padding != null ? new UnitValue[]{this.padding[0], this.padding[1], this.padding[2], this.padding[3]} : null;
   }

   public void setPadding(UnitValue[] sides) {
      this.padding = sides != null ? new UnitValue[]{sides[0], sides[1], sides[2], sides[3]} : null;
   }

   public int getSkip() {
      return this.skip;
   }

   public void setSkip(int cells) {
      this.skip = cells;
   }

   public int getSpanX() {
      return this.spanX;
   }

   public void setSpanX(int cells) {
      this.spanX = cells;
   }

   public int getSpanY() {
      return this.spanY;
   }

   public void setSpanY(int cells) {
      this.spanY = cells;
   }

   public Float getPushX() {
      return this.pushX;
   }

   public void setPushX(Float weight) {
      this.pushX = weight;
   }

   public Float getPushY() {
      return this.pushY;
   }

   public void setPushY(Float weight) {
      this.pushY = weight;
   }

   public int getSplit() {
      return this.split;
   }

   public void setSplit(int parts) {
      this.split = parts;
   }

   public String getTag() {
      return this.tag;
   }

   public void setTag(String tag) {
      this.tag = tag;
   }

   public boolean isWrap() {
      return this.wrap != null;
   }

   public void setWrap(boolean b) {
      this.wrap = b ? (this.wrap == null ? DEF_GAP : this.wrap) : null;
   }

   public BoundSize getWrapGapSize() {
      return this.wrap == DEF_GAP ? null : this.wrap;
   }

   public void setWrapGapSize(BoundSize s) {
      this.wrap = s == null ? (this.wrap != null ? DEF_GAP : null) : s;
   }

   public boolean isNewline() {
      return this.newline != null;
   }

   public void setNewline(boolean b) {
      this.newline = b ? (this.newline == null ? DEF_GAP : this.newline) : null;
   }

   public BoundSize getNewlineGapSize() {
      return this.newline == DEF_GAP ? null : this.newline;
   }

   public void setNewlineGapSize(BoundSize s) {
      this.newline = s == null ? (this.newline != null ? DEF_GAP : null) : s;
   }

   private Object readResolve() throws ObjectStreamException {
      return LayoutUtil.getSerializedObject(this);
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      LayoutUtil.setSerializedObject(this, LayoutUtil.readAsXML(in));
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      if (this.getClass() == CC.class) {
         LayoutUtil.writeAsXML(out, this);
      }
   }
}
