package dist.index;

import java.util.ArrayList;
import java.util.HashMap;

import common.crypto.Encryption;

import common.lsh.GenAllPairsLsh;
import common.util.Converter;

/**
 * Recover plaintext ID
 * @author maggie liu
 *
 */
public class PlaintextIdRecovery {

	public ArrayList<String> recoverId(HashMap<String, String> resMap,
			ArrayList<byte[]> keyList) {
		ArrayList<String> idList = new ArrayList<String>();

		for (int i = 0; i < keyList.size(); i++) {
			byte[] perLshRelatedKeyByte = keyList.get(i);

			int counter = 1;
			byte[] counterB = Converter.int2BitwiseArray(counter);

			String labelStr = GenAllPairsLsh.getLabel(counterB,
					perLshRelatedKeyByte);
			;

			while (resMap.get(labelStr) != null) {
				String eid = resMap.get(labelStr);

				int id2 = Encryption.decryptId(eid, perLshRelatedKeyByte);

				if (!idList.contains(Integer.toString(id2))) {
					idList.add(id2 + "");
				}

				counter++;
				counterB = Converter.int2BitwiseArray(counter);

				labelStr = GenAllPairsLsh.getLabel(counterB,
						perLshRelatedKeyByte);
			}

		}

		return idList;
	}
}
