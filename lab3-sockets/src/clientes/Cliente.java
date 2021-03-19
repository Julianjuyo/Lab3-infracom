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
	 * 
	 * @param file
	 * @throws Exception
	 */
	public static void printContent(File file) throws Exception {
		System.out.println("Print File Content");
		BufferedReader br = new BufferedReader(new FileReader(file));

		String line = null;
//		while ((line = br.readLine()) != null) {
//			System.out.println(line);
//		}

		br.close();
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
				
				if(scaner.nextLine().equals("Listo")) 
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
            
            

            // Se envia un mensaje de petición de fichero.
            ObjectOutputStream oos = new ObjectOutputStream(sc.getOutputStream());
            
            String pathNuevoArchvio ="/Users/julianoliveros/ArchivosRecibidos";
            MensajeDameFichero mensaje = new MensajeDameFichero();
            
            mensaje.nombreFichero = pathNuevoArchvio+"/Cliente"+id+"-Prueba"+numeroDeConexiones+".pdf";
            
            oos.writeObject(mensaje);

            // Se abre un fichero para empezar a copiar lo que se reciba.
            FileOutputStream fos = new FileOutputStream(pathNuevoArchvio+"/Cliente"+id+"-Prueba"+numeroDeConexiones+".pdf");
            
            
            // Se crea un ObjectInputStream del socket para leer los mensajes
            // que contienen el fichero.
            ObjectInputStream ois = new ObjectInputStream(sc.getInputStream());
 
            
            MensajeTomaFichero mensajeRecibido;
            Object mensajeAux;
            
            try {
            	
            do
            {
                // Se lee el mensaje en una variabla auxiliar
                
					mensajeAux = ois.readObject();
		
                
                // Si es del tipo esperado, se trata
                if (mensajeAux instanceof MensajeTomaFichero)
                {
                    mensajeRecibido = (MensajeTomaFichero) mensajeAux;
                    // Se escribe en pantalla y en el fichero
                    // System.out.print(new String(
                    // mensajeRecibido.contenidoFichero, 0,
                    // mensajeRecibido.bytesValidos));
                    
                    fos.write(mensajeRecibido.contenidoFichero, 0,
                            mensajeRecibido.bytesValidos);
                } else
                {
                    // Si no es del tipo esperado, se marca error y se termina
                    // el bucle
                    System.err.println("Mensaje no esperado "
                            + mensajeAux.getClass().getName());
                    break;
                }
            } while (!mensajeRecibido.ultimoMensaje);
            
            
            long endTime = System.currentTimeMillis() - startTime;
            System.out.println("tarde:"+endTime);
            // Se cierra socket y fichero
            fos.close();
            ois.close();
            sc.close();
			
			
    		} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			// Intercambio de texto NUESTRO
//			byte[] arregloRecibido= new byte[t]; 
//			String[] recibido;
//
//			int mayor=0;
//			
//			//long startTime = System.currentTimeMillis();
//			
//			
//			while (!"terminoEnvio".equalsIgnoreCase(line)) { 
//
//
//				//System.out.println("entro");
//
//				// Leer por consola
//				line = in.readLine();
//				
//				if(line.getBytes().length> mayor) {
//					
//					mayor= line.getBytes().length;
//				}
//				
//				if(line.equals("terminoEnvio")) {
//					continue;
//				}
//				recibido =  line.split("_"); 
//
//				//				System.out.println("1:"+recibido[0]);
//				//				System.out.println("2:"+recibido[1]);
//				int s = Integer.parseInt(recibido[1]);
//
//				byte[] bb = {(byte) s};
//
//				//				System.out.println("3:"+bb[0]);
//
//
//				int p= Integer.parseInt(recibido[0]);
//				arregloRecibido[p]=bb[0];
//
//				//				System.out.println("4:"+arregloRecibido[p]);
//
//
//
//				// displaying server reply 
//				//System.out.println("Server replied " + in.readLine()); 
//			} 
//
//
//			long endTime = System.currentTimeMillis() - startTime; 
//			System.out.println("El tiempo que tardo fue de:"+endTime);
//			System.out.println("EL mayor es:"+mayor);

            
            

			//ruta donde creara el archivo 
			//File file = new File("H:/Desktop/Cliente"+id+"-Prueba-5.pdf");
//			File file = new File("/Users/julianoliveros/Cliente"+id+"-Prueba-5.pdf");
//			
//			try {
//
//				OutputStream os = new FileOutputStream(file);
//
//				os.write(arregloRecibido);
//				System.out.println("Write bytes to file.");
//
//				printContent(file);
//
//				os.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//
//			//Se verifica que el hash sea el mismo
//			String hashArchivoRecibido =  getHash(file);
//
//			if(!hashArchivoRecibido.equals(hashRecibido)) {
//				System.out.println("EL ARHCIVO NO ES CORRECTO!!!!");
//			}
//			else {
//				System.out.println("\n"+"EL VALOR CALCULADO PARA EL HASH DEL ARHCIVO ES CORRECTO"+"\n");
//			}
//			
//			// cerrar socket
//			sc.close(); 



		} catch (IOException ex) {

			Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

}


