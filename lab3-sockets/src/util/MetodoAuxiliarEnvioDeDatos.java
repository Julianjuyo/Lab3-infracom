package util;

import java.io.Serializable;

public class MetodoAuxiliarEnvioDeDatos implements Serializable
{
   // Nombre del fichero que se transmite. Por defecto
    public String nombreFichero="";

    // Si este es el ultimo mensaje del fichero en cuestion o hay mas despues
    public boolean ultimoMensaje=true;

    // Cuantos bytes son validos en el array de bytes
    public int bytesValidos=0;

    //Array con bytes leidos del fichero
    public byte[] contenidoFichero = new byte[LONGITUD_MAXIMA];
    
    //Longitud de bytes por paquete
    public final static int LONGITUD_MAXIMA=1460;
}
