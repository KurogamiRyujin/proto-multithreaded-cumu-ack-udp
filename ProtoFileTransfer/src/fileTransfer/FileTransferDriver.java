package fileTransfer;

import java.io.File;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class FileTransferDriver {
	public static void main(String[] args) throws Exception{
		FileSender fileSender = new FileSender();
		DatagramSocket clientSocket = new DatagramSocket(1234);
		InetAddress IPAddress = InetAddress.getByName("localhost");
		File file = new File("C:/Users/Jarl Brent/Pictures/sidecharacters.jpg");
		
		PacketsHolder packetHolder = new PacketsHolder(file,1500);
		fileSender.sendFile(packetHolder, clientSocket, IPAddress);
	}
}
