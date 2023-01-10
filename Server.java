package com.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Server {
	static int count = 0;
	static int setTime = 60;
	static int[] selectedPlayers = {0,0,0,0,0,0};
	static ArrayList<String> ready = new ArrayList<String>();
	public static void main(String[] args) {
		List<PrintWriter> list = new ArrayList<PrintWriter>();
		List<User> userList = new ArrayList<User>();
		ServerSocket serve = null;
		
		try {
			serve = new ServerSocket(8080);
			while (true) {
				final Socket sock = serve.accept();
				// 인원 수 제한
				if(list.size() < 4) {
				Thread thr = new Thread() {
					@Override
					public void run() {
						InputStream is = null;
						OutputStream os = null;
						InputStreamReader isr = null;
						OutputStreamWriter osw = null;
						BufferedReader br = null;
						PrintWriter pw = null;
						boolean isRun = true;
						try {
							is = sock.getInputStream();
							os = sock.getOutputStream();
							isr = new InputStreamReader(is);
							osw = new OutputStreamWriter(os);
							br = new BufferedReader(isr);
							pw = new PrintWriter(osw);
							list.add(pw);
							// 플레이어 번호 부여
							count++;
							String num = Cons.GIVE_NUM + "|" + count;
							pw.println(num);
							pw.flush();
							
							String msg = null;
							while (true) {
								msg = br.readLine();
								String[] msgs = msg.split("\\|");

								// 메세지 송수신
								if (msgs[0].equals(Cons.CHATTING)) {
									for (int i = 0; i < list.size(); i++) {
										PrintWriter w = list.get(i);
										w.println(msg);
										w.flush();
									}
								}
								// 플레이어 입장
								else if (msgs[0].equals(Cons.JOINPLAYER)) {
									User newUser = new User(msgs[1], "false"); // 유저 등록
									userList.add(newUser);
									for (int j = 0; j < userList.size(); j++) {
										User user = userList.get(j);
										user.num = (j + 1) + "";
										for (int i = 0; i < list.size(); i++) {
											PrintWriter w = list.get(i);
											msg = Cons.JOINPLAYER + "|" + user.name + "|" + user.num;
											w.println(msg);
											w.flush();
										}
									}
								}
								// 준비 표시
								else if (msgs[0].equals(Cons.READY)) {
									msg = Cons.READY + "|";
									for (int j = 0; j < userList.size(); j++) {
										User user = userList.get(j);
										if(user.name.equals(msgs[1])) {
											if(user.ready.equals("false")) {
												user.ready = "true";
											} else {
												user.ready = "false";
											}
										}
										msg += user.ready + "|";
									}
									for (int i = 0; i < list.size(); i++) {
										PrintWriter w = list.get(i);
										w.println(msg);
										w.flush();
									}
								}
								
								// 게임 시작
								else if (msgs[0].equals(Cons.START)) {
									System.out.println("서버 시작 부분");
									// 시간 가는 쓰레드
									Thread timeThr = new Thread() {
										@Override
										public void run() {
											// 시작 - 유저 게임 정보 초기화
											for(User user : userList) {
												user.alive = "alive";
												user.job = "citizen";
												user.ready = "false";
											}
											// 시작 - 채팅창 초기화 및 게임 시작 알림
											ready.clear();
											String msg = Cons.START;
											for (int i = 0; i < list.size(); i++) {
												PrintWriter w = list.get(i);
												w.println(msg);
												w.flush();
											}
											// 마피아 랜덤 선택
											int mafia = (int)(Math.random() * 4);
											User user = userList.get(mafia);
											user.job = "mafia";
											// 마피아 설정 확인
											System.out.println("마피아 플레이어:"+(mafia+1));
											for(User u : userList) {
												System.out.println(u.num+"번:"+ u.job);
											}
											// 각 플레이어에게 직업 안내
											for (int i = 0; i < userList.size(); i++) {
												PrintWriter pw = list.get(i);
												user = userList.get(i);
												msg = Cons.INFO + "|" +user.job;
												pw.println(msg);
												pw.flush();
											}
											// 10초 경과 후 낮 시작
											for(int time = 1; time >= 0; time--) {
												msg = Cons.TIMER + "|" + time;
												for (int i = 0; i < list.size(); i++) {
													PrintWriter w = list.get(i);
													w.println(msg);
													w.flush();
												}
												try {
													Thread.sleep(1000);
												} catch (InterruptedException e) {
													e.printStackTrace();
												}
												// 0초 될 경우 낮 시작
												if(time == 0) {
													msg = Cons.DAY_START;
													for (int i = 0; i < list.size(); i++) {
														PrintWriter w = list.get(i);
														w.println(msg);
														w.flush();
													}
												}
											}
										}
									};
									timeThr.start();
									
									// 시작하여서 채팅창 초기화
									msg = Cons.START;
									for (int i = 0; i < list.size(); i++) {
										PrintWriter w = list.get(i);
										w.println(msg);
										w.flush();
									}
									
								}
								// 낮 시작
								else if(msgs[0].equals(Cons.DAY_START)) {
									ready.add(msgs[1]);
									if(ready.size() % 4 == 0) {
										Thread timeThr = new Thread() {
											@Override
											public void run() {
												String msg = "";
												for(int time = 1; time >= 0; time--) {
													msg = Cons.TIMER + "|" + time;
													for (int i = 0; i < list.size(); i++) {
														PrintWriter w = list.get(i);
														w.println(msg);
														w.flush();
													}
													try {
														Thread.sleep(1000);
													} catch (InterruptedException e) {
														e.printStackTrace();
													}
													// 시간 종료 후
													if (time==0) {
														msg = Cons.DAY_END;
														for (int i = 0; i < list.size(); i++) {
															PrintWriter w = list.get(i);
															w.println(msg);
															w.flush();
														} 
													}
												}
											}
											
										};
										ready.clear();
										timeThr.start();
									}
								}
								// 낮 끝 -> 투표시작
								else if(msgs[0].equals(Cons.DAY_END)) {
									ready.add(msgs[1]);
									if(ready.size() == 4) {
										msg = Cons.VOTE_START;
										for(User user : userList) {
											msg += "|" + user.alive;
										}
										for (int i = 0; i < list.size(); i++) {
											PrintWriter w = list.get(i);
											w.println(msg);
											w.flush();
										}
										ready.clear();
									}
								}
								// 투표 중
								else if(msgs[0].equals(Cons.VOTING)) {
									int selectedPlayer = 0;
									if(!msgs[1].equals("skip")) {
										selectedPlayer = Integer.parseInt(msgs[1]);
									}
									selectedPlayers[selectedPlayer] += 1;
									String voterPlayer = msgs[2];
									ready.add(voterPlayer);
									// 투표 완료! -> 유저 상태 변경 및 결과 반환
									if(ready.size() == 4) {
										// 투표 결과 확인
										String diePlayer = "0";
										int maxVote = 0;
										// 최다 투표 수 계산
										for(int j = 0; j < selectedPlayers.length - 1; j++) {
											if(maxVote < selectedPlayers[j] ) {
												maxVote = selectedPlayers[j];
												diePlayer = j + "";
											}
										}
										System.out.println("최다 득표 수 : " + maxVote);
										System.out.println("최다 득표 자 : " + diePlayer);
										// 최다 투표 수 여러명인지 확인
										int dup = 0;
										for(int k = 0; k < selectedPlayers.length - 1; k++) {
											if(selectedPlayers[k] == maxVote) {
												dup++;
											}
										}
										System.out.println("중복 확인 : " + dup);
										// 유저 상태 변경
										String result = "";
										if(dup == 1) {
											for(User user : userList) {
												if(user.num.equals(diePlayer)) {
													user.alive = "dead";
												}
												result += user.alive + "|";
											}
										} else {
											diePlayer = "0";
											for(User user : userList) {
												result += user.alive + "|";
											}
										}
										// 결과 반환
										msg = Cons.VOTE_END + "|" + diePlayer + "|" + result;
										System.out.println("투표 결과 : "+ msg);
										for (int i = 0; i < list.size(); i++) {
											PrintWriter w = list.get(i);
											w.println(msg);
											w.flush();
										}
										selectedPlayers = new int[] {0,0,0,0,0,0};
										ready.clear();
									}
											
								}
								// 밤 시작
								else if(msgs[0].equals(Cons.NIGHT_START)) {
									ready.add(msgs[1]);
									if(ready.size() == 4) {
										// 킬 선택창의 버튼들에 생존여부 띄워주기
										msg = Cons.MAFIA;
										for(int i = 0; i < userList.size(); i++) {
											User user = userList.get(i);
											msg += "|" + user.alive;
										}
										System.out.println("생존 여부 : "+msg);
										// 마피아인 사람에게만 선택창이 뜰 수 있도록함
										for(int j = 0; j < userList.size(); j++) {
											User user = userList.get(j);
											if(user.job.equals("mafia")) {
												PrintWriter w = list.get(j);
												w.println(msg);
												w.flush();
											}
										}
										ready.clear();
									}
								}
								// 마피아의 죽일 사람 선택
								else if (msgs[0].equals(Cons.MAFIA)) {
									String killPlayer = msgs[1];
									String result = "";
									for(User user : userList) {
										if(user.num.equals(killPlayer)) {
											user.alive = "dead";
										}
										result += user.alive + "|";
									}
									// 결과 반환 + 밤 끝
									msg = Cons.NIGHT_END + "|" + killPlayer + "|" + result;
									System.out.println("킬 선택 : " + msg);
									for (int i = 0; i < list.size(); i++) {
										PrintWriter w = list.get(i);
										w.println(msg);
										w.flush();
									}
									
								}
								
								// 현재 상황 체크
								else if(msgs[0].equals(Cons.RESULT)) {
									ready.add(msgs[0]);
									if (ready.size() == 4) {
										// 생존자 수
										int count = 0;
										String mafia = "alive";
										for (User user : userList) {
											// 마피아가 사망한 경우
											if (user.job.equals("mafia") && user.alive.equals("dead")) {
												mafia = "dead";
											} else if (user.job.equals("citizen") && user.alive.equals("alive")) {
												count++;
											} 
										}
										// 남은 시민 생존자 수가 1명 이하일 경우
										if (count <= 1) {
											msg = Cons.GAMEOVER + "|"+ "시민 과반수 사망 마피아의 승리";
											for (int i = 0; i < list.size(); i++) {
												PrintWriter w = list.get(i);
												w.println(msg);
												w.flush();
											}
										}
										// 마피아가 죽은 경우
										else if (mafia.equals("dead")) {
											msg = Cons.GAMEOVER + "|"+ "마피아 사망 시민의 승리";
											for (int i = 0; i < list.size(); i++) {
												PrintWriter w = list.get(i);
												w.println(msg);
												w.flush();
											}
										} else {
											// 투표, 밤 끝난 후 다음 턴 진행
											String nextTurn = msgs[1];
											msg = nextTurn;
											for (int i = 0; i < list.size(); i++) {
												PrintWriter w = list.get(i);
												w.println(msg);
												w.flush();
											}
										}
										ready.clear();
									}
									
								}
								
								if (isRun == false)break;
							}

							if (pw != null)pw.close();
							if (br != null)br.close();
							if (osw != null)osw.close();
							if (isr != null)isr.close();
							if (is != null)is.close();
							if (os != null)os.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				};
				thr.start();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
