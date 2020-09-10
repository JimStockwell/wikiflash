package fdshow;

/*
 * FDCard's fdshow direct dependencies
 *   Card
 */

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A Card that can read itself from and write itself to a FlashCards Deluxe
 * file.
 * 
 * Note: Handling of code points vs char.
 * 
 * Data is read in with Reader objects,
 * one 16 bit character at a time and written out the same way.
 * Code points that are broken up into two characters
 * and coded as surrogate characters
 * do not need any special handling since
 * 1) they won't be mistaken for regular characters as they get their own range,
 * 2) the pairs will be kept together.
 */
class FDCard extends Card {
	/**
	 * The names of the fields as they come in the flashcard data file.
	 */
	FieldNames fieldNames;

	/**
	 * Construct a Card from the specified reader and field names. The reader is
	 * positioned right before the fields of the Card to be constructed.
	 *
	 * @param r      the reader from which to read the Card fields
	 * @param fields the field names in the order they will be read
	 */
	FDCard(Reader r, FieldNames fields) {
		super(readCard(r, fields));
		fieldNames = fields;
	}

	/**
	 * Constructs a Card from the specified field content, card ID, and field names.
	 * The field names in <code>datByFields</code> and <code>fields</code> are
	 * expected to match.
	 * 
	 * @param dataByField The data to fill the card with
	 * @param id          The card ID number
	 * @param fields      the field names in the order they will be written
	 *                    eventually
	 */
	FDCard(Map<String, String> dataByField, Integer id, FieldNames fields) {
		super(dataByField, id);
		fieldNames = fields;
	}

	/**
	 * Construct a Card from the specified card
	 * 
	 * @param c      the card to use as a starting point
	 * @param fields the field names in the order they will be written eventually
	 */
	FDCard(Card c, FieldNames fields) {
		super(c);
		fieldNames = fields;
	}

	/**
	 * Returns a Card from the specified reader and field names. The reader is
	 * positioned right before the fields of the Card to be constructed.
	 *
	 * @param r      the reader from which to read the Card fields
	 * @param fields the names of the fields that will be read in
	 * @return the Card read
	 */
	private static Card readCard(Reader r, FieldNames fields) {
		final var cardData = new HashMap<String, String>();
		Integer id = null;
		final var fieldsData = fields.toArray();
		for (int i = 0; i < fields.length(); i++) {
			cardData.put(fieldsData[i], nextField(r));
		}
		final String notes = cardData.get("Notes");
		if (encodesId(notes)) {
			final String[] splits = notes.split(" : DO NOT MODIFY THIS LINE ", 2);
			cardData.put("Notes", splits[1]);
			id = Integer.valueOf(splits[0]);
		}
		return new Card(cardData, id);
	}

	/**
	 * Returns the next field.
	 *
	 * Between calls, the Reader points at the start of a field, just after any
	 * preceding delimiters.
	 *
	 * Control characters that are part of the persistent storage control, for
	 * example quotes at the end of a multiline field, are not included.
	 *
	 * FDCard's format: If there are " (lone quotes) or line separators in the field
	 * then start the field and end the field with a lone quote, replacing any lone
	 * quotes in the actual text with "" (repeated quotes). Line separators are a
	 * \r\n sequences.
	 *
	 * Internally (that is, not in a Flashcards Deluxe file), quotes are not
	 * repeated and a system appropriate line separator is used.
	 *
	 * @param r the source of the fields
	 * @return the data associated with the next field
	 */
	private static String nextField(Reader r) {
		try {
			int ch = r.read();
			boolean quotedText = ch == '"';
			if (quotedText) {
				return getRestOfQuotedString(r);
			} else {
				return getRegularString(ch, r);
			}
		} catch (java.io.IOException x) {
			throw new Error("Unexpected IOException");
		}
	}

	/**
	 * Reads in a regular (non-quoted) string and returns it. (The first character
	 * is already read and passed in separately.)
	 * 
	 * The reason for passing the first character separately is so that we don't
	 * require the Read to support the mark method. It's the caller's look ahead.
	 * 
	 * @param FirstCharacter The first character of the string
	 * @param r              The Reader to read the rest of the string from
	 * @return The first character plus the rest of the string read
	 * @throws java.io.IOException
	 */
	static private String getRegularString(int FirstCharacter, Reader r) throws java.io.IOException {
		int ch = FirstCharacter;
		var accum = new StringBuilder();
		while (ch != '\t' && ch != '\r' && ch != -1) {
			accum.append((char) ch);
			ch = r.read();
		}
		if (ch == '\r') {
			r.read(); // soak up \n
		}
		return accum.toString();
	}

	/**
	 * Reads in a quoted FD sting and returns it. Assumes the leading quote has
	 * already been read and discarded.
	 * 
	 * Converts from Flashcards Deluxe multi-line format to a more standard internal
	 * Java representation. That is, replaces "" with " and \r\n with
	 * System.lineSeparator().
	 * 
	 * @param r The Reader that is providing the quoted string
	 * @return The string data
	 * @throws java.io.IOException
	 */
	static private String getRestOfQuotedString(Reader r) throws java.io.IOException {
		var accum = new StringBuilder();
		//
		// at this point, r is at the character after the introductory quote,
		// poised to read the first non-delimiter character.
		//
		int ch = r.read();
		while (ch != '"' || (ch = r.read()) == '"') {
			// ch is either a regular character, or the second of two quotes.
			accum.append((char) ch);
			ch = r.read();
		}
		//
		// we have reached the end of the quoted field.
		// So now we either have a delimiter in ch,
		// or the first half of one in ch,
		// or -1 for "end of file" in ch.
		//
		assert ch == '\t' || ch == '\r' || ch == -1 : "Error in flashcard file";
		if (ch == '\r') {
			ch = r.read();
			assert ch == '\n' : "Return not followed by linefeed in flashcard file";
		}

		return accum.toString().replaceAll("\r\n", System.lineSeparator());
	}

	/**
	 * Returns true if the specified String encodes a card ID
	 *
	 * @param note the string to check for an ID
	 * @return true if the field encodes an ID
	 */
	static private boolean encodesId(String note) {
		return note != null && Pattern.compile("^-?[0-9]+ : DO NOT MODIFY THIS LINE ").matcher(note).find();
	}

	/**
	 * Given a string in just a simple normal format, returns a string suitable for
	 * writing into a Flashcards Deluxe data file.
	 *
	 * @param s the string to convert
	 * @return the specified string, converted into Flashcards Deluxe format.
	 */
	private static String canonicalField(String s) {
		class Fixer {

			private boolean needsFixing(String x) {
				return x.contains(System.lineSeparator()) || x.contains("\"");
			}

			private String fixQuotes(String x) {
				return x.replace("\"", "\"\"");
			}

			private String fixNewLines(String x) {
				return x.replace(System.lineSeparator(), "\r\n");
			}

			private String quoteIt(String x) {
				return "\"" + x + "\"";
			}

			public String fix(String x) {
				return needsFixing(x) ? quoteIt(fixQuotes(fixNewLines(x))) : x;
			}
		}
		return new Fixer().fix(s);
	}

	/**
	 * Returns the Card as a string suitable for writing into a Flashcards Deluxe
	 * data file.
	 * 
	 * @return the card in a Flashcards Deluxe suitable format.
	 */
	@Override
	public String toString() {
		final Map<String, String> content = getData();
		final var sb = new StringBuilder();

		// If we don't have notes, can't save IDs!
		final boolean hasNotes = Pattern.compile("(.*\\t)?Notes(\\t.*)?").matcher(fieldNames.toString()).find();

		for (int i = 0; i < fieldNames.length(); i++) {
			if (getId() != null && !hasNotes) {
				throw new UnsupportedOperationException("Can't save IDs in an FDFile with no 'Notes' fields."
						+ System.lineSeparator() + "Fields are: " + fieldNames.toString().replaceAll("\t", "\\\\t"));
			}

			final String[] fieldNamesData = fieldNames.toArray();
			if (fieldNamesData[i].equals("Notes") && getId() != null) {
				sb.append(canonicalField(
						getId().toString() + " : DO NOT MODIFY THIS LINE " + content.get(fieldNamesData[i])));
			} else {
				sb.append(canonicalField(content.get(fieldNamesData[i])));
			}
			if (i < fieldNames.length() - 1)
				sb.append("\t");
		}
		return sb.toString();
	}
}
