package js.java.schaltungen.chatcomng;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(
   name = "cul"
)
public class CurrentUserList implements IncludeSender {
   @XmlElement(
      name = "u"
   )
   public List<CurrentUserListUserEntry> users = new LinkedList();
   @XmlElement(
      name = "f"
   )
   @XmlJavaTypeAdapter(BooleanAdapter.class)
   public Boolean finalList = false;
   @XmlElement(
      name = "n"
   )
   public int listNum = 0;
   @XmlElement(
      name = "m"
   )
   @XmlJavaTypeAdapter(BooleanAdapter.class)
   public Boolean masterMode = false;
   @XmlTransient
   private String sender = null;

   public CurrentUserList() {
      super();
   }

   public CurrentUserList(int listNum) {
      super();
      this.listNum = listNum;
   }

   @Override
   public void setSender(String nick) {
      this.sender = nick;
   }

   @XmlTransient
   public String getSender() {
      return this.sender;
   }
}
