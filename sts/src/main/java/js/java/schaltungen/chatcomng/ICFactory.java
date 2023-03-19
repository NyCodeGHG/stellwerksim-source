package js.java.schaltungen.chatcomng;

public interface ICFactory<T extends IrcChannel> {
   T newInstance(ChatNG var1, String var2);
}
