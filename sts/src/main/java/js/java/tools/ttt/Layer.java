package js.java.tools.ttt;

import java.util.ArrayList;

public class Layer {
   ArrayList<Node> nodes = new ArrayList();
   int layerNum = 0;

   public Layer(int Num) {
      this.layerNum = Num;
   }

   public void refreshLayer() {
      for (int i = 0; i < this.nodes.size(); i++) {
         Node temp = (Node)this.nodes.get(i);
         if (temp.nodeType == 0) {
            temp.pref = 0;

            for (int j = 0; j < temp.sub_nodes.size(); j++) {
               temp.pref = temp.pref + ((Node)temp.sub_nodes.get(j)).pref / 2;
            }
         }
      }
   }

   public Node searchByState(int[][] state) {
      String temp = "" + state[0][0] + state[0][1] + state[0][2] + state[1][0] + state[1][1] + state[1][2] + state[2][0] + state[2][1] + state[2][2];
      return this.searchById(temp);
   }

   public Node searchById(String id) {
      Node ret = null;

      for (int i = 0; i < this.nodes.size(); i++) {
         if (((Node)this.nodes.get(i)).id.equals(id)) {
            ret = (Node)this.nodes.get(i);
            break;
         }
      }

      return ret;
   }
}
