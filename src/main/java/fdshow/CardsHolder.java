package fdshow;

interface CardsHolder {
  int[] getUnmarkedCardsIndexes();
  void mark(int[] indexes);
  void addCardsFrom(CardsHolder source, int[] indexes);

  /**
   * Saves the cards to the provided storage.
   */
  void saveTo( java.io.OutputStream out );

  /**
   * Loads the cards from the provided storage.
   *
   * Uses BufferedReader to support mark and reset.
   */
  void loadFrom( java.io.BufferedReader r );
}
