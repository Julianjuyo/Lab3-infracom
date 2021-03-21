package util;

import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

public class Logger {
	
	private String PATH="/logs/";
	private boolean append_to_file = false;
	private String name  ;
	
	
	public Logger(int numRequest ) {
		long millis= System.currentTimeMillis();
		Date date= new Date(millis);
		this.name= date.getYear() + "-" ;
		this.name += date.getMonth() + "-";
		this.name += date.getDay() +"-";
		this.name += date.getHours() +"-";
		this.name += date.getMinutes() +"-";
		this.name += date.getSeconds() +".txt";


	}
	
	public void log(String msg) throws IOException{
		
		FileWriter write = new FileWriter(PATH + "this.name", append_to_file);
		PrintWriter print_line= new PrintWriter(write);
		
		long millis=System.currentTimeMillis();
		Date date= new Date(millis);
		
		print_line.println("/ "+ date);
		print_line.println(msg);
		
		print_line.close();
	}

}
