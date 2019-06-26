package chat1;

import java.awt.Button;
import java.awt.TextArea;
import java.awt.TextField;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Severex01 extends Thread{
	JFrame jf = new JFrame();
	TextField tf = new TextField(40);
	Button input = new Button("입력");
	TextArea ta = new TextArea(100,100);
	Button exit = new Button("종료");
	BufferedReader br = null; 
	BufferedWriter bw = null;
	Socket s = null; 
	ServerSocket ss = null;
	BufferedReader stin = null;
	
	@Override
	public void run() {
		try {
		
			ss = new ServerSocket(9000);
			s = ss.accept();
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			stin = new BufferedReader(new InputStreamReader(System.in));
			bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			
			
			
			jf.setTitle("ㅇㅇ");
			jf.setBounds(500,500,300,300);
			jf.setVisible(true);
			JPanel p = new JPanel();
			p.add(tf);
			p.add(ta);
			p.add(input);
			p.add("South" , p);
			String inputMessage;
			
			while(true) { 
				inputMessage = br.readLine();
				 if(inputMessage.equalsIgnoreCase("exit")) break;
				 ta.append(inputMessage);
				 String outputMessage = stin.readLine();
				 
				 
			}
			
			
			
		} catch (Exception e) {
		}
	}

	
	
	public static void main(String[] args) {
	}
}
