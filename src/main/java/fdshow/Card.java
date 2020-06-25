package fdshow;

/*
 * Card's fdshow direct dependencies
 *   None.
 */

/**
 * Represents, abstractly, a Card.
 * Expected to be fleshed out by subclasses that represent particular
 * storage formats of cards, both on the flash card side and the wiki side.
 */
interface Card
{
  String getField0();
  void setField0(String content);
}
