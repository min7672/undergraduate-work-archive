package internship_assignment;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

class JsonProcess{
	
	private static JSONParser jsonParser = new JSONParser();
	
	public void my_Json(String respose){
		JSONObject mainjorObject;		//원문
		JSONObject resultsObject;		//result 태그 결과 
		JSONArray module_resultArray;	//module_result 태그 결과

		try {
			mainjorObject = (JSONObject) jsonParser.parse(respose);
			
			resultsObject = (JSONObject)  jsonParser.parse(((JSONArray) mainjorObject.get("results")).get(0).toString());  		
			module_resultArray =  (JSONArray)resultsObject.get("module_result");		
			my_JsonTrim(module_resultArray,"face");
			
			resultsObject =(JSONObject)  jsonParser.parse(((JSONArray) mainjorObject.get("results")).get(1).toString());		
			module_resultArray =  (JSONArray)resultsObject.get("module_result");			//object 
			my_JsonTrim(module_resultArray,"object");
		} catch (ParseException e1) {
			e1.printStackTrace();
		}		
		}
	
	private void my_JsonTrim(JSONArray tagArray,String tagName ) {		
		String instance="";			
		JSONObject instanceObject;		//position  tag level
		JSONObject elementObject;		//position  tag's element
		
		JSONArray labalArray;
		JSONObject instanceObject_layer2;		//label's instance for (score, description)
		
		ArrayList<String> result =new  ArrayList<String>();
		
		for(int i =0; i<tagArray.size();i++) {
			instance =tagName+"{";
			try {
				instanceObject = (JSONObject)  jsonParser.parse(tagArray.get(i).toString());
				elementObject= (JSONObject)  jsonParser.parse(instanceObject.get("position").toString());
				
				instance+= "[ "+elementObject.get("x")+", "+elementObject.get("y")+", "+elementObject.get("w")+", "+elementObject.get("h")+"]";
				labalArray= (JSONArray)  instanceObject.get("label");
				
				for (int j = 0 ; j<labalArray.size();j++) {
					instanceObject_layer2= (JSONObject)  jsonParser.parse(labalArray.get(j).toString());
					instance+="[ "+instanceObject_layer2.get("score")+", "+instanceObject_layer2.get("description")+" ]";
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			result.add(instance);
		}  
		
		//파일쓰기
		FileOutputStream fos = null;
	    OutputStreamWriter osw = null;
	    BufferedWriter bfw = null;
	      try {
	          fos = new FileOutputStream("20200909_ httprequest_"+tagName+"_강성민.txt");
	          osw = new OutputStreamWriter(fos, "MS949");
	          bfw = new BufferedWriter(osw);
	          for(String element:result) {
	        	  System.out.println(element);
	        	  bfw.write(element+"\n");
				}
	      } catch (Exception e) {
	          e.printStackTrace();
	      } finally {  //닫기
	    	  if(bfw != null) {try { bfw.close(); } catch (IOException e) { e.printStackTrace(); }}
	          if(osw != null) {try { osw.close(); } catch (IOException e) { e.printStackTrace(); }}
	    	  if(fos != null) {try { fos.close(); } catch (IOException e) { e.printStackTrace(); }}
	          
	      }
	}
	

}

