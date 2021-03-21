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
import java.io.FileReader;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.io.*;
import util.*;



/**
 * clase de cliente 
 * 
 * @author je.oliverosf
 *
 */
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
	 * Main de la clase cliente 
	 * @param args
	 */
	public static void main(String[] args) {

		//Host del servidor
		final String HOST = "localhost";

		//Puerto del servidor
		final int PUERTO =61001;

		//Meodos para ecribir y leer
		PrintWriter out = null; 
		BufferedReader in = null; 

		//Id del cliente
		String id=" ";



		try {
			
			// Se crea el socket y conecta a la ip y puerto 
			Socket sc = new Socket(HOST, PUERTO);

			// Escribir a el servidor
			out = new PrintWriter( sc.getOutputStream(), true); 

			// Leer del servidor 
			in = new BufferedReader(new InputStreamReader( sc.getInputStream())); 

			//Lector
			Scanner scaner = new Scanner(System.in);

			//Se pregunta y envia a el servidor el id del cliente
			System.out.println("Escriba el id del cliente (numero)");
			id = scaner.nextLine();
			out.println(id);


			// ciclo hasta que escriba la palabra Listo
			boolean listo=true;
			while(listo) {
				System.out.println("Indique cuando este listo para la empezar la recepcion del archivo escribiendo: Listo");
				
				String ComprbanteDeEnvio= scaner.nextLine();//"Listo";//
				if(ComprbanteDeEnvio.equals("Listo")) 
					listo=false;
			}
			out.println("Listo");


			
			//Comienza Transferencia de Archivos
			
			//recibe el hash
			String line = in.readLine();
			String hashRecibido = line;
			System.out.println("recibo Hash: "+hashRecibido);

			//recibe el path
			line= in.readLine();
			String path = line;
			String[] split =  path.split("\\.");
			String tipoDeArchivo =  split[1]; 
			System.out.println("recibo path: "+ path);
			System.out.println("recibido Tipo Archivo: "+tipoDeArchivo);
			
			//recibe el numero de conexiones
			line= in.readLine();
			int numeroDeConexiones = Integer.parseInt(line);
			System.out.println("recibo numeroDeConexiones: "+ numeroDeConexiones);


            long startTime = System.currentTimeMillis();
            
            
            String pathNuevoArchvio ="/Users/julianoliveros/ArchivosRecibidos/Cliente"+id+"-Prueba"+numeroDeConexiones+"."+tipoDeArchivo;
            
 
            // Se abre un fichero para empezar a copiar lo que se reciba.
            FileOutputStream fos = new FileOutputStream(pathNuevoArchvio);
            
            
            // Se crea un ObjectInputStream del socket para leer los mensajes
            // que contienen el fichero.
            ObjectInputStream ois = new ObjectInputStream(sc.getInputStream());
 
            
            MetodoAuxiliarEnvioDeDatos mensajeRecibido;
            Object mensajeAux;
            
            
            try {
            	
            do
            {
                // Se lee el mensaje en una variabla auxiliar
					mensajeAux = ois.readObject();
		
                // Si es del tipo esperado, se trata
                if (mensajeAux instanceof MetodoAuxiliarEnvioDeDatos)
                {
                    mensajeRecibido = (MetodoAuxiliarEnvioDeDatos) mensajeAux;
                    
                    fos.write(mensajeRecibido.contenidoFichero, 0,mensajeRecibido.bytesValidos);
                } 
                else
                {
                    // Si no es del tipo esperado, se marca error y se termina
                    // el bucle
                    System.err.println("Mensaje no esperado "+ mensajeAux.getClass().getName());
                    break;
                }
            } while (!mensajeRecibido.ultimoMensaje);
            
			
  
            // Se cierra socket y fichero
            fos.close();
            ois.close();
			
    		} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            		
            File fichero = new File(pathNuevoArchvio);
            
            
			//Se verifica que el hash sea el mismo
			String hashArchivoNuevo =  getHash(fichero);

			if(!hashArchivoNuevo.equals(hashRecibido)) {
				System.out.println("Correcto");
				out.println("Correcto");
			}
			else {
				System.out.println("Error");
				out.println("Error");
			}
			
	
            
            long endTime = System.currentTimeMillis() - startTime;

            System.out.println("Se demoro: "+endTime+" milisegundos en enviar el archivo");
            out.println(endTime);
			sc.close(); 

			out.close();

		} catch (IOException ex) {

			Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

}


