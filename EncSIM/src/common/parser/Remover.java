package common.parser;

import java.util.ArrayList;


import common.util.UtilHelper;

/**
 * remove meaningless words
 * @author maggie liu
 *
 */
public class Remover {

	private String LOWERCASE_THE = "the";
	private String LOWERCASE_A = "a";
	private String LOWERCASE_AN = "an";
	private String LOWERCASE_WHICH = "which";
	private String LOWERCASE_WHAT = "what";
	private String LOWERCASE_HOW = "how";
	private String LOWERCASE_WHERE = "where";
	private String LOWERCASE_WHEN = "when";
	private String LOWERCASE_TO = "to";
	private String LOWERCASE_THAT = "that";
	private String LOWERCASE_DIDN = "didn";
	private String LOWERCASE_IT = "it";
	private String LOWERCASE_IS = "is";
	private String LOWERCASE_HTTP = "http";
	private String LOWERCASE_HTTPS = "https";
	private String LOWERCASE_WWW = "www";
	private String LOWERCASE_EDU = "edu";
	private String LOWERCASE_COM = "com";
	private String LOWERCASE_MY = "my";
	private String LOWERCASE_ME = "me";
	private String LOWERCASE_YOU = "you";
	private String LOWERCASE_WE = "we";
	private String LOWERCASE_OUR = "our";
	private String LOWERCASE_US = "us";
	private String LOWERCASE_THEY = "they";
	private String LOWERCASE_OF = "of";
	private String LOWERCASE_YOUR = "your";
	private String LOWERCASE_AT = "at";
	private String LOWERCASE_DO = "do";
	private String LOWERCASE_DOES = "does";
	private String LOWERCASE_DID = "did";
	private String LOWERCASE_UP = "up";
	private String LOWERCASE_IF = "if";

	public ArrayList<String> removeMeaninglessWords(String[] wordsArray) {

		// add to list
		ArrayList<String> wordsList = new ArrayList<String>();
		wordsList.add(LOWERCASE_A);
		wordsList.add(LOWERCASE_AN);
		wordsList.add(LOWERCASE_HOW);
		wordsList.add(LOWERCASE_THAT);
		wordsList.add(LOWERCASE_THE);
		wordsList.add(LOWERCASE_TO);
		wordsList.add(LOWERCASE_WHAT);
		wordsList.add(LOWERCASE_WHEN);
		wordsList.add(LOWERCASE_WHERE);
		wordsList.add(LOWERCASE_WHICH);
		wordsList.add(LOWERCASE_IT);
		wordsList.add(LOWERCASE_DIDN);
		wordsList.add(LOWERCASE_IS);
		wordsList.add(LOWERCASE_HTTP);
		wordsList.add(LOWERCASE_HTTPS);
		wordsList.add(LOWERCASE_WWW);
		wordsList.add(LOWERCASE_EDU);
		wordsList.add(LOWERCASE_COM);
		wordsList.add(LOWERCASE_MY);
		wordsList.add(LOWERCASE_ME);
		wordsList.add(LOWERCASE_YOU);
		wordsList.add(LOWERCASE_WE);
		wordsList.add(LOWERCASE_OUR);
		wordsList.add(LOWERCASE_US);
		wordsList.add(LOWERCASE_THEY);
		wordsList.add(LOWERCASE_OF);
		wordsList.add(LOWERCASE_YOUR);
		wordsList.add(LOWERCASE_AT);
		wordsList.add(LOWERCASE_DO);
		wordsList.add(LOWERCASE_DOES);
		wordsList.add(LOWERCASE_DID);
		wordsList.add(LOWERCASE_UP);
		wordsList.add(LOWERCASE_IF);

		ArrayList<String> clearString = new ArrayList<String>();

		// remover

		for (int j = 0; j < wordsArray.length; j++) {
			String word = wordsArray[j];
			boolean isRemove = false;

			for (int i = 0; i < wordsList.size(); i++) {
				if (word.equalsIgnoreCase(wordsList.get(i))
						|| UtilHelper.isEmptyString(word) || word.length() == 1
						|| word.matches("-?\\d+(\\.\\d+)?")
						|| word.matches(".*\\d+.*")
						) {
					isRemove = true;
				}
			}

			if (!isRemove) {
				
				clearString.add(word);
				
			}

		}

		return clearString;

	}
}
