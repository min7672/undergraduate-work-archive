package internship_assignment;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
/*
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.Header;
*/

class HttpClientEx {
/* 
 * 6.include로 설정을 해결하지 못했습니다.
*/
    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    public void close() throws IOException { httpClient.close();}

    public String sendGet(String word) throws Exception {
		String certKeyNo = System.getenv("DICT_CERT_NO");  
		String apiKey = System.getenv("DICT_API_KEY");  
		String url = "https://stdict.korean.go.kr/api/search.do?certkey_no=" 
             + certKeyNo + "&key=" + apiKey + "&type_search=search&q=" + word;
    	HttpGet request = new HttpGet(url);
    	String result="";
        //request.addHeader("host", "-");<- 오류
        //request.addHeader("key", "-");
        //request.addHeader("q", "당근"));
         
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            //Header headers = entity.getContentType();
            if (entity != null) {
                result = EntityUtils.toString(entity);
            }
        }
        return result;
    }
  
}

public class Api_example {
	public static void main(String[] args) {
		String sample_str = "\"당근\", '양파sdfgs', 오4535이’s, 2345수dfgsv박!@#%@$^, /참$%^외^&*/";		
		ArrayList<String> word_list = new ArrayList<String>();
		ArrayList<String[]> response_list = new ArrayList<String[]>();
		
		// 단어-> 한글만 추출
		for(String x : sample_str.split(",")) {			
			char[] txtChar = x.toCharArray();
		    String word="";
			for (int j = 0; j < txtChar.length; j++) {
		        if (txtChar[j] >= '\uAC00' && txtChar[j] <= '\uD7A3') {
		            String targetText = String.valueOf(txtChar[j]);
		            word+=targetText;
		        } 
		    }
			word_list.add(word);
		}
		
		
		//단어-> api 요청
		HttpClientEx httpEx = new HttpClientEx();
		String xml="";
		try {
		for(String x : word_list) {
			xml=httpEx.sendGet(x);
			Thread.sleep(100);
			response_list.add(my_trim(xml));
		}} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {httpEx.close();} catch (IOException e) {e.printStackTrace();}
		}

		
		// 파일 쓰기
	    FileOutputStream fos = null;
	    OutputStreamWriter osw = null;
	    BufferedWriter bfw = null;
	      try {
	          fos = new FileOutputStream("result.csv");
	          osw = new OutputStreamWriter(fos, "MS949");
	          bfw = new BufferedWriter(osw);
	          for(String[] container:response_list) {
					for(String element:container) {
						bfw.write(element+";");
						//System.out.print(element+"\t");
					}
					bfw.write("\n");
					//System.out.println("");
				}
	      } catch (Exception e) {
	          e.printStackTrace();
	      } finally {  //닫기
	    	  if(bfw != null) {try { bfw.close(); } catch (IOException e) { e.printStackTrace(); }}
	          if(osw != null) {try { osw.close(); } catch (IOException e) { e.printStackTrace(); }}
	    	  if(fos != null) {try { fos.close(); } catch (IOException e) { e.printStackTrace(); }}
	          
	      }
	  }

	public static String[] my_trim(String result_xml) {	
		String[] result={result_xml.substring(result_xml.indexOf("<word><![CDATA[")+15, result_xml.indexOf("</word>")-3).trim(),
		result_xml.substring(result_xml.indexOf("<pos>")+5, result_xml.indexOf("</pos>")).trim(),
		result_xml.substring(result_xml.indexOf("<definition><![CDATA[")+21, result_xml.indexOf("</definition")-3).trim(),
		result_xml.substring(result_xml.indexOf("<type>")+6, result_xml.indexOf("</type")).trim()};
		return result;
	}
	
}
