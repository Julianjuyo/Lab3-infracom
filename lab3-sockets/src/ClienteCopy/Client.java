/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClienteCopy;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 *
 * @author SHUBHAM
 */
public class Client {

	private final static int PUERTO =4445;
	public final static int LONGITUD_MAXIMA=60000;//1460;


	public static void main(String[] args) throws SocketException, IOException {

		//Se establece la ip de la maquina
		InetAddress IP = InetAddress.getByName("127.0.0.1");


		//Se crea una conexion UDP
		DatagramSocket clientSocket = new DatagramSocket();

		
		
		byte[] bufferEnviar = new byte[LONGITUD_MAXIMA];
		byte[] bufferRecibir = new byte[LONGITUD_MAXIMA];


		System.out.print("Establecer conexion CLIENTE "+"\n");

		String clientData = "1";
		bufferEnviar = clientData.getBytes();    
		DatagramPacket sendPacket = new DatagramPacket(bufferEnviar, bufferEnviar.length, IP, PUERTO);
		clientSocket.send(sendPacket);

		long startTime = System.currentTimeMillis();

		System.out.print("Comienza transferencia de Archivo Cliente"+"\n");

		//File output = new File("/Users/julianoliveros/documentoCOPIA.pdf");
		File output = new File("/Users/julianoliveros/100MBCOPY.zip");
		
		FileOutputStream fos = new FileOutputStream(output);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		int data;

		System.out.print("Entro a while"+"\n");
		int a=0;
		while(true) 
		{
			DatagramPacket recvdpkt = new DatagramPacket(bufferRecibir, bufferRecibir.length);
			clientSocket.receive(recvdpkt);
			data = recvdpkt.getLength();
			

//			String clientdata = new String(recvdpkt.getData());
//			System.out.println("Recibi los bytes: "+ clientdata+"\n");
			System.out.print("data "+a+" : "+data+"\n");
			a++;

			

			if(data != -1)
			{
				if(data==LONGITUD_MAXIMA) {
					
//					System.out.println("BUUFER COMIENZO : "+"\n");
//					for (int i = 0; i < bufferRecibir.length; i++) {
//						System.out.println(bufferRecibir[i]);
//					}
//					System.out.println("BUUFER FINAL : "+"\n");
//					
//					System.out.println("1 if "+"\n");
					bos.write(bufferRecibir, 0, LONGITUD_MAXIMA);
				}
				else {
					
					byte[] newBuffer = new byte[data];
					
					for (int i = 0; i < newBuffer.length; i++) {
						newBuffer[i]= bufferRecibir[i];
					}
					
//					System.out.println("BUUFER COMIENZO : ");
//					for (int i = 0; i < newBuffer.length; i++) {
//						System.out.println(newBuffer[i]);
//					}
//					System.out.println("BUUFER FINAL : "+"\n");			
//					System.out.println("2 if "+"\n");
					
					bos.write(newBuffer, 0, data);
					
					System.out.println("Se termino de enviar el archivo");
					bos.close();
					break;
				}
			}
		}
		
		System.out.println("el a es"+a);
		//File archivo = new File("/Users/julianoliveros/documentoCOPIA.pdf");
		File archivo = new File("/Users/julianoliveros/100MBCOPY.zip");
		System.out.println("tamano archivo recibido: "+archivo.length());

		
		long endTime = System.currentTimeMillis() - startTime;
		System.out.println("Se tardo:"+endTime+" milisegundos");
		
		System.out.println("salio cliente");
		clientSocket.close();
	}

}
