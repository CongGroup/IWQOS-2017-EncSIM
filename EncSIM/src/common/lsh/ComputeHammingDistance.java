package common.lsh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import common.util.Converter;
import common.util.StringXORer;
import common.util.UtilHelper;

/**
 * compute hamming distance
 * 
 * @author maggie liu
 * 
 */
public class ComputeHammingDistance {

	/**
	 * compute hamming distance
	 * 
	 * @param query
	 * @param result
	 * @return
	 */
	public static int hammingDistance(byte[] query, byte[] result) {
		int distance = 0;

		byte[] xorByte = StringXORer.xorWithKey(query, result);
		byte[] xorBitwiseArray = Converter.ToBinaryByte(xorByte);

		for (int i = 0; i < xorBitwiseArray.length; i++) {
			if (xorBitwiseArray[i] == 1) {
				distance++;
			}
		}

		return distance;
	}

	/**
	 * sort result by hamming distance
	 * 
	 * @param query
	 * @param resultMap
	 * @return
	 */
	public static TreeMap<Integer, ArrayList<String>> sortResHammingDistance(
			byte[] query, HashMap<Integer, byte[]> resultMap) {
		TreeMap<Integer, ArrayList<String>> distanceMap = new TreeMap<Integer, ArrayList<String>>();
		Iterator<Entry<Integer, byte[]>> it = resultMap.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<Integer, byte[]> entry = (Entry<Integer, byte[]>) it
					.next();

			int dataId = entry.getKey();
			byte[] value = entry.getValue();

			int distance = hammingDistance(query, value);
			sortDistance(distance, dataId, distanceMap);

		}

		return distanceMap;
	}

	/**
	 * sort results within radius
	 * 
	 * @param query
	 * @param resultMap
	 * @param r
	 * @return
	 */
	public static TreeMap<Integer, ArrayList<String>> sortResHammingDistanceWithinR(
			byte[] query, HashMap<Integer, byte[]> resultMap, int r) {
		TreeMap<Integer, ArrayList<String>> distanceMap = new TreeMap<Integer, ArrayList<String>>();
		Iterator<Entry<Integer, byte[]>> it = resultMap.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<Integer, byte[]> entry = (Entry<Integer, byte[]>) it
					.next();

			int dataId = entry.getKey();
			byte[] value = entry.getValue();

			int distance = hammingDistance(query, value);

			if (distance <= r) {
				sortDistance(distance, dataId, distanceMap);
			}

		}

		return distanceMap;
	}

	/**
	 * sort distance
	 * 
	 * @param distance
	 * @param id
	 * @param distanceMap
	 */
	public static void sortDistance(int distance, int id,
			TreeMap<Integer, ArrayList<String>> distanceMap) {
		ArrayList<String> valueArray = new ArrayList<String>();

		if (distanceMap.containsKey(distance)) {
			valueArray = distanceMap.get(distance);
			valueArray.add(Integer.toString(id));
			distanceMap.put(distance, valueArray);

		} else {

			valueArray.add(Integer.toString(id));
			distanceMap.put(distance, valueArray);
		}

	}

	/**
	 * compute recall
	 * 
	 * @param allTweetsDistanceMapWithinR
	 * @param idList
	 * @return
	 */
	public static double recall(
			TreeMap<Integer, ArrayList<String>> allTweetsDistanceMapWithinR,
			ArrayList<String> idList) {
		TreeMap<Integer, ArrayList<String>> resDistanceMap = new TreeMap<Integer, ArrayList<String>>();
		double recall = (double) 0;
		int lshNum = 0;
		int realNum = 0;

		Iterator<Entry<Integer, ArrayList<String>>> it = allTweetsDistanceMapWithinR.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, ArrayList<String>> entry = (Entry<Integer, ArrayList<String>>) it.next();
			int distance = entry.getKey();
			ArrayList<String> allIdList = entry.getValue();

			ArrayList<String> resIdList = new ArrayList<String>();
			for (int i = 0; i < idList.size(); i++) {
				String indexRes = idList.get(i);
				// within r
				if (allIdList.contains(indexRes)) {
					// add to list
					resIdList.add(indexRes);
					// System.out.println("id:" + indexRes + " within radius.");
				}
			}

			// put into map
			if (!UtilHelper.isEmpty(resIdList)) {
				resDistanceMap.put(distance, resIdList);
				lshNum += resIdList.size();
			}
			realNum += allIdList.size();

		}

		// PrintTool.printMap(resDistanceMap);
		recall = (double) lshNum / realNum;
		System.out.println("lshNum:" + lshNum + " realNum" + realNum);
		System.out.println("Recall:" + recall);

		return recall;
	}

	/**
	 * false positive filter
	 * 
	 * @param allTweetsDistanceMapWithinR
	 * @param idList
	 * @return
	 */
	public static TreeMap<Integer, ArrayList<String>> falsePositiveFilterWithinR(
			TreeMap<Integer, ArrayList<String>> allTweetsDistanceMapWithinR,
			ArrayList<String> idList) {
		TreeMap<Integer, ArrayList<String>> resDistanceMap = new TreeMap<Integer, ArrayList<String>>();
		double recall = (double) 0;
		int lshNum = 0;
		int realNum = 0;

		Iterator<Entry<Integer, ArrayList<String>>> it = allTweetsDistanceMapWithinR.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, ArrayList<String>> entry = (Entry<Integer, ArrayList<String>>) it.next();
			int distance = entry.getKey();
			ArrayList<String> allIdList = entry.getValue();

			ArrayList<String> resIdList = new ArrayList<String>();
			for (int i = 0; i < idList.size(); i++) {
				String indexRes = idList.get(i);
				// within r
				if (allIdList.contains(indexRes)) {
					// add to list
					resIdList.add(indexRes);
					
				}
			}

			// put into map
			if (!UtilHelper.isEmpty(resIdList)) {
				resDistanceMap.put(distance, resIdList);
				lshNum += resIdList.size();
			}
			realNum += allIdList.size();

		}

		
		recall = (double) lshNum / realNum;
		System.out.println("lshNum:" + lshNum + " realNum" + realNum);
		System.out.println("Recall:" + recall);

		return resDistanceMap;
	}

	/**
	 * compute top k distance
	 * @param distanceMap
	 * @param topK
	 * @return
	 */
	public static int computeDistanceK(
			TreeMap<Integer, ArrayList<String>> distanceMap, int topK) {

		Iterator<Entry<Integer, ArrayList<String>>> it1 = distanceMap.entrySet().iterator();

		int disK1 = 0;

		int counter1 = 0;
		while (it1.hasNext()) {
			Entry<Integer, ArrayList<String>> entry1 = (Entry<Integer, ArrayList<String>>) it1.next();

			int dis1 = entry1.getKey();
			int listSize1 = entry1.getValue().size();

			System.out.println("distance:" + dis1 + " size:" + listSize1);
			counter1 += listSize1;

			if (counter1 <= topK) {
				disK1 += listSize1 * dis1;
			} else {
				disK1 += (topK - (counter1 - listSize1)) * dis1;
				break;
			}

		}

		return disK1;
	}

	/**
	 * compute precision
	 * @param distanceMap
	 * @param distanceMapLsh
	 * @param topK
	 * @return
	 */
	public static double computePrecision(
			TreeMap<Integer, ArrayList<String>> distanceMap,
			TreeMap<Integer, ArrayList<String>> distanceMapLsh, int topK) {
		double disK1 = computeDistanceK(distanceMap, topK);
		double disK2 = computeDistanceK(distanceMapLsh, topK);
		double precision = (double) (disK1 / disK2);
		System.out.println("Precision:" + precision + " top" + topK + " disK1:"
				+ disK1 + " disK2:" + disK2);
		return precision;
	}

}
