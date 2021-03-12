package clientes;


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
	
	
	/**
	 * 
	 * @param file
	 * @return
	 */
	public String getHash(File file){
		String r="";
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			FileInputStream fis = new FileInputStream(file);
			DigestInputStream dis = new DigestInputStream(fis, md5);
			r=dis.toString(); 
			fis.close();
			System.out.println("El hash de confirmación md5 del archivo " + file.getName() + "es: /n " + r);
			
		} catch (Exception e) {
			System.out.println("Problemas al convertir el archivo a hash md5: "+ e.getMessage());
		}
		return  r;
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


