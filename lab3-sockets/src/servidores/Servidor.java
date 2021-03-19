package servidores;

import java.beans.beancontext.BeanContextServiceProviderBeanInfo;
import java.io.*;
import java.net.ServerSocket;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.*;
import java.security.*;
import java.math.BigInteger; 
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException; 
import java.nio.file.spi.FileTypeDetector;
import java.lang.Object;


/**
 * Clase que representa un servidor
 * 
 * @author julianoliveros
 *
 */
public class Servidor {

	//private final static String RUTA1="H:/Desktop/Laboratorio3TCP.pdf";
	
	private final static String RUTA1="/Users/julianoliveros/Public/matricula.pdf";
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
			System.out.println("El archivo " + file.getName() +
					" tiene " + bytesCount +" bytes."+"\n");

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
		private String  idCliente;
		private final String log;
		private final String hash;
		private final String path;
		private final int  numeroDeConexciones;

		
		public Peticion(Socket sc, String pidCliente, String plog,String phash ,String ppath, int  pnumeroDeConexciones ) {
			this.clienteSC= sc;
			this.idCliente=pidCliente;
			this.log= plog;
			this.hash= phash;
			this.path= ppath;
			this.numeroDeConexciones =  pnumeroDeConexciones;
		}

		public void run() 
		{
			PrintWriter out = null; 
			BufferedReader in = null; 

			try { 

				// Ecribir a el cliente
				out = new PrintWriter( clienteSC.getOutputStream(), true); 

				// Leer del cliente 
				in = new BufferedReader( new InputStreamReader(clienteSC.getInputStream())); 


				System.out.println("Comenzo Thread: "+idCliente);

				//Se enviua el hash del archvio
				out.println(hash);
				System.out.println("envio el hash: "+hash);

				//Se envia el path
				out.println(path);
				System.out.println("envio el path: "+path);

				//Se envia el numero de conexciones
				out.println(numeroDeConexciones);
				System.out.println("envio el numero de conecciones: "+ numeroDeConexciones);


				//Se realiza la tranferencia del archivo
				
				ObjectOutputStream oos = new ObjectOutputStream(clienteSC.getOutputStream());
				boolean enviadoUltimo=false;

				// Se abre el fichero.
				FileInputStream fis = new FileInputStream(path);

				// Se instancia y rellena un mensaje de envio de fichero
				MetodoAuxiliarEnvioDeDatos mensaje = new MetodoAuxiliarEnvioDeDatos();
				mensaje.nombreFichero = path;


				// Se leen los primeros bytes del fichero en un campo del mensaje
				int leidos = fis.read(mensaje.contenidoFichero);

				// Bucle mientras se vayan leyendo datos del fichero
				while (leidos > -1)
				{

					// Se rellena el n�mero de bytes leidos
					mensaje.bytesValidos = leidos;

					// Si no se han leido el m�ximo de bytes, es porque el fichero
					// se ha acabado y este es el �ltimo mensaje
					if (leidos < MetodoAuxiliarEnvioDeDatos.LONGITUD_MAXIMA)
					{
						mensaje.ultimoMensaje = true;
						enviadoUltimo=true;
					}
					else
						mensaje.ultimoMensaje = false;

					// Se env�a por el socket
					oos.writeObject(mensaje);

					// Si es el �ltimo mensaje, salimos del bucle.
					if (mensaje.ultimoMensaje)
						break;

					// Se crea un nuevo mensaje
					mensaje = new MetodoAuxiliarEnvioDeDatos();
					mensaje.nombreFichero = path;

					// y se leen sus bytes.
					leidos = fis.read(mensaje.contenidoFichero);
				}

				if (enviadoUltimo==false)
				{
					mensaje.ultimoMensaje=true;
					mensaje.bytesValidos=0;
					oos.writeObject(mensaje);
				}
				// Se cierra el ObjectOutputStream
				oos.close();


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



	/**
	 * Main del metodo.
	 * @param args
	 */
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

				numeroConexiones =Integer.parseInt(scaner.nextLine());

				System.out.println(
						"Indique que archivo quiere enviar (ESCRIBA EL NUMERO 1,2,3) \n"+
								"1: archivo de 100 MB \n"+
								"2: Archivo de 250 MB \n"+
								"3: Otro (pasar ruta por parametro) \n"
						);

				String Archivo = scaner.nextLine();

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


			String path = fichero.getPath();

			String hash = getHash(fichero);

			
			//se crea log
			File file = new File("/Users/julianoliveros/"); //TODO Deber ser /logs
			String log = "";

			
			//alamceno info de cada socket			
			ArrayList<Peticion> Clientes = new ArrayList<>();
			int estado=0;

			//While que se queda esperando a que lleguen clientes.
			while (true) {

				//Espero a que un cliente se conecte
				Socket clienteSC = servidor.accept();

				PrintWriter out = new PrintWriter( clienteSC.getOutputStream(), true); 
				BufferedReader in = new BufferedReader(new InputStreamReader( clienteSC.getInputStream())); 

				String  idCliente = in.readLine();

				String  estadoActual = in.readLine();
				System.out.println("Cliente conectado con ID: "+idCliente +" y con IP: "+ clienteSC.getInetAddress().getHostAddress());

				if(estadoActual.equals("Listo")){
					estado++;					
				}

				Peticion threadCliente = new Peticion(clienteSC,idCliente, log ,hash,path,numeroConexiones); //hash tambien envio, log 
				Clientes.add(threadCliente); 

				if(Clientes.size()== numeroConexiones && estado== numeroConexiones){
					for (int i = 0; i < Clientes.size(); i++) {
						Clientes.get(i).start();
					}
				}
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


