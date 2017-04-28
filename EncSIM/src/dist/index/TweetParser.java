package dist.index;

import java.util.ArrayList;
import common.parser.Remover;
import common.parser.Tokenizer;
import common.parser.WriteFile;

/**
 * Tweet parser
 * 
 * @author maggie liu
 * 
 */
public class TweetParser {

	/**
	 * Parse tweet to vector
	 * 
	 * @param tweets
	 * @param filePath
	 * @param dictionaryName
	 * @param tweetsName
	 */
	public void parseTweet(ArrayList<String> tweets, String filePath,
			String dictionaryName, String tweetsName) {
		Tokenizer spliter = new Tokenizer();
		Remover remover = new Remover();
		WordDictionaryBuilder dicBuilder = new WordDictionaryBuilder();
		WriteFile wf = new WriteFile();

		ArrayList<String> dictionary = new ArrayList<String>();

		ArrayList<ArrayList<String>> clearTweetsList = new ArrayList<ArrayList<String>>();

		boolean isLastTweet = false;
		for (int i = 0; i < tweets.size(); i++) {

			// 1.split tweet into words
			String[] words = spliter.splitTweetToWords(tweets.get(i));

			// 2. remove meaningless words
			ArrayList<String> clearWords = remover
					.removeMeaninglessWords(words);
			clearTweetsList.add(clearWords);

			for (int j = 0; j < clearWords.size(); j++) {

				if (i == tweets.size() - 1 && j == clearWords.size() - 1) {
					System.out.println("is last line");
					isLastTweet = true;
				}

			}

			// 3. add to dictionary
			dicBuilder.buildDictionary(filePath, dictionaryName, isLastTweet,
					clearWords, dictionary);

		}

		int dimension = dictionary.size();
		int divide = dimension / 8;
		int mod = dimension % 8;
		int size = 0;
		if (mod == 0) {
			size = divide;
		} else {
			size = divide + 1;
		}
		System.out.println("dimension:" + dimension);

		boolean isLastLine = false;
		for (int i = 0; i < clearTweetsList.size(); i++) {
			byte[] singleTweetByte = new byte[size];

			ArrayList<String> tweetTemp = clearTweetsList.get(i);

			// 4. compare with dictionary

			for (int j = 0; j < dimension; j++) {

				int indexB = j / 8;
				int indexBit = j % 8;
				// /byte b = singleTweetByte[indexB];

				String dicStr = dictionary.get(j);

				boolean isContain = compareWithDictionary(dicStr, tweetTemp);
				;

				if (isContain) {
					singleTweetByte[indexB] = (byte) (singleTweetByte[indexB] | (1 << indexBit));

				} else {
					singleTweetByte[indexB] = (byte) (singleTweetByte[indexB] & ~(1 << indexBit));
				}

			}

			// 5. write to file
			if (i == clearTweetsList.size() - 1) {
				isLastLine = true;
			}

			wf.writeByteFile(filePath, tweetsName, singleTweetByte, isLastLine);

		}

	}

	/**
	 * compare word with dictionary
	 * @param dicStr
	 * @param tweetTemp
	 * @return
	 */
	public boolean compareWithDictionary(String dicStr,
			ArrayList<String> tweetTemp) {
		boolean isContain = false;

		for (int t = 0; t < tweetTemp.size(); t++) {
			String tweetStr = tweetTemp.get(t);
			if (tweetStr.equalsIgnoreCase(dicStr)
					|| (dicStr.startsWith(tweetStr) && dicStr.substring(
							tweetStr.length(), dicStr.length() - 1).matches(
							"s|es|d|ed"))
					|| (tweetStr.startsWith(dicStr) && tweetStr.substring(
							dicStr.length(), tweetStr.length() - 1).matches(
							"s|es|d|ed"))) {
				isContain = true;
			}
		}

		return isContain;

	}

}
