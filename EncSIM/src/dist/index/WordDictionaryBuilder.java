package dist.index;

import java.util.ArrayList;

import common.parser.WriteFile;

/**
 * Dictionary builder
 * @author maggie liu
 *
 */
public class WordDictionaryBuilder {

	public void buildDictionary(String filePath, String dictionaryName, boolean isLastTweet, ArrayList<String> tweets,
			ArrayList<String> dictionary) {
		WriteFile wf = new WriteFile();
		for (int i = 0; i < tweets.size(); i++) {
			String tweetStr = tweets.get(i);
			if(dictionary.size()!=0){
				if(dictionary.contains(tweetStr)){
					continue;
				}
				boolean isAdd = true;
				for (int j = 0; j < dictionary.size(); j++) {
					
					String dicStr = dictionary.get(j);
				
					if (tweetStr.equalsIgnoreCase(dicStr)
							|| (dicStr.startsWith(tweetStr) && dicStr
									.substring(tweetStr.length(),
											dicStr.length() - 1)
									.matches("s|es|d|ed"))
							|| (tweetStr.startsWith(dicStr) && tweetStr
									.substring(dicStr.length(),
											tweetStr.length() - 1)
									.matches("s|es|d|ed"))) {
						isAdd = false;
						break;
						
					}

				}
				
				if(isAdd){
					dictionary.add(tweetStr);
					wf.writeDictionary(filePath, dictionaryName, tweetStr, isLastTweet);
				}
			}else{
				dictionary.add(tweetStr);
				wf.writeDictionary(filePath, dictionaryName, tweetStr, isLastTweet);
			}
			

		}

	}

}
