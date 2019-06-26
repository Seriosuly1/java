package chat1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import sun.util.locale.StringTokenIterator;

public class ServerChat extends JFrame implements ActionListener {
	private JPanel contentPane;
	private JTextField porttextfield;
	private JTextArea textArea = new JTextArea();
	private JButton startbtn = new JButton("서버실행");
	private JButton exitbtn = new JButton("서버종료");
	//net work
	private ServerSocket ss;
	private Socket s;
	private int port;
	private Vector uservc = new Vector();
	private StringTokenizer st; 
	

	ServerChat() { // 생성자
		init();// 화면 생성 메소드
		start();

	}

	private void start() {
		startbtn.addActionListener(this);
		exitbtn.addActionListener(this);

	}

	public void Server_start() {
		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (ss != null) {
			connect();

		}

	}

	private void connect() {

		Thread th = new Thread(new Runnable() {// inner class

			@Override
			public void run() { // 스레드 구동부
				while(true) {
					
				try {
					textArea.append("대기중\n");
					s = ss.accept();
					textArea.append("접속완료");
					
					UserInfo user = new UserInfo(s);
					
					user.start();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				}//while end 

			}
		});
		th.start();
	}

	private void init() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(57, 30, 314, 172);
		contentPane.add(scrollPane);

		scrollPane.setViewportView(textArea);

		JLabel lblNewLabel = new JLabel("포트번호");
		lblNewLabel.setBounds(12, 202, 71, 49);
		contentPane.add(lblNewLabel);
 
		porttextfield = new JTextField();
		porttextfield.setBounds(98, 216, 174, 35);
		contentPane.add(porttextfield);
		porttextfield.setColumns(10);

		startbtn.setBounds(284, 215, 97, 23);
		contentPane.add(startbtn);

		exitbtn.setBounds(284, 238, 97, 23);
		contentPane.add(exitbtn);

		this.setVisible(true);// 트루일경우 화면에나옴

	}

	public static void main(String[] args) {
		new ServerChat();

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == startbtn) {
			System.out.println("스타트 버튼 클릭");
			port = Integer.parseInt(porttextfield.getText().trim());
			Server_start();// 버튼누르면 서버 시작

		} else if (e.getSource() == exitbtn) {
			System.out.println("종료 버튼 클릭");
		}
	}//이벤트
	
	class UserInfo extends Thread{ 
		private InputStream is;
		private OutputStream os;
		private DataInputStream dis;
		private DataOutputStream dos;
		
		private Socket user_socket;
		private String Nickname = "";
		
		public UserInfo(Socket soc) { //생성자 함수 
			this.user_socket = soc;
			
			UserNetwork();
		}
		
		public void UserNetwork(){ 
			try {
				is = user_socket.getInputStream();
				dis = new DataInputStream(is);
				
				os = user_socket.getOutputStream();
				dos = new DataOutputStream(os);
				
				
				Nickname = dis.readUTF();
				textArea.append(Nickname + " 사용자 접속 ");
				
				//새로들어온 사용자 추가 
				Broad("NewUser/"+ Nickname);
				//자신에게 받아오기 .
				for(int i = 0; i < uservc.size(); i++) {
					UserInfo u = (UserInfo)uservc.elementAt(i);
					sendM("OldUser/" + u.Nickname);
				}
				
				uservc.add(this);
				
				Broad("user_list_update/" );
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		public void Broad(String str) {
			for(int i = 0; i< uservc.size(); i++ ) {// 전체 사용자에게 메세지 
				UserInfo u = (UserInfo)uservc.elementAt(i);
				u.sendM(str);
				
				//사용자들이 들어있으면 백터에서 사용자를 하나 꺼내 userinfo형으로 변환하여 한명을 꺼냄 
			} 
			
		}
		public void run() { //thread 에서 처리 
			while(true) {
				try {
					String msg = dis.readUTF(); //메세지수신
					textArea.append(Nickname + "으로 부터 들어온 메세지" + msg);
					Inmsg(msg);
				} catch (IOException e) {
					e.printStackTrace();
				} //메세지 수신 
				
				
			}//while end 
		}
		
		public void Inmsg(String str) {
			st = new StringTokenizer(str, "/");
			
			String protocol =st.nextToken();
			String message = st.nextToken();
			System.out.println("프로토콜   : " + protocol);
			System.out.println("메세지   : " + message);
			
			
			if(protocol.equals("Note"))
			{
			
					String note = st.nextToken();
				
				System.out.println("받는 사람  : " + message);
				System.out.println("보낼 내용  : "  +  note);
				for(int i = 0; i <uservc.size(); i ++) {
					UserInfo u = (UserInfo)uservc.elementAt(i);
					if(u.Nickname.equals(message)) {
						u.sendM("Note" + Nickname+"@"+note);
					}
				}
			
			}
			
			
		}
		
		private void sendM(String str) {
			try {
				dos.writeUTF(str);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		
	}

}
