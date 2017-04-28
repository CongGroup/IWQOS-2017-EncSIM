package common.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import redis.clients.jedis.Jedis;
import common.db.ConnectRedis;

public class TestDB {
	
	public static void main(String[] args){
		ConnectRedis cr = new ConnectRedis();
		Jedis jedis = cr.connectDb("localhost");
		 Set<String> names=jedis.keys("*");

		    Iterator<String> it = names.iterator();
		    while (it.hasNext()) {
		        String s = it.next();
		        System.out.println(s+" "+jedis.get(s));
		    }
		
		    ArrayList<String> a1 = new ArrayList<String>();
		   for(int i=0; i<100; i++){
			   a1.add(i+" ");
		   }
		   
		   System.out.println(a1.get(0)+a1.get(1)+a1.get(2)+a1.get(3));
		    
		
	}

}
