package net.miginfocom.layout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.WeakHashMap;
import java.util.Map.Entry;

public final class Grid {
   public static final boolean TEST_GAPS = true;
   private static final Float[] GROW_100 = new Float[]{ResizeConstraint.WEIGHT_100};
   private static final DimConstraint DOCK_DIM_CONSTRAINT = new DimConstraint();
   private static final int MAX_GRID = 30000;
   private static final int MAX_DOCK_GRID = 32767;
   private static final ResizeConstraint GAP_RC_CONST = new ResizeConstraint(200, ResizeConstraint.WEIGHT_100, 50, null);
   private static final ResizeConstraint GAP_RC_CONST_PUSH = new ResizeConstraint(200, ResizeConstraint.WEIGHT_100, 50, ResizeConstraint.WEIGHT_100);
   private final LC lc;
   private final ContainerWrapper container;
   private final LinkedHashMap<Integer, Grid.Cell> grid = new LinkedHashMap();
   private HashMap<Integer, BoundSize> wrapGapMap = null;
   private final TreeSet<Integer> rowIndexes = new TreeSet();
   private final TreeSet<Integer> colIndexes = new TreeSet();
   private final AC rowConstr;
   private final AC colConstr;
   private Grid.FlowSizeSpec colFlowSpecs = null;
   private Grid.FlowSizeSpec rowFlowSpecs = null;
   private final ArrayList<Grid.LinkedDimGroup>[] colGroupLists;
   private final ArrayList<Grid.LinkedDimGroup>[] rowGroupLists;
   private int[] width = null;
   private int[] height = null;
   private ArrayList<int[]> debugRects = null;
   private HashMap<String, Boolean> linkTargetIDs = null;
   private final int dockOffY;
   private final int dockOffX;
   private final Float[] pushXs;
   private final Float[] pushYs;
   private final ArrayList<LayoutCallback> callbackList;
   private static WeakHashMap[] PARENT_ROWCOL_SIZES_MAP = null;
   private static WeakHashMap<Object, LinkedHashMap<Integer, Grid.Cell>> PARENT_GRIDPOS_MAP = null;

   public Grid(ContainerWrapper container, LC lc, AC rowConstr, AC colConstr, Map<ComponentWrapper, CC> ccMap, ArrayList<LayoutCallback> callbackList) {
      this.lc = lc;
      this.rowConstr = rowConstr;
      this.colConstr = colConstr;
      this.container = container;
      this.callbackList = callbackList;
      int wrap = lc.getWrapAfter() != 0 ? lc.getWrapAfter() : (lc.isFlowX() ? colConstr : rowConstr).getConstaints().length;
      ComponentWrapper[] comps = container.getComponents();
      boolean hasTagged = false;
      boolean hasPushX = false;
      boolean hasPushY = false;
      boolean hitEndOfRow = false;
      int[] cellXY = new int[2];
      ArrayList<int[]> spannedRects = new ArrayList(2);
      DimConstraint[] specs = (lc.isFlowX() ? rowConstr : colConstr).getConstaints();
      int sizeGroupsX = 0;
      int sizeGroupsY = 0;
      int[] dockInsets = null;
      LinkHandler.clearTemporaryBounds(container.getLayout());
      int i = 0;

      while (i < comps.length) {
         ComponentWrapper comp = comps[i];
         CC rootCc = getCC(comp, ccMap);
         this.addLinkIDs(rootCc);
         int hideMode = comp.isVisible() ? -1 : (rootCc.getHideMode() != -1 ? rootCc.getHideMode() : lc.getHideMode());
         if (hideMode == 3) {
            this.setLinkedBounds(comp, rootCc, comp.getX(), comp.getY(), comp.getWidth(), comp.getHeight(), rootCc.isExternal());
            i++;
         } else {
            if (rootCc.getHorizontal().getSizeGroup() != null) {
               sizeGroupsX++;
            }

            if (rootCc.getVertical().getSizeGroup() != null) {
               sizeGroupsY++;
            }

            UnitValue[] pos = this.getPos(comp, rootCc);
            BoundSize[] cbSz = this.getCallbackSize(comp);
            if (pos != null || rootCc.isExternal()) {
               Grid.CompWrap cw = new Grid.CompWrap(comp, rootCc, hideMode, pos, cbSz);
               Grid.Cell cell = (Grid.Cell)this.grid.get(null);
               if (cell == null) {
                  this.grid.put(null, new Grid.Cell(cw));
               } else {
                  cell.compWraps.add(cw);
               }

               if (!rootCc.isBoundsInGrid() || rootCc.isExternal()) {
                  this.setLinkedBounds(comp, rootCc, comp.getX(), comp.getY(), comp.getWidth(), comp.getHeight(), rootCc.isExternal());
                  i++;
                  continue;
               }
            }

            if (rootCc.getDockSide() != -1) {
               if (dockInsets == null) {
                  dockInsets = new int[]{-32767, -32767, 32767, 32767};
               }

               this.addDockingCell(dockInsets, rootCc.getDockSide(), new Grid.CompWrap(comp, rootCc, hideMode, pos, cbSz));
               i++;
            } else {
               Boolean cellFlowX = rootCc.getFlowX();
               Grid.Cell cellx = null;
               if (rootCc.isNewline()) {
                  this.wrap(cellXY, rootCc.getNewlineGapSize());
               } else if (hitEndOfRow) {
                  this.wrap(cellXY, null);
               }

               hitEndOfRow = false;
               boolean rowNoGrid = lc.isNoGrid() || ((DimConstraint)LayoutUtil.getIndexSafe(specs, lc.isFlowX() ? cellXY[1] : cellXY[0])).isNoGrid();
               int cx = rootCc.getCellX();
               int cy = rootCc.getCellY();
               if ((cx < 0 || cy < 0) && !rowNoGrid && rootCc.getSkip() == 0) {
                  while (!this.isCellFree(cellXY[1], cellXY[0], spannedRects)) {
                     if (Math.abs(this.increase(cellXY, 1)) >= wrap) {
                        this.wrap(cellXY, null);
                     }
                  }
               } else {
                  if (cx >= 0 && cy >= 0) {
                     if (cy >= 0) {
                        cellXY[0] = cx;
                        cellXY[1] = cy;
                     } else if (lc.isFlowX()) {
                        cellXY[0] = cx;
                     } else {
                        cellXY[1] = cx;
                     }
                  }

                  cellx = this.getCell(cellXY[1], cellXY[0]);
               }

               int s = 0;
               int skipCount = rootCc.getSkip();

               while (true) {
                  if (s >= skipCount) {
                     if (cellx == null) {
                        s = Math.min(rowNoGrid && lc.isFlowX() ? 2097051 : rootCc.getSpanX(), 30000 - cellXY[0]);
                        skipCount = Math.min(rowNoGrid && !lc.isFlowX() ? 2097051 : rootCc.getSpanY(), 30000 - cellXY[1]);
                        cellx = new Grid.Cell(s, skipCount, cellFlowX != null ? cellFlowX : lc.isFlowX());
                        this.setCell(cellXY[1], cellXY[0], cellx);
                        if (s > 1 || skipCount > 1) {
                           spannedRects.add(new int[]{cellXY[0], cellXY[1], s, skipCount});
                        }
                     }

                     boolean wrapHandled = false;
                     skipCount = rowNoGrid ? 2097051 : rootCc.getSplit() - 1;
                     boolean splitExit = false;
                     boolean spanRestOfRow = (lc.isFlowX() ? rootCc.getSpanX() : rootCc.getSpanY()) == 2097051;

                     while (skipCount >= 0 && i < comps.length) {
                        label600: {
                           ComponentWrapper compAdd = comps[i];
                           CC cc = getCC(compAdd, ccMap);
                           this.addLinkIDs(cc);
                           boolean visible = compAdd.isVisible();
                           hideMode = visible ? -1 : (cc.getHideMode() != -1 ? cc.getHideMode() : lc.getHideMode());
                           if (!cc.isExternal() && hideMode != 3) {
                              hasPushX |= (visible || hideMode > 1) && cc.getPushX() != null;
                              hasPushY |= (visible || hideMode > 1) && cc.getPushY() != null;
                              if (cc != rootCc) {
                                 if (cc.isNewline() || !cc.isBoundsInGrid() || cc.getDockSide() != -1) {
                                    break label600;
                                 }

                                 if (skipCount > 0 && cc.getSkip() > 0) {
                                    splitExit = true;
                                    break label600;
                                 }

                                 pos = this.getPos(compAdd, cc);
                                 cbSz = this.getCallbackSize(compAdd);
                              }

                              Grid.CompWrap cwx = new Grid.CompWrap(compAdd, cc, hideMode, pos, cbSz);
                              cellx.compWraps.add(cwx);
                              cellx.hasTagged = cellx.hasTagged | cc.getTag() != null;
                              hasTagged |= cellx.hasTagged;
                              if (cc != rootCc) {
                                 if (cc.getHorizontal().getSizeGroup() != null) {
                                    sizeGroupsX++;
                                 }

                                 if (cc.getVertical().getSizeGroup() != null) {
                                    sizeGroupsY++;
                                 }
                              }

                              i++;
                              if (cc.isWrap() || spanRestOfRow && skipCount == 0) {
                                 if (cc.isWrap()) {
                                    this.wrap(cellXY, cc.getWrapGapSize());
                                 } else {
                                    hitEndOfRow = true;
                                 }

                                 wrapHandled = true;
                                 break label600;
                              }
                           } else {
                              i++;
                              skipCount++;
                           }

                           skipCount--;
                        }

                        if (!wrapHandled && !rowNoGrid) {
                           int span = lc.isFlowX() ? cellx.spanx : cellx.spany;
                           if (Math.abs(lc.isFlowX() ? cellXY[0] : cellXY[1]) + span >= wrap) {
                              hitEndOfRow = true;
                           } else {
                              this.increase(cellXY, splitExit ? span - 1 : span);
                           }
                        }
                        break;
                     }
                  }

                  do {
                     if (Math.abs(this.increase(cellXY, 1)) >= wrap) {
                        this.wrap(cellXY, null);
                     }
                  } while (!this.isCellFree(cellXY[1], cellXY[0], spannedRects));

                  s++;
               }
            }
         }
      }

      if (sizeGroupsX > 0 || sizeGroupsY > 0) {
         HashMap<String, int[]> sizeGroupMapX = sizeGroupsX > 0 ? new HashMap(sizeGroupsX) : null;
         HashMap<String, int[]> sizeGroupMapY = sizeGroupsY > 0 ? new HashMap(sizeGroupsY) : null;
         ArrayList<Grid.CompWrap> sizeGroupCWs = new ArrayList(Math.max(sizeGroupsX, sizeGroupsY));

         for (Grid.Cell cellxx : this.grid.values()) {
            for (int ix = 0; ix < cellxx.compWraps.size(); ix++) {
               Grid.CompWrap cwxx = (Grid.CompWrap)cellxx.compWraps.get(ix);
               String sgx = cwxx.cc.getHorizontal().getSizeGroup();
               String sgy = cwxx.cc.getVertical().getSizeGroup();
               if (sgx != null || sgy != null) {
                  if (sgx != null && sizeGroupMapX != null) {
                     addToSizeGroup(sizeGroupMapX, sgx, cwxx.horSizes);
                  }

                  if (sgy != null && sizeGroupMapY != null) {
                     addToSizeGroup(sizeGroupMapY, sgy, cwxx.verSizes);
                  }

                  sizeGroupCWs.add(cwxx);
               }
            }
         }

         for (Grid.CompWrap cwxx : sizeGroupCWs) {
            if (sizeGroupMapX != null) {
               cwxx.setSizes((int[])sizeGroupMapX.get(cwxx.cc.getHorizontal().getSizeGroup()), true);
            }

            if (sizeGroupMapY != null) {
               cwxx.setSizes((int[])sizeGroupMapY.get(cwxx.cc.getVertical().getSizeGroup()), false);
            }
         }
      }

      if (sizeGroupsX > 0 || sizeGroupsY > 0) {
         HashMap<String, int[]> sizeGroupMapX = sizeGroupsX > 0 ? new HashMap(sizeGroupsX) : null;
         HashMap<String, int[]> sizeGroupMapY = sizeGroupsY > 0 ? new HashMap(sizeGroupsY) : null;
         ArrayList<Grid.CompWrap> sizeGroupCWs = new ArrayList(Math.max(sizeGroupsX, sizeGroupsY));

         for (Grid.Cell cellxx : this.grid.values()) {
            for (int ixx = 0; ixx < cellxx.compWraps.size(); ixx++) {
               Grid.CompWrap cwxx = (Grid.CompWrap)cellxx.compWraps.get(ixx);
               String sgx = cwxx.cc.getHorizontal().getSizeGroup();
               String sgy = cwxx.cc.getVertical().getSizeGroup();
               if (sgx != null || sgy != null) {
                  if (sgx != null && sizeGroupMapX != null) {
                     addToSizeGroup(sizeGroupMapX, sgx, cwxx.horSizes);
                  }

                  if (sgy != null && sizeGroupMapY != null) {
                     addToSizeGroup(sizeGroupMapY, sgy, cwxx.verSizes);
                  }

                  sizeGroupCWs.add(cwxx);
               }
            }
         }

         for (Grid.CompWrap cwxx : sizeGroupCWs) {
            if (sizeGroupMapX != null) {
               cwxx.setSizes((int[])sizeGroupMapX.get(cwxx.cc.getHorizontal().getSizeGroup()), true);
            }

            if (sizeGroupMapY != null) {
               cwxx.setSizes((int[])sizeGroupMapY.get(cwxx.cc.getVertical().getSizeGroup()), false);
            }
         }
      }

      if (hasTagged) {
         sortCellsByPlatform(this.grid.values(), container);
      }

      boolean ltr = LayoutUtil.isLeftToRight(lc, container);

      for (Grid.Cell cellxx : this.grid.values()) {
         ArrayList<Grid.CompWrap> cws = cellxx.compWraps;
         int ixxx = 0;

         for (int lastI = cws.size() - 1; ixxx <= lastI; ixxx++) {
            Grid.CompWrap cwxx = (Grid.CompWrap)cws.get(ixxx);
            ComponentWrapper cwBef = ixxx > 0 ? ((Grid.CompWrap)cws.get(ixxx - 1)).comp : null;
            ComponentWrapper cwAft = ixxx < lastI ? ((Grid.CompWrap)cws.get(ixxx + 1)).comp : null;
            String tag = getCC(cwxx.comp, ccMap).getTag();
            CC ccBef = cwBef != null ? getCC(cwBef, ccMap) : null;
            CC ccAft = cwAft != null ? getCC(cwAft, ccMap) : null;
            cwxx.calcGaps(cwBef, ccBef, cwAft, ccAft, tag, cellxx.flowx, ltr);
         }
      }

      this.dockOffX = getDockInsets(this.colIndexes);
      this.dockOffY = getDockInsets(this.rowIndexes);
      int ixxx = 0;

      for (int iSz = rowConstr.getCount(); ixxx < iSz; ixxx++) {
         this.rowIndexes.add(ixxx);
      }

      ixxx = 0;

      for (int iSz = colConstr.getCount(); ixxx < iSz; ixxx++) {
         this.colIndexes.add(ixxx);
      }

      this.colGroupLists = this.divideIntoLinkedGroups(false);
      this.rowGroupLists = this.divideIntoLinkedGroups(true);
      this.pushXs = !hasPushX && !lc.isFillX() ? null : this.getDefaultPushWeights(false);
      this.pushYs = !hasPushY && !lc.isFillY() ? null : this.getDefaultPushWeights(true);
      if (LayoutUtil.isDesignTime(container)) {
         saveGrid(container, this.grid);
      }
   }

   private static CC getCC(ComponentWrapper comp, Map<ComponentWrapper, CC> ccMap) {
      CC cc = (CC)ccMap.get(comp);
      return cc != null ? cc : new CC();
   }

   private void addLinkIDs(CC cc) {
      String[] linkIDs = cc.getLinkTargets();

      for (String linkID : linkIDs) {
         if (this.linkTargetIDs == null) {
            this.linkTargetIDs = new HashMap();
         }

         this.linkTargetIDs.put(linkID, null);
      }
   }

   public void invalidateContainerSize() {
      this.colFlowSpecs = null;
   }

   public boolean layout(int[] bounds, UnitValue alignX, UnitValue alignY, boolean debug, boolean checkPrefChange) {
      if (debug) {
         this.debugRects = new ArrayList();
      }

      this.checkSizeCalcs();
      this.resetLinkValues(true, true);
      this.layoutInOneDim(bounds[2], alignX, false, this.pushXs);
      this.layoutInOneDim(bounds[3], alignY, true, this.pushYs);
      HashMap<String, Integer> endGrpXMap = null;
      HashMap<String, Integer> endGrpYMap = null;
      int compCount = this.container.getComponentCount();
      boolean layoutAgain = false;
      if (compCount > 0) {
         for (int j = 0; j < (this.linkTargetIDs != null ? 2 : 1); j++) {
            int count = 0;

            boolean doAgain;
            do {
               doAgain = false;

               for (Grid.Cell cell : this.grid.values()) {
                  ArrayList<Grid.CompWrap> compWraps = cell.compWraps;
                  int i = 0;

                  for (int iSz = compWraps.size(); i < iSz; i++) {
                     Grid.CompWrap cw = (Grid.CompWrap)compWraps.get(i);
                     if (j == 0) {
                        doAgain |= this.doAbsoluteCorrections(cw, bounds);
                        if (!doAgain) {
                           if (cw.cc.getHorizontal().getEndGroup() != null) {
                              endGrpXMap = addToEndGroup(endGrpXMap, cw.cc.getHorizontal().getEndGroup(), cw.x + cw.w);
                           }

                           if (cw.cc.getVertical().getEndGroup() != null) {
                              endGrpYMap = addToEndGroup(endGrpYMap, cw.cc.getVertical().getEndGroup(), cw.y + cw.h);
                           }
                        }

                        if (this.linkTargetIDs != null && (this.linkTargetIDs.containsKey("visual") || this.linkTargetIDs.containsKey("container"))) {
                           layoutAgain = true;
                        }
                     }

                     if (this.linkTargetIDs == null || j == 1) {
                        if (cw.cc.getHorizontal().getEndGroup() != null) {
                           cw.w = (Integer)endGrpXMap.get(cw.cc.getHorizontal().getEndGroup()) - cw.x;
                        }

                        if (cw.cc.getVertical().getEndGroup() != null) {
                           cw.h = (Integer)endGrpYMap.get(cw.cc.getVertical().getEndGroup()) - cw.y;
                        }

                        cw.x = cw.x + bounds[0];
                        cw.y = cw.y + bounds[1];
                        layoutAgain |= cw.transferBounds(checkPrefChange && !layoutAgain);
                        if (this.callbackList != null) {
                           for (LayoutCallback callback : this.callbackList) {
                              callback.correctBounds(cw.comp);
                           }
                        }
                     }
                  }
               }

               this.clearGroupLinkBounds();
               if (++count > (compCount << 3) + 10) {
                  System.err.println("Unstable cyclic dependency in absolute linked values!");
                  break;
               }
            } while (!doAgain);
         }
      }

      if (debug) {
         for (Grid.Cell cell : this.grid.values()) {
            ArrayList<Grid.CompWrap> compWraps = cell.compWraps;
            int i = 0;

            for (int iSz = compWraps.size(); i < iSz; i++) {
               Grid.CompWrap cwx = (Grid.CompWrap)compWraps.get(i);
               Grid.LinkedDimGroup hGrp = getGroupContaining(this.colGroupLists, cwx);
               Grid.LinkedDimGroup vGrp = getGroupContaining(this.rowGroupLists, cwx);
               if (hGrp != null && vGrp != null) {
                  this.debugRects
                     .add(
                        new int[]{
                           hGrp.lStart + bounds[0] - (hGrp.fromEnd ? hGrp.lSize : 0),
                           vGrp.lStart + bounds[1] - (vGrp.fromEnd ? vGrp.lSize : 0),
                           hGrp.lSize,
                           vGrp.lSize
                        }
                     );
               }
            }
         }
      }

      return layoutAgain;
   }

   public void paintDebug() {
      if (this.debugRects != null) {
         this.container.paintDebugOutline();
         ArrayList<int[]> painted = new ArrayList();
         int i = 0;

         for (int iSz = this.debugRects.size(); i < iSz; i++) {
            int[] r = (int[])this.debugRects.get(i);
            if (!painted.contains(r)) {
               this.container.paintDebugCell(r[0], r[1], r[2], r[3]);
               painted.add(r);
            }
         }

         for (Grid.Cell cell : this.grid.values()) {
            ArrayList<Grid.CompWrap> compWraps = cell.compWraps;
            int ix = 0;

            for (int iSzx = compWraps.size(); ix < iSzx; ix++) {
               ((Grid.CompWrap)compWraps.get(ix)).comp.paintDebugOutline();
            }
         }
      }
   }

   public ContainerWrapper getContainer() {
      return this.container;
   }

   public final int[] getWidth() {
      this.checkSizeCalcs();
      return (int[])this.width.clone();
   }

   public final int[] getHeight() {
      this.checkSizeCalcs();
      return (int[])this.height.clone();
   }

   private void checkSizeCalcs() {
      if (this.colFlowSpecs == null) {
         this.colFlowSpecs = this.calcRowsOrColsSizes(true);
         this.rowFlowSpecs = this.calcRowsOrColsSizes(false);
         this.width = this.getMinPrefMaxSumSize(true);
         this.height = this.getMinPrefMaxSumSize(false);
         if (this.linkTargetIDs == null) {
            this.resetLinkValues(false, true);
         } else {
            this.layout(new int[4], null, null, false, false);
            this.resetLinkValues(false, false);
         }

         this.adjustSizeForAbsolute(true);
         this.adjustSizeForAbsolute(false);
      }
   }

   private UnitValue[] getPos(ComponentWrapper cw, CC cc) {
      UnitValue[] cbPos = null;
      if (this.callbackList != null) {
         for (int i = 0; i < this.callbackList.size() && cbPos == null; i++) {
            cbPos = ((LayoutCallback)this.callbackList.get(i)).getPosition(cw);
         }
      }

      UnitValue[] ccPos = cc.getPos();
      if (cbPos != null && ccPos != null) {
         for (int i = 0; i < 4; i++) {
            UnitValue cbUv = cbPos[i];
            if (cbUv != null) {
               ccPos[i] = cbUv;
            }
         }

         return ccPos;
      } else {
         return cbPos != null ? cbPos : ccPos;
      }
   }

   private BoundSize[] getCallbackSize(ComponentWrapper cw) {
      if (this.callbackList != null) {
         for (LayoutCallback callback : this.callbackList) {
            BoundSize[] bs = callback.getSize(cw);
            if (bs != null) {
               return bs;
            }
         }
      }

      return null;
   }

   private static int getDockInsets(TreeSet<Integer> set) {
      int c = 0;

      for (Integer i : set) {
         if (i >= -30000) {
            break;
         }

         c++;
      }

      return c;
   }

   private boolean setLinkedBounds(ComponentWrapper cw, CC cc, int x, int y, int w, int h, boolean external) {
      String id = cc.getId() != null ? cc.getId() : cw.getLinkId();
      if (id == null) {
         return false;
      } else {
         String gid = null;
         int grIx = id.indexOf(46);
         if (grIx != -1) {
            gid = id.substring(0, grIx);
            id = id.substring(grIx + 1);
         }

         Object lay = this.container.getLayout();
         boolean changed = false;
         if (external || this.linkTargetIDs != null && this.linkTargetIDs.containsKey(id)) {
            changed = LinkHandler.setBounds(lay, id, x, y, w, h, !external, false);
         }

         if (gid != null && (external || this.linkTargetIDs != null && this.linkTargetIDs.containsKey(gid))) {
            if (this.linkTargetIDs == null) {
               this.linkTargetIDs = new HashMap(4);
            }

            this.linkTargetIDs.put(gid, Boolean.TRUE);
            changed |= LinkHandler.setBounds(lay, gid, x, y, w, h, !external, true);
         }

         return changed;
      }
   }

   private int increase(int[] p, int cnt) {
      return this.lc.isFlowX() ? (p[0] += cnt) : (p[1] += cnt);
   }

   private void wrap(int[] cellXY, BoundSize gapSize) {
      boolean flowx = this.lc.isFlowX();
      cellXY[0] = flowx ? 0 : cellXY[0] + 1;
      cellXY[1] = flowx ? cellXY[1] + 1 : 0;
      if (gapSize != null) {
         if (this.wrapGapMap == null) {
            this.wrapGapMap = new HashMap(8);
         }

         this.wrapGapMap.put(cellXY[flowx ? 1 : 0], gapSize);
      }

      if (flowx) {
         this.rowIndexes.add(cellXY[1]);
      } else {
         this.colIndexes.add(cellXY[0]);
      }
   }

   private static void sortCellsByPlatform(Collection<Grid.Cell> cells, ContainerWrapper parent) {
      String order = PlatformDefaults.getButtonOrder();
      String orderLo = order.toLowerCase();
      int unrelSize = PlatformDefaults.convertToPixels(1.0F, "u", true, 0.0F, parent, null);
      if (unrelSize == -87654312) {
         throw new IllegalArgumentException("'unrelated' not recognized by PlatformDefaults!");
      } else {
         int[] gapUnrel = new int[]{unrelSize, unrelSize, -2147471302};
         int[] flGap = new int[]{0, 0, -2147471302};

         for (Grid.Cell cell : cells) {
            if (cell.hasTagged) {
               Grid.CompWrap prevCW = null;
               boolean nextUnrel = false;
               boolean nextPush = false;
               ArrayList<Grid.CompWrap> sortedList = new ArrayList(cell.compWraps.size());
               int i = 0;

               for (int iSz = orderLo.length(); i < iSz; i++) {
                  char c = orderLo.charAt(i);
                  if (c != '+' && c != '_') {
                     String tag = PlatformDefaults.getTagForChar(c);
                     if (tag != null) {
                        int j = 0;

                        for (int jSz = cell.compWraps.size(); j < jSz; j++) {
                           Grid.CompWrap cw = (Grid.CompWrap)cell.compWraps.get(j);
                           if (tag.equals(cw.cc.getTag())) {
                              if (Character.isUpperCase(order.charAt(i))) {
                                 int min = PlatformDefaults.getMinimumButtonWidth().getPixels(0.0F, parent, cw.comp);
                                 if (min > cw.horSizes[0]) {
                                    cw.horSizes[0] = min;
                                 }

                                 correctMinMax(cw.horSizes);
                              }

                              sortedList.add(cw);
                              if (nextUnrel) {
                                 (prevCW != null ? prevCW : cw).mergeGapSizes(gapUnrel, cell.flowx, prevCW == null);
                                 if (nextPush) {
                                    cw.forcedPushGaps = 1;
                                    nextUnrel = false;
                                    nextPush = false;
                                 }
                              }

                              if (c == 'u') {
                                 nextUnrel = true;
                              }

                              prevCW = cw;
                           }
                        }
                     }
                  } else {
                     nextUnrel = true;
                     if (c == '+') {
                        nextPush = true;
                     }
                  }
               }

               if (sortedList.size() > 0) {
                  Grid.CompWrap cw = (Grid.CompWrap)sortedList.get(sortedList.size() - 1);
                  if (nextUnrel) {
                     cw.mergeGapSizes(gapUnrel, cell.flowx, false);
                     if (nextPush) {
                        cw.forcedPushGaps = cw.forcedPushGaps | 2;
                     }
                  }

                  if (cw.cc.getHorizontal().getGapAfter() == null) {
                     cw.setGaps(flGap, 3);
                  }

                  Grid.CompWrap var22 = (Grid.CompWrap)sortedList.get(0);
                  if (var22.cc.getHorizontal().getGapBefore() == null) {
                     var22.setGaps(flGap, 1);
                  }
               }

               if (cell.compWraps.size() == sortedList.size()) {
                  cell.compWraps.clear();
               } else {
                  cell.compWraps.removeAll(sortedList);
               }

               cell.compWraps.addAll(sortedList);
            }
         }
      }
   }

   private Float[] getDefaultPushWeights(boolean isRows) {
      ArrayList<Grid.LinkedDimGroup>[] groupLists = isRows ? this.rowGroupLists : this.colGroupLists;
      Float[] pushWeightArr = GROW_100;
      int i = 0;

      for (int ix = 1; i < groupLists.length; ix += 2) {
         ArrayList<Grid.LinkedDimGroup> grps = groupLists[i];
         Float rowPushWeight = null;

         for (Grid.LinkedDimGroup grp : grps) {
            for (int c = 0; c < grp._compWraps.size(); c++) {
               Grid.CompWrap cw = (Grid.CompWrap)grp._compWraps.get(c);
               int hideMode = cw.comp.isVisible() ? -1 : (cw.cc.getHideMode() != -1 ? cw.cc.getHideMode() : this.lc.getHideMode());
               Float pushWeight = hideMode < 2 ? (isRows ? cw.cc.getPushY() : cw.cc.getPushX()) : null;
               if (rowPushWeight == null || pushWeight != null && pushWeight > rowPushWeight) {
                  rowPushWeight = pushWeight;
               }
            }
         }

         if (rowPushWeight != null) {
            if (pushWeightArr == GROW_100) {
               pushWeightArr = new Float[(groupLists.length << 1) + 1];
            }

            pushWeightArr[ix] = rowPushWeight;
         }

         i++;
      }

      return pushWeightArr;
   }

   private void clearGroupLinkBounds() {
      if (this.linkTargetIDs != null) {
         for (Entry<String, Boolean> o : this.linkTargetIDs.entrySet()) {
            if (o.getValue() == Boolean.TRUE) {
               LinkHandler.clearBounds(this.container.getLayout(), (String)o.getKey());
            }
         }
      }
   }

   private void resetLinkValues(boolean parentSize, boolean compLinks) {
      Object lay = this.container.getLayout();
      if (compLinks) {
         LinkHandler.clearTemporaryBounds(lay);
      }

      boolean defIns = !this.hasDocks();
      int parW = parentSize ? this.lc.getWidth().constrain(this.container.getWidth(), (float)getParentSize(this.container, true), this.container) : 0;
      int parH = parentSize ? this.lc.getHeight().constrain(this.container.getHeight(), (float)getParentSize(this.container, false), this.container) : 0;
      int insX = LayoutUtil.getInsets(this.lc, 0, defIns).getPixels(0.0F, this.container, null);
      int insY = LayoutUtil.getInsets(this.lc, 1, defIns).getPixels(0.0F, this.container, null);
      int visW = parW - insX - LayoutUtil.getInsets(this.lc, 2, defIns).getPixels(0.0F, this.container, null);
      int visH = parH - insY - LayoutUtil.getInsets(this.lc, 3, defIns).getPixels(0.0F, this.container, null);
      LinkHandler.setBounds(lay, "visual", insX, insY, visW, visH, true, false);
      LinkHandler.setBounds(lay, "container", 0, 0, parW, parH, true, false);
   }

   private static Grid.LinkedDimGroup getGroupContaining(ArrayList<Grid.LinkedDimGroup>[] groupLists, Grid.CompWrap cw) {
      for (ArrayList<Grid.LinkedDimGroup> groups : groupLists) {
         int j = 0;

         for (int jSz = groups.size(); j < jSz; j++) {
            ArrayList<Grid.CompWrap> cwList = ((Grid.LinkedDimGroup)groups.get(j))._compWraps;
            int k = 0;

            for (int kSz = cwList.size(); k < kSz; k++) {
               if (cwList.get(k) == cw) {
                  return (Grid.LinkedDimGroup)groups.get(j);
               }
            }
         }
      }

      return null;
   }

   private boolean doAbsoluteCorrections(Grid.CompWrap cw, int[] bounds) {
      boolean changed = false;
      int[] stSz = this.getAbsoluteDimBounds(cw, bounds[2], true);
      if (stSz != null) {
         cw.setDimBounds(stSz[0], stSz[1], true);
      }

      stSz = this.getAbsoluteDimBounds(cw, bounds[3], false);
      if (stSz != null) {
         cw.setDimBounds(stSz[0], stSz[1], false);
      }

      if (this.linkTargetIDs != null) {
         changed = this.setLinkedBounds(cw.comp, cw.cc, cw.x, cw.y, cw.w, cw.h, false);
      }

      return changed;
   }

   private void adjustSizeForAbsolute(boolean isHor) {
      int[] curSizes = isHor ? this.width : this.height;
      Grid.Cell absCell = (Grid.Cell)this.grid.get(null);
      if (absCell != null && !absCell.compWraps.isEmpty()) {
         ArrayList<Grid.CompWrap> cws = absCell.compWraps;
         int maxEnd = 0;
         int j = 0;

         for (int cwSz = absCell.compWraps.size(); j < cwSz + 3; j++) {
            boolean doAgain = false;

            for (int i = 0; i < cwSz; i++) {
               Grid.CompWrap cw = (Grid.CompWrap)cws.get(i);
               int[] stSz = this.getAbsoluteDimBounds(cw, 0, isHor);
               int end = stSz[0] + stSz[1];
               if (maxEnd < end) {
                  maxEnd = end;
               }

               if (this.linkTargetIDs != null) {
                  doAgain |= this.setLinkedBounds(cw.comp, cw.cc, stSz[0], stSz[0], stSz[1], stSz[1], false);
               }
            }

            if (!doAgain) {
               break;
            }

            maxEnd = 0;
            this.clearGroupLinkBounds();
         }

         maxEnd += LayoutUtil.getInsets(this.lc, isHor ? 3 : 2, !this.hasDocks()).getPixels(0.0F, this.container, null);
         if (curSizes[0] < maxEnd) {
            curSizes[0] = maxEnd;
         }

         if (curSizes[1] < maxEnd) {
            curSizes[1] = maxEnd;
         }
      }
   }

   private int[] getAbsoluteDimBounds(Grid.CompWrap cw, int refSize, boolean isHor) {
      if (cw.cc.isExternal()) {
         return isHor ? new int[]{cw.comp.getX(), cw.comp.getWidth()} : new int[]{cw.comp.getY(), cw.comp.getHeight()};
      } else {
         int[] plafPad = this.lc.isVisualPadding() ? cw.comp.getVisualPadding() : null;
         UnitValue[] pad = cw.cc.getPadding();
         if (cw.pos == null && plafPad == null && pad == null) {
            return null;
         } else {
            int st = isHor ? cw.x : cw.y;
            int sz = isHor ? cw.w : cw.h;
            if (cw.pos != null) {
               UnitValue stUV = cw.pos != null ? cw.pos[isHor ? 0 : 1] : null;
               UnitValue endUV = cw.pos != null ? cw.pos[isHor ? 2 : 3] : null;
               int minSz = cw.getSize(0, isHor);
               int maxSz = cw.getSize(2, isHor);
               sz = Math.min(Math.max(cw.getSize(1, isHor), minSz), maxSz);
               if (stUV != null) {
                  st = stUV.getPixels(stUV.getUnit() == 12 ? (float)sz : (float)refSize, this.container, cw.comp);
                  if (endUV != null) {
                     sz = Math.min(Math.max((isHor ? cw.x + cw.w : cw.y + cw.h) - st, minSz), maxSz);
                  }
               }

               if (endUV != null) {
                  if (stUV != null) {
                     sz = Math.min(Math.max(endUV.getPixels((float)refSize, this.container, cw.comp) - st, minSz), maxSz);
                  } else {
                     st = endUV.getPixels((float)refSize, this.container, cw.comp) - sz;
                  }
               }
            }

            if (pad != null) {
               UnitValue uv = pad[isHor ? 1 : 0];
               int p = uv != null ? uv.getPixels((float)refSize, this.container, cw.comp) : 0;
               st += p;
               uv = pad[isHor ? 3 : 2];
               sz += -p + (uv != null ? uv.getPixels((float)refSize, this.container, cw.comp) : 0);
            }

            if (plafPad != null) {
               int p = plafPad[isHor ? 1 : 0];
               st += p;
               sz += -p + plafPad[isHor ? 3 : 2];
            }

            return new int[]{st, sz};
         }
      }
   }

   private void layoutInOneDim(int refSize, UnitValue align, boolean isRows, Float[] defaultPushWeights) {
      boolean fromEnd = isRows ? !this.lc.isTopToBottom() : !LayoutUtil.isLeftToRight(this.lc, this.container);
      DimConstraint[] primDCs = (isRows ? this.rowConstr : this.colConstr).getConstaints();
      Grid.FlowSizeSpec fss = isRows ? this.rowFlowSpecs : this.colFlowSpecs;
      ArrayList<Grid.LinkedDimGroup>[] rowCols = isRows ? this.rowGroupLists : this.colGroupLists;
      int[] rowColSizes = LayoutUtil.calculateSerial(fss.sizes, fss.resConstsInclGaps, defaultPushWeights, 1, refSize);
      if (LayoutUtil.isDesignTime(this.container)) {
         TreeSet<Integer> indexes = isRows ? this.rowIndexes : this.colIndexes;
         int[] ixArr = new int[indexes.size()];
         int ix = 0;

         for (Integer i : indexes) {
            ixArr[ix++] = i;
         }

         putSizesAndIndexes(this.container.getComponent(), rowColSizes, ixArr, isRows);
      }

      int curPos = align != null ? align.getPixels((float)(refSize - LayoutUtil.sum(rowColSizes)), this.container, null) : 0;
      if (fromEnd) {
         curPos = refSize - curPos;
      }

      for (int i = 0; i < rowCols.length; i++) {
         ArrayList<Grid.LinkedDimGroup> linkedGroups = rowCols[i];
         int scIx = i - (isRows ? this.dockOffY : this.dockOffX);
         int bIx = i << 1;
         int bIx2 = bIx + 1;
         curPos += fromEnd ? -rowColSizes[bIx] : rowColSizes[bIx];
         DimConstraint primDC = scIx >= 0 ? primDCs[scIx >= primDCs.length ? primDCs.length - 1 : scIx] : DOCK_DIM_CONSTRAINT;
         int rowSize = rowColSizes[bIx2];

         for (Grid.LinkedDimGroup group : linkedGroups) {
            int groupSize = rowSize;
            if (group.span > 1) {
               groupSize = LayoutUtil.sum(rowColSizes, bIx2, Math.min((group.span << 1) - 1, rowColSizes.length - bIx2 - 1));
            }

            group.layout(primDC, curPos, groupSize, group.span);
         }

         curPos += fromEnd ? -rowSize : rowSize;
      }
   }

   private static void addToSizeGroup(HashMap<String, int[]> sizeGroups, String sizeGroup, int[] size) {
      int[] sgSize = (int[])sizeGroups.get(sizeGroup);
      if (sgSize == null) {
         sizeGroups.put(sizeGroup, new int[]{size[0], size[1], size[2]});
      } else {
         sgSize[0] = Math.max(size[0], sgSize[0]);
         sgSize[1] = Math.max(size[1], sgSize[1]);
         sgSize[2] = Math.min(size[2], sgSize[2]);
      }
   }

   private static HashMap<String, Integer> addToEndGroup(HashMap<String, Integer> endGroups, String endGroup, int end) {
      if (endGroup != null) {
         if (endGroups == null) {
            endGroups = new HashMap(2);
         }

         Integer oldEnd = (Integer)endGroups.get(endGroup);
         if (oldEnd == null || end > oldEnd) {
            endGroups.put(endGroup, end);
         }
      }

      return endGroups;
   }

   private Grid.FlowSizeSpec calcRowsOrColsSizes(boolean isHor) {
      ArrayList<Grid.LinkedDimGroup>[] groupsLists = isHor ? this.colGroupLists : this.rowGroupLists;
      Float[] defPush = isHor ? this.pushXs : this.pushYs;
      int refSize = isHor ? this.container.getWidth() : this.container.getHeight();
      BoundSize cSz = isHor ? this.lc.getWidth() : this.lc.getHeight();
      if (!cSz.isUnset()) {
         refSize = cSz.constrain(refSize, (float)getParentSize(this.container, isHor), this.container);
      }

      DimConstraint[] primDCs = (isHor ? this.colConstr : this.rowConstr).getConstaints();
      TreeSet<Integer> primIndexes = isHor ? this.colIndexes : this.rowIndexes;
      int[][] rowColBoundSizes = new int[primIndexes.size()][];
      HashMap<String, int[]> sizeGroupMap = new HashMap(2);
      DimConstraint[] allDCs = new DimConstraint[primIndexes.size()];
      Iterator<Integer> primIt = primIndexes.iterator();

      for (int r = 0; r < rowColBoundSizes.length; r++) {
         int cellIx = (Integer)primIt.next();
         int[] rowColSizes = new int[3];
         if (cellIx >= -30000 && cellIx <= 30000) {
            allDCs[r] = primDCs[cellIx >= primDCs.length ? primDCs.length - 1 : cellIx];
         } else {
            allDCs[r] = DOCK_DIM_CONSTRAINT;
         }

         ArrayList<Grid.LinkedDimGroup> groups = groupsLists[r];
         int[] groupSizes = new int[]{getTotalGroupsSizeParallel(groups, 0, false), getTotalGroupsSizeParallel(groups, 1, false), 2097051};
         correctMinMax(groupSizes);
         BoundSize dimSize = allDCs[r].getSize();

         for (int sType = 0; sType <= 2; sType++) {
            int rowColSize = groupSizes[sType];
            UnitValue uv = dimSize.getSize(sType);
            if (uv != null) {
               int unit = uv.getUnit();
               if (unit == 14) {
                  rowColSize = groupSizes[1];
               } else if (unit == 13) {
                  rowColSize = groupSizes[0];
               } else if (unit == 15) {
                  rowColSize = groupSizes[2];
               } else {
                  rowColSize = uv.getPixels((float)refSize, this.container, null);
               }
            } else if (cellIx >= -30000 && cellIx <= 30000 && rowColSize == 0) {
               rowColSize = LayoutUtil.isDesignTime(this.container) ? LayoutUtil.getDesignTimeEmptySize() : 0;
            }

            rowColSizes[sType] = rowColSize;
         }

         correctMinMax(rowColSizes);
         addToSizeGroup(sizeGroupMap, allDCs[r].getSizeGroup(), rowColSizes);
         rowColBoundSizes[r] = rowColSizes;
      }

      if (sizeGroupMap.size() > 0) {
         for (int r = 0; r < rowColBoundSizes.length; r++) {
            if (allDCs[r].getSizeGroup() != null) {
               rowColBoundSizes[r] = (int[])sizeGroupMap.get(allDCs[r].getSizeGroup());
            }
         }
      }

      ResizeConstraint[] resConstrs = getRowResizeConstraints(allDCs);
      boolean[] fillInPushGaps = new boolean[allDCs.length + 1];
      int[][] gapSizes = this.getRowGaps(allDCs, refSize, isHor, fillInPushGaps);
      Grid.FlowSizeSpec fss = mergeSizesGapsAndResConstrs(resConstrs, fillInPushGaps, rowColBoundSizes, gapSizes);
      this.adjustMinPrefForSpanningComps(allDCs, defPush, fss, groupsLists);
      return fss;
   }

   private static int getParentSize(ComponentWrapper cw, boolean isHor) {
      ComponentWrapper p = cw.getParent();
      return p != null ? (isHor ? cw.getWidth() : cw.getHeight()) : 0;
   }

   private int[] getMinPrefMaxSumSize(boolean isHor) {
      int[][] sizes = isHor ? this.colFlowSpecs.sizes : this.rowFlowSpecs.sizes;
      int[] retSizes = new int[3];
      BoundSize sz = isHor ? this.lc.getWidth() : this.lc.getHeight();

      for (int i = 0; i < sizes.length; i++) {
         if (sizes[i] != null) {
            int[] size = sizes[i];

            for (int sType = 0; sType <= 2; sType++) {
               if (sz.getSize(sType) != null) {
                  if (i == 0) {
                     retSizes[sType] = sz.getSize(sType).getPixels((float)getParentSize(this.container, isHor), this.container, null);
                  }
               } else {
                  int s = size[sType];
                  if (s != -2147471302) {
                     if (sType == 1) {
                        int bnd = size[2];
                        if (bnd != -2147471302 && bnd < s) {
                           s = bnd;
                        }

                        bnd = size[0];
                        if (bnd > s) {
                           s = bnd;
                        }
                     }

                     retSizes[sType] += s;
                  }

                  if (size[2] == -2147471302 || retSizes[2] > 2097051) {
                     retSizes[2] = 2097051;
                  }
               }
            }
         }
      }

      correctMinMax(retSizes);
      return retSizes;
   }

   private static ResizeConstraint[] getRowResizeConstraints(DimConstraint[] specs) {
      ResizeConstraint[] resConsts = new ResizeConstraint[specs.length];

      for (int i = 0; i < resConsts.length; i++) {
         resConsts[i] = specs[i].resize;
      }

      return resConsts;
   }

   private static ResizeConstraint[] getComponentResizeConstraints(ArrayList<Grid.CompWrap> compWraps, boolean isHor) {
      ResizeConstraint[] resConsts = new ResizeConstraint[compWraps.size()];

      for (int i = 0; i < resConsts.length; i++) {
         CC fc = ((Grid.CompWrap)compWraps.get(i)).cc;
         resConsts[i] = fc.getDimConstraint(isHor).resize;
         int dock = fc.getDockSide();
         if (isHor ? dock == 0 || dock == 2 : dock == 1 || dock == 3) {
            ResizeConstraint dc = resConsts[i];
            resConsts[i] = new ResizeConstraint(dc.shrinkPrio, dc.shrink, dc.growPrio, ResizeConstraint.WEIGHT_100);
         }
      }

      return resConsts;
   }

   private static boolean[] getComponentGapPush(ArrayList<Grid.CompWrap> compWraps, boolean isHor) {
      boolean[] barr = new boolean[compWraps.size() + 1];

      for (int i = 0; i < barr.length; i++) {
         boolean push = i > 0 && ((Grid.CompWrap)compWraps.get(i - 1)).isPushGap(isHor, false);
         if (!push && i < barr.length - 1) {
            push = ((Grid.CompWrap)compWraps.get(i)).isPushGap(isHor, true);
         }

         barr[i] = push;
      }

      return barr;
   }

   private int[][] getRowGaps(DimConstraint[] specs, int refSize, boolean isHor, boolean[] fillInPushGaps) {
      BoundSize defGap = isHor ? this.lc.getGridGapX() : this.lc.getGridGapY();
      if (defGap == null) {
         defGap = isHor ? PlatformDefaults.getGridGapX() : PlatformDefaults.getGridGapY();
      }

      int[] defGapArr = defGap.getPixelSizes((float)refSize, this.container, null);
      boolean defIns = !this.hasDocks();
      UnitValue firstGap = LayoutUtil.getInsets(this.lc, isHor ? 1 : 0, defIns);
      UnitValue lastGap = LayoutUtil.getInsets(this.lc, isHor ? 3 : 2, defIns);
      int[][] retValues = new int[specs.length + 1][];
      int i = 0;

      for (int wgIx = 0; i < retValues.length; i++) {
         DimConstraint specBefore = i > 0 ? specs[i - 1] : null;
         DimConstraint specAfter = i < specs.length ? specs[i] : null;
         boolean edgeBefore = specBefore == DOCK_DIM_CONSTRAINT || specBefore == null;
         boolean edgeAfter = specAfter == DOCK_DIM_CONSTRAINT || specAfter == null;
         if (!edgeBefore || !edgeAfter) {
            BoundSize wrapGapSize = this.wrapGapMap != null && isHor != this.lc.isFlowX() ? (BoundSize)this.wrapGapMap.get(wgIx++) : null;
            if (wrapGapSize == null) {
               int[] gapBefore = specBefore != null ? specBefore.getRowGaps(this.container, null, refSize, false) : null;
               int[] gapAfter = specAfter != null ? specAfter.getRowGaps(this.container, null, refSize, true) : null;
               if (edgeBefore && gapAfter == null && firstGap != null) {
                  int bef = firstGap.getPixels((float)refSize, this.container, null);
                  retValues[i] = new int[]{bef, bef, bef};
               } else if (edgeAfter && gapBefore == null && firstGap != null) {
                  int aft = lastGap.getPixels((float)refSize, this.container, null);
                  retValues[i] = new int[]{aft, aft, aft};
               } else {
                  retValues[i] = gapAfter != gapBefore ? mergeSizes(gapAfter, gapBefore) : new int[]{defGapArr[0], defGapArr[1], defGapArr[2]};
               }

               if (specBefore != null && specBefore.isGapAfterPush() || specAfter != null && specAfter.isGapBeforePush()) {
                  fillInPushGaps[i] = true;
               }
            } else {
               if (wrapGapSize.isUnset()) {
                  retValues[i] = new int[]{defGapArr[0], defGapArr[1], defGapArr[2]};
               } else {
                  retValues[i] = wrapGapSize.getPixelSizes((float)refSize, this.container, null);
               }

               fillInPushGaps[i] = wrapGapSize.getGapPush();
            }
         }
      }

      return retValues;
   }

   private static int[][] getGaps(ArrayList<Grid.CompWrap> compWraps, boolean isHor) {
      int compCount = compWraps.size();
      int[][] retValues = new int[compCount + 1][];
      retValues[0] = ((Grid.CompWrap)compWraps.get(0)).getGaps(isHor, true);

      for (int i = 0; i < compCount; i++) {
         int[] gap1 = ((Grid.CompWrap)compWraps.get(i)).getGaps(isHor, false);
         int[] gap2 = i < compCount - 1 ? ((Grid.CompWrap)compWraps.get(i + 1)).getGaps(isHor, true) : null;
         retValues[i + 1] = mergeSizes(gap1, gap2);
      }

      return retValues;
   }

   private boolean hasDocks() {
      return this.dockOffX > 0 || this.dockOffY > 0 || (Integer)this.rowIndexes.last() > 30000 || (Integer)this.colIndexes.last() > 30000;
   }

   private void adjustMinPrefForSpanningComps(DimConstraint[] specs, Float[] defPush, Grid.FlowSizeSpec fss, ArrayList<Grid.LinkedDimGroup>[] groupsLists) {
      for (int r = groupsLists.length - 1; r >= 0; r--) {
         for (Grid.LinkedDimGroup group : groupsLists[r]) {
            if (group.span != 1) {
               int[] sizes = group.getMinPrefMax();

               for (int s = 0; s <= 1; s++) {
                  int cSize = sizes[s];
                  if (cSize != -2147471302) {
                     int rowSize = 0;
                     int sIx = (r << 1) + 1;
                     int len = Math.min(group.span << 1, fss.sizes.length - sIx) - 1;

                     for (int j = sIx; j < sIx + len; j++) {
                        int sz = fss.sizes[j][s];
                        if (sz != -2147471302) {
                           rowSize += sz;
                        }
                     }

                     if (rowSize < cSize && len > 0) {
                        int eagerness = 0;

                        for (int newRowSize = 0; eagerness < 4 && newRowSize < cSize; eagerness++) {
                           newRowSize = fss.expandSizes(specs, defPush, cSize, sIx, len, s, eagerness);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private ArrayList<Grid.LinkedDimGroup>[] divideIntoLinkedGroups(boolean isRows) {
      boolean fromEnd = isRows ? !this.lc.isTopToBottom() : !LayoutUtil.isLeftToRight(this.lc, this.container);
      TreeSet<Integer> primIndexes = isRows ? this.rowIndexes : this.colIndexes;
      TreeSet<Integer> secIndexes = isRows ? this.colIndexes : this.rowIndexes;
      DimConstraint[] primDCs = (isRows ? this.rowConstr : this.colConstr).getConstaints();
      ArrayList<Grid.LinkedDimGroup>[] groupLists = new ArrayList[primIndexes.size()];
      int gIx = 0;

      for (int i : primIndexes) {
         DimConstraint dc;
         if (i >= -30000 && i <= 30000) {
            dc = primDCs[i >= primDCs.length ? primDCs.length - 1 : i];
         } else {
            dc = DOCK_DIM_CONSTRAINT;
         }

         ArrayList<Grid.LinkedDimGroup> groupList = new ArrayList(2);
         groupLists[gIx++] = groupList;

         for (Integer ix : secIndexes) {
            Grid.Cell cell = isRows ? this.getCell(i, ix) : this.getCell(ix, i);
            if (cell != null && !cell.compWraps.isEmpty()) {
               int span = isRows ? cell.spany : cell.spanx;
               if (span > 1) {
                  span = convertSpanToSparseGrid(i, span, primIndexes);
               }

               boolean isPar = cell.flowx == isRows;
               if (!isPar && cell.compWraps.size() > 1 || span > 1) {
                  int linkType = isPar ? 1 : 0;
                  Grid.LinkedDimGroup lg = new Grid.LinkedDimGroup("p," + ix, span, linkType, !isRows, fromEnd);
                  lg.setCompWraps(cell.compWraps);
                  groupList.add(lg);
               } else {
                  for (int cwIx = 0; cwIx < cell.compWraps.size(); cwIx++) {
                     Grid.CompWrap cw = (Grid.CompWrap)cell.compWraps.get(cwIx);
                     boolean rowBaselineAlign = isRows && this.lc.isTopToBottom() && dc.getAlignOrDefault(!isRows) == UnitValue.BASELINE_IDENTITY;
                     boolean isBaseline = isRows && cw.isBaselineAlign(rowBaselineAlign);
                     String linkCtx = isBaseline ? "baseline" : null;
                     boolean foundList = false;
                     int glIx = 0;

                     for (int lastGl = groupList.size() - 1; glIx <= lastGl; glIx++) {
                        Grid.LinkedDimGroup group = (Grid.LinkedDimGroup)groupList.get(glIx);
                        if (group.linkCtx == linkCtx || linkCtx != null && linkCtx.equals(group.linkCtx)) {
                           group.addCompWrap(cw);
                           foundList = true;
                           break;
                        }
                     }

                     if (!foundList) {
                        glIx = isBaseline ? 2 : 1;
                        Grid.LinkedDimGroup lg = new Grid.LinkedDimGroup(linkCtx, 1, glIx, !isRows, fromEnd);
                        lg.addCompWrap(cw);
                        groupList.add(lg);
                     }
                  }
               }
            }
         }
      }

      return groupLists;
   }

   private static int convertSpanToSparseGrid(int curIx, int span, TreeSet<Integer> indexes) {
      int lastIx = curIx + span;
      int retSpan = 1;

      for (Integer ix : indexes) {
         if (ix > curIx) {
            if (ix >= lastIx) {
               break;
            }

            retSpan++;
         }
      }

      return retSpan;
   }

   private boolean isCellFree(int r, int c, ArrayList<int[]> occupiedRects) {
      if (this.getCell(r, c) != null) {
         return false;
      } else {
         for (int[] rect : occupiedRects) {
            if (rect[0] <= c && rect[1] <= r && rect[0] + rect[2] > c && rect[1] + rect[3] > r) {
               return false;
            }
         }

         return true;
      }
   }

   private Grid.Cell getCell(int r, int c) {
      return (Grid.Cell)this.grid.get((r << 16) + c);
   }

   private void setCell(int r, int c, Grid.Cell cell) {
      if (c < 0 || r < 0) {
         throw new IllegalArgumentException("Cell position cannot be negative. row: " + r + ", col: " + c);
      } else if (c <= 30000 && r <= 30000) {
         this.rowIndexes.add(r);
         this.colIndexes.add(c);
         this.grid.put((r << 16) + c, cell);
      } else {
         throw new IllegalArgumentException("Cell position out of bounds. Out of cells. row: " + r + ", col: " + c);
      }
   }

   private void addDockingCell(int[] dockInsets, int side, Grid.CompWrap cw) {
      int spanx = 1;
      int spany = 1;
      int r;
      int c;
      switch (side) {
         case 0:
         case 2:
            r = side == 0 ? dockInsets[0]++ : dockInsets[2]--;
            c = dockInsets[1];
            spanx = dockInsets[3] - dockInsets[1] + 1;
            this.colIndexes.add(dockInsets[3]);
            break;
         case 1:
         case 3:
            c = side == 1 ? dockInsets[1]++ : dockInsets[3]--;
            r = dockInsets[0];
            spany = dockInsets[2] - dockInsets[0] + 1;
            this.rowIndexes.add(dockInsets[2]);
            break;
         default:
            throw new IllegalArgumentException("Internal error 123.");
      }

      this.rowIndexes.add(r);
      this.colIndexes.add(c);
      this.grid.put((r << 16) + c, new Grid.Cell(cw, spanx, spany, spanx > 1));
   }

   private static void layoutBaseline(
      ContainerWrapper parent, ArrayList<Grid.CompWrap> compWraps, DimConstraint dc, int start, int size, int sizeType, int spanCount
   ) {
      int[] aboveBelow = getBaselineAboveBelow(compWraps, sizeType, true);
      int blRowSize = aboveBelow[0] + aboveBelow[1];
      CC cc = ((Grid.CompWrap)compWraps.get(0)).cc;
      UnitValue align = cc.getVertical().getAlign();
      if (spanCount == 1 && align == null) {
         align = dc.getAlignOrDefault(false);
      }

      if (align == UnitValue.BASELINE_IDENTITY) {
         align = UnitValue.CENTER;
      }

      int offset = start + aboveBelow[0] + (align != null ? Math.max(0, align.getPixels((float)(size - blRowSize), parent, null)) : 0);
      int i = 0;

      for (int iSz = compWraps.size(); i < iSz; i++) {
         Grid.CompWrap cw = (Grid.CompWrap)compWraps.get(i);
         cw.y = cw.y + offset;
         if (cw.y + cw.h > start + size) {
            cw.h = start + size - cw.y;
         }
      }
   }

   private static void layoutSerial(
      ContainerWrapper parent, ArrayList<Grid.CompWrap> compWraps, DimConstraint dc, int start, int size, boolean isHor, int spanCount, boolean fromEnd
   ) {
      Grid.FlowSizeSpec fss = mergeSizesGapsAndResConstrs(
         getComponentResizeConstraints(compWraps, isHor), getComponentGapPush(compWraps, isHor), getComponentSizes(compWraps, isHor), getGaps(compWraps, isHor)
      );
      Float[] pushW = dc.isFill() ? GROW_100 : null;
      int[] sizes = LayoutUtil.calculateSerial(fss.sizes, fss.resConstsInclGaps, pushW, 1, size);
      setCompWrapBounds(parent, sizes, compWraps, dc.getAlignOrDefault(isHor), start, size, isHor, fromEnd);
   }

   private static void setCompWrapBounds(
      ContainerWrapper parent, int[] allSizes, ArrayList<Grid.CompWrap> compWraps, UnitValue rowAlign, int start, int size, boolean isHor, boolean fromEnd
   ) {
      int totSize = LayoutUtil.sum(allSizes);
      CC cc = ((Grid.CompWrap)compWraps.get(0)).cc;
      UnitValue align = correctAlign(cc, rowAlign, isHor, fromEnd);
      int cSt = start;
      int slack = size - totSize;
      if (slack > 0 && align != null) {
         int al = Math.min(slack, Math.max(0, align.getPixels((float)slack, parent, null)));
         cSt = start + (fromEnd ? -al : al);
      }

      int i = 0;
      int bIx = 0;

      for (int iSz = compWraps.size(); i < iSz; i++) {
         Grid.CompWrap cw = (Grid.CompWrap)compWraps.get(i);
         if (fromEnd) {
            cSt -= allSizes[bIx++];
            cw.setDimBounds(cSt - allSizes[bIx], allSizes[bIx], isHor);
            cSt -= allSizes[bIx++];
         } else {
            cSt += allSizes[bIx++];
            cw.setDimBounds(cSt, allSizes[bIx], isHor);
            cSt += allSizes[bIx++];
         }
      }
   }

   private static void layoutParallel(
      ContainerWrapper parent, ArrayList<Grid.CompWrap> compWraps, DimConstraint dc, int start, int size, boolean isHor, boolean fromEnd
   ) {
      int[][] sizes = new int[compWraps.size()][];

      for (int i = 0; i < sizes.length; i++) {
         Grid.CompWrap cw = (Grid.CompWrap)compWraps.get(i);
         DimConstraint cDc = cw.cc.getDimConstraint(isHor);
         ResizeConstraint[] resConstr = new ResizeConstraint[]{
            cw.isPushGap(isHor, true) ? GAP_RC_CONST_PUSH : GAP_RC_CONST, cDc.resize, cw.isPushGap(isHor, false) ? GAP_RC_CONST_PUSH : GAP_RC_CONST
         };
         int[][] sz = new int[][]{cw.getGaps(isHor, true), isHor ? cw.horSizes : cw.verSizes, cw.getGaps(isHor, false)};
         Float[] pushW = dc.isFill() ? GROW_100 : null;
         sizes[i] = LayoutUtil.calculateSerial(sz, resConstr, pushW, 1, size);
      }

      UnitValue rowAlign = dc.getAlignOrDefault(isHor);
      setCompWrapBounds(parent, sizes, compWraps, rowAlign, start, size, isHor, fromEnd);
   }

   private static void setCompWrapBounds(
      ContainerWrapper parent, int[][] sizes, ArrayList<Grid.CompWrap> compWraps, UnitValue rowAlign, int start, int size, boolean isHor, boolean fromEnd
   ) {
      for (int i = 0; i < sizes.length; i++) {
         Grid.CompWrap cw = (Grid.CompWrap)compWraps.get(i);
         UnitValue align = correctAlign(cw.cc, rowAlign, isHor, fromEnd);
         int[] cSizes = sizes[i];
         int gapBef = cSizes[0];
         int cSize = cSizes[1];
         int gapAft = cSizes[2];
         int cSt = fromEnd ? start - gapBef : start + gapBef;
         int slack = size - cSize - gapBef - gapAft;
         if (slack > 0 && align != null) {
            int al = Math.min(slack, Math.max(0, align.getPixels((float)slack, parent, null)));
            cSt += fromEnd ? -al : al;
         }

         cw.setDimBounds(fromEnd ? cSt - cSize : cSt, cSize, isHor);
      }
   }

   private static UnitValue correctAlign(CC cc, UnitValue rowAlign, boolean isHor, boolean fromEnd) {
      UnitValue align = (isHor ? cc.getHorizontal() : cc.getVertical()).getAlign();
      if (align == null) {
         align = rowAlign;
      }

      if (align == UnitValue.BASELINE_IDENTITY) {
         align = UnitValue.CENTER;
      }

      if (fromEnd) {
         if (align == UnitValue.LEFT) {
            align = UnitValue.RIGHT;
         } else if (align == UnitValue.RIGHT) {
            align = UnitValue.LEFT;
         }
      }

      return align;
   }

   private static int[] getBaselineAboveBelow(ArrayList<Grid.CompWrap> compWraps, int sType, boolean centerBaseline) {
      int maxAbove = -32768;
      int maxBelow = -32768;
      int i = 0;

      for (int iSz = compWraps.size(); i < iSz; i++) {
         Grid.CompWrap cw = (Grid.CompWrap)compWraps.get(i);
         int height = cw.getSize(sType, false);
         if (height >= 2097051) {
            return new int[]{1048525, 1048525};
         }

         int baseline = cw.getBaseline(sType);
         int above = baseline + cw.getGapBefore(sType, false);
         maxAbove = Math.max(above, maxAbove);
         maxBelow = Math.max(height - baseline + cw.getGapAfter(sType, false), maxBelow);
         if (centerBaseline) {
            cw.setDimBounds(-baseline, height, false);
         }
      }

      return new int[]{maxAbove, maxBelow};
   }

   private static int getTotalSizeParallel(ArrayList<Grid.CompWrap> compWraps, int sType, boolean isHor) {
      int size = sType == 2 ? 2097051 : 0;
      int i = 0;

      for (int iSz = compWraps.size(); i < iSz; i++) {
         Grid.CompWrap cw = (Grid.CompWrap)compWraps.get(i);
         int cwSize = cw.getSizeInclGaps(sType, isHor);
         if (cwSize >= 2097051) {
            return 2097051;
         }

         if (sType == 2 ? cwSize < size : cwSize > size) {
            size = cwSize;
         }
      }

      return constrainSize(size);
   }

   private static int getTotalSizeSerial(ArrayList<Grid.CompWrap> compWraps, int sType, boolean isHor) {
      int totSize = 0;
      int i = 0;
      int iSz = compWraps.size();

      for (int lastGapAfter = 0; i < iSz; i++) {
         Grid.CompWrap wrap = (Grid.CompWrap)compWraps.get(i);
         int gapBef = wrap.getGapBefore(sType, isHor);
         if (gapBef > lastGapAfter) {
            totSize += gapBef - lastGapAfter;
         }

         totSize += wrap.getSize(sType, isHor);
         totSize += lastGapAfter = wrap.getGapAfter(sType, isHor);
         if (totSize >= 2097051) {
            return 2097051;
         }
      }

      return constrainSize(totSize);
   }

   private static int getTotalGroupsSizeParallel(ArrayList<Grid.LinkedDimGroup> groups, int sType, boolean countSpanning) {
      int size = sType == 2 ? 2097051 : 0;
      int i = 0;

      for (int iSz = groups.size(); i < iSz; i++) {
         Grid.LinkedDimGroup group = (Grid.LinkedDimGroup)groups.get(i);
         if (countSpanning || group.span == 1) {
            int grpSize = group.getMinPrefMax()[sType];
            if (grpSize >= 2097051) {
               return 2097051;
            }

            if (sType == 2 ? grpSize < size : grpSize > size) {
               size = grpSize;
            }
         }
      }

      return constrainSize(size);
   }

   private static int[][] getComponentSizes(ArrayList<Grid.CompWrap> compWraps, boolean isHor) {
      int[][] compSizes = new int[compWraps.size()][];

      for (int i = 0; i < compSizes.length; i++) {
         Grid.CompWrap cw = (Grid.CompWrap)compWraps.get(i);
         compSizes[i] = isHor ? cw.horSizes : cw.verSizes;
      }

      return compSizes;
   }

   private static Grid.FlowSizeSpec mergeSizesGapsAndResConstrs(ResizeConstraint[] resConstr, boolean[] gapPush, int[][] minPrefMaxSizes, int[][] gapSizes) {
      int[][] sizes = new int[(minPrefMaxSizes.length << 1) + 1][];
      ResizeConstraint[] resConstsInclGaps = new ResizeConstraint[sizes.length];
      sizes[0] = gapSizes[0];
      int i = 0;

      for (int crIx = 1; i < minPrefMaxSizes.length; crIx += 2) {
         resConstsInclGaps[crIx] = resConstr[i];
         sizes[crIx] = minPrefMaxSizes[i];
         sizes[crIx + 1] = gapSizes[i + 1];
         if (sizes[crIx - 1] != null) {
            resConstsInclGaps[crIx - 1] = gapPush[i < gapPush.length ? i : gapPush.length - 1] ? GAP_RC_CONST_PUSH : GAP_RC_CONST;
         }

         if (i == minPrefMaxSizes.length - 1 && sizes[crIx + 1] != null) {
            resConstsInclGaps[crIx + 1] = gapPush[i + 1 < gapPush.length ? i + 1 : gapPush.length - 1] ? GAP_RC_CONST_PUSH : GAP_RC_CONST;
         }

         i++;
      }

      for (int ix = 0; ix < sizes.length; ix++) {
         if (sizes[ix] == null) {
            sizes[ix] = new int[3];
         }
      }

      return new Grid.FlowSizeSpec(sizes, resConstsInclGaps);
   }

   private static int[] mergeSizes(int[] oldValues, int[] newValues) {
      if (oldValues == null) {
         return newValues;
      } else if (newValues == null) {
         return oldValues;
      } else {
         int[] ret = new int[oldValues.length];

         for (int i = 0; i < ret.length; i++) {
            ret[i] = mergeSizes(oldValues[i], newValues[i], true);
         }

         return ret;
      }
   }

   private static int mergeSizes(int oldValue, int newValue, boolean toMax) {
      if (oldValue == -2147471302 || oldValue == newValue) {
         return newValue;
      } else if (newValue == -2147471302) {
         return oldValue;
      } else {
         return toMax != oldValue > newValue ? newValue : oldValue;
      }
   }

   private static int constrainSize(int s) {
      return s > 0 ? (s < 2097051 ? s : 2097051) : 0;
   }

   private static void correctMinMax(int[] s) {
      if (s[0] > s[2]) {
         s[0] = s[2];
      }

      if (s[1] < s[0]) {
         s[1] = s[0];
      }

      if (s[1] > s[2]) {
         s[1] = s[2];
      }
   }

   private static Float[] extractSubArray(DimConstraint[] specs, Float[] arr, int ix, int len) {
      if (arr != null && arr.length >= ix + len) {
         Float[] newArr = new Float[len];

         for (int i = 0; i < len; i++) {
            newArr[i] = arr[ix + i];
         }

         return newArr;
      } else {
         Float[] growLastArr = new Float[len];

         for (int i = ix + len - 1; i >= 0; i -= 2) {
            int specIx = i >> 1;
            if (specs[specIx] != DOCK_DIM_CONSTRAINT) {
               growLastArr[i - ix] = ResizeConstraint.WEIGHT_100;
               return growLastArr;
            }
         }

         return growLastArr;
      }
   }

   private static synchronized void putSizesAndIndexes(Object parComp, int[] sizes, int[] ixArr, boolean isRows) {
      if (PARENT_ROWCOL_SIZES_MAP == null) {
         PARENT_ROWCOL_SIZES_MAP = new WeakHashMap[]{new WeakHashMap(4), new WeakHashMap(4)};
      }

      PARENT_ROWCOL_SIZES_MAP[isRows ? 0 : 1].put(parComp, new int[][]{ixArr, sizes});
   }

   static synchronized int[][] getSizesAndIndexes(Object parComp, boolean isRows) {
      return PARENT_ROWCOL_SIZES_MAP == null ? (int[][])null : (int[][])PARENT_ROWCOL_SIZES_MAP[isRows ? 0 : 1].get(parComp);
   }

   private static synchronized void saveGrid(ComponentWrapper parComp, LinkedHashMap<Integer, Grid.Cell> grid) {
      if (PARENT_GRIDPOS_MAP == null) {
         PARENT_GRIDPOS_MAP = new WeakHashMap();
      }

      PARENT_GRIDPOS_MAP.put(parComp.getComponent(), grid);
   }

   static synchronized HashMap<Object, int[]> getGridPositions(Object parComp) {
      if (PARENT_GRIDPOS_MAP == null) {
         return null;
      } else {
         LinkedHashMap<Integer, Grid.Cell> grid = (LinkedHashMap<Integer, Grid.Cell>)PARENT_GRIDPOS_MAP.get(parComp);
         if (grid == null) {
            return null;
         } else {
            HashMap<Object, int[]> retMap = new HashMap();

            for (Entry<Integer, Grid.Cell> e : grid.entrySet()) {
               Grid.Cell cell = (Grid.Cell)e.getValue();
               Integer xyInt = (Integer)e.getKey();
               if (xyInt != null) {
                  int x = xyInt & 65535;
                  int y = xyInt >> 16;

                  for (Grid.CompWrap cw : cell.compWraps) {
                     retMap.put(cw.comp.getComponent(), new int[]{x, y, cell.spanx, cell.spany});
                  }
               }
            }

            return retMap;
         }
      }
   }

   static {
      DOCK_DIM_CONSTRAINT.setGrowPriority(0);
   }

   private static class Cell {
      private final int spanx;
      private final int spany;
      private final boolean flowx;
      private final ArrayList<Grid.CompWrap> compWraps = new ArrayList(1);
      private boolean hasTagged = false;

      private Cell(Grid.CompWrap cw) {
         this(cw, 1, 1, true);
      }

      private Cell(int spanx, int spany, boolean flowx) {
         this(null, spanx, spany, flowx);
      }

      private Cell(Grid.CompWrap cw, int spanx, int spany, boolean flowx) {
         if (cw != null) {
            this.compWraps.add(cw);
         }

         this.spanx = spanx;
         this.spany = spany;
         this.flowx = flowx;
      }
   }

   private static final class CompWrap {
      private final ComponentWrapper comp;
      private final CC cc;
      private final UnitValue[] pos;
      private int[][] gaps;
      private final int[] horSizes = new int[3];
      private final int[] verSizes = new int[3];
      private int x = -2147471302;
      private int y = -2147471302;
      private int w = -2147471302;
      private int h = -2147471302;
      private int forcedPushGaps = 0;

      private CompWrap(ComponentWrapper c, CC cc, int eHideMode, UnitValue[] pos, BoundSize[] callbackSz) {
         this.comp = c;
         this.cc = cc;
         this.pos = pos;
         if (eHideMode <= 0) {
            BoundSize hBS = callbackSz != null && callbackSz[0] != null ? callbackSz[0] : cc.getHorizontal().getSize();
            BoundSize vBS = callbackSz != null && callbackSz[1] != null ? callbackSz[1] : cc.getVertical().getSize();
            int wHint = -1;
            int hHint = -1;
            if (this.comp.getWidth() > 0 && this.comp.getHeight() > 0) {
               hHint = this.comp.getHeight();
               wHint = this.comp.getWidth();
            }

            for (int i = 0; i <= 2; i++) {
               this.horSizes[i] = this.getSize(hBS, i, true, hHint);
               this.verSizes[i] = this.getSize(vBS, i, false, wHint > 0 ? wHint : this.horSizes[i]);
            }

            Grid.correctMinMax(this.horSizes);
            Grid.correctMinMax(this.verSizes);
         }

         if (eHideMode > 1) {
            this.gaps = new int[4][];

            for (int i = 0; i < this.gaps.length; i++) {
               this.gaps[i] = new int[3];
            }
         }
      }

      private int getSize(BoundSize uvs, int sizeType, boolean isHor, int sizeHint) {
         if (uvs != null && uvs.getSize(sizeType) != null) {
            ContainerWrapper par = this.comp.getParent();
            return uvs.getSize(sizeType).getPixels(isHor ? (float)par.getWidth() : (float)par.getHeight(), par, this.comp);
         } else {
            switch (sizeType) {
               case 0:
                  return isHor ? this.comp.getMinimumWidth(sizeHint) : this.comp.getMinimumHeight(sizeHint);
               case 1:
                  return isHor ? this.comp.getPreferredWidth(sizeHint) : this.comp.getPreferredHeight(sizeHint);
               default:
                  return isHor ? this.comp.getMaximumWidth(sizeHint) : this.comp.getMaximumHeight(sizeHint);
            }
         }
      }

      private void calcGaps(ComponentWrapper before, CC befCC, ComponentWrapper after, CC aftCC, String tag, boolean flowX, boolean isLTR) {
         ContainerWrapper par = this.comp.getParent();
         int parW = par.getWidth();
         int parH = par.getHeight();
         BoundSize befGap = before != null ? (flowX ? befCC.getHorizontal() : befCC.getVertical()).getGapAfter() : null;
         BoundSize aftGap = after != null ? (flowX ? aftCC.getHorizontal() : aftCC.getVertical()).getGapBefore() : null;
         this.mergeGapSizes(this.cc.getVertical().getComponentGaps(par, this.comp, befGap, flowX ? null : before, tag, parH, 0, isLTR), false, true);
         this.mergeGapSizes(this.cc.getHorizontal().getComponentGaps(par, this.comp, befGap, flowX ? before : null, tag, parW, 1, isLTR), true, true);
         this.mergeGapSizes(this.cc.getVertical().getComponentGaps(par, this.comp, aftGap, flowX ? null : after, tag, parH, 2, isLTR), false, false);
         this.mergeGapSizes(this.cc.getHorizontal().getComponentGaps(par, this.comp, aftGap, flowX ? after : null, tag, parW, 3, isLTR), true, false);
      }

      private void setDimBounds(int start, int size, boolean isHor) {
         if (isHor) {
            this.x = start;
            this.w = size;
         } else {
            this.y = start;
            this.h = size;
         }
      }

      private boolean isPushGap(boolean isHor, boolean isBefore) {
         if (isHor && ((isBefore ? 1 : 2) & this.forcedPushGaps) != 0) {
            return true;
         } else {
            DimConstraint dc = this.cc.getDimConstraint(isHor);
            BoundSize s = isBefore ? dc.getGapBefore() : dc.getGapAfter();
            return s != null && s.getGapPush();
         }
      }

      private boolean transferBounds(boolean checkPrefChange) {
         this.comp.setBounds(this.x, this.y, this.w, this.h);
         if (checkPrefChange && this.w != this.horSizes[1]) {
            BoundSize vSz = this.cc.getVertical().getSize();
            if (vSz.getPreferred() == null && this.comp.getPreferredHeight(-1) != this.verSizes[1]) {
               return true;
            }
         }

         return false;
      }

      private void setSizes(int[] sizes, boolean isHor) {
         if (sizes != null) {
            int[] s = isHor ? this.horSizes : this.verSizes;
            s[0] = sizes[0];
            s[1] = sizes[1];
            s[2] = sizes[2];
         }
      }

      private void setGaps(int[] minPrefMax, int ix) {
         if (this.gaps == null) {
            this.gaps = new int[][]{null, null, null, null};
         }

         this.gaps[ix] = minPrefMax;
      }

      private void mergeGapSizes(int[] sizes, boolean isHor, boolean isTL) {
         if (this.gaps == null) {
            this.gaps = new int[][]{null, null, null, null};
         }

         if (sizes != null) {
            int gapIX = this.getGapIx(isHor, isTL);
            int[] oldGaps = this.gaps[gapIX];
            if (oldGaps == null) {
               oldGaps = new int[]{0, 0, 2097051};
               this.gaps[gapIX] = oldGaps;
            }

            oldGaps[0] = Math.max(sizes[0], oldGaps[0]);
            oldGaps[1] = Math.max(sizes[1], oldGaps[1]);
            oldGaps[2] = Math.min(sizes[2], oldGaps[2]);
         }
      }

      private int getGapIx(boolean isHor, boolean isTL) {
         return isHor ? (isTL ? 1 : 3) : (isTL ? 0 : 2);
      }

      private int getSizeInclGaps(int sizeType, boolean isHor) {
         return this.filter(sizeType, this.getGapBefore(sizeType, isHor) + this.getSize(sizeType, isHor) + this.getGapAfter(sizeType, isHor));
      }

      private int getSize(int sizeType, boolean isHor) {
         return this.filter(sizeType, isHor ? this.horSizes[sizeType] : this.verSizes[sizeType]);
      }

      private int getGapBefore(int sizeType, boolean isHor) {
         int[] gaps = this.getGaps(isHor, true);
         return gaps != null ? this.filter(sizeType, gaps[sizeType]) : 0;
      }

      private int getGapAfter(int sizeType, boolean isHor) {
         int[] gaps = this.getGaps(isHor, false);
         return gaps != null ? this.filter(sizeType, gaps[sizeType]) : 0;
      }

      private int[] getGaps(boolean isHor, boolean isTL) {
         return this.gaps[this.getGapIx(isHor, isTL)];
      }

      private int filter(int sizeType, int size) {
         if (size == -2147471302) {
            return sizeType != 2 ? 0 : 2097051;
         } else {
            return Grid.constrainSize(size);
         }
      }

      private boolean isBaselineAlign(boolean defValue) {
         Float g = this.cc.getVertical().getGrow();
         if (g != null && g.intValue() != 0) {
            return false;
         } else {
            UnitValue al = this.cc.getVertical().getAlign();
            return (al != null ? al == UnitValue.BASELINE_IDENTITY : defValue) && this.comp.hasBaseline();
         }
      }

      private int getBaseline(int sizeType) {
         return this.comp.getBaseline(this.getSize(sizeType, true), this.getSize(sizeType, false));
      }
   }

   private static final class FlowSizeSpec {
      private final int[][] sizes;
      private final ResizeConstraint[] resConstsInclGaps;

      private FlowSizeSpec(int[][] sizes, ResizeConstraint[] resConstsInclGaps) {
         this.sizes = sizes;
         this.resConstsInclGaps = resConstsInclGaps;
      }

      private int expandSizes(DimConstraint[] specs, Float[] defGrow, int targetSize, int fromIx, int len, int sizeType, int eagerness) {
         ResizeConstraint[] resConstr = new ResizeConstraint[len];
         int[][] sizesToExpand = new int[len][];

         for (int i = 0; i < len; i++) {
            int[] minPrefMax = this.sizes[i + fromIx];
            sizesToExpand[i] = new int[]{minPrefMax[sizeType], minPrefMax[1], minPrefMax[2]};
            if (eagerness <= 1 && i % 2 == 0) {
               int cIx = i + fromIx - 1 >> 1;
               DimConstraint spec = (DimConstraint)LayoutUtil.getIndexSafe(specs, cIx);
               BoundSize sz = spec.getSize();
               if (sizeType == 0 && sz.getMin() != null && sz.getMin().getUnit() != 13
                  || sizeType == 1 && sz.getPreferred() != null && sz.getPreferred().getUnit() != 14) {
                  continue;
               }
            }

            resConstr[i] = (ResizeConstraint)LayoutUtil.getIndexSafe(this.resConstsInclGaps, i + fromIx);
         }

         Float[] growW = eagerness != 1 && eagerness != 3 ? null : Grid.extractSubArray(specs, defGrow, fromIx, len);
         int[] newSizes = LayoutUtil.calculateSerial(sizesToExpand, resConstr, growW, 1, targetSize);
         int newSize = 0;

         for (int i = 0; i < len; i++) {
            int s = newSizes[i];
            this.sizes[i + fromIx][sizeType] = s;
            newSize += s;
         }

         return newSize;
      }
   }

   private static class LinkedDimGroup {
      private static final int TYPE_SERIAL = 0;
      private static final int TYPE_PARALLEL = 1;
      private static final int TYPE_BASELINE = 2;
      private final String linkCtx;
      private final int span;
      private final int linkType;
      private final boolean isHor;
      private final boolean fromEnd;
      private ArrayList<Grid.CompWrap> _compWraps = new ArrayList(4);
      private int[] sizes = null;
      private int lStart = 0;
      private int lSize = 0;

      private LinkedDimGroup(String linkCtx, int span, int linkType, boolean isHor, boolean fromEnd) {
         this.linkCtx = linkCtx;
         this.span = span;
         this.linkType = linkType;
         this.isHor = isHor;
         this.fromEnd = fromEnd;
      }

      private void addCompWrap(Grid.CompWrap cw) {
         this._compWraps.add(cw);
         this.sizes = null;
      }

      private void setCompWraps(ArrayList<Grid.CompWrap> cws) {
         if (this._compWraps != cws) {
            this._compWraps = cws;
            this.sizes = null;
         }
      }

      private void layout(DimConstraint dc, int start, int size, int spanCount) {
         this.lStart = start;
         this.lSize = size;
         if (!this._compWraps.isEmpty()) {
            ContainerWrapper parent = ((Grid.CompWrap)this._compWraps.get(0)).comp.getParent();
            if (this.linkType == 1) {
               Grid.layoutParallel(parent, this._compWraps, dc, start, size, this.isHor, this.fromEnd);
            } else if (this.linkType == 2) {
               Grid.layoutBaseline(parent, this._compWraps, dc, start, size, 1, spanCount);
            } else {
               Grid.layoutSerial(parent, this._compWraps, dc, start, size, this.isHor, spanCount, this.fromEnd);
            }
         }
      }

      private int[] getMinPrefMax() {
         if (this.sizes == null && this._compWraps.size() > 0) {
            this.sizes = new int[3];

            for (int sType = 0; sType <= 1; sType++) {
               if (this.linkType == 1) {
                  this.sizes[sType] = Grid.getTotalSizeParallel(this._compWraps, sType, this.isHor);
               } else if (this.linkType == 2) {
                  int[] aboveBelow = Grid.getBaselineAboveBelow(this._compWraps, sType, false);
                  this.sizes[sType] = aboveBelow[0] + aboveBelow[1];
               } else {
                  this.sizes[sType] = Grid.getTotalSizeSerial(this._compWraps, sType, this.isHor);
               }
            }

            this.sizes[2] = 2097051;
         }

         return this.sizes;
      }
   }
}
