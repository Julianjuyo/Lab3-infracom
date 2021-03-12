//package clientes;


import java.io.BufferedReader;
import java.security.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Cliente {
	
	
	private static String getFileChecksum(MessageDigest digest, File file) throws IOException
	{
	    //Get file input stream for reading the file content
	    FileInputStream fis = new FileInputStream(file);
	     
	    //Create byte array to read data in chunks
	    byte[] byteArray = new byte[1024];
	    int bytesCount = 0; 
	      
	    //Read file data and update in message digest
	    while ((bytesCount = fis.read(byteArray)) != -1) {
	        digest.update(byteArray, 0, bytesCount);
	    };
	     
	    //close the stream; We don't need it now.
	    fis.close();
	     
	    //Get the hash's bytes
	    byte[] bytes = digest.digest();
	     
	    //This bytes[] has bytes in decimal format;
	    //Convert it to hexadecimal format
	    StringBuilder sb = new StringBuilder();
	    for(int i=0; i< bytes.length ;i++)
	    {
	        sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
	    }
	     
	    //return complete hash
	   return sb.toString();
	}
	

	public static void main(String[] args) {

		//Host del servidor
		final String HOST = "localhost";

		//Puerto del servidor
		final int PUERTO =61001;

		PrintWriter out = null; 
		BufferedReader in = null; 
		


		try {
			Socket sc = new Socket(HOST, PUERTO);

			// Escribir a el servidor
			out = new PrintWriter( sc.getOutputStream(), true); 
	

			// Leer del servidor 
			in = new BufferedReader(new InputStreamReader( sc.getInputStream())); 
			
			
			
			
			//TRANSFERENCIA DE ARCHIVOS
			/*
		    DataOutputStream outD = new DataOutputStream(new OutputStream(sc.getOutputStream()));
		    DataInputStream inD = new DataInputStream(new InputStream(sc.getInputStream()));
		   
		    byte[] bytes = new byte[1024];

		    inD.read(bytes);
		    System.out.println(bytes);

		    FileOutputStream fos = new FileOutputStream("C:\\test2.xml");
		    fos.write(bytes);
		    
*/
		    
		    
			
		    
			
			// Intercambio de texto
			Scanner scaner = new Scanner(System.in); 
			String line = null; 

			while (!"exit".equalsIgnoreCase(line)) { 

				// Leer por consola
				line = scaner.nextLine(); 

				// Enviar de usuario a servidor
				out.println(line); 
				out.flush(); 

				// displaying server reply 
				System.out.println("Server replied " + in.readLine()); 
			} 


			// cerrar socket
			sc.close(); 


		} catch (IOException ex) {

			Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

}


