package dist.main;

import java.util.Random;

import redis.clients.jedis.Jedis;
import common.db.ConnectRedis;
import common.parser.ReadTxt;
import common.util.Config;
import common.util.Constants;

public class PlaintextSetup {
	
	public static void main(String[] args){
		String filePath = "";
		String dictionaryName = "";
		String tweetFileName = "";
		String query = "";
		if (args.length >= 1) {
			
			query = args[0];
		} else {
			
			query = Config.getSetting(Constants.CONFIG_QUERY);
		}
		filePath = Config.getSetting(Constants.CONFIG_FILE_PATH);
		tweetFileName = Config.getSetting(Constants.CONFIG_TWEET_FILE_NAME);
		dictionaryName = Config
				.getSetting(Constants.CONFIG_DICTIONARY_NAME);
		int m = Config.getSettingInt(Constants.CONFIG_LSH_M);
		int k = Config.getSettingInt(Constants.CONFIG_LSH_K);
		int d = Config.getSettingInt(Constants.CONFIG_LSH_D);
		String ip = Config.getSetting(Constants.CONFIG_DB_IP);

		int seed = Config.getSettingInt(Constants.CONFIG_LSH_SEED);
		Random ranSeed = new Random(seed);
		String clientKeyStr = Config
				.getSetting(Constants.CONFIG_LSH_CLIENT_KEY);
		
		
		ConnectRedis connR = new ConnectRedis();
		Jedis jedis = connR.connectDb(ip);
		
		ReadTxt rt = new ReadTxt();
		rt.readToDBPlaintext(filePath, tweetFileName, m, k, d, ranSeed, jedis);
		
		System.out.println("Finished");
		
		
	}

}
