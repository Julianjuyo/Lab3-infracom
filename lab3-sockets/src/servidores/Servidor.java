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
     * Env�a el fichero indicado a trav�s del ObjectOutputStream indicado.
     * @param fichero Nombre de fichero
     * @param oos ObjectOutputStream por el que enviar el fichero
     */
    private static void enviaFichero(String fichero, ObjectOutputStream oos)
    {
        try
        {
            boolean enviadoUltimo=false;
            
            // Se abre el fichero.
            FileInputStream fis = new FileInputStream(fichero);
            
            // Se instancia y rellena un mensaje de envio de fichero
            MensajeTomaFichero mensaje = new MensajeTomaFichero();
            mensaje.nombreFichero = fichero;
            
            
            // Se leen los primeros bytes del fichero en un campo del mensaje
            int leidos = fis.read(mensaje.contenidoFichero);
            
            // Bucle mientras se vayan leyendo datos del fichero
            while (leidos > -1)
            {
                
                // Se rellena el n�mero de bytes leidos
                mensaje.bytesValidos = leidos;
                
                // Si no se han leido el m�ximo de bytes, es porque el fichero
                // se ha acabado y este es el �ltimo mensaje
                if (leidos < MensajeTomaFichero.LONGITUD_MAXIMA)
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
                mensaje = new MensajeTomaFichero();
                mensaje.nombreFichero = fichero;
                
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
        } catch (Exception e)
        {
            e.printStackTrace();
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
		private final byte[] arregloByte;
		private final String log;
		private final String hash;
		private final String path;
		private final int  numeroDeConexciones;
		
//		private static int numeroDeClientesActuales = 0;
//		private static int NUMERO_CONEXIONES_TOTALES = 0;
		

	  

		public Peticion(Socket sc, String pidCliente ,byte[] parregloBits, String plog,String phash ,String ppath, int  pnumeroDeConexciones ) {
			this.clienteSC= sc;
			this.idCliente=pidCliente;
			this.arregloByte= parregloBits;
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
				System.out.println("envio el hash ");
				
				//Se envia el path
				out.println(path);
				System.out.println("envio el path");
				
				//Se envia el numero de conexciones
				out.println(numeroDeConexciones);
				System.out.println("envio el # conecciones");
				
				
		         // Se lee el mensaje de petici�n de fichero del cliente.
	            ObjectInputStream ois = new ObjectInputStream(clienteSC.getInputStream());
	            
	            Object mensaje;
	            
				try {
					mensaje = ois.readObject();
					
	
	            
	            // Si el mensaje es de petici�n de fichero
					
	            if (mensaje instanceof MensajeDameFichero)
	            {
	                // Se muestra en pantalla el fichero pedido y se envia
	                //System.out.println("Me piden: "+ ((MensajeDameFichero) mensaje).nombreFichero);
	                
	                
	                enviaFichero(path, new ObjectOutputStream(clienteSC.getOutputStream()));
	                
	            }
	            else
	            {
	                // Si no es el mensaje esperado, se avisa y se sale todo.
	                System.err.println ( "Mensaje no esperado "+mensaje.getClass().getName());
	            }
	            
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				

				
				
				//file unptu stream
				//file.read (tamano)

				//Se comienza el envio del rchivo
//				System.out.println("Comenzo a enviar archivo ");
//				for (int i = 0; i < arregloByte.length; i++) {
//					//1460	
//					
//					String Paquete= i+"_";
//					
//					for (int j = 0; j < 1460; j++) {
//						
//					}
//					
//					out.println(i+"_"+arregloByte[i]);
//
//					
////					byte[] bb = {(byte) arregloByte[i]};
////					System.out.println("aaaa:"+bb[0]);
////					
////					String ss = new String(bb, StandardCharsets.US_ASCII);
////					System.out.println("bbbb:"+ss);
//				}
//				out.println("terminoEnvio");

	            
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

				numeroConexiones =1;// Integer.parseInt(scaner.nextLine());

				System.out.println(
						"Indique que archivo quiere enviar (ESCRIBA EL NUMERO 1,2,3) \n"+
								"1: archivo de 100 MB \n"+
								"2: Archivo de 250 MB \n"+
								"3: Otro (pasar ruta por parametro) \n"
						);

				String Archivo = "1";//scaner.nextLine();

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
		
			System.out.println(path);
			
			
			String tipodeArchivo = "";
				
			
			//FileTypeDetector ad = new	FileTypeDetector();
			
			String[] recibido =  path.split("/"); 
			System.out.println(recibido.toString() );
			System.out.println(recibido[0].toString());
			System.out.println(recibido[1].toString());
			
			
			
			byte[] arregloBits = getArray(fichero);
			
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
				System.out.println("Cliente conectado: "+ clienteSC.getInetAddress().getHostAddress());
				
				PrintWriter out = new PrintWriter( clienteSC.getOutputStream(), true); 
				BufferedReader in = new BufferedReader(new InputStreamReader( clienteSC.getInputStream())); 
				
				String  idCliente = in.readLine();
				System.out.println(idCliente);
				
				String  estadoActual = in.readLine();
				System.out.println(idCliente+estadoActual);
				if(estadoActual.equals("Listo")){
					estado++;					
				}
				
				Peticion threadCliente = new Peticion(clienteSC,idCliente,arregloBits, log ,hash,path,numeroConexiones); //hash tambien envio, log 
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


