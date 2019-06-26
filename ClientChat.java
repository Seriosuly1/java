package chat1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ClientChat extends JFrame implements ActionListener {
	// login gui 변수
	private JFrame log = new JFrame();
	private JPanel logPane1;
	private JTextField ipserver;
	private JTextField portnum;
	private JTextField userid;
	JButton logbtn = new JButton("접속");
	// 클라이언트
	private JPanel contentPane;
	private JTextField msgtf;
	private JButton sendnote = new JButton("쪽지보내기");
	private JLabel lblNewLabel_1 = new JLabel("채팅방목록");
	private JButton joinroom = new JButton("채팅방참여");
	private JButton createroom = new JButton("방만들기");
	private JButton send = new JButton("전송");
	private JList userlist = new JList(); // 접속자 리스트
	private JList roomlist = new JList(); // 방목록 리스트
	private JTextArea textArea = new JTextArea();

	private Socket socket;
	String ip = "";
	int port;
	String id;
	InputStream is;
	OutputStream os;
	DataInputStream dis;
	DataOutputStream dos;

	// 그외 변수
	Vector useral = new Vector();
	Vector rommal = new Vector();
	StringTokenizer st;

	ClientChat() {
		Login_init();
		Main_init();
		start();
	}

	public void start() {
		logbtn.addActionListener(this);
		sendnote.addActionListener(this);
		joinroom.addActionListener(this);
		createroom.addActionListener(this);
		send.addActionListener(this);
	}

	public void Main_init() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 745, 518);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("전체접속자");
		lblNewLabel.setBounds(33, 28, 57, 15);
		contentPane.add(lblNewLabel);

		userlist.setBounds(12, 59, 87, 98);
		contentPane.add(userlist);
		userlist.setListData(useral);;
		
		sendnote.setBounds(12, 188, 97, 23);
		contentPane.add(sendnote);

		lblNewLabel_1.setBounds(12, 238, 97, 15);
		contentPane.add(lblNewLabel_1);

		roomlist.setBounds(12, 278, 97, 65);
		contentPane.add(roomlist);
		roomlist.setListData(rommal);
		joinroom.setBounds(12, 353, 97, 23);
		contentPane.add(joinroom);

		createroom.setBounds(12, 386, 97, 23);
		contentPane.add(createroom);

		textArea.setBounds(111, 36, 540, 373);
		contentPane.add(textArea);

		msgtf = new JTextField();
		msgtf.setBounds(92, 419, 473, 50);
		contentPane.add(msgtf);
		msgtf.setColumns(10);

		send.setBounds(577, 424, 113, 38);
		contentPane.add(send);

		this.setVisible(true);
	}

	public void Login_init() {
		log.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		log.setBounds(100, 100, 279, 497);
		logPane1 = new JPanel();
		logPane1.setBorder(new EmptyBorder(5, 5, 5, 5));
		log.setContentPane(logPane1);
		logPane1.setLayout(null);

		JLabel lblNewLabel = new JLabel("서버 ip");
		lblNewLabel.setBounds(27, 248, 57, 15);
		logPane1.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("포트번호");
		lblNewLabel_1.setBounds(27, 295, 57, 15);
		logPane1.add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("사용자아이디");
		lblNewLabel_2.setBounds(27, 347, 85, 15);
		logPane1.add(lblNewLabel_2);

		ipserver = new JTextField();
		ipserver.setBounds(96, 248, 116, 21);
		logPane1.add(ipserver);
		ipserver.setColumns(10);

		portnum = new JTextField();
		portnum.setBounds(96, 292, 116, 21);
		logPane1.add(portnum);
		portnum.setColumns(10);

		userid = new JTextField();
		userid.setBounds(111, 344, 116, 21);
		logPane1.add(userid);
		userid.setColumns(10);

		logbtn.setBounds(12, 408, 167, 23);
		logPane1.add(logbtn);

		log.setVisible(true);
	}

	private void Network() {

		try {
			socket = new Socket(ip, port);
			if (socket != null) {
				Connect();

			}

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void Connect() {
		try {
			is = socket.getInputStream();
			dis = new DataInputStream(is);

			os = socket.getOutputStream();
			dos = new DataOutputStream(os);

		} catch (IOException e) {
			e.printStackTrace();
		}

		// 처음접속시 id전송
		Sendmsg(id);
		
		useral.add(id);
		userlist.setListData(useral);

		Thread th = new Thread(new Runnable() { // inner class

			@Override
			public void run() {
				while (true) {
					try {
						String msg = dis.readUTF();
						
						System.out.println("받은 메세지  : " + msg);
						
						inmsg(msg);
					} catch (IOException e) {
						e.printStackTrace();
					} // 메세지 수신

				}

			}
		});
		th.start();
	}

	private void inmsg(String str) { //받은메세지 다들어옴
		st = new StringTokenizer(str, "/" );
		
		String protocol = st.nextToken();
		String Message = st.nextToken();
		
		System.out.println("프로토콜  : " + protocol);
		System.out.println("내용  : " + Message);  
		//토큰을 통해서 해당문자열을 구분시켜줌 
		
		if(protocol.equals("NewUser")) {
			useral.add(Message);
			userlist.setListData(useral);
			
		}else if(protocol.equals("OldUser")) {
			useral.add(Message);
		}else if(protocol.equals("Note")) {
			
			
			String note = st.nextToken();
			
			System.out.println(Message + "로부터 온 쪽지"+ note); 
			JOptionPane.showMessageDialog(null, note, Message + "님으로부터 온쪽지", JOptionPane.CLOSED_OPTION);
			
			
			
		}
		else if(protocol.equals("user_list_update")) {
			userlist.setListData(useral);
			
		}
		
	}
	
	private void Sendmsg(String str) {// 서버에게 메세지를 보내는 부분
		// outputstream을 통해 보냄
		try {
			dos.writeUTF(str); // 문자열을 받아서 dos.writeUTF로 보냄
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		new ClientChat();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == logbtn) {
			// loginbtn
			System.out.println("로그인버튼 클릭");
			ip = ipserver.getText().trim(); // ip받아옴
			port = Integer.parseInt(portnum.getText().trim()); // port받아옴
			id = userid.getText().trim(); // id받아옴
			// port 는 int형태라 String인 getText에 받아 올수 없다 그래서 형변환을 해야함.
			Network();

		} else if (e.getSource() == sendnote) {
			System.out.println("쪽지보내기 클릭");
			String user = (String)userlist.getSelectedValue();
			
			String note = JOptionPane.showInputDialog("보낼메세지");
			if(note != null) {
				Sendmsg("Note/"+user + "@"+note);
				System.out.println("받는사람  : " + user + "보낸내용" + note);
				
				
			}
			
			
		} else if (e.getSource() == joinroom) {
			System.out.println("방참여");
		} else if (e.getSource() == createroom) {
			System.out.println("방만들기");
		} else if (e.getSource() == send) {
			Sendmsg("테스트\n");
			System.out.println("보내기");
		}
	}
}
