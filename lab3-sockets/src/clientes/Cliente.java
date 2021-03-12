package clientes;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Cliente {

	public static void main(String[] args) {

		//Host del servidor
		final String HOST = "localhost";

		//Puerto del servidor
		final int PUERTO =60000;

		PrintWriter out = null; 
		BufferedReader in = null; 

		try {
			Socket sc = new Socket(HOST, PUERTO);

			// Escribir a el servidor
			out = new PrintWriter( sc.getOutputStream(), true); 

			// Leer del servidor 
			in = new BufferedReader(new InputStreamReader( sc.getInputStream())); 


			// Intercambio de texto
			Scanner scaner = new Scanner(System.in); 
			String line = null; 

			while (!"exit".equalsIgnoreCase(line)) { 

				// Leer por consola
				line = scaner.nextLine(); 

				// Enviar de usuario a servidor
				out.println(line); 
				out.flush(); 

				// displaying server reply 
				System.out.println("Server replied " + in.readLine()); 
			} 


			// cerrar socket
			sc.close(); 


		} catch (IOException ex) {

			Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

}


