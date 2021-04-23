package ServidorCopy;


import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

public class Logger {
	
	private String PATH="/home/infracom/Lab3-infracom/lab3-sockets/logs/";
	//private String PATH="/Users/julianoliveros/Public";
	private boolean append_to_file = true;
	private String name  ;


	public static void log(String msg) throws IOException{
		
		FileWriter write = new FileWriter(PATH + this.name , append_to_file);
		PrintWriter print_line= new PrintWriter(write);
		
		long millis=System.currentTimeMillis();
		Date date= new Date(millis);
		
		print_line.println("/log "+ date + "\n");
		print_line.println(msg);
		
		print_line.close();
	}

}
