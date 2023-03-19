package js.java.tools.ttt;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.MatteBorder;

public class TicTacToe extends JFrame {
   static int[][] winComb = new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {1, 4, 7}, {2, 5, 8}, {3, 6, 9}, {1, 5, 9}, {3, 5, 7}};
   public static int[][] state = new int[][]{{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
   Player pl1 = new Human();
   Player pl2 = new Computer("mind\\layer");
   public static int butClicked = 0;
   int w1 = 0;
   int w2 = 0;
   int dr = 0;
   private JLabel Notification;
   public static JButton b11;
   public static JButton b12;
   public static JButton b13;
   public static JButton b21;
   public static JButton b22;
   public static JButton b23;
   public static JButton b31;
   public static JButton b32;
   public static JButton b33;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JLabel jLabel4;
   private JLabel jLabel5;
   private JLabel jLabel6;
   private JPanel jPanel1;
   private JPanel jPanel2;

   public TicTacToe() {
      super();
      this.initComponents();
   }

   public void start() {
      if (this.w1 == 500) {
         System.exit(0);
      }

      int current = 1;
      int turn = 1;
      int w = 0;

      while((w = checkWin(turn, state)) == 0) {
         if (current == 1) {
            this.pl1.playTurn(1, turn);
            this.refreshGrid();
            current = 2;
         } else if (current == 2) {
            this.pl2.playTurn(2, turn);
            this.refreshGrid();
            current = 1;
         }

         ++turn;

         try {
            Thread.sleep(0L);
         } catch (InterruptedException var6) {
            Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, var6);
         }
      }

      if (w == 1) {
         this.pl1.notifyWin(1);
         this.pl2.notifyLose(2);
         this.print("Player 1 Won The Game !");
         ++this.w1;
      } else if (w == 2) {
         this.pl2.notifyWin(1);
         this.pl1.notifyLose(2);
         this.print("Player 2 Won The Game !");
         ++this.w2;
      } else if (w == -1) {
         this.print("Game DRAW !");
         ++this.dr;
      }

      try {
         Thread.sleep(0L);
      } catch (InterruptedException var5) {
         Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, var5);
      }
   }

   public void refreshGrid() {
      b11.setText(state[0][0] == 1 ? "X" : (state[0][0] == 2 ? "O" : ""));
      b12.setText(state[0][1] == 1 ? "X" : (state[0][1] == 2 ? "O" : ""));
      b13.setText(state[0][2] == 1 ? "X" : (state[0][2] == 2 ? "O" : ""));
      b21.setText(state[1][0] == 1 ? "X" : (state[1][0] == 2 ? "O" : ""));
      b22.setText(state[1][1] == 1 ? "X" : (state[1][1] == 2 ? "O" : ""));
      b23.setText(state[1][2] == 1 ? "X" : (state[1][2] == 2 ? "O" : ""));
      b31.setText(state[2][0] == 1 ? "X" : (state[2][0] == 2 ? "O" : ""));
      b32.setText(state[2][1] == 1 ? "X" : (state[2][1] == 2 ? "O" : ""));
      b33.setText(state[2][2] == 1 ? "X" : (state[2][2] == 2 ? "O" : ""));
      this.jLabel1.setText(" X wins : " + this.w1);
      this.jLabel2.setText(" O wins : " + this.w2);
      this.jLabel3.setText(" Draws  : " + this.dr);
   }

   public static int checkWin(int turn, int[][] st) {
      int ret = 0;
      String x = "";
      String o = "";
      int i = 0;
      int j = 0;
      int c = 0;

      for(int p = 0; p < 3; ++p) {
         for(int q = 0; q < 3; ++q) {
            ++c;
            if (st[p][q] == 1) {
               x = x + c;
            } else if (st[p][q] == 2) {
               o = o + c;
            }
         }
      }

      ret = checkWin2(x, o);
      if (turn == 10 && ret == 0) {
         ret = -1;
      }

      return ret;
   }

   public static int checkWin2(String x, String o) {
      int ret = 0;

      for(int p = 0; p < 8; ++p) {
         if (x.indexOf((char)winComb[p][0] + '0') > -1 && x.indexOf((char)winComb[p][1] + '0') > -1 && x.indexOf((char)winComb[p][2] + '0') > -1) {
            ret = 1;
            break;
         }

         if (o.indexOf((char)winComb[p][0] + '0') > -1 && o.indexOf((char)winComb[p][1] + '0') > -1 && o.indexOf((char)winComb[p][2] + '0') > -1) {
            ret = 2;
            break;
         }
      }

      return ret;
   }

   public void print(String s) {
      this.Notification.setText("\t" + s);
   }

   public void gameInit() {
      state[0][0] = 0;
      state[0][1] = 0;
      state[0][2] = 0;
      state[1][0] = 0;
      state[1][1] = 0;
      state[1][2] = 0;
      state[2][0] = 0;
      state[2][1] = 0;
      state[2][2] = 0;
      this.refreshGrid();
   }

   private void initComponents() {
      this.jPanel1 = new JPanel();
      b21 = new JButton();
      b11 = new JButton();
      b22 = new JButton();
      b12 = new JButton();
      b13 = new JButton();
      b23 = new JButton();
      b31 = new JButton();
      b32 = new JButton();
      b33 = new JButton();
      this.Notification = new JLabel();
      this.jPanel2 = new JPanel();
      this.jLabel1 = new JLabel();
      this.jLabel2 = new JLabel();
      this.jLabel3 = new JLabel();
      this.jLabel4 = new JLabel();
      this.jLabel5 = new JLabel();
      this.jLabel6 = new JLabel();
      this.setDefaultCloseOperation(3);
      this.setAlwaysOnTop(true);
      this.setPreferredSize(new Dimension(600, 400));
      b21.setBackground(new Color(255, 255, 255));
      b21.setFont(new Font("Arial", 1, 48));
      b21.setCursor(new Cursor(12));
      b21.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            TicTacToe.this.b21ActionPerformed(evt);
         }
      });
      b11.setBackground(new Color(255, 255, 255));
      b11.setFont(new Font("Arial", 1, 48));
      b11.setCursor(new Cursor(12));
      b11.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            TicTacToe.this.b11ActionPerformed(evt);
         }
      });
      b22.setBackground(new Color(255, 255, 255));
      b22.setFont(new Font("Arial", 1, 48));
      b22.setCursor(new Cursor(12));
      b22.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            TicTacToe.this.b22ActionPerformed(evt);
         }
      });
      b12.setBackground(new Color(255, 255, 255));
      b12.setFont(new Font("Arial", 1, 48));
      b12.setCursor(new Cursor(12));
      b12.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            TicTacToe.this.b12ActionPerformed(evt);
         }
      });
      b13.setBackground(new Color(255, 255, 255));
      b13.setFont(new Font("Arial", 1, 48));
      b13.setCursor(new Cursor(12));
      b13.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            TicTacToe.this.b13ActionPerformed(evt);
         }
      });
      b23.setBackground(new Color(255, 255, 255));
      b23.setFont(new Font("Arial", 1, 48));
      b23.setCursor(new Cursor(12));
      b23.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            TicTacToe.this.b23ActionPerformed(evt);
         }
      });
      b31.setBackground(new Color(255, 255, 255));
      b31.setFont(new Font("Arial", 1, 48));
      b31.setCursor(new Cursor(12));
      b31.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            TicTacToe.this.b31ActionPerformed(evt);
         }
      });
      b32.setBackground(new Color(255, 255, 255));
      b32.setFont(new Font("Arial", 1, 48));
      b32.setCursor(new Cursor(12));
      b32.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            TicTacToe.this.b32ActionPerformed(evt);
         }
      });
      b33.setBackground(new Color(255, 255, 255));
      b33.setFont(new Font("Arial", 1, 48));
      b33.setCursor(new Cursor(12));
      b33.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            TicTacToe.this.b33ActionPerformed(evt);
         }
      });
      this.Notification.setBackground(new Color(255, 255, 0));
      this.Notification.setFont(new Font("Tahoma", 0, 18));
      this.Notification.setForeground(new Color(0, 0, 102));
      this.Notification.setText("Tic - Tac - Toe");
      this.Notification.setBorder(new MatteBorder(null));
      this.jLabel1.setFont(new Font("Arial", 0, 18));
      this.jLabel1.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
      this.jLabel2.setFont(new Font("Arial", 0, 18));
      this.jLabel2.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
      this.jLabel3.setFont(new Font("Arial", 0, 18));
      this.jLabel3.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
      this.jLabel4.setFont(new Font("Arial", 0, 18));
      this.jLabel4.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
      this.jLabel5.setFont(new Font("Arial", 0, 18));
      this.jLabel5.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
      this.jLabel6.setFont(new Font("Arial", 0, 18));
      this.jLabel6.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
      GroupLayout jPanel2Layout = new GroupLayout(this.jPanel2);
      this.jPanel2.setLayout(jPanel2Layout);
      jPanel2Layout.setHorizontalGroup(
         jPanel2Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(
               jPanel2Layout.createSequentialGroup()
                  .addContainerGap()
                  .addGroup(
                     jPanel2Layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(this.jLabel1, -1, -1, 32767)
                        .addComponent(this.jLabel2, -1, 203, 32767)
                        .addComponent(this.jLabel3, -1, -1, 32767)
                        .addComponent(this.jLabel4, -1, -1, 32767)
                        .addComponent(this.jLabel5, -1, -1, 32767)
                        .addComponent(this.jLabel6, -1, -1, 32767)
                  )
                  .addContainerGap()
            )
      );
      jPanel2Layout.setVerticalGroup(
         jPanel2Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(
               jPanel2Layout.createSequentialGroup()
                  .addContainerGap()
                  .addComponent(this.jLabel1, -2, 40, -2)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(this.jLabel2, -2, 40, -2)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(this.jLabel3, -2, 40, -2)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(this.jLabel4, -2, 40, -2)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(this.jLabel5, -2, 40, -2)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(this.jLabel6, -2, 40, -2)
                  .addContainerGap(-1, 32767)
            )
      );
      this.jLabel1.getAccessibleContext().setAccessibleName("l1");
      this.jLabel1.getAccessibleContext().setAccessibleDescription("");
      GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
      this.jPanel1.setLayout(jPanel1Layout);
      jPanel1Layout.setHorizontalGroup(
         jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(
               jPanel1Layout.createSequentialGroup()
                  .addContainerGap()
                  .addGroup(
                     jPanel1Layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(this.Notification, -1, -1, 32767)
                        .addGroup(
                           jPanel1Layout.createSequentialGroup()
                              .addGroup(
                                 jPanel1Layout.createParallelGroup(Alignment.LEADING)
                                    .addGroup(
                                       jPanel1Layout.createSequentialGroup()
                                          .addGroup(
                                             jPanel1Layout.createParallelGroup(Alignment.LEADING).addComponent(b21, -2, 100, -2).addComponent(b11, -2, 100, -2)
                                          )
                                          .addPreferredGap(ComponentPlacement.RELATED)
                                          .addGroup(
                                             jPanel1Layout.createParallelGroup(Alignment.LEADING)
                                                .addGroup(
                                                   jPanel1Layout.createSequentialGroup()
                                                      .addComponent(b22, -2, 100, -2)
                                                      .addPreferredGap(ComponentPlacement.RELATED)
                                                      .addComponent(b23, -2, 100, -2)
                                                )
                                                .addGroup(
                                                   jPanel1Layout.createSequentialGroup()
                                                      .addComponent(b12, -2, 100, -2)
                                                      .addPreferredGap(ComponentPlacement.RELATED)
                                                      .addComponent(b13, -2, 100, -2)
                                                )
                                          )
                                    )
                                    .addGroup(
                                       jPanel1Layout.createSequentialGroup()
                                          .addComponent(b31, -2, 100, -2)
                                          .addPreferredGap(ComponentPlacement.RELATED)
                                          .addComponent(b32, -2, 100, -2)
                                          .addPreferredGap(ComponentPlacement.RELATED)
                                          .addComponent(b33, -2, 100, -2)
                                    )
                              )
                              .addPreferredGap(ComponentPlacement.RELATED)
                              .addComponent(this.jPanel2, -1, -1, 32767)
                        )
                  )
                  .addContainerGap()
            )
      );
      jPanel1Layout.setVerticalGroup(
         jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(
               jPanel1Layout.createSequentialGroup()
                  .addContainerGap()
                  .addGroup(
                     jPanel1Layout.createParallelGroup(Alignment.LEADING, false)
                        .addGroup(
                           jPanel1Layout.createSequentialGroup()
                              .addGroup(
                                 jPanel1Layout.createParallelGroup(Alignment.LEADING)
                                    .addComponent(b12, -2, 100, -2)
                                    .addComponent(b13, -2, 100, -2)
                                    .addComponent(b11, -2, 100, -2)
                              )
                              .addPreferredGap(ComponentPlacement.RELATED)
                              .addGroup(
                                 jPanel1Layout.createParallelGroup(Alignment.BASELINE)
                                    .addComponent(b22, -2, 100, -2)
                                    .addComponent(b21, -2, 100, -2)
                                    .addComponent(b23, -2, 100, -2)
                              )
                              .addPreferredGap(ComponentPlacement.RELATED)
                              .addGroup(
                                 jPanel1Layout.createParallelGroup(Alignment.BASELINE)
                                    .addComponent(b32, -2, 100, -2)
                                    .addComponent(b31, -2, 100, -2)
                                    .addComponent(b33, -2, 100, -2)
                              )
                        )
                        .addComponent(this.jPanel2, -1, -1, 32767)
                  )
                  .addPreferredGap(ComponentPlacement.UNRELATED)
                  .addComponent(this.Notification, -1, 40, 32767)
                  .addContainerGap()
            )
      );
      GroupLayout layout = new GroupLayout(this.getContentPane());
      this.getContentPane().setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(this.jPanel1, -2, 561, -2).addGap(0, 0, 32767))
      );
      layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(this.jPanel1, -1, 385, 32767));
      this.pack();
   }

   private void b33ActionPerformed(ActionEvent evt) {
      if (state[2][2] == 0) {
         butClicked = 9;
      }
   }

   private void b32ActionPerformed(ActionEvent evt) {
      if (state[2][1] == 0) {
         butClicked = 8;
      }
   }

   private void b31ActionPerformed(ActionEvent evt) {
      if (state[2][0] == 0) {
         butClicked = 7;
      }
   }

   private void b23ActionPerformed(ActionEvent evt) {
      if (state[1][2] == 0) {
         butClicked = 6;
      }
   }

   private void b13ActionPerformed(ActionEvent evt) {
      if (state[0][2] == 0) {
         butClicked = 3;
      }
   }

   private void b12ActionPerformed(ActionEvent evt) {
      if (state[0][1] == 0) {
         butClicked = 2;
      }
   }

   private void b22ActionPerformed(ActionEvent evt) {
      if (state[1][1] == 0) {
         butClicked = 5;
      }
   }

   private void b11ActionPerformed(ActionEvent evt) {
      if (state[0][0] == 0) {
         butClicked = 1;
      }
   }

   private void b21ActionPerformed(ActionEvent evt) {
      if (state[1][0] == 0) {
         butClicked = 4;
      }
   }

   public static void main(String[] args) {
      try {
         for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
               UIManager.setLookAndFeel(info.getClassName());
               break;
            }
         }
      } catch (ClassNotFoundException var5) {
         Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, var5);
      } catch (InstantiationException var6) {
         Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, var6);
      } catch (IllegalAccessException var7) {
         Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, var7);
      } catch (UnsupportedLookAndFeelException var8) {
         Logger.getLogger(TicTacToe.class.getName()).log(Level.SEVERE, null, var8);
      }

      TicTacToe t = new TicTacToe();
      t.setVisible(true);

      while(true) {
         t.start();
         t.gameInit();
      }
   }
}
