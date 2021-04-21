/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClienteCopy;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 *
 * @author SHUBHAM
 */
public class Server {

	private final static int PUERTO =4445;
	public final static int LONGITUD_MAXIMA=60000;//1460;


	public static void main(String[] args) throws SocketException, IOException {

		// Se realiza la conexion TCP
		DatagramSocket serverSocket = new DatagramSocket(PUERTO);

		//Se crear dos buffer para recibir y enviar datos
		byte[] bufferRecibir = new byte[LONGITUD_MAXIMA];
		byte[] bufferEnviar  = new byte[LONGITUD_MAXIMA];


		System.out.print("Establecer conexion  SERVIDOR "+"\n");

		// recibe info del cliente que se conecta
		DatagramPacket recvdpkt = new DatagramPacket(bufferRecibir, bufferRecibir.length);
		serverSocket.receive(recvdpkt);
		InetAddress IP = recvdpkt.getAddress();
		int portno = recvdpkt.getPort();
		String clientdata = new String(recvdpkt.getData());
		System.out.println("Cliente con ID: "+ clientdata);


		System.out.print("Comienza transferencia de Archivo Servidor "+"\n");
		
		
		//File fichero = new File("/Users/julianoliveros/documento.pdf");
		File fichero = new File("/Users/julianoliveros/100MB.zip");
		FileInputStream fis;


		fis = new FileInputStream(fichero);
		BufferedInputStream bis = new BufferedInputStream(fis);
		int data;
		
		System.out.println("tamano archivo: "+fichero.length());

		System.out.print("Entro a while"+"\n");
		
		//Se comienza a enviar el archvio 
		int a=0;
		while(true) 
		{
			data = bis.read(bufferEnviar);
			
			System.out.print("data "+a+" : "+data+"\n");
			a++;
			
			if(data != -1)
			{
				if(data==LONGITUD_MAXIMA) {
					//System.out.println("1 if"+"\n");
					DatagramPacket sendPacket = new DatagramPacket(bufferEnviar, bufferEnviar.length, IP,portno);
					serverSocket.send(sendPacket);
					
//					System.out.println("BUUFER COMIENZO : "+"\n");
//					for (int i = 0; i < bufferEnviar.length; i++) {
//						System.out.println(bufferEnviar[i]);
//					}
//					System.out.println("BUUFER FINAL : "+"\n");
				}
				else {
//					System.out.println("2 if"+"\n");
					
					byte[] newBuffer = new byte[data];
					
					for (int i = 0; i < newBuffer.length; i++) {
						newBuffer[i]= bufferEnviar[i];
					}
					
//					System.out.println("BUUFER COMIENZO : "+"\n");
//					for (int i = 0; i < newBuffer.length; i++) {
//						System.out.println(newBuffer[i]);
//					}
//					System.out.println("BUUFER FINAL : "+"\n");
					
					
					DatagramPacket sendPacket = new DatagramPacket(newBuffer, newBuffer.length, IP,portno);
					serverSocket.send(sendPacket); 
				}
			}
			else
			{
				System.out.println("Se termino de enviar el archivo");
				break;

			}
		}
		
		System.out.println("el a es"+a);
		
		System.out.println("salio servidor");
		serverSocket.close();

	}
	
}

