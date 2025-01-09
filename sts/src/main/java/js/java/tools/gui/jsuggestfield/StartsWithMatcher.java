package js.java.tools.gui.jsuggestfield;

public class StartsWithMatcher implements SuggestMatcher {
   @Override
   public boolean matches(String dataWord, String searchWord) {
      return dataWord.toLowerCase().startsWith(searchWord);
   }
}
