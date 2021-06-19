package assignment_0909;

public class ExcuteMain {
	
	
	
	public static void main(String[] args) throws Exception {
		HttpClientEx request = new HttpClientEx();
		JsonProcess obj = new JsonProcess();
		obj.my_Json(request.sendPost());
	}	
}

