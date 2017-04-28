package dist.main;

import java.util.Random;

import redis.clients.jedis.Jedis;
import common.db.ConnectRedis;
import common.parser.ReadTxt;
import common.util.Converter;

public class GeneralLshSetup {
	
	public static void main(String[] args){
		String filePath = "";
		String tweetFileName = "";
		if(args.length >= 2 ){
			 filePath = args[0];
			 tweetFileName = args[1];
		}else{
			 filePath = "F:/LSH/dataset";
			 tweetFileName = "tweetsByte.txt";
		}
		int m = 10;
		int k = 10;
		int d = 2077;
		String ip = "localhost";
		
		
		int seed = 2017;
		Random ranSeed = new Random(seed);
		String clientKeyStr = "distSSE";
		byte[] clientKey = Converter.convertString2BitsStore(clientKeyStr);
		
		
		ConnectRedis connR = new ConnectRedis();
		Jedis jedis = connR.connectDb(ip);
		
		ReadTxt rt = new ReadTxt();
		rt.readToDBGeneral(filePath, tweetFileName, m, k, d, ranSeed, clientKey, jedis);
		
		System.out.println("Finished");
		
		
	}

}
