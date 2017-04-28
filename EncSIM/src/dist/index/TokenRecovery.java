package dist.index;

import java.util.ArrayList;
import java.util.HashMap;


import common.crypto.PRF;
import common.util.Config;
import common.util.Constants;

import common.util.StringXORer;
import dist.model.TokenTuple;

/**
 * Recover search token
 * @author maggie liu
 *
 */
public class TokenRecovery {
	
	
	
	/**
	 * recover lsh related key - xor
	 * @param tokenMap
	 * @return
	 */
	public ArrayList<byte[]> recoverPerLshKeyXOR(HashMap<Integer, TokenTuple> tokenMap){
		ArrayList<byte[]> labelList = new ArrayList<byte[]>();
		
		int m = Config.getSettingInt(Constants.CONFIG_LSH_M);
		for(int x=0; x < m-1; x++){
			for(int y=x+1; y < m; y++){
				
				byte[] tx = tokenMap.get(x).getToken();
				byte[] ty = tokenMap.get(y).getToken();
				byte[] txy = StringXORer.xorFast(tx, ty);
				
				byte[] rXor = tokenMap.get(x).getRandom();
				
				for(int offset = x+1; offset < y; offset++){
					byte[] rOffset = tokenMap.get(offset).getRandom();
					rXor = StringXORer.xorFast(rXor, rOffset);
				}
				
				
				byte[] perLshRelatedKeyByte = StringXORer.xorWithKey(txy, rXor);
			
				labelList.add(perLshRelatedKeyByte);
				
				
				if(labelList.size() == Config.getSettingInt(Constants.CONFIG_CANDIDATE)){
					return labelList;
				}
				
			}
			
		}
		
		return labelList;
	}
	
	
	
	/**
	 * recover lsh key - general
	 * @param tokenList
	 * @return
	 */
	public ArrayList<byte[]> recoverPerLshKeyGeneral(ArrayList<byte[]> tokenList){
		ArrayList<byte[]> labelList = new ArrayList<byte[]>();
		
		int m = tokenList.size();
		
		
		//int counter = 0;
		for(int x=0; x < m-1; x++){
			for(int y=x+1; y < m; y++){
				
				byte[] tx = tokenList.get(x);
				byte[] ty = tokenList.get(y);
				byte[] txy =  PRF.HMACSHA1ToByteArray(tx, ty);
				
				
				
				
				byte[] perLshRelatedKeyByte = txy;
				
				
				labelList.add(perLshRelatedKeyByte);
				
				//counter++;
			}
			
		}
		
		return labelList;
	}
	
	
	

}
