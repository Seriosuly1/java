package chat1;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;




class UserInfo{ 
	Socket serverSocket; 
	
	UserInfo(Socket serverSocket){
		this.serverSocket = serverSocket;
	}
}


class UserThread extends Thread{ 
	Socket serverSocket; 
	List<UserInfo> list = new ArrayList<UserInfo>();
	
	UserThread(Socket serverSocket , List list){
		this.serverSocket = serverSocket;
		this.list = list; 
		
	}


	class ConnectThread extends Thread {
	    ServerSocket mainServerSocket = null;
	    List<UserInfo> list = new ArrayList<UserInfo>();
	    
	    ConnectThread(ServerSocket mainServerSocket) {
	        this.mainServerSocket = mainServerSocket;
	    }
	 


	
@Override
public void run() {
	try {
		while(true) {
		InputStream is = serverSocket.getInputStream();
		byte[] bt = new byte[256];
        int size = is.read(bt);
        
        if(size == -1) {
        	break;
        }
        String sendMessage = new String(bt, 0 , size);
        System.out.println(sendMessage);
        for(int i = 0; i <list.size(); i++) {
        	if(list.get(i).serverSocket != serverSocket) {
        		OutputStream os = list.get(i).serverSocket.getOutputStream();
        		os.write(bt);
        	
        	}
        }
        
		}
	} catch (Exception e) {
	}
}



public class SocketChat {
    public void main(String[] args) {        
        System.out.println("-SERVER 시작");
        
        try {
            ServerSocket mainServerSocket = null;
            mainServerSocket = new ServerSocket();
            mainServerSocket.bind(new InetSocketAddress(InetAddress.getLocalHost(), 4040));
            
            ConnectThread connectThread = new ConnectThread(mainServerSocket);
            connectThread.start();
            
            Scanner input = new Scanner(System.in);
            int temp = input.nextInt();
        } catch (Exception e) {}
        
        System.out.println("-SERVER 종료");
    }
}
	}
}

