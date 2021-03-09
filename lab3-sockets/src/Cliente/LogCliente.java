package Cliente;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class LogCliente {

    public static void escribirLog(String rutaArchivo, String mensaje) {

        Logger logger = Logger.getLogger("MyLog");
        FileHandler fh;

        try {

            fh = new FileHandler(rutaArchivo,true);
            logger.addHandler(fh);

            SimpleFormatter formatter = new SimpleFormatter();
            
            fh.setFormatter(formatter);

            logger.info(mensaje);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public static void main(String... args){
    	
        for(int i=1;i<6;i++){
        	
            escribirLog("C:/rutaLog/archivo.log", "MensajePrueba"+i);
        }
    }
}
