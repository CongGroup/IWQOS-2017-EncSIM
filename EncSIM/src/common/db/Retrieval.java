package common.db;

import java.util.ArrayList;
import java.util.HashMap;

import common.lsh.GenAllPairsLsh;
import common.parser.ReadCsv;
import common.util.Config;
import common.util.Constants;
import common.util.Converter;

import redis.clients.jedis.Jedis;

/**
 * retrieve index
 * @author maggie liu
 *27/4/2017
 */
public class Retrieval {

	/**
	 * Retrieve index from db - XOR way
	 * @param jedis
	 * @param keyList
	 * @return
	 */
	public HashMap<String, String> retrieveFromDbXOR(Jedis jedis,
			ArrayList<byte[]> keyList) {
		HashMap<String, String> resMap = new HashMap<String, String>();

		for (int i = 0; i < keyList.size(); i++) {
			byte[] perLshRelatedKeyByte = keyList.get(i);
			int counter = 1;
			byte[] counterB = Converter.int2BitwiseArray(counter);
		
			String labelStr = GenAllPairsLsh.getLabel(counterB,
					perLshRelatedKeyByte);

		
			while (jedis.get(labelStr) != null) {
				
				
				String value = jedis.get(labelStr);
				resMap.put(labelStr, value);

				counter++;
				
				counterB = Converter.int2BitwiseArray(counter);
				labelStr = GenAllPairsLsh.getLabel(counterB,
						perLshRelatedKeyByte);
				
				if(resMap.keySet().size() == Config.getSettingInt(Constants.CONFIG_CANDIDATE)){
					return resMap;
				}
			}

		}

		return resMap;
	}

	
	/**
	 * Retrieve index from db- plaintext
	 * @param jedis
	 * @param keyList
	 * @return
	 */
	public HashMap<String, String> retrieveFromDbPlaintext(Jedis jedis,
			ArrayList<String> keyList) {
		HashMap<String, String> resMap = new HashMap<String, String>();

		for (int i = 0; i < keyList.size(); i++) {
			String perLshRelatedKeyByte = keyList.get(i);
			int counter = 1;

			String labelStr = perLshRelatedKeyByte + Converter.int2BinaryString(counter);
			
			while (jedis.get(labelStr) != null) {
				
				String value = jedis.get(labelStr);
				resMap.put(labelStr, value);

				counter++;
				labelStr = perLshRelatedKeyByte + Converter.int2BinaryString(counter);
				
				if(resMap.keySet().size() == Config.getSettingInt(Constants.CONFIG_CANDIDATE)){
					return resMap;
				}
				
				
				
			}

		}

		return resMap;
	}

	/**
	 * Retrieve index from db - general
	 * @param jedis
	 * @param keyList
	 * @return
	 */
	public HashMap<String, String> retrieveFromDbGeneral(Jedis jedis,
			ArrayList<byte[]> keyList) {
		HashMap<String, String> resMap = new HashMap<String, String>();

		
		for (int i = 0; i < keyList.size(); i++) {
			
			byte[] perLshRelatedKeyByte = keyList.get(i);
			int counter = 1;
			byte[] counterB = Converter.int2BitwiseArray(counter);
			

			String labelStr ;
			while (jedis.get(labelStr = GenAllPairsLsh.getLabel(counterB,
					perLshRelatedKeyByte) ) != null) {
				
			
				
				String value = jedis.get(labelStr);
				resMap.put(labelStr, value);

				counter++;
				counterB = Converter.int2BitwiseArray(counter);
				
				if(resMap.keySet().size() == Config.getSettingInt(Constants.CONFIG_CANDIDATE)){
					return resMap;
				}
				
				
				
			}

		}

		return resMap;
	}
	
	/**
	 * Retrieve index from CSV file - general
	 * 
	 * @param filePath
	 * @param keyList
	 * @return
	 */
	public HashMap<String, String> retrieveFromCSVFileGeneral(String filePath,
			ArrayList<byte[]> keyList) {
		HashMap<String, String> resMap = new HashMap<String, String>();

		ReadCsv reader = new ReadCsv();
		HashMap<String, String> indexMap = new HashMap<String,String>();
		indexMap = reader.readCsvFileIndex(filePath);
		
		for (int i = 0; i < keyList.size(); i++) {
			byte[] perLshRelatedKeyByte = keyList.get(i);
			int counter = 1;
			byte[] counterB = Converter.int2BitwiseArray(counter);
			
			String labelStr = GenAllPairsLsh.getLabel(counterB,
					perLshRelatedKeyByte);

		
			while (indexMap.containsKey(labelStr)) {
				
				String value =indexMap.get(labelStr);
				resMap.put(labelStr, value);

				counter++;
				counterB = Converter.int2BitwiseArray(counter);
			
				labelStr = GenAllPairsLsh.getLabel(counterB,
						perLshRelatedKeyByte);
			}

		}
		
		indexMap.clear();
		System.gc();

		return resMap;
	}

}
