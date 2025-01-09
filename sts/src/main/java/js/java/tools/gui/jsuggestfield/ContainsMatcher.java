package js.java.tools.gui.jsuggestfield;

public class ContainsMatcher implements SuggestMatcher {
   @Override
   public boolean matches(String dataWord, String searchWord) {
      return dataWord.toLowerCase().contains(searchWord);
   }
}
