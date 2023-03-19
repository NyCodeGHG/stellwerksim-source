package js.java.tools.ttt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Computer extends Player {
   int t = 0;
   Node begin = new Node("000000000", 0, this);
   Node current = this.begin;
   double lr = 0.0;
   File[] layerFiles = new File[9];
   ArrayList<Layer> layers = new ArrayList();

   public Computer(String l) {
      super();

      for(int i = 0; i < 9; ++i) {
         this.layerFiles[i] = new File(l + (i + 1) + ".nodes");
         if (!this.layerFiles[i].exists()) {
            try {
               this.layerFiles[i].createNewFile();
            } catch (Exception var4) {
               System.out.println(var4);
            }
         }
      }

      this.makeNewMind();
      this.evaluate();
      this.saveMind();
      this.begin.subNodes = "100000000,010000000,001000000,000100000,000010000,000001000,000000100,000000010,000000001,";
      this.begin.extractNodes();
   }

   public void evaluate() {
      for(int i = 0; i < 9; ++i) {
         Layer l = (Layer)this.layers.get(i);

         for(int j = 0; j < l.nodes.size(); ++j) {
            String x = "";
            String o = "";
            String s = ((Node)l.nodes.get(j)).id;

            for(int k = 0; k < 9; ++k) {
               char ch = s.charAt(k);
               if (ch == '1') {
                  x = x + (k + 1);
               } else if (ch == '2') {
                  o = o + (k + 1);
               }
            }

            int r = TicTacToe.checkWin2(x, o);
            switch(r) {
               case -1:
                  ((Node)l.nodes.get(j)).nodeType = 1;
               case 0:
               default:
                  break;
               case 1:
                  ((Node)l.nodes.get(j)).pref = 50;
                  ((Node)l.nodes.get(j)).nodeType = 1;
                  break;
               case 2:
                  ((Node)l.nodes.get(j)).pref = -50;
                  ((Node)l.nodes.get(j)).nodeType = 1;
            }
         }
      }
   }

   public void loadMind() {
      int i = 0;

      try {
         for(int l = 0; l < 9; ++l) {
            this.layers.add(new Layer(l + 1));
            FileReader f = new FileReader(this.layerFiles[l]);
            BufferedReader r = new BufferedReader(f);

            while(r.readLine() != null) {
               Node temp = new Node(r.readLine(), l + 1, this);
               temp.subNodes = r.readLine();
               String no = r.readLine();
               temp.pref = Integer.parseInt(no);
               temp.n = Integer.parseInt(r.readLine());
               temp.nodeType = Integer.parseInt(r.readLine());
               ((Layer)this.layers.get(l)).nodes.add(temp);
            }
         }

         for(int l = 0; l < 9; ++l) {
            for(int j = 0; j < ((Layer)this.layers.get(l)).nodes.size(); ++j) {
               ((Node)((Layer)this.layers.get(l)).nodes.get(j)).extractNodes();
            }
         }
      } catch (Exception var8) {
         var8.printStackTrace(System.out);
      }
   }

   public void saveMind() {
      for(int i = 7; i >= 0; --i) {
         ((Layer)this.layers.get(i)).refreshLayer();
      }

      try {
         for(int i = 0; i < 9; ++i) {
            Layer l = (Layer)this.layers.get(i);
            PrintWriter p = new PrintWriter(new BufferedWriter(new FileWriter(this.layerFiles[i])));

            for(int j = 0; j < l.nodes.size(); ++j) {
               Node temp = (Node)l.nodes.get(j);
               p.println("***********************************************");
               p.println(temp.id);
               String s = "";

               for(int k = 0; k < temp.sub_nodes.size(); ++k) {
                  s = s + ((Node)temp.sub_nodes.get(k)).id + ",";
               }

               p.println(s);
               p.println(temp.pref);
               p.println(temp.n);
               p.println(temp.nodeType);
            }

            p.close();
         }
      } catch (Exception var8) {
         var8.printStackTrace(System.out);
      }
   }

   public void makeNewMind() {
      this.layers.add(new Layer(1));
      this.layers.add(new Layer(2));
      this.layers.add(new Layer(3));
      this.layers.add(new Layer(4));
      this.layers.add(new Layer(5));
      this.layers.add(new Layer(6));
      this.layers.add(new Layer(7));
      this.layers.add(new Layer(8));
      this.layers.add(new Layer(9));

      for(int i1 = 0; i1 <= 2; ++i1) {
         for(int i2 = 0; i2 <= 2; ++i2) {
            for(int i3 = 0; i3 <= 2; ++i3) {
               for(int i4 = 0; i4 <= 2; ++i4) {
                  for(int i5 = 0; i5 <= 2; ++i5) {
                     for(int i6 = 0; i6 <= 2; ++i6) {
                        for(int i7 = 0; i7 <= 2; ++i7) {
                           for(int i8 = 0; i8 <= 2; ++i8) {
                              for(int i9 = 0; i9 <= 2; ++i9) {
                                 int l = 9;
                                 if (i1 == 0) {
                                    --l;
                                 }

                                 if (i2 == 0) {
                                    --l;
                                 }

                                 if (i3 == 0) {
                                    --l;
                                 }

                                 if (i4 == 0) {
                                    --l;
                                 }

                                 if (i5 == 0) {
                                    --l;
                                 }

                                 if (i6 == 0) {
                                    --l;
                                 }

                                 if (i7 == 0) {
                                    --l;
                                 }

                                 if (i8 == 0) {
                                    --l;
                                 }

                                 if (i9 == 0) {
                                    --l;
                                 }

                                 int x = 0;
                                 if (i1 == 1) {
                                    ++x;
                                 }

                                 if (i2 == 1) {
                                    ++x;
                                 }

                                 if (i3 == 1) {
                                    ++x;
                                 }

                                 if (i4 == 1) {
                                    ++x;
                                 }

                                 if (i5 == 1) {
                                    ++x;
                                 }

                                 if (i6 == 1) {
                                    ++x;
                                 }

                                 if (i7 == 1) {
                                    ++x;
                                 }

                                 if (i8 == 1) {
                                    ++x;
                                 }

                                 if (i9 == 1) {
                                    ++x;
                                 }

                                 int o = l - x;
                                 if ((x - o == 0 || x - o == 1) && l != 0) {
                                    String id = "" + i1 + i2 + i3 + i4 + i5 + i6 + i7 + i8 + i9;
                                    ((Layer)this.layers.get(l - 1)).nodes.add(new Node(id, l, this));
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      for(int l = 1; l < 9; ++l) {
         for(int j = 0; j < ((Layer)this.layers.get(l)).nodes.size(); ++j) {
            Node node = (Node)((Layer)this.layers.get(l)).nodes.get(j);

            for(int i = 0; i < 9; ++i) {
               String newId = "";

               for(int k = 0; k < 9; ++k) {
                  char ch = node.id.charAt(k);
                  if (k == i) {
                     ch = '0';
                  }

                  newId = newId + ch;
               }

               if (!newId.equals(node.id)) {
                  try {
                     ((Layer)this.layers.get(l - 1)).searchById(newId).sub_nodes.add(node);
                  } catch (NullPointerException var14) {
                  }
               }
            }
         }
      }

      this.begin.extractNodes();
   }

   @Override
   public void playTurn(int p, int turn) {
      this.t = turn;
      if (turn != 1) {
         this.current = ((Layer)this.layers.get(turn - 2)).searchByState(TicTacToe.state);
      }

      if (turn == 1) {
         this.current = this.begin;
      }

      if (p == 1) {
         this.current.playNextn();
      }

      if (p == 2) {
         this.current.playNext2();
      }
   }

   @Override
   void notifyWin(int pl) {
      if (pl == 1) {
         this.current.pref += 10;
      }

      if (pl == 2) {
         this.current.pref -= 10;
      }

      this.current.nodeType = 1;
      this.saveMind();
   }

   @Override
   void notifyLose(int pl) {
      if (pl == 1) {
         this.current.pref -= 10;
      }

      if (pl == 2) {
         this.current.pref += 10;
      }

      this.current.nodeType = 1;
      this.saveMind();
   }
}
