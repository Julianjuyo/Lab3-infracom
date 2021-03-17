package util;

import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

public class Logger {
	
	private String path;
	private boolean append_to_file = false;
	
	public Logger(String path) {
		this.path = path;
	}
	
	public Logger( String path , boolean append_to_file ) {
		this.path = path;
		this.append_to_file = append_to_file;
	}
	
	public void log(String msg) throws IOException{
		
		FileWriter write = new FileWriter(path, append_to_file);
		PrintWriter print_line= new PrintWriter(write);
		
		long millis=System. currentTimeMillis();
		Date date= new Date(millis);
		
		print_line.println("/ "+ date);
		print_line.println(msg);
		
		print_line.close();
	}
	
	public static void main(String[] args) {
		
		  String string = "0";
		  byte[] bytes = string.getBytes(Charset.defaultCharset());
		  System.out.println("String: " + string);
		  System.out.println("Bytes: " + Arrays.toString(bytes));

		  
		  
		  String s = "4";
		  byte[] b = s.getBytes(StandardCharsets.UTF_8);
		  
		  System.out.println(s);
		  System.out.println(b[0]);
		  
		  

		  byte[] bb = {(byte) 37};
		  String ss = new String(bb, StandardCharsets.US_ASCII);
		  
		  System.out.println(bb[0]);
		  System.out.println(ss);
		  
		  
	}

}
