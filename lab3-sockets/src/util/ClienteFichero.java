/**
 * Javier Abell�n. 18 Mar 2006
 * 
 * Programa de ejemplo de como transmitir un fichero por un socket.
 * Esta es el main con el cliente, que piede un fichero, lo muestra en
 * pantalla y lo escribe cambiando el nombre.
 */

package util;

import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Pide un fichero al ServidorFichero, lo escribe en pantalla cuando lo recibe y
 * lo guarda en disco a�adiendo "_copia" al final del nombre del fichero.
 * 
 * @author Javier Abell�n
 */
public class ClienteFichero
{

    /**
     * Main del Cliente
     * 
     * @param args
     *            de la l�nea de comandos. Se ignora.
     */
    public static void main(String[] args)
    {
        // Se crea el cliente y se le manda pedir el fichero.
        ClienteFichero cf = new ClienteFichero();
        cf.pide("/Users/julianoliveros/Public/matricula.pdf", "localhost", 35557);
    }

    /**
     * Establece comunicaci�n con el servidor en el puerto indicado. Pide el
     * fichero. Cuando llega, lo escribe en pantalla y en disco duro.
     * 
     * @param fichero
     *            path completo del fichero que se quiere
     * @param servidor
     *            host donde est� el servidor
     * @param puerto
     *            Puerto de conexi�n
     */
    public void pide(String fichero, String servidor, int puerto)
    {
        try
        {
            // Se abre el socket.
            Socket socket = new Socket(servidor, puerto);
            
            long startTime = System.currentTimeMillis();
          
            

            // Se env�a un mensaje de petici�n de fichero.
            ObjectOutputStream oos = new ObjectOutputStream(socket
                    .getOutputStream());
            MensajeDameFichero mensaje = new MensajeDameFichero();
            mensaje.nombreFichero = fichero;
            oos.writeObject(mensaje);

            // Se abre un fichero para empezar a copiar lo que se reciba.
            FileOutputStream fos = new FileOutputStream(mensaje.nombreFichero
                    + "_copia");

            // Se crea un ObjectInputStream del socket para leer los mensajes
            // que contienen el fichero.
            ObjectInputStream ois = new ObjectInputStream(socket
                    .getInputStream());
            MensajeTomaFichero mensajeRecibido;
            Object mensajeAux;
            do
            {
                // Se lee el mensaje en una variabla auxiliar
                mensajeAux = ois.readObject();
                
                // Si es del tipo esperado, se trata
                if (mensajeAux instanceof MensajeTomaFichero)
                {
                    mensajeRecibido = (MensajeTomaFichero) mensajeAux;
                    // Se escribe en pantalla y en el fichero
//                    System.out.print(new String(
//                            mensajeRecibido.contenidoFichero, 0,
//                            mensajeRecibido.bytesValidos));
                    
                    fos.write(mensajeRecibido.contenidoFichero, 0,
                            mensajeRecibido.bytesValidos);
                } else
                {
                    // Si no es del tipo esperado, se marca error y se termina
                    // el bucle
                    System.err.println("Mensaje no esperado "
                            + mensajeAux.getClass().getName());
                    break;
                }
            } while (!mensajeRecibido.ultimoMensaje);
            
            long endTime = System.currentTimeMillis() - startTime;
            System.out.println("tarde:"+endTime);
            // Se cierra socket y fichero
            fos.close();
            ois.close();
            socket.close();

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
