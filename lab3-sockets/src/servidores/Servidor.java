//package servidores;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor {
	
	
	
	/**
	 * Clase que crear un thread por cada peticion
	 * @author julianoliveros
	 *
	 */
	
	/*
	private static class Peticion extends Thread{
		
		private final Socket clienteSC;
		
		public Peticion(Socket sc) {
			this.clienteSC=sc;
		}
		
		public void run() 
        {
            DataInputStream in = null;
            DataOutputStream out = null;
            
            try { 
            	
                //recino de datos
                in = new DataInputStream(clienteSC.getInputStream());
                
                //envio de datos
                out = new DataOutputStream(clienteSC.getOutputStream());

                //Leo el mensaje que me envia
                String mensaje = in.readUTF();

                System.out.println(mensaje);

                //Le envio un mensaje
                out.writeUTF("¡Hola mundo desde el servidor!");

                clienteSC.close();
                System.out.println("Cliente desconectado");
                                  
                  // get the outputstream of client 
            	
                out = new PrintWriter( 
                		clienteSC.getOutputStream(), true); 
  
                  // get the inputstream of client 
                
                in = new BufferedReader( 
                    new InputStreamReader( 
                    		clienteSC.getInputStream())); 
  
                String line; 
                
                while ((line = in.readLine()) != null) { 
  
                    // writing the received message from 
                    // client 
                    System.out.printf( 
                        " Sent from the client: %s\n", 
                        line); 
                    out.println(line);    
                } 
                
                
                
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
		*/
		
		
	

    public static void main(String[] args) {

        ServerSocket servidor = null;
        
        DataInputStream in = null;
        DataOutputStream out = null;
        Socket clienteSC=null;

        int numeroDeClientes=0;
        final int PUERTO =60000;


        try {
        	
            //Creamos el socket del servidor
            servidor = new ServerSocket(PUERTO);
            
            //Permite que se se pueda utilizar el mismo puerto
            //servidor.setReuseAddress(true);
            System.out.println("Servidor iniciado");

            
            //Siempre estara escuchando peticiones
            while (true) {

            	
               //Espero a que un cliente se conecte
              clienteSC = servidor.accept();
              
              System.out.println("Cliente conectado"+ clienteSC.getInetAddress().getHostAddress());
                
                
              //recino de datos
              in = new DataInputStream(clienteSC.getInputStream());
              
              //envio de datos
              out = new DataOutputStream(clienteSC.getOutputStream());

              //Leo el mensaje que me envia
              String mensaje = in.readUTF();

              System.out.println(mensaje);

              //Le envio un mensaje
              out.writeUTF("¡Hola mundo desde el servidor!");

              clienteSC.close();
              System.out.println("Cliente desconectado");
                
                
//                Peticion threadCliente = new Peticion(sc); 
//                threadCliente.start();
                
                numeroDeClientes++;
               
            }

        }
        catch (IOException ex)
            {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
    }

}


