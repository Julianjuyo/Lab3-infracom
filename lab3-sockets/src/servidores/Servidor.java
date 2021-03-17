//package servidores;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


import java.security.*;
import java.math.BigInteger; 
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException; 




public class Servidor {

	private final static String RUTA1="/Users/julianoliveros/100MBcopy.bin";
	private final static String RUTA2="/Users/julianoliveros/250MBcopy.zip";
	private static File fichero;



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
//        while ((line = br.readLine()) != null) {
//            System.out.println(line);
//        }
 
        br.close();
    }



	/**
	 * Clase que crear un thread por cada peticion
	 * @author julianoliveros
	 *
	 */
	private static class Peticion extends Thread{

		private final Socket clienteSC;
		private final byte[] arregloByte;
		private final String log;
		private final String hash;
		private final long tamanoArchivo;
		
		private static int numeroDeClientesActuales = 0;

		private static int NUMERO_CONEXIONES_TOTALES = 0;


		public Peticion(Socket sc, byte[] parregloBits, String plog,String phash ,long pTamanoArchivo ) {
			this.clienteSC= sc;
			this.arregloByte= parregloBits;
			this.log= plog;
			this.hash= phash;
			this.tamanoArchivo= pTamanoArchivo;
		}

		public void run() 
		{
			PrintWriter out = null; 
			BufferedReader in = null; 

			//DataOutputStream outD = null;
			//DataInputStream inD = null;




			try { 

				numeroDeClientesActuales++;

				// Ecribir a el cliente
				out = new PrintWriter( clienteSC.getOutputStream(), true); 
				//outD = new DataOutputStream(clienteSC.getOutputStream());

				// Leer del cliente 
				in = new BufferedReader( new InputStreamReader(clienteSC.getInputStream())); 
				//inD = new DataInputStream(clienteSC.getInputStream());

				
				boolean a=false;
				
				//METODO PARA QUE LOS THREAD ENTREN A EL MISMO TIEMPO NO SIRVE
				System.out.println(numeroDeClientesActuales);
				System.out.println(NUMERO_CONEXIONES_TOTALES);
				
				
//				while(numeroDeClientesActuales < NUMERO_CONEXIONES_TOTALES) {	
//					System.out.println("\n");
//					
//					
//				}
				
				
				System.out.println("Salio");					
				
				

				System.out.println("envio el hash ");
				out.println(hash);
				
				System.out.println("envio el hash ");
				out.println(tamanoArchivo);
				//01001000 01101111 01101100 01100001
				
				System.out.println("Comenzo a enviar archivo ");
				for (int i = 0; i < arregloByte.length; i++) {
					
					//1460	 
					
					out.println(i+"_"+arregloByte[i]);
					
					byte[] bb = {(byte) arregloByte[i]};
					System.out.println("aaaa:"+bb[0]);
					
					String ss = new String(bb, StandardCharsets.US_ASCII);
					System.out.println("bbbb:"+ss);
					
					
//					
//					if(bb[0]==10) {
//						
//						String ss = "\n";
//						System.out.println("bbbb:"+ss);
//						out.println(i+"_"+ss);
//					}
//					else {
//						
//						out.println(i+"_"+ss);
//					}
//					
					
					
//					if(i>(arregloByte.length-100)) {
//						//System.out.println("el byte enviado"+arregloByte[i]);
//					}
					
					
				}
				out.println("terminoEnvio");




				//TRANSFERENCIA DE ARCHIVOS
				/*
			    File file = new File("C:\\test.xml");
			    InputStream is = new FileInputStream(file);
			    //Get the size of the file
			    long length = file.length();
			    if (length > Integer.MAX_VALUE) {
			        System.out.println("File is too large.");
			    }
			    byte[] bytes = new byte[(int) length];

			    //out.write(bytes);
			    System.out.println(bytes);
				 */



				// Tamano segmento TCP 1500 Bytes = 12000 bits
				
				//archivos 1460 bytes

				//encabezados TCP 40 Bytes = 320 bits


				//puerto origen 16 bits

				//puerto destino 16 bits

				// numero secuencia 32 bits

				// numero de reconocimiento 32 bits

				// Long Cabec 4 bits

				//no usadao 6 bits

				// indicadores 6 bits

				// ventana 16 bits

				// suma verificacion 24 bits 

				// puntero de urgencia 8 bits

				// opciones si las hay 

				//info que envio 





				//				String line; 
				//				while ((line = in.readLine()) != null) {
				//
				//
				//					// Escribiendo el mesanje del cliente
				//					System.out.printf( " Sent from the client: %s\n", line); 
				//
				//					out.println(line); 
				//				} 



				clienteSC.close();
				System.out.println("Cliente desconectado");
			} 
			catch (IOException e) { 
				e.printStackTrace(); 
			} 

			finally { 
				try { 
					if (out != null) { 
						out.close(); 
					} 
					if (in != null) { 
						in.close();  
					} 
				} 
				catch (IOException e) { 
					e.printStackTrace(); 
				} 
			} 
		} 
	} 






	public static void main(String[] args) {

		ServerSocket servidor = null;


		final int PUERTO =61001;

		String ruta=" ";


		int numeroConexiones=0;


		try {

			boolean CargoDatos=false;

			while(CargoDatos==false) {

				//Seleccionar que archivo quiere enviar
				Scanner scaner = new Scanner(System.in);

				System.out.println("\n"+"Indique el numero de clientes a los que archivo quiere enviar el archivo \n");

				numeroConexiones = 2;//Integer.parseInt(scaner.nextLine());

				System.out.println(
						"Indique que archivo quiere enviar (ESCRIBA EL NUMERO 1,2,3) \n"+
								"1: archivo de 100 MB \n"+
								"2: Archivo de 250 MB \n"+
								"3: Otro (pasar ruta por parametro) \n"
						);

				String Archivo = "3" ;//scaner.nextLine();

				//Transferir archivo de 100MB
				if(Archivo.equals("1")) {
					ruta= RUTA1;
					fichero = new File(ruta);
					if(fichero.exists()) { 
						CargoDatos=true;
						System.out.println("Se cargo el archivo correctamente \n");
					}			
				}

				//Transferir archivo de 250MB
				else if(Archivo.equals("2")) {
					ruta= RUTA2;
					fichero = new File(ruta);
					if(fichero.exists()) { 
						CargoDatos=true;
						System.out.println("Se cargo el archivo correctamente \n");
					}
				}
				//Transferir otro archivo
				else if(Archivo.equals("3")) {
					boolean c=true;
					while(c) {
						System.out.println("Escriba la ruta del archivo \n");
						ruta = scaner.nextLine();
						fichero = new File("/Users/julianoliveros/Public/matricula.pdf");

						if(fichero.exists()) { 
							CargoDatos=true;
							c=false;
							System.out.println("Se cargo el archivo correctamente \n");
						}
						else {
							System.out.println("Ruta inavlida \n");							
						}
					}
				}
			}


			//Creamos el socket del servidor
			servidor = new ServerSocket(PUERTO);

			//Permite que se se pueda utilizar el mismo puerto
			servidor.setReuseAddress(true);
			System.out.println("---------------- \n" +"Servidor iniciado \n");




			//se crea log

			long tamanoArchivo = fichero.length();

			byte[] arregloBits = getArray(fichero);

			String hash = getHash(fichero);

			
			for (int i = 0; i < 20; i++) {
				System.out.println("aaaa"+arregloBits[i]);
			}
			
//			File file = new File("/Users/julianoliveros/Cliente1-Prueba-5.pdf");
//			
//			File archivo;
//			
//			try {
//				 
//	            OutputStream os = new FileOutputStream(file);
//	            
//	            os.write(arregloBits);
//	            System.out.println("Write bytes to file.");
//	            
//	            printContent(file);
//	            
//	            os.close();
//	        } catch (Exception e) {
//	            e.printStackTrace();
//	        }
			
			
			String log = "";


			
			
			
			
			Peticion.NUMERO_CONEXIONES_TOTALES = numeroConexiones;
			
			
			
			while (true) {


				

				//Espero a que un cliente se conecte
				Socket clienteSC = servidor.accept();

				System.out.println("Cliente conectado"+ clienteSC.getInetAddress().getHostAddress());


				Peticion threadCliente = new Peticion(clienteSC,arregloBits, log ,hash,tamanoArchivo); //hash tambien envio, log 
				threadCliente.start();




			}





			//imprime log


		}
		catch (IOException ex)
		{
			Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			if(servidor!=null) {
				try {
					servidor.close();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}


