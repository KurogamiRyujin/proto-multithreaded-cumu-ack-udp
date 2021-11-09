package fileReceiver;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.SwingWorker;

public class FileReceiverDriver extends JFrame{
	
	public static void main(String[] args) throws Exception{
		PacketFuser pf = new PacketFuser();
		
		pf.receiveFile();
	}
}
