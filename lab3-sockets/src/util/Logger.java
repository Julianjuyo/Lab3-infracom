package util;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
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

}
