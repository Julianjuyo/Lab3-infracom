package servidores;



import java.io.*;
import java.net.ServerSocket;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.math.BigInteger; 
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException; 


/**
 * Clase que representa un servidor
 * 
 * @author julianoliveros
 *
 */
public class Servidorcopy {

	//private final static String RUTA1="H:/Desktop/Laboratorio3TCP.pdf";

	private final static String RUTA1="/Users/julianoliveros/Public/hola.txt";
	//private final static String RUTA1="/Users/julianoliveros/Public/matricula.pdf";
	private final static String RUTA2="/Users/julianoliveros/100MBcopy.zip";
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

		//Longitud de bytes por paquete
		public final static int LONGITUD_MAXIMA=1460;//1460;


		private final Socket clienteSC;
		private String  idCliente;
		private final String log;
		private final String hash;
		private final String path;
		private final int tamanoArchvio;
		private final int  numeroDeConexciones;
		


		// Si este es el ultimo mensaje del fichero en cuestion o hay mas despues
		public boolean ultimoMensaje=true;

		// Cuantos bytes son validos en el array de bytes
		public int bytesValidos=0;

		//Array con bytes leidos del fichero
		public byte[] contenidoFichero = new byte[LONGITUD_MAXIMA];




		public Peticion(Socket sc, String pidCliente, String plog,String phash ,String ppath, int  pnumeroDeConexciones, int ptamanoArchvio ) {
			this.clienteSC= sc;
			this.idCliente=pidCliente;
			this.log= plog;
			this.hash= phash;
			this.path= ppath;
			this.numeroDeConexciones =  pnumeroDeConexciones;
			this.tamanoArchvio= ptamanoArchvio;
			
			this.ultimoMensaje=true;
			this.bytesValidos=0;
			this.contenidoFichero= new byte[LONGITUD_MAXIMA];
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

				//Se envia el tamano archivo
				out.println(tamanoArchvio);
				System.out.println("envio el tamanoArchvio: "+ tamanoArchvio);
				int division= tamanoArchvio/LONGITUD_MAXIMA;
				

				
		        FileInputStream fis;
		        BufferedInputStream bis;
		        OutputStream os;
		        BufferedOutputStream bos;
		        int contador=0;

		        
		        try 
		        {
		            File input = new File(path);
		            fis = new FileInputStream(input);
		            bis = new BufferedInputStream(fis);
		            
		            
		            os = clienteSC.getOutputStream();
		            
		            bos = new BufferedOutputStream(os);
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
			                	contador++;
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
		        catch (FileNotFoundException ex) 
		        {
		            Logger.getLogger(Servidorcopy.class.getName()).log(Level.SEVERE, null, ex);
		        } 
		        catch (IOException ex) 
		        {
		            Logger.getLogger(Servidorcopy.class.getName()).log(Level.SEVERE, null, ex);
		        }
		        
		        
//
//
//
//				//Se realiza la tranferencia del archivo
//
//				ObjectOutputStream oos = new ObjectOutputStream(clienteSC.getOutputStream());
//				boolean enviadoUltimo=false;
//
//
//				// Se abre el fichero.
//				FileInputStream fis = new FileInputStream(path);
//
//				// Se instancia y rellena un mensaje de envio de fichero
//				//MetodoAuxiliarEnvioDeDatos mensaje = new MetodoAuxiliarEnvioDeDatos();
//				//mensaje.nombreFichero = path;
//
//
//
//				// Se leen los primeros bytes del fichero en un campo del mensaje
//				int leidos = fis.read(contenidoFichero);
//
//				// Bucle mientras se vayan leyendo datos del fichero
//				while (leidos > -1)
//				{
//
//					// Se rellena el n�mero de bytes leidos
//					bytesValidos = leidos;
//
//
//					// Si no se han leido el m�ximo de bytes, es porque el fichero
//					// se ha acabado y este es el �ltimo mensaje
//					if (leidos < LONGITUD_MAXIMA)
//					{
//						ultimoMensaje = true;
//						enviadoUltimo=true;
//					}
//					else {
//						ultimoMensaje = false;						
//					}
//
//					// Se envia por el socket
//					//System.out.println("envio el contenido Fichero: "+contenidoFichero);
//					
//					String a= new String(contenidoFichero, 0,bytesValidos);
//					
//                    System.out.println("\n"+"INICIO --------------------------------------");
//                    System.out.print(a);
//                    System.out.println("\n"+"FIN --------------------------------------");
//					
//                    oos.writeObject(a);
//                    
//					//out.println(a);
//					//System.out.println("1. envio el contenido Fichero: "+contenidoFichero);
//					
//					//[B@ef4d61c
//					 
//					//Se envia bytes Validos
//					//out.println(bytesValidos);
//					oos.writeObject(bytesValidos);
//					System.out.println("1. envio bytes Validos: "+bytesValidos);
//					
//					//Se envia ultimo Mensaje
//					//out.println(ultimoMensaje);
//					oos.writeObject(ultimoMensaje);
//					System.out.println("1. envio ultimo Mensaje: "+ultimoMensaje);
//					
//					
//
//					// Si es el ultimo mensaje, salimos del bucle.
//					if (ultimoMensaje)
//						break;
//
//					// Se empeiza de nuevo con el ciclo
//				    ultimoMensaje=true;
//				    bytesValidos=0;
//				    contenidoFichero = new byte[LONGITUD_MAXIMA];
//					
//				    
//					// y se leen sus bytes.
//					leidos = fis.read(contenidoFichero);
//					
//				}
//
//				if (enviadoUltimo==false)
//				{
//					ultimoMensaje=true;
//					bytesValidos=0;
//					
//					// Se envia por el socket	
//					String arregloBytesEnString= new String(contenidoFichero, 0,bytesValidos);
//					//out.println(contenidoFichero);
//					oos.writeObject(arregloBytesEnString);
//					System.out.println("2. envio el contenido Fichero: "+contenidoFichero);
//					
//					
//					//Se envia bytes Validos
//					//out.println(bytesValidos);
//					oos.writeObject(bytesValidos);
//					System.out.println("2. envio bytes Validos: "+bytesValidos);
//					
//					//Se envia ultimo Mensaje
//					//out.println(ultimoMensaje);
//					oos.writeObject(ultimoMensaje);
//					System.out.println("2. envio ultimo Mensaje: "+ultimoMensaje);
//					
//					
//				}
//				// Se cierra el ObjectOutputStream
//				oos.close();


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
		final int PUERTO =61101;
		String ruta=" ";
		int numeroConexiones=0;


		try {

			boolean CargoDatos=false;

			while(CargoDatos==false) {

				//Seleccionar que archivo quiere enviar
				Scanner scaner = new Scanner(System.in);

				System.out.println("\n"+"Indique el numero de clientes a los que archivo quiere enviar el archivo \n");

				numeroConexiones= Integer.parseInt(scaner.nextLine());

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
			int tamanoArchivo= (int) fichero.length();
			System.out.println("tam"+tamanoArchivo);
			
			int division= tamanoArchivo/19;
			System.out.println(division);
			

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

				Peticion threadCliente = new Peticion(clienteSC,idCliente, log ,hash,path,numeroConexiones,tamanoArchivo); //hash tambien envio, log 
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
			Logger.getLogger(Servidorcopy.class.getName()).log(Level.SEVERE, null, ex);
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


