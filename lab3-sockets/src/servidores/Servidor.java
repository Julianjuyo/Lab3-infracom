//package servidores;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor {



	/**
	 * Clase que crear un thread por cada peticion
	 * @author julianoliveros
	 *
	 */
	private static class Peticion extends Thread{

		private final Socket clienteSC;

		public Peticion(Socket sc) {
			this.clienteSC=sc;
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



				Scanner scaner = new Scanner(System.in); 
				
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

		//        DataInputStream in = null;
		//        DataOutputStream out = null;

		int numeroDeClientes=0;
		final int PUERTO =60000;


		try {

			//Creamos el socket del servidor
			servidor = new ServerSocket(PUERTO);

			//Permite que se se pueda utilizar el mismo puerto
			servidor.setReuseAddress(true);
			System.out.println("Servidor iniciado");


			//Siempre estara escuchando peticiones
			while (true) {


				//Espero a que un cliente se conecte
				Socket clienteSC = servidor.accept();

				System.out.println("Cliente conectado"+ clienteSC.getInetAddress().getHostAddress());

				Peticion threadCliente = new Peticion(clienteSC); 
				threadCliente.start();

				numeroDeClientes++;

			}
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


