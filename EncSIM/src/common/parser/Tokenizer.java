package common.parser;


import common.util.UtilHelper;

/**
 * Split tweets to words
 * @author maggie liu
 *
 */
public class Tokenizer {

	public String[] splitTweetToWords(String singleTweet){
	
		String[] wordsArray = null;
		if(!UtilHelper.isEmptyString(singleTweet)){
			
			//wordsArray = singleTweet.trim().split("\\s+");
			
			wordsArray = singleTweet.trim().split("[\\p{Punct}\\s]+");
			
		}else{
			System.out.println("Empty");
		}
		
		
		
		return wordsArray;
	}
}
