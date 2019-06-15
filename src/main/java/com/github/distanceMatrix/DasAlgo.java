package com.github.distanceMatrix;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DasAlgo {
	List<List<String>> dmatrix = null;
	List<DistNode> pts = null;
	Double depot_x,depot_y = 0.0; 
	String mapKey = "";
	
	
	
public DasAlgo(List<DistNode> pts, Double depot_x, Double depot_y, String mapKey) {
		super();
		this.pts = pts;
		this.depot_x = depot_x;
		this.depot_y = depot_y;
		this.mapKey = mapKey;
	}



public String[][] getDistanceMatrix() {

		dmatrix = new ArrayList<List<String>>();
		
		
		List<String> strL = new ArrayList<String>();
		
		for(DistNode bk : pts) {
			strL.add(bk.getDropLatitude()+","+bk.getDropLongitude());
		}
		

		List<String> strList = new ArrayList<String>();
		strList.add(""+depot_x+","+depot_y);
		strList.addAll(strL);
	    
		String joinedString = String.join("|", strList);
		System.out.println(strList);
		
		
		int max_elements = 100;
		int num_addresses = strList.size();
		int max_rows = max_elements / num_addresses;
		
		int q = num_addresses / max_rows;
		int r = num_addresses % max_rows;
		
		
		/**
		 * 
		 * q times max_rows with num_addresses + r with num_addresses
		 * */
		int a = 0;
		
		List<String> addrList = new ArrayList<String>();
		
		List<String> str = new ArrayList<String>();
		
		for(int i=0;i<q;i++) {
			String add = "";
			for(int j=0;j<max_rows;j++) {
				
				add +=  strList.get(a)+"|";
				
				a++;
			}
			
			addrList.add(add);
			
			String ad = "origins="+add+"&destinations="+joinedString.replace("null", "");
			str.add(ad);
			
					
		}
		
		String add1 = "";
		
		for(int k = 0; k< r;k++) {		
			add1 +=  strList.get(a)+"|";
			
			a++;
		}
		addrList.add(add1);
		String ad1 = "&origins="+add1+"&destinations="+joinedString.replace("null", "");
		str.add(ad1);
		
		 for(String st:str) {
			 generateMatrix(getDistanceMatrix(st, mapKey));
			
		 }
		
		
		 
		 String[][] array = dmatrix.stream()
				    .map(l -> l.stream().toArray(String[]::new))
				    .toArray(String[][]::new);
		 
		return array;
		
	}
	
	
	
private  String getDistanceMatrix(String str, String key) {

		 String url = "https://maps.googleapis.com/maps/api/distancematrix/json?"+str+"&key="+key; 
		 RestTemplate restTemplate = new RestTemplate();		 
		 HttpHeaders requestHeaders = new HttpHeaders();
		 requestHeaders.set("Content-Type", "application/json");
		
		 restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		 String res= restTemplate.getForObject(url, String.class);		 
		 return res;

	}
	

private void generateMatrix(String res) {
	
	List<List<String>> dm1 = new ArrayList<List<String>>();
	
	JsonElement jelement = new JsonParser().parse(res);
	JsonObject  jobject = jelement.getAsJsonObject();
	
	JsonArray jarray = jobject.getAsJsonArray("rows"); 
	
	for(int i=0; i<jarray.size(); i++){  
		
		JsonObject  rowdata = jarray.get(i).getAsJsonObject();
    	JsonArray elements = rowdata.getAsJsonArray("elements");
    	List<String> dm = new ArrayList<String>();
    	for(int j=0; j<elements.size(); j++){
    		
    	JsonObject  distances = elements.get(j).getAsJsonObject();
    		
   		 if(distances.get("status").toString().equals("\"OK\"")){
   			 
   	  	 JsonObject  duration=distances.getAsJsonObject("duration");
   	  	 JsonObject  distance=distances.getAsJsonObject("distance");
   	  	 
   	  	 int durationValue = duration.get("value").getAsInt();
   	  	 int distanceValue = distance.get("value").getAsInt();
   	  	 
   	  	 String durationDistance = durationValue+"|"+distanceValue;
   	  	 dm.add(durationDistance);
   		}
   	  	 else{
   	  		 	dm.add("NA");
   	  	 }  
    	
    	}
    	
    	dm1.add(dm);
    	
		
	}
	
	dmatrix.addAll(dm1);
	
	
	
}



	
	
	
	
	
	
	
	
	
	
	
}
