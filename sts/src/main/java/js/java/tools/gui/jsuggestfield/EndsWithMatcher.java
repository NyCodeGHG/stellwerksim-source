package js.java.tools.gui.jsuggestfield;

public class EndsWithMatcher implements SuggestMatcher {
   @Override
   public boolean matches(String dataWord, String searchWord) {
      return dataWord.toLowerCase().endsWith(searchWord);
   }
}
