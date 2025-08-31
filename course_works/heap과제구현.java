package course_works;

import java.util.Arrays;
import java.io.*;
public class heap과제구현{

	
	public static void buildHeap(int a[],int n){
		for(int i = (int)Math.floor(n/2); i>=0 ; i--){
			heapify(a,i,n);
		}
	}
	
	public static void heapify(int a[],int r, int n){		// parameter("r") is parent value
		int left = 2*r-1;
		int right =2*r;
		int smaller; 
		
		if(left==-1){
			left+= 2;
			right+= 2;
		}
		else if(left<0 || right < 0)
			return;
		
		if(right < n){				//leaf's end check (left or right)
			if(a[right] >= a[left]){		//if leaf's end is right, then find small value between (left and right)
				smaller = left;
			}
			else 
				smaller = right;
		}
		else if( left< n)
			smaller = left;
		else 					// instance for recursive act
			return;
		
		if( a[smaller]< a[r]){
			int temp;
			temp = a[r];
			a[r] = a[smaller];
			a[smaller]= temp;
			heapify(a,smaller, n);	//  change is happening disorder so do;
		}
	}
			
	public static void main(String args[] ){
		int list[]  ={4,12,6,8,30,10,2,14,3,15,19,20,29,78,16};
		
		
		String name ="./os1.txt";					//value store ( condition 1)
		FileOutputStream output2 = null;
		try {
			output2 = new FileOutputStream(name);
		}catch (FileNotFoundException e) {e.printStackTrace();}
		
		try {
			output2.write(Arrays.toString(list).getBytes());
		}catch (IOException e) {e.printStackTrace();}	
		

		
        String line =null;

		BufferedReader br =null;				//read list
		try {
			br = new BufferedReader(new FileReader(name));
		}catch (FileNotFoundException e) {e.printStackTrace();}
		
		try {
			line = br.readLine();
		}catch (IOException e) {e.printStackTrace();}	
		
		try {
			br.close();
		}catch (IOException e) {e.printStackTrace();}
		
		String [] lineArray = new String[(line.length()-1)];		//String array to int array
		for(int i = 0 ; i < lineArray.length ; i++){
			lineArray[i] = null;
		}
		if(line != null)
			lineArray= (line.substring(1,(line.length()-1))).split(",");
		
		int array[] = new int [lineArray.length];
		for( int i = 0; i< lineArray.length; i++){
			lineArray[i]= lineArray[i].trim();
			array[i]= Integer.parseInt(lineArray[i]);
		}

		buildHeap(array, array.length);						//heap sort
		
		name ="./os2.txt";					//value sorted store ( condition 2)
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(name);
		}catch (FileNotFoundException e) {e.printStackTrace();}
		
		try {
			output.write(Arrays.toString(array).getBytes());
		}catch (IOException e) {e.printStackTrace();}			
   }   
}