package fileReceiver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class PacketReceiver {
	protected final static String ENCODING = "ISO-8859-1";
	
	protected DatagramSocket clientSocket;
	protected String header;
	protected String filename;
	protected int packetNum;
	protected int fileSize;
	protected byte[] chunk;
	
	protected FilePacket filePacket;
	
	public PacketReceiver() throws Exception{
		clientSocket = new DatagramSocket(9876);
	}
	
	public InetAddress receivePacket(int packetSize) throws Exception{
		byte[] receiveData = new byte[packetSize];
		System.out.println("Packet Size: " + packetSize);
//		int packetLength;
		
		
//		do{
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			this.clientSocket.receive(receivePacket);
//			byte[] data = new String(receivePacket.getData(), ENCODING);
			System.out.println("Newly Received Packet: " + receivePacket.getLength());
			System.out.println("Data Content: " + receivePacket.getData().toString());
			System.out.println("Data length: " + receivePacket.getLength());
			
			this.chunk = extractChunk(receivePacket.getData());
//			this.extractHeader(receivePacket.getData());
			
//			packetLength = receivePacket.getLength();
			
			byte[] receivedPacketNum = ByteBuffer.allocate(4).putInt(this.packetNum).array();
			
			DatagramPacket sendPacket = new DatagramPacket(receivedPacketNum,receivedPacketNum.length,receivePacket.getAddress(),2011);
			clientSocket.send(sendPacket);
			
//		} while(packetLength < packetSize);
		
		this.filePacket = new FilePacket(new Header(filename,packetNum,fileSize),chunk);
		
		return receivePacket.getAddress();
	}
	
	public byte[] extractChunk(byte[] rawData) throws Exception{
//		return data.split("\\+\\+");
		byte[] chunk = new byte[1500];
		byte[] header = new byte[rawData.length-1500];
		int j = 1;
		for(int i = rawData.length-1; i >= 0; i--){
			if(j <= 1500){
				chunk[1500-j] = rawData[rawData.length-j];
			}
			else{
				header[rawData.length-j] = rawData[rawData.length-j];
			}
			j++;
		}
		
		String headerPart = new String(header,"ISO-8859-1");
		System.out.println("Header: " + headerPart);
		String[] arrHeader = new String[3];
		arrHeader = headerPart.split("\\+\\+");
		
		this.filename = arrHeader[0];
		this.packetNum = Integer.parseInt(arrHeader[1]);
		this.fileSize = Integer.parseInt(arrHeader[2]);
		
		return chunk;
	}
	
	public void extractHeader(byte[] rawData) throws Exception{
		byte[] header = new byte[rawData.length - 1500];
		for(int i = 0; i < rawData.length-1500;i++){
			header[i] = rawData[i];
		}
		
		String headerPart = new String(header,"ISO-8859-1");
		System.out.println("Header: " + headerPart);
		String[] arrHeader = new String[3];
		arrHeader = headerPart.split("\\+\\+");
		
		this.filename = arrHeader[0];
		this.packetNum = Integer.parseInt(arrHeader[1]);
		this.fileSize = Integer.parseInt(arrHeader[2]);
	}

	public DatagramSocket getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(DatagramSocket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getPacketNum() {
		return packetNum;
	}

	public void setPacketNum(int packetNum) {
		this.packetNum = packetNum;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public byte[] getChunk() {
		return chunk;
	}

	public void setChunk(byte[] chunk) {
		this.chunk = chunk;
	}
	
	public FilePacket getFilePacket(){
		return this.filePacket;
	}
}
