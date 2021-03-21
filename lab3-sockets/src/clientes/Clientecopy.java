package clientes;


import java.security.*;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;



/**
 * clase de cliente 
 * 
 * @author je.oliverosf
 *
 */
public class Clientecopy {

	public final static int LONGITUD_MAXIMA=1460;


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
		final int PUERTO =61101;

		//Meodos para ecribir y leer
		PrintWriter out = null; 
		BufferedReader in = null; 

		//Id del cliente
		String id=" ";

		// Si este es el ultimo mensaje del fichero en cuestion o hay mas despues
		boolean ultimoMensaje=true;

		// Cuantos bytes son validos en el array de bytes
		int bytesValidos=0;

		//Array con bytes leidos del fichero
		byte[] contenidoFichero = new byte[LONGITUD_MAXIMA];




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

				String ComprbanteDeEnvio= "Listo";//scaner.nextLine();//
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
			
			//recibe el tamanoArchvio
			line= in.readLine();
			int tamanoArchvio = Integer.parseInt(line);
			System.out.println("recibo tamanoArchvio: "+ tamanoArchvio);
			
			long startTime = System.currentTimeMillis();


			String pathNuevoArchvio ="/Users/julianoliveros/ArchivosRecibidos/Cliente"+id+"-Prueba"+numeroDeConexiones+"."+tipoDeArchivo;
			
	        InputStream is;
	        BufferedInputStream bis;
	        FileOutputStream fos   ;
	        BufferedOutputStream bos;

	        
	        try 
	        {
	            File output = new File(pathNuevoArchvio);
	            is = sc.getInputStream();
	            
	            bis = new BufferedInputStream(is);
	            fos = new FileOutputStream(output);
	            bos = new BufferedOutputStream(fos);
	            byte[] buffer = new byte[LONGITUD_MAXIMA];
	            int data;
	            
	            while(true)
	            {
	                data = bis.read(buffer);
	                System.out.println("data: "+data);
	                
	                if(data != -1)
	                {
	                	if(data==LONGITUD_MAXIMA) {
		                	System.out.println("1");
		                	bos.write(buffer, 0, LONGITUD_MAXIMA);
	                	}
	                	else {
	                		System.out.println("2");
	                		bos.write(buffer, 0, data);
	                	}
	                	
	
	                }
	                else
	                {
	                	System.out.println("3");
	                    bis.close();
	                    bos.close();
	                    break;
	                }
	            }
	        } 
	        catch (IOException ex) 
	        {
	            Logger.getLogger(Clientecopy.class.getName()).log(Level.SEVERE, null, ex);
	        }

//			// Se abre un fichero para empezar a copiar lo que se reciba.
//			FileOutputStream fos = new FileOutputStream(pathNuevoArchvio);
//
//
//			// Se crea un ObjectInputStream del socket para leer los mensajes
//			// que contienen el fichero.
//			ObjectInputStream ois = new ObjectInputStream(sc.getInputStream());
//
//
//			//MetodoAuxiliarEnvioDeDatos mensajeRecibido;
//			//Object mensajeAux;
//
//
//			do
//			{
//				// Se lee el mensaje en una variabla auxiliar
//					//mensajeAux = ois.readObject();
////
////					// Si es del tipo esperado, se trata
////					if (mensajeAux instanceof MetodoAuxiliarEnvioDeDatos)
//				
////					{
//					//mensajeRecibido = (MetodoAuxiliarEnvioDeDatos) mensajeAux;
//
//					//recibe el contenido fichero
//					//
//					try {
//						String lin = (String) ois.readObject();
//						
//						System.out.println("\n"+"INICIO --------------------------------------");
//						System.out.print(lin);
//						System.out.println("\n"+"FIN --------------------------------------");
//						
//						//contenidoFichero = lin;
//						//line =in.readLine();
//						//System.out.println("recibo contenido Fichero: "+lin);
//						
//						//recibe los bytes validos
//						line = (String) ois.readObject();//in.readLine();
//						bytesValidos = Integer.parseInt(line);
//						System.out.println("recibo bytesValidos: "+bytesValidos);
//						
//						
//						//escribe
//						//fos.write(contenidoFichero,0,bytesValidos);
//
//						
//						//recibe el ultimo mensaje
//						line =(String) ois.readObject();//in.readLine();
//						ultimoMensaje = Boolean.parseBoolean(line);
//						System.out.println("recibo ultimoMensaje: "+ultimoMensaje);
//						
//						
//					} catch (ClassNotFoundException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					
//					
//			} while (!ultimoMensaje);
//
//
//
//			// Se cierra socket y fichero
//			fos.close();
//			ois.close();


			File fichero = new File(pathNuevoArchvio);


			//Se verifica que el hash sea el mismo
			String hashArchivoNuevo =  getHash(fichero);
			System.out.println("Hash archivo recibido"+hashArchivoNuevo);

			if(!hashArchivoNuevo.equals(hashRecibido)) {
				System.out.println("EL ARHCIVO NO ES CORRECTO!!!!");
			}
			else {
				System.out.println("\n"+"EL VALOR CALCULADO PARA EL HASH DEL ARHCIVO ES CORRECTO"+"\n");
			}



			long endTime = System.currentTimeMillis() - startTime;
			System.out.println("Se demoro: "+endTime+" milisegundos en enviar el archivo");

			sc.close(); 



		} catch (IOException ex) {

			Logger.getLogger(Clientecopy.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

}

