package fdshow;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.util.HashMap;

public class CardTest
{
  @Test
  void should_BeEqual_When_IsSame() {
    final var hm = new HashMap<String,String>();
    hm.put("Text 1","Hello _____");
    hm.put("Text 2","World");
    final Card c1 = new Card(hm,1);
    final Card c2 = new Card(hm,1);
    assertTrue(c1.equals(c2));
  }

  @Test
  void should_BeUnequal_When_IsDifferent() {
    final var hm = new HashMap<String,String>();
    hm.put("Text 1","Hello _____");
    hm.put("Text 2","World");
    final Card c1 = new Card(hm,1);
    final Card c2 = new Card(hm,2);
    assertFalse(c1.equals(c2));
  }

  @Test
  void should_hashSame_When_IsSame() {
    final var hm = new HashMap<String,String>();
    hm.put("Text 1","Hello _____");
    hm.put("Text 2","World");
    final Card c1 = new Card(hm,1);
    final Card c2 = new Card(hm,1);
    assertEquals(c1.hashCode(),c2.hashCode());
  }

  // Now this isn't strictly quite right.
  // They COULD hash the same,
  // but it's unlikely with a decent hashing method.
  @Test
  void should_hashDifferent_When_IsDifferent() {
    final var hm = new HashMap<String,String>();
    hm.put("Text 1","Hello _____");
    hm.put("Text 2","World");
    final Card c1 = new Card(hm,1);
    final Card c2 = new Card(hm,2);
    assertNotEquals(c1.hashCode(),c2.hashCode());
  }
  
  @Test
  void should_notEqualANull() {
    final var hm = new HashMap<String,String>();
    hm.put("Text 1","Hello _____");
    hm.put("Text 2","World");
    final Card c1 = new Card(hm,1);
    assertFalse(c1.equals(null));
  }
      
  @Test
  void should_notBeEqual_when_differentClasses() {
    final SimpleCard sc = new SimpleCard("A:B",3);
    assertNotEquals(new Card(sc),sc);
  }
  
  @Test
  void should_BeEqualAsCards_when_madeIntoSameCardClass() {
    final SimpleCard sc = new SimpleCard("A:B",3);
    assertEquals(new Card(sc),new Card(sc));
  }
}


