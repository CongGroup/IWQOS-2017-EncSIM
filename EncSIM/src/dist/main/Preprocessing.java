package dist.main;

import java.util.ArrayList;

import common.parser.ReadCsv;
import common.parser.Remover;
import common.parser.Tokenizer;
import common.parser.WriteFile;
import dist.index.TweetParser;

public class Preprocessing {

	public static void main(String[] args) {
		//String dir1 = "F:/LSH/dataset/testdata.manual.2009.06.14.csv";
		//String dir2 = "F:/LSH/dataset/millionsTweets.csv";
		String directory = args[0];
		String filePath = args[1];
		String outputTweetsName = "tweetsByte.txt";
		String outputDicName = "dictionary.txt";
		char delimiter = ',';
		ReadCsv reader = new ReadCsv();
		ArrayList<String> tweets = new ArrayList<String>();
		
		// read source csv file
		tweets = reader.readCsvFileTweetVector(directory, delimiter);

		// parse tweet and output 
		TweetParser tweetP = new TweetParser();
		tweetP.parseTweet(tweets, filePath, outputDicName, outputTweetsName);
		
		
		
		
		System.out.println("Done");

	}

}
