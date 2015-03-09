# buga-sample
국세청 전자신고암호화 (DLL) 를 windows 위에서 Java JNA 를 이용하여 소켓통신하는 서버를 구축한다.

## 개요
Web Application 상에서 국세청 전자신고암호화를 통하여 암호화된 파일을 제공한다

## 유의점
1. DLL 파일은 Windows 에서만 실행이 가능하다.
2. 국세청에서 제공된 암호화 DLL 은 32bit 로 되어 있다.
3. 여기에서 Server 는 암호화를 진행하는 Windows 서버가 되고 Client 는 Web Application가 된다.
4. DLL에 접근하기 위해 Server 에서는 32bit JVM 위에서 동작하여야 한다.

## Flow
1. Client 에서 부가세 Text 파일을 작성한다.
2. Text 파일을 Server 로 파일 전송한다.
3. Server 는 수신 받은 Text 파일을 국세청에서 제공하는 DLL 파일로 암호화를 진행한다.
4. Server 는 암호화된 파일을 Client 로 전송한다.
5. Server 에서 Text 파일과 암호화 파일을 제거한다.

## 적용 기술
1. 파일 송수신을 위한 양방향 소켓 통신
2. DLL 파일을 Java 에서 실행하기 위해 JNA 사용
3. 다수의 Client 를 안정하게 받기 위해 ThreadPool 사용
