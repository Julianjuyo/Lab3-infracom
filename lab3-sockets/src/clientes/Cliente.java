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
import java.math.BigInteger;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Cliente {
	
	
	/**
	 * Convierte en un arreglo de bits un archivo 
	 * @param file
	 * @return
	 */	
	public static byte[] getArray(File file){
		byte[] byteArray = new byte[(int) file.length()];
		try {
			FileInputStream fis = new FileInputStream(file);
			int bytesCount = 0; 
			bytesCount = fis.read(byteArray);
			fis.close();
			System.out.println("El archivo " + file.getName() + " tiene " + bytesCount +" bytes.");

		} catch (Exception e) {
			System.out.println("Problemas al convertir el archivo a bytes: "+ e.getMessage());
		}
		return byteArray;
	}

	/**
	 * Funacion que crea un hash a partir de un archivo 
	 * @param file
	 * @return
	 */
	public static String getHash(File input) 
	{ 
		try { 
			MessageDigest md = MessageDigest.getInstance("MD5"); 

			byte[] messageDigest = md.digest(getArray(input)); 

			BigInteger no = new BigInteger(1, messageDigest); 

			String hashtext = no.toString(16); 
			while (hashtext.length() < 32) { 
				hashtext = "0" + hashtext; 
			} 
			return hashtext; 
		} 

		catch (NoSuchAlgorithmException e) { 
			throw new RuntimeException(e); 
		} 
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


