package com.socket;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client extends Frame {
	// 플레이어 패널
	Panel playerPanel = new Panel(new GridLayout(1,2));
	Panel playerPanel2 = new Panel(new GridLayout(1,2));
	Panel playerPanel3 = new Panel(new GridLayout(1,2));
	Panel playerPanel4 = new Panel(new GridLayout(1,2));
	Panel emptyPanel = new Panel();
	// 플레이어 명
	static Label la1 = new Label();
	static Label la2 = new Label();
	static Label la3 = new Label();
	static Label la4 = new Label();
	// 플레이어 준비여부, 정체 여부
	static Label status1 = new Label();
	static Label status2 = new Label();
	static Label status3 = new Label();
	static Label status4 = new Label();
	// 채팅 작성 필드
	static TextField tf = new TextField(50);
	// 채팅 패널
	static TextArea ta = new TextArea();
	// 버튼들
	static Button btnReady = new Button("Ready");
	static Button btnStart = new Button("Start");
	// 최초 접속 창 닉네임 입력 창
	Frame login = new Frame("닉네임입력");
	TextField nameTf = new TextField(20);
	Button nameBtn = new Button("access");
	// 시간 초 라벨
	static Label timer = new Label("0sec");
	// 투표 창
	static Frame voteFrame = new Frame("투표");
	static Button vote0 = new Button();
	static Button vote1 = new Button();
	static Button vote2 = new Button();
	static Button vote3 = new Button();
	static Button vote4 = new Button();
	// 킬 선택 창
	static Frame killFrame = new Frame("킬 선택");
	static Button kill1 = new Button();
	static Button kill2= new Button();
	static Button kill3= new Button();
	static Button kill4= new Button();
	
	public Client() {
		// 플레이어 패널 세팅
		playerPanel.setBounds(10, 100, 200, 100);
		playerPanel.setBackground(Color.CYAN);
		playerPanel2.setBounds(10, 200, 200, 100);
		playerPanel2.setBackground(Color.RED);
		playerPanel3.setBounds(10, 300, 200, 100);
		playerPanel3.setBackground(Color.GREEN);
		playerPanel4.setBounds(10, 400, 200, 100);
		playerPanel4.setBackground(Color.YELLOW);
		la1.setText("empty");
		playerPanel.add(la1);
		playerPanel.add(status1);
		la2.setText("empty");
		playerPanel2.add(la2);
		playerPanel2.add(status2);
		la3.setText("empty");
		playerPanel3.add(la3);
		playerPanel3.add(status3);
		la4.setText("empty");
		playerPanel4.add(la4);
		playerPanel4.add(status4);

		// 채팅 필드 세팅
		tf.setBounds(230, 500, 550, 25);
		tf.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = Cons.CHATTING +"|" + name+ "|" + tf.getText();
				pw.println(msg);
				pw.flush();
				tf.setText("");
			}
		});
		
		// 채팅창 텍스트 에어리얼 세팅
		ta.setBounds(230, 50, 550, 450);
		ta.setBackground(Color.GRAY);
		ta.setEditable(false);

		// 준비 버튼 세팅
		btnReady.setBounds(790, 50, 40, 35);
		btnReady.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = Cons.READY + "|" + name;
				pw.println(msg);
				pw.flush();
			}
		});
		
		// 시작 버튼 세팅
		btnStart.setBounds(830, 50, 40, 35);
		btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(status1.getText().equals("READY!")
						&& status2.getText().equals("READY!")
						&& status3.getText().equals("READY!")
						&& status4.getText().equals("READY!")
						) {
					System.out.println("시작됨!");
					String msg = Cons.START + "|" + num;
					pw.println(msg);
					pw.flush();
				} else {
					System.out.println("준비가 되지않았습니다.");
				}
			}
		});
		
		// 투표 창 세팅
		vote0.addActionListener(new VoteAction());
		vote1.addActionListener(new VoteAction());
		vote2.addActionListener(new VoteAction());
		vote3.addActionListener(new VoteAction());
		vote4.addActionListener(new VoteAction());
		vote0.setLabel("skip");
		
		voteFrame.add(vote0);
		voteFrame.add(vote1);
		voteFrame.add(vote2);
		voteFrame.add(vote3);
		voteFrame.add(vote4);
		voteFrame.setLayout(new GridLayout(1, 4));
		voteFrame.setBounds(600, 300, 700, 200);
		voteFrame.setVisible(false);
		
		// 킬 선택 창 세팅
		kill1.addActionListener(new KillAction());
		kill2.addActionListener(new KillAction());
		kill3.addActionListener(new KillAction());
		kill4.addActionListener(new KillAction());
		
		killFrame.add(kill1);
		killFrame.add(kill2);
		killFrame.add(kill3);
		killFrame.add(kill4);
		killFrame.setLayout(new GridLayout(1, 3));
		killFrame.setBounds(600, 300, 700, 200);
		killFrame.setVisible(false);
		
		
		// 시간 초 라벨 세팅
		timer.setBounds(790, 200, 50, 50);
		
		add(timer);
		add(btnStart);
		add(btnReady);
		add(ta);
		add(tf);
		add(playerPanel);
		add(playerPanel2);
		add(playerPanel3);
		add(playerPanel4);
		add(emptyPanel);
		setTitle("play");
		setBounds(100, 100, 900, 600);
		setVisible(false);
		
		// 입장 시 닉네임 입력 GUI
		login.setLayout(new BorderLayout());
		login.add(nameTf, BorderLayout.CENTER);
		login.add(nameBtn, BorderLayout.SOUTH);
		nameTf.addActionListener(new LoginAction());
		nameBtn.addActionListener(new LoginAction());
		login.setBounds(250, 250, 300, 100);
		login.setVisible(true);
		
		// 종료 버튼 액션
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				if(isGame == false) {
					String msg = Cons.EXIT + "|" + num;
					System.out.println(msg);
					pw.println(msg);
					pw.flush();
				}
			}
		});
	}

	// 입장 시 닉네임 입력 액션
	class LoginAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			name = nameTf.getText();
			String msg = Cons.JOINPLAYER + "|" + name;
			pw.println(msg);
			pw.flush();
			login.dispose();
			setVisible(true);
		}
	}
	// 투표 액션
	class VoteAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Button btn = (Button) e.getSource();
			// label = 1-name
			String seletedPlayer = btn.getLabel().split("-")[0];
			String msg = Cons.VOTING + "|" + seletedPlayer + "|" + num;
			pw.println(msg);
			pw.flush();
			voteFrame.setVisible(false);
		}
		
	}
	
	// 킬 선택 액션
	class KillAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Button btn = (Button) e.getSource();
			// label = 1-name
			String seletedPlayer = btn.getLabel().split("-")[0];
			if(seletedPlayer != num) {
				String msg = Cons.MAFIA + "|" + seletedPlayer;
				pw.println(msg);
				pw.flush();
			}
			killFrame.setVisible(false);
			
		}
	}
	
	static boolean isGame = false; 
	static String num;
	static String name;
	static String status = "alive";
	static PrintWriter pw = null;

	public static void main(String[] args) {
		// GUI 생성
		Client client = new Client();
		// socket 통신
		String url = "192.168.240.119";
		int port = 8080;
		Socket sock = null;
		InputStream is = null;
		OutputStream os = null;
		InputStreamReader isr = null;
		OutputStreamWriter osw = null;
		BufferedReader br = null;
		boolean isRun = true;
		
		try {
			sock = new Socket(url, port);
			is = sock.getInputStream();
			os = sock.getOutputStream();
			isr = new InputStreamReader(is);
			osw = new OutputStreamWriter(os);
			br = new BufferedReader(isr);
			pw = new PrintWriter(osw);
			
			String msg = null;
			while(true) {
				msg = br.readLine(); // 여기서 락이 걸린다.
				String[] msgs = msg.split("\\|");
				
				// 상태확인
				if(status.equals("dead")) tf.setEnabled(false);
				
				// 채팅 출력
				// Cons.CHATTING|name|Hello, World
				if(msgs[0].equals(Cons.CHATTING)) {
					ta.append("[" + msgs[1] + "] " + msgs[2] + "\n"); // 읽어온 값을 ta에 넣어주는 것 
				}
				// 입장 시 각 클라이언트 플레이어 번호 부여 받음
				else if(msgs[0].equals(Cons.GIVE_NUM)) {
					num = msgs[1];
					System.out.println("번호부여 : " + num);
				}
				// 입장 완료 및 플레이어 이름 출력
				else if(msgs[0].equals(Cons.JOINPLAYER)) {
					System.out.println("유저 입장");
					String name = msgs[1];
					String index = msgs[2];
					System.out.println(name + ":" + index);
					switch (index) {
					case "1":la1.setText(name);vote1.setLabel("1-" + name);kill1.setLabel("1-" + name);break;
					case "2":la2.setText(name);vote2.setLabel("2-" + name);kill2.setLabel("2-" + name);break;
					case "3":la3.setText(name);vote3.setLabel("3-" + name);kill3.setLabel("3-" + name);break;
					case "4":la4.setText(name);vote4.setLabel("4-" + name);kill4.setLabel("4-" + name);break;
					}
				}
				// 준비
				else if(msgs[0].equals(Cons.READY)) {
					String stat = "";
					// 준비 여부 확인 Cons.READY|true|false|false|true
					for(int i = 1; i < msgs.length; i++) {
						if(msgs[i].equals("true")) {
							stat = "READY!";
						} else {
							stat = "";
						}
						switch(i) {
						case 1: status1.setText(stat);break;
						case 2: status2.setText(stat);break;
						case 3: status3.setText(stat);break;
						case 4: status4.setText(stat);break;
						}
					}
				}
				// 시작
				else if(msgs[0].equals(Cons.START)) {
					// 텍스트 창 초기화
					ta.setText("");
					// 생존 여부 초기화
					status1.setText("alive");
					status2.setText("alive");
					status3.setText("alive");
					status4.setText("alive");
					// 투표 버튼 활성화
					vote1.setEnabled(true);
					vote2.setEnabled(true);
					vote3.setEnabled(true);
					vote4.setEnabled(true);
					// 킬 선택 버튼 활성화
					kill1.setEnabled(true);
					kill2.setEnabled(true);
					kill3.setEnabled(true);
					kill4.setEnabled(true);
					// 준비 시작 버튼 비활성화
					btnReady.setEnabled(false);
					btnStart.setEnabled(false);
					ta.append("게임이 시작되었습니다.\n 3초 뒤 게임이 시작됩니다.\n");
					tf.setEnabled(false);
					// 게임 시작 확인
					isGame = true;
				}
				// 직업 확인
				else if(msgs[0].equals(Cons.INFO)) {
					if(msgs[1].equals("mafia")){
						ta.append("당신은 마피아 입니다.\n모두를 죽이고 승리하세요!\n");
					} else {
						ta.append("당신은 시민입니다.\n마피아를 찾아 승리하세요!\n");
					}
				}
				// 시간 초 경과
				else if(msgs[0].equals(Cons.TIMER)) {
					timer.setText(msgs[1] + "sec");
				}
				// 낮 시작
				else if(msgs[0].equals(Cons.DAY_START)) {
					ta.append("낮이 시작되었습니다.\n");
					pw.println(Cons.DAY_START + "|" + num);
					pw.flush();
					tf.setEnabled(true);
				}
				// 낮 끝
				else if(msgs[0].equals(Cons.DAY_END)) {
					ta.append("낮이 끝났습니다.\n");
					pw.println(Cons.DAY_END + "|" + num);
					pw.flush();
					tf.setEnabled(false);
				}
				// 투표 시작
				else if(msgs[0].equals(Cons.VOTE_START)) {
					// 죽지않아서 투표가능
					if(!status.equals("dead")) {
						ta.append("투표를 시작합니다.\n");
						ta.append("사형 시킬 플레이어를 선택해주세요\n");
						String[] playerState = {msgs[1], msgs[2], msgs[3], msgs[4]};
						for(int i = 0; i < playerState.length; i++) {
							if(playerState[i].equals("dead")) {
								switch (i) {
								case 0:vote1.setEnabled(false);break;
								case 1:vote2.setEnabled(false);break;
								case 2:vote3.setEnabled(false);break;
								case 3:vote4.setEnabled(false);break;
								}
							}
						}
						voteFrame.setVisible(true);
					}
					// 죽었기 때문에 강제로 5번에 투표 되도록함
					else {
						ta.append("당신은 죽었기 때문에 투표를 할 수 없습니다.\n");
						msg = Cons.VOTING + "|" + 5 + "|" + num;
						pw.println(msg);
						pw.flush();
					}
				}
				// 투표 끝
				else if(msgs[0].equals(Cons.VOTE_END)) {
					ta.append("투표가 끝났습니다.\n");
					String[] playerAlive = {msgs[2], msgs[3], msgs[4], msgs[5]};
					for(int i = 0; i < playerAlive.length; i++) {
						switch (i) {
						case 0:status1.setText(playerAlive[i]); break;
						case 1:status2.setText(playerAlive[i]); break;
						case 2:status3.setText(playerAlive[i]); break;
						case 3:status4.setText(playerAlive[i]); break;
						}
					}
					if(msgs[1].equals("0")) {
						ta.append("투표가 동률이거나 무효표로 아무도 죽지 않았습니다.\n");
						pw.println(Cons.RESULT + "|" + Cons.NIGHT_START);
						pw.flush();
					} else {
						ta.append("이번 투표 결과로 "+ msgs[1] + "번 플레이어가 죽었습니다.\n");
						if(num.equals(msgs[1])) {
							status="dead";
						}
						pw.println(Cons.RESULT + "|" + Cons.NIGHT_START);
						pw.flush();
					}
				}
								
				// 밤 시작
				else if(msgs[0].equals(Cons.NIGHT_START)) {
					ta.append("밤이 되었습니다.");
					pw.println(Cons.NIGHT_START + "|" + name);
					pw.flush();
				}
				// 마피아의 죽일 사람 선택창 등장
				else if(msgs[0].equals(Cons.MAFIA)) {
					String[] playerState = {msgs[1], msgs[2], msgs[3], msgs[4]};
					for(int i = 0; i < playerState.length; i++) {
						if(playerState[i].equals("dead")) {
							switch (i) {
							case 0:kill1.setEnabled(false);break;
							case 1:kill2.setEnabled(false);break;
							case 2:kill3.setEnabled(false);break;
							case 3:kill4.setEnabled(false);break;
							}
						}
					}
					killFrame.setVisible(true);
				}
				// 밤 끝
				else if(msgs[0].equals(Cons.NIGHT_END)) {
					String[] playerAlive = {msgs[2], msgs[3], msgs[4], msgs[5]};
					for(int i = 0; i < playerAlive.length; i++) {
						switch (i) {
						case 0:status1.setText(playerAlive[i]); break;
						case 1:status2.setText(playerAlive[i]); break;
						case 2:status3.setText(playerAlive[i]); break;
						case 3:status4.setText(playerAlive[i]); break;
						}
					}
					ta.setText("");
					ta.append("밤 사이에 마피아에 의해 " + msgs[1] + "번 플레이어가 살해 당했습니다.\n");
					if(num.equals(msgs[1])) status="dead";
					pw.println(Cons.RESULT + "|" + Cons.DAY_START);
					pw.flush();
				}
				
				// 게임 종료
				else if (msgs[0].equals(Cons.GAMEOVER)) {
					// 게임 종료 메세지
					String endMsg = msgs[1];
					ta.append(endMsg + "\n");
					// 채팅 활성화
					tf.setEnabled(true);
					// 준비 시작 버튼 활성화
					btnReady.setEnabled(true);
					btnStart.setEnabled(true);
					// 부활
					status="alive";
					// 게임 끝 확인
					isGame = false;
				}
				
				// 클라이언트 종료
				else if (msgs[0].equals(Cons.EXIT)) {
					isRun = false;
					client.dispose();
				}
				
				if(isRun == false)break;
			}
			
			if(pw!=null)pw.close();
			if(br!=null)br.close();
			if(osw!=null)osw.close();
			if(isr!=null)isr.close();
			if(os!=null)os.close();
			if(is!=null)is.close();
			if(sock!=null)sock.close();
			System.out.println("클라이언트 종료 완료");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

}
