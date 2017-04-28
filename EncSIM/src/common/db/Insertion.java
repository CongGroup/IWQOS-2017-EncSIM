package common.db;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import redis.clients.jedis.Jedis;

/**
 * insert to db
 * @author maggie liu
 *
 */
public class Insertion {

	public void insertToDb(Jedis jedis, HashMap<String, String> tweetMap){
		
		Iterator<Entry<String, String>> it = tweetMap.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, String> entry = (Entry<String, String>)it.next();
			String key = entry.getKey();
			String value = entry.getValue();
			jedis.set(key, value);
			
		}
		
	}
}
