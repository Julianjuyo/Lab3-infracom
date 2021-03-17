package servidores;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


import java.security.*;
import java.math.BigInteger; 
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException; 




public class Servidor {

	private final static String RUTA1="H:/Desktop/Laboratorio3TCP.pdf";
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
		private String  idCliente;
		private final byte[] arregloByte;
		private final String log;
		private final String hash;
		private final long tamanoArchivo;
		
//		private static int numeroDeClientesActuales = 0;
//		private static int NUMERO_CONEXIONES_TOTALES = 0;


		public Peticion(Socket sc, String pidCliente ,byte[] parregloBits, String plog,String phash ,long pTamanoArchivo ) {
			this.clienteSC= sc;
			this.idCliente=pidCliente;
			this.arregloByte= parregloBits;
			this.log= plog;
			this.hash= phash;
			this.tamanoArchivo= pTamanoArchivo;
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

			
				System.out.println("Comenzo Thread:"+idCliente);
						
				//Se enviua el hash del archvio
				out.println(hash);
				System.out.println("envio el hash ");
				
				//Se envia el tamano del archivo
				out.println(tamanoArchivo);
				System.out.println("envio el tamanoArchivo");
				
				//Se comienza el envio del rchivo
				System.out.println("Comenzo a enviar archivo ");
				for (int i = 0; i < arregloByte.length; i++) {
					//1460	 
					out.println(i+"_"+arregloByte[i]);
//					byte[] bb = {(byte) arregloByte[i]};
//					System.out.println("aaaa:"+bb[0]);
//					
//					String ss = new String(bb, StandardCharsets.US_ASCII);
//					System.out.println("bbbb:"+ss);
				}
				out.println("termino Envio Del archivo");

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

				numeroConexiones = Integer.parseInt(scaner.nextLine());

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

			
			long tamanoArchivo = fichero.length();
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
				
				Peticion threadCliente = new Peticion(clienteSC,idCliente,arregloBits, log ,hash,tamanoArchivo); //hash tambien envio, log 
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


