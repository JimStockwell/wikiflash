package fdshow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.util.HashMap;

public class CardTest
{
  @Test
  void should_BeEqual_When_IsSame() {
    var hm = new HashMap<String,String>();
    hm.put("Text 1","Hello _____");
    hm.put("Text 2","World");
    final Card c1 = new Card(hm,1);
    final Card c2 = new Card(hm,1);
    assertTrue(c1.equals(c2));
  }

  @Test
  void should_BeUnequal_When_IsDifferent() {
    var hm = new HashMap<String,String>();
    hm.put("Text 1","Hello _____");
    hm.put("Text 2","World");
    final Card c1 = new Card(hm,1);
    final Card c2 = new Card(hm,2);
    assertFalse(c1.equals(c2));
  }

  @Test
  void should_hashSame_When_IsSame() {
    var hm = new HashMap<String,String>();
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
    var hm = new HashMap<String,String>();
    hm.put("Text 1","Hello _____");
    hm.put("Text 2","World");
    final Card c1 = new Card(hm,1);
    final Card c2 = new Card(hm,2);
    assertNotEquals(c1.hashCode(),c2.hashCode());
  }
  
  @Test
  void should_notEqualANull() {
    var hm = new HashMap<String,String>();
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
  void should_BeEqualAsCards_although_differentSubclasses() {
    final SimpleCard sc = new SimpleCard("A:B",3);
    assertTrue((new Card(sc)).equalsAsCard(sc));
  }
}

