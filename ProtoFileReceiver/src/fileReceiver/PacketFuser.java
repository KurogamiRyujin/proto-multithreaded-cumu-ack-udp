package fileReceiver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class PacketFuser {
	ArrayList<FilePacket> packets;
	protected byte[] dataBytes;
	
	public PacketFuser(){
		packets = new ArrayList<FilePacket>();
		
	}
	
	public void sortPackets(){
		ArrayList<FilePacket> temp = new ArrayList<FilePacket>();
		for(int i = 0; i < packets.size(); i++){
			for(int j = 0; j < packets.size(); j++){
				if(packets.get(j).getHeader().getPcktNumInt() == i)
					temp.add(packets.get(j));
			}
		}
		this.packets = temp;
	}
	
	public void receiveFile() throws Exception{
		PacketReceiver receiver = new PacketReceiver();
		boolean receiving = true;
		int packetSize = 0;
		DatagramSocket localSocket = new DatagramSocket(4000);
		InetAddress senderAdd;
		
		
		do {
			byte[] byteSize = new byte[4];
			
			DatagramPacket receivePacketSize = new DatagramPacket(byteSize,byteSize.length);
			localSocket.receive(receivePacketSize);
			System.out.println(new String(receivePacketSize.getData(),PacketReceiver.ENCODING));
			int nPacketSize = java.nio.ByteBuffer.wrap(byteSize).getInt();
			
			
			System.out.println("Receiving file...");
			senderAdd = receiver.receivePacket(nPacketSize);
			this.packets.add(receiver.getFilePacket());
			System.out.println("Packet Count: " + this.packets.size());
			packetSize += receiver.getChunk().length;
			System.out.println("Receiver Chunk byte Length: " + receiver.getChunk().length);
			System.out.println("PacketSize: " + packetSize);
			System.out.println("Receiver File Size" + receiver.getFileSize());
			if(packetSize >= receiver.getFileSize()){
				packetSize = 0;
				receiving = false;
			}
		}while(receiving);
		
		byte[] receivedPacketNum = ByteBuffer.allocate(4).putInt(-1).array();
		
		DatagramPacket sendPacket = new DatagramPacket(receivedPacketNum,receivedPacketNum.length,senderAdd,2011);
		localSocket.send(sendPacket);
		
		this.sortPackets();
		this.setDataBytes(fuseChunks());
		
//		byte[] receivedSize = ByteBuffer.allocate(4).putInt(this.getDatabytes().length).array();
//		
//		DatagramPacket sendPacket = new DatagramPacket(receivedSize,receivedSize.length,senderAddress,2011);
//		localSocket.send(sendPacket);
		
		this.fileMaker(this.getDatabytes());
		
		localSocket.close();
	}
	
	public byte[] fuseChunks() throws Exception{
		byte[] allChunks = null;
		System.out.println("Fusing Chunks");
//		for(int i = 0; i < packets.size(); i++){
//			allChunks = allChunks + packets.get(i).getChunk();
//			System.out.println("All Chunks Length: " + allChunks.length());
//		}
		
		int temp = 0;
		for(int i = 0; i < this.packets.size(); i++){
			temp += this.packets.get(i).getChunk().length;
		}
		
		allChunks = new byte[temp];
		
		int j = 0;
		for(int i = 0; i < this.packets.size(); i++){
			for(int k = 0; k < this.packets.get(i).getChunk().length; k++){
				allChunks[j] = this.packets.get(i).getChunk()[k];
				j++;
			}
		}
		
		return allChunks;
	}
	
	public void fileMaker(byte[] fileBytes) throws Exception{
		FileOutputStream fos = new FileOutputStream(new File("C:/Users/Jarl Brent/NETWORK SAMPLES/" + this.packets.get(0).getHeader().getFileName()));
		
		
		System.out.println("Writing File " + this.packets.get(0).getHeader().getFileName());
		fos.write(fileBytes);
		
		fos.close();
	}
	
	public void setPackets(ArrayList<FilePacket> packets){
		this.packets = packets;
	}
	
	public ArrayList<FilePacket> getPackets(){
		return this.packets;
	}
	
	public void setDataBytes(byte[] dataBytes){
		this.dataBytes = dataBytes;
	}
	
	public byte[] getDatabytes(){
		return this.dataBytes;
	}
}
