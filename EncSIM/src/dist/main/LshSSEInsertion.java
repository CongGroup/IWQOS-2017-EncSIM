package dist.main;

import java.util.Random;

import redis.clients.jedis.Jedis;
import common.db.ConnectRedis;
import common.parser.ReadTxt;
import common.util.Config;
import common.util.Constants;
import common.util.Converter;

public class LshSSEInsertion {

	public static void main(String[] args) {
		String filePath = "";
		String tweetFileName = "";
		String insertFileName = "";
		String dictionaryName = "";
		if (args.length >= 1) {
			filePath = args[0];
			tweetFileName = args[1];
		} else {
			// filePath = "G:/LSH/dataset/100Data";
			// tweetFileName = "tweetsByte.txt";
			filePath = Config.getSetting(Constants.CONFIG_FILE_PATH);
			tweetFileName = Config.getSetting(Constants.CONFIG_TWEET_FILE_NAME);
		}
		/*
		 * int m = 40; int k = 16; int d = 70098;
		 */
		insertFileName = Config.getSetting(Constants.CONFIG_INSERT_FILE_NAME);
		dictionaryName = Config.getSetting(Constants.CONFIG_DICTIONARY_NAME);
		
		int m = Config.getSettingInt(Constants.CONFIG_LSH_M);
		int k = Config.getSettingInt(Constants.CONFIG_LSH_K);
		int d = Config.getSettingInt(Constants.CONFIG_LSH_D);
		String ip = Config.getSetting(Constants.CONFIG_DB_IP);

		int seed = Config.getSettingInt(Constants.CONFIG_LSH_SEED);
		Random ranSeed = new Random(seed);
		String clientKeyStr = Config
				.getSetting(Constants.CONFIG_LSH_CLIENT_KEY);

		int l = m * (m - 1) / 2;

		int k2 = k * 2;

		byte[] clientKey = Converter.convertString2BitsStore(clientKeyStr);

		ConnectRedis connR = new ConnectRedis();
		Jedis jedis = connR.connectDb(ip);

		ReadTxt rt = new ReadTxt();
		rt.readParseInsertLshSSE(filePath, insertFileName, dictionaryName, l, k2, d, ranSeed, clientKey,
				jedis);
		
		jedis.close();

		System.out.println("Finished");

	}

}
