package dist.main;

import java.util.Random;

import redis.clients.jedis.Jedis;
import common.db.ConnectRedis;
import common.parser.ReadTxt;
import common.util.Config;
import common.util.Constants;
import common.util.Converter;

public class IndexSetup {
	
	public static void main(String[] args){
		String filePath = "";
		String tweetFileName = "";
		if(args.length >= 2 ){
			 filePath = args[0];
			 tweetFileName = args[1];
		}else{
			/* filePath = "G:/LSH/dataset/100Data";
			 tweetFileName = "tweetsByte.txt";*/
			filePath = Config.getSetting(Constants.CONFIG_FILE_PATH);
			tweetFileName = Config.getSetting(Constants.CONFIG_TWEET_FILE_NAME);
			
		}
		/*int m = 40;
		int k = 16;
		int d = 70098;
		String ip = "localhost";
		
		int seed = 2017;
		String clientKeyStr = "distSSE";*/
		int m = Config.getSettingInt(Constants.CONFIG_LSH_M);
		int k = Config.getSettingInt(Constants.CONFIG_LSH_K);
		int d = Config.getSettingInt(Constants.CONFIG_LSH_D);
		String ip = Config.getSetting(Constants.CONFIG_DB_IP);

		int seed = Config.getSettingInt(Constants.CONFIG_LSH_SEED);
		Random ranSeed = new Random(seed);
		String clientKeyStr = Config
				.getSetting(Constants.CONFIG_LSH_CLIENT_KEY);
		
		byte[] clientKey = Converter.convertString2BitsStore(clientKeyStr);
		
		
		ConnectRedis connR = new ConnectRedis();
		Jedis jedis = connR.connectDb(ip);
		
		ReadTxt rt = new ReadTxt();
		rt.readToDBXOR(filePath, tweetFileName, m, k, d, ranSeed, clientKey, jedis);
		
		jedis.close();
		
		System.out.println("Finished");
		
		
	}

}
