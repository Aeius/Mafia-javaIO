# ✨ Mafia-java-IO
## 📢 Introduction
주제 : java 소켓통신과 IO를 이용하여 멀티플레이 마피아게임 구현   
기간 : 2023-01-09 ~ 2023-01-10 (2일)   

## 🚀 Utillity
#### java I/O    
- BuffredReader
- PrintWriter
#### java Socket
#### java awt - GUI

## ⭐ I/O Rule
클라이언트와 서버간의 통신 시 문자열을 주고 받도록 하였고 식별자|값|값|... 의 형태로 통신    
ex) 1|name|jaehyeon    
식별자의 경우는 상수로 지정하여 사용 ex) Cons.JOIN, Cons.START,...   

## ⚡ Trouble shooting
### 클라이언트에서 제대로 작성한 문자열이 서버에 도달하지 않는 이유
- PrintWiter가 제대로 작동하지 않았을 때 반드시 println으로 개행처리를 한 데이터를 보내주어야 서로 받을 수 있다. 그냥 print까지만 작성해선 데이터를 주고 받을 수 없었다.

### 클라이언트는 다수고 서버는 하나다.
- **개요** : 서버단에서 쓰레드를 이용해서 시간 경과를 각 클라이언트에게 보내주고 시간이 종료 되었을 때 다음 턴을 진행하도록 클라이언트에서 서버에 요청을 줘 다음턴을 진행
- **문제** : 서버가 받은 요청은 클라이언트 수만큼 되기 때문에 서버에서는 받은 요청 수를 파악하지 않는다면 다음턴이 계속 이어질 수 록 요청과 응답이 클라이언트 배수만큼 늘어나는 현상이 발생
- **해결** : 서버에서 다음턴으로 자연스럽게 넘어가도록 클라이언트의 요청을 모아두는 ready배열을 하나 정의하여 해당 배열의 갯수와 클라이언트의 요청 수가 같아지면 서버단에서 다음 턴을 진행 시키도록 구현했다.

### 게임을 위해 클라이언트들의 정보를 서버에서 관리
- **개요** : 마피아와 시민의 구분, 각 플레이어의 생존여부 파악에 따른 GUI 변경, 게임진행에 따른 GUI 변경이 필요
- **문제** : 소켓 연결 시 얻을 수 있는 ip주소로는 각 클라이언트 마다 구분을 지어주기 어렵고 클라이언트에서는 본인의 정보만 가지고 있기 때문에 다른 유저의 정보 갱신이 어려움
- **해결** : 서버단에서 새로운 클라이언트가 접속 시 Thread를 생성하며 해당 PrintWiter를 하나의 배열을 통해서 각 클라이언트의 연결을 구분해뒀기 때문에 같은 index에 위치하도록 User 객체의 배열을 만들어 유저의 정보 저장하여 클라이언트단에 뿌려질 개개인 유저들의 정보를 서버에서 관리하도록함, 플레이어 번호, 개인 생존여부의 정보는 서버단에서 관리하되 각 클라이언트에서 개별적으로 사용할 수 있도록 전달하도록 했다.

### 초기화의 중요성
- **개요** : 투표의 갯수를 파악하여 이번 투표 결과가 동률일 경우 넘어가고 제일 다수의 투표를 받았을 때만 사형이 되야함
- **문제** : 클라이언트마다 투표를 받되, 죽은인원은 투표할 수 없어야하며, 최다득표가 동률인 경우 무효처리, 오직 최다득표가 혼자 일 때만 사형 되도록 해야하는데 투표 진행 후 기록들이 초기화 하지않아 투표 내용이 계속 쌓여서 잘못된 결과 발생
- **해결** : 전체적인 투표 관리는 {skip,1p,2p,3p,4p,dieplayer} 형태로 int배열 선언하여 매 투표 마다 각각의 클라이언트에서 선택한 투표내용을 기록하고 해당 투표기록을 토대로 최다 득표 수, 최다 득표 수를 받은 사람은 몇명인지 최다 득표 수를 받은 사람이 1명이라면 그 사람의 유저정보를 갱신, 투표 종료 시 투표관리용 배열을 다시 {0,0,0,0,0,0}으로 초기화 해줌으로써 잘못된 투표결과가 나오지 않도록 했다. 이것 외에도 새로운 게임을 위한 초기화, 다음턴 인식을 위한 ready 배열 초기화를 해주는 과정이 필요했다.
