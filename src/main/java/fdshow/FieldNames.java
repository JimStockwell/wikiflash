package fdshow;

import java.io.BufferedReader;

class FieldNames
{
  String[] data;

  FieldNames(BufferedReader r)
  {
    // readLine will deal with \n\r correctly,
    // regardless of local settings, per contract.
    try {
      data = r.readLine().split("\t");
    } catch (java.io.IOException x) {throw new Error("Unexpected IOException");}
  }

  public String toString()
  {
    var joiner = java.util.stream.Collectors.joining("\t","","\r\n");
    var result = java.util.Arrays.stream(data).collect(joiner);
    return result;
  }

  int length()
  {
    return data.length;
  }
}
