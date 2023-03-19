package js.java.isolate.sim.autoMsg;

import de.deltaga.serial.Base64;
import de.deltaga.serial.XmlMarshal;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModel;
import js.java.isolate.sim.sim.stellwerksim_main;
import js.java.isolate.sim.zug.zug;
import js.java.schaltungen.chatcomng.ChannelsNameParser;

public class control {
   private final stellwerksim_main my_main;
   private final gleisbildModel my_gleisbild;
   LinkedList<msgItem> msgitems = new LinkedList();
   private String[] nach = null;
   private ChannelsNameParser.ChannelName[] nachbarn = null;
   private final XmlMarshal xmlConverter = new XmlMarshal(new Class[]{StoredItem.class, MsgAidStore.class});

   public control(stellwerksim_main m, gleisbildModel p) {
      super();
      this.my_main = m;
      this.my_gleisbild = p;
   }

   gleis findElement(String sig) {
      Iterator<gleis> it = this.my_gleisbild.findIteratorWithElementName(sig, gleis.ELEMENT_SIGNAL);
      return it.hasNext() ? (gleis)it.next() : null;
   }

   gleis findElement(int sigenr) {
      Iterator<gleis> it = this.my_gleisbild.findIterator(sigenr, gleis.ALLE_SIGNALE);
      return it.hasNext() ? (gleis)it.next() : null;
   }

   String[] getNach() {
      if (this.nach == null) {
         Iterator<gleis> it = this.my_gleisbild.findIterator(gleis.ELEMENT_AUSFAHRT);
         TreeSet<String> s = new TreeSet();

         while(it.hasNext()) {
            gleis g = (gleis)it.next();
            s.add(g.getSWWert_special().trim());
         }

         this.nach = new String[s.size()];
         s.toArray(this.nach);
      }

      return this.nach;
   }

   ChannelsNameParser.ChannelName[] getNachbarn() {
      if (this.nachbarn == null) {
         Set<ChannelsNameParser.ChannelName> s = this.my_main.getChat().channelsSet();
         this.nachbarn = new ChannelsNameParser.ChannelName[s.size()];
         s.toArray(this.nachbarn);
      }

      return this.nachbarn;
   }

   public void zugEnterFs(zug z, gleis gl) {
      if (gl.getElement() == gleis.ELEMENT_SIGNAL) {
         for(msgItem mi : this.msgitems) {
            try {
               if (mi.signal.sameGleis(gl) && (mi.ziel == null || mi.ziel.equalsIgnoreCase(z.getNachCT().toString()))) {
                  this.my_main.autoMsg(mi.zielnachbar, z);
               }
            } catch (NullPointerException var6) {
            }
         }
      }
   }

   public boolean save(File file) {
      return this.save(this.msgitems, file);
   }

   boolean save(List<msgItem> msgitems, File file) {
      try {
         MsgAidStore store = new MsgAidStore();
         store.aid = this.my_gleisbild.getAid();

         for(msgItem mi : msgitems) {
            store.items.add(new StoredItem(mi));
         }

         store.checksum = store.calcChecksum();
         String msg = this.xmlConverter.serialize(store);
         if (!msg.isEmpty()) {
            String b64 = Base64.toBase64(msg, 10);
            if (!b64.isEmpty()) {
               Files.write(file.toPath(), b64.getBytes(StandardCharsets.UTF_8), new OpenOption[0]);
               return true;
            }
         }
      } catch (Exception var6) {
         Logger.getLogger(control.class.getName()).log(Level.SEVERE, null, var6);
      }

      return false;
   }

   public boolean load(File file) {
      try {
         byte[] b64 = Files.readAllBytes(file.toPath());
         Object o = this.xmlConverter.deserialize(Base64.fromBase64(new String(b64, StandardCharsets.UTF_8)));
         if (o != null && o instanceof MsgAidStore) {
            MsgAidStore store = (MsgAidStore)o;
            if (store.aid != this.my_gleisbild.getAid()) {
               return false;
            }

            LinkedList<msgItem> nmsgitems = new LinkedList();
            Iterator var6 = store.items.iterator();

            while(true) {
               StoredItem si;
               gleis signal;
               while(true) {
                  if (!var6.hasNext()) {
                     this.msgitems.clear();
                     this.msgitems = nmsgitems;
                     return true;
                  }

                  si = (StoredItem)var6.next();
                  signal = this.findElement(si.signal);
                  if (signal != null) {
                     if (si.zielnachbar == null || si.zielnachbar.isEmpty()) {
                        break;
                     }

                     boolean found = false;
                     ChannelsNameParser.ChannelName[] n = this.getNachbarn();

                     for(ChannelsNameParser.ChannelName c : n) {
                        if (c.title.equalsIgnoreCase(si.zielnachbar)) {
                           found = true;
                           break;
                        }
                     }

                     if (found) {
                        break;
                     }
                  }
               }

               msgItem mi = new msgItem();
               mi.signal = signal;
               mi.ziel = si.ziel;
               mi.zielnachbar = si.zielnachbar;
               nmsgitems.add(mi);
            }
         }
      } catch (Exception var15) {
         Logger.getLogger(control.class.getName()).log(Level.SEVERE, null, var15);
      }

      return false;
   }
}
