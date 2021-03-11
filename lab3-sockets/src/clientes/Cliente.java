//package clientes;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Cliente {
	
//	private int idCliente;
//	
//	
//	public Cliente(int id) {
//		this.idCliente=id;
//	}
	
	
	
	
    public static void main(String[] args) {

        //Host del servidor
        final String HOST = "localhost";

        //Puerto del servidor
        final int PUERTO = 50000;
                
        DataInputStream in;
        DataOutputStream out;

        try {
        	
            //Creo el socket para conectarme con el cliente
            Socket sc = new Socket(HOST, PUERTO);


            //recibe datos
            in = new DataInputStream(sc.getInputStream());
            
            //envio de datos
            out = new DataOutputStream(sc.getOutputStream());


            //Envio un mensaje al cliente
            out.writeUTF("¡Hola mundo desde el cliente!");


            //Recibo el mensaje del servidor
            String mensaje = in.readUTF();

            //imprimo mensaje del servidor
            System.out.println(mensaje);

            
            //cierro la conexion
            sc.close();

        } catch (IOException ex) {
        	
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}


