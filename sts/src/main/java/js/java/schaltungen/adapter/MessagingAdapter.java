package js.java.schaltungen.adapter;

public interface MessagingAdapter {
   int M_MESSAGE = 0;
   int M_ERROR = 1;
   int M_WARNING = 2;
   int M_INFO = 3;
   int M_EXCEPTION = 4;
   int M_DIALOG = 5;

   void showStatus(String var1, int var2);

   void showStatus(String var1);

   void setProgress(int var1);
}
