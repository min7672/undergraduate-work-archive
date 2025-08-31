package internship_assignment;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

class HttpClientEx {				
	
    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    public void close() throws IOException { httpClient.close();}  
    
    public String sendPost() throws Exception {										//post 통신 후 Respose 스트링으로 리턴
		HttpPost post = new HttpPost("http://210.217.95.183:8000/analyzer/");
		
		FileBody image = new FileBody(new File("C:/Users/gsm73/Desktop/과제2/Oh_1.jpg"));  
		StringBody comment = new StringBody("missoh.face, object", ContentType.TEXT_PLAIN);
		HttpEntity reqEntity = MultipartEntityBuilder.create()
				.addPart("image", image)
				.addPart("modules", comment)
				.build();
		post.setEntity(reqEntity);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = httpClient.execute(post);
		return EntityUtils.toString(response.getEntity());
    }
}
