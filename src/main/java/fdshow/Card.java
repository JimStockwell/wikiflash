package fdshow;

import java.util.Map;
import java.util.HashMap;

//
// Card's fdshow direct dependencies
//   None.
//

/**
 * Contains the flashcard fields that are common both to the wiki
 * and to flashcard programs.
 * So, for example, it does not include scheduling data,
 * or if it does, it is for reference only.
 *
 * Intentionally includes no method for reading or writing the Card.
 */
class Card
{
  private Map<String,String> dataByField;
  private Integer id;

  Card() {dataByField = new HashMap<String,String>();}

  String getField0() {return dataByField.get("Text 1");}

  void setField0(String content) {dataByField.put("Text 1",content);}

  /**
   * Returns a new Map from field name to field contents.
   */ 
  Map<String,String> getData() {return dataByField;}

  /**
   * Sets the field names and contents to those specified in the provided Map.
   */
  void setData(Map<String,String> m) {
    dataByField = new HashMap<String,String>();
    dataByField.putAll(m);
  }

  void setId(Integer id)  { this.id = id; } // null to reset the ID to "no ID"
  Integer getId()         { return id; }
}
