package servidores;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
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
	 * Clase que crear un thread por cada peticion
	 * @author julianoliveros
	 *
	 */
	private static class Peticion extends Thread{

		private final Socket clienteSC;
		private final byte[] arregloBits;
		private final String log;
		private final long tamano;


		public Peticion(Socket sc, byte[] parregloBits,String plog,long ptamano  ) {
			this.clienteSC= sc;
			this.arregloBits= parregloBits;
			this.log= plog;
			this.tamano= ptamano;
		}

		public void run() 
		{
			PrintWriter out = null; 
			BufferedReader in = null; 

			//DataOutputStream outD = null;
			//DataInputStream inD = null;


			try { 


				// Ecribir a el cliente
				out = new PrintWriter( clienteSC.getOutputStream(), true); 
				//outD = new DataOutputStream(clienteSC.getOutputStream());

				// Leer del cliente 
				in = new BufferedReader( new InputStreamReader(clienteSC.getInputStream())); 
				//inD = new DataInputStream(clienteSC.getInputStream());


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

				String line; 
				while ((line = in.readLine()) != null) {


					// Escribiendo el mesanje del cliente
					System.out.printf( " Sent from the client: %s\n", line); 

					out.println(line); 
				} 



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

		int numeroDeClientes=0;
		final int PUERTO =61001;

		String ruta=" ";
		int numeroConexiones=0;


		try {

			boolean CargoDatos=false;

			while(CargoDatos==false) {

				//Seleccionar que archivo quiere enviar
				Scanner scaner = new Scanner(System.in);

				System.out.println("\n"+"Indique el numero de clientes a los que archivo quiere enviar el archivo \n");

				numeroConexiones = 25;//Integer.parseInt(scaner.nextLine());

				System.out.println(
						"Indique que archivo quiere enviar (ESCRIBA EL NUMERO 1,2,3) \n"+
								"1: archivo de 100 MB \n"+
								"2: Archivo de 250 MB \n"+
								"3: Otro (pasar ruta por parametro) \n"
						);

				String Archivo = "1" ;//scaner.nextLine();

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
						fichero = new File(ruta);

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

			long tamano = fichero.length();

			byte[] arregloBits = getArray(fichero);

			String hash = getHash(fichero);
			
	


			//Siempre estara escuchando peticiones


			while (numeroDeClientes <= numeroConexiones) {


				//Espero a que un cliente se conecte
				Socket clienteSC = servidor.accept();

				System.out.println("Cliente conectado"+ clienteSC.getInetAddress().getHostAddress());

				Peticion threadCliente = new Peticion(clienteSC,arregloBits,hash,tamano); //hash tambien envio, log 
				threadCliente.start();

				numeroDeClientes++;

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


