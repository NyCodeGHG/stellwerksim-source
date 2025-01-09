package js.java.tools.ttt;

import java.util.ArrayList;

public class Node {
   ArrayList<Node> sub_nodes = new ArrayList();
   String subNodes = "";
   String id = "";
   int pref = 0;
   int n = 0;
   int lNum;
   Computer comp;
   int nodeType = 0;

   public Node(String i, int l, Computer c) {
      this.id = i;
      this.lNum = l;
      this.comp = c;
   }

   public void setAsState() {
      for (int i = 0; i < this.id.length(); i++) {
         int val = this.id.charAt(i) - '0';
         int t = i / 3;
         TicTacToe.state[t][i - t * 3] = val;
      }
   }

   public void playNext1() {
      ArrayList<Node> temp = new ArrayList();
      long max = (long)((Node)this.sub_nodes.get(0)).pref;

      for (int i = 0; i < this.sub_nodes.size(); i++) {
         if ((long)((Node)this.sub_nodes.get(i)).pref > max) {
            temp.clear();
            temp.add(this.sub_nodes.get(i));
            max = (long)((Node)this.sub_nodes.get(i)).pref;
         } else if ((long)((Node)this.sub_nodes.get(i)).pref == max) {
            temp.add(this.sub_nodes.get(i));
         }
      }

      int choice = (int)(Math.random() * (double)temp.size());
      ((Node)temp.get(choice)).n++;
      ((Node)temp.get(choice)).setAsState();
   }

   public void playNext2() {
      ArrayList<Node> temp = new ArrayList();
      long min = (long)((Node)this.sub_nodes.get(0)).pref;

      for (int i = 0; i < this.sub_nodes.size(); i++) {
         if ((long)((Node)this.sub_nodes.get(i)).pref < min) {
            temp.clear();
            temp.add(this.sub_nodes.get(i));
            min = (long)((Node)this.sub_nodes.get(i)).pref;
         } else if ((long)((Node)this.sub_nodes.get(i)).pref == min) {
            temp.add(this.sub_nodes.get(i));
         }
      }

      int choice = (int)(Math.random() * (double)temp.size());
      ((Node)temp.get(choice)).n++;
      ((Node)temp.get(choice)).setAsState();
   }

   public void playNextn() {
      new ArrayList();
      int choice = 0;
      long min = (long)((Node)this.sub_nodes.get(0)).n;

      for (int i = 0; i < this.sub_nodes.size(); i++) {
         if ((long)((Node)this.sub_nodes.get(i)).n < min) {
            min = (long)((Node)this.sub_nodes.get(i)).n;
            choice = i;
         }
      }

      ((Node)this.sub_nodes.get(choice)).n++;
      ((Node)this.sub_nodes.get(choice)).setAsState();
   }

   public void extractNodes() {
      if (this.lNum != 9) {
         int l = this.subNodes.length();
         String w = "";

         for (int i = 0; i < l; i++) {
            char ch = this.subNodes.charAt(i);
            if (ch != ',') {
               w = w + ch;
            } else {
               this.sub_nodes.add(((Layer)this.comp.layers.get(this.lNum)).searchById(w));
               w = "";
            }
         }
      }
   }
}
