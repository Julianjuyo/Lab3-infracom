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
import java.math.BigInteger;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;




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




	/**
	 * 
	 * @param file
	 * @throws Exception
	 */
	public static void printContent(File file) throws Exception {
		System.out.println("Print File Content");
		BufferedReader br = new BufferedReader(new FileReader(file));

		String line = null;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}

		br.close();
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

			System.out.println("Entro");

			//Comienza Transferencia de Archivos

			String line = in.readLine();
			String hashRecibido = line;
			System.out.println("recibo Hash"+hashRecibido);

			line= in.readLine();
			String tamano = line;
			int t= Integer.parseInt(tamano);
			System.out.println("recibo tamano"+t);


			// Intercambio de texto


			byte[] arregloRecibido= new byte[t]; 
			String[] recibido;

			System.out.println("paso aqui"); 


			while (!"terminoEnvio".equalsIgnoreCase(line)) { 


				System.out.println("entro");

				// Leer por consola
				line = in.readLine();

				if(line.equals("terminoEnvio")) {
					continue;
				}
				recibido =  line.split("_"); 

				System.out.println("1:"+recibido[0]);
				System.out.println("2:"+recibido[1]);
				
				
			
				
				int s = Integer.parseInt(recibido[1]);
				
				byte[] bb = {(byte) s};

				System.out.println("3:"+bb[0]);
				

//				String s = recibido[1];
//				byte[] b = recibido[1].getBytes(StandardCharsets.UTF_8);

				int p= Integer.parseInt(recibido[0]);

				arregloRecibido[p]=bb[0];
				
				System.out.println("4:"+arregloRecibido[p]);


				if(p>(t-100)){
					//System.out.println("el byte recibido"+b);
					// Enviar de usuario a servidor
				}



				// displaying server reply 
				//System.out.println("Server replied " + in.readLine()); 
			} 


			// cerrar socket
			sc.close(); 



			File file = new File("/Users/julianoliveros/Cliente1-Prueba-5.pdf");




			byte[] arregloBits;

			//Se envia el archivo correctamente. 
			File archivo;

			try {

				OutputStream os = new FileOutputStream(file);

				os.write(arregloRecibido);
				System.out.println("Write bytes to file.");

				printContent(file);

				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}


			String hashArchivoRecibido =  getHash(file);

			if(!hashArchivoRecibido.equals(hashRecibido)) {

				System.out.println("EL ARHCIVO NO ES CORRECTO!!!!");

			}
			else {
				System.out.println("ARHCIVO ES CORRECTO");
			}




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








		} catch (IOException ex) {

			Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

}


