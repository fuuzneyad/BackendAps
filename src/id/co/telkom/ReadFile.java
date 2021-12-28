package id.co.telkom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReadFile {

	private static BufferedReader br;

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		File file = new File("/Users/Fauzan/Documents/compilean/smsToHutch.csv"); 
		  
		  br = new BufferedReader(new FileReader(file)); 
		  Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		  String st; 
		  while ((st = br.readLine()) != null) 
			  if(st.contains("from :")) {
				  
				  String baru = st.split("from :")[1].split("\\ to")[0]; 
//				  System.out.println(baru);
				  Integer test = map.get(baru);
				  if(test==null)
					  map.put(baru, 1);
				  else
					  map.put(baru, test+1);
			  }
			  for (Map.Entry<String, Integer> entry : map.entrySet())
			  {
			      System.out.println(entry.getKey() + "=" + entry.getValue());
			  }
		  } 
	

}
