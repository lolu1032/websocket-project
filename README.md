# 2024-10-22
## 게시글 작성 시 ,이 붙는 현상 발견
![123](https://github.com/user-attachments/assets/c9011b7c-78c4-40ab-8baf-80a47a25b230)
log를 찍어서 확인해 보니 클라에서 보내는 값에 ,123으로 오는걸 확인
## 문제해결
문제 원인은 formData.append에서 나온 것이다. append는 배열로 저장하는데 마크업 언어 처음이 공백으로 저장되면서 123이 두번째로 저장 그래서 ' ',1234 이렇게 들어가기 시작 그래서 ,1234가 나온거다 <br>
해결 방법은 formData.append 에서 formData.set으로 교체하여 고쳤다.
# 2024-10-21
## 게시글 페이지 마크업 언어 적용해서 올리기
![게시글 페이지](https://github.com/user-attachments/assets/86cef1b6-d941-48fb-adb5-d18006407385)

현재 디비에 저장된 각 게시글의 데이터를 추출하여 뽑아내는거 성공 <br>
마크업 언어로 저장된 데이터 또한 commonmark의존성을 추가 후 Parser 객체를 사용하여 마크업 언어로 저장된 데이터를 html로 변환 후 적용 잘되는지 확인 완료
## 이번주 목표
### 게시글 꾸민 후 서버 배포 공부해보기
# 2024-10-19
## 게시글 작성 시 의문점
만약 데이터베이스에 마크업 언어 내용을 그대로 저장한다면 이미지나 마크업 기능들이 제대로 될지가 의문이다. 그래서 내일은 디비에 저장해보고 꺼내서 확인해 볼 예정이다.
## 마크업 언어가 정상적으로 적용될 때의 화면 예상 구성
![Group 12](https://github.com/user-attachments/assets/4bf1514f-c7d7-4c04-9c4b-c39dba1342cc)

# 2024-10-18
## 게시글 작성
![dlalwl](https://github.com/user-attachments/assets/5c5606f3-b97e-488c-8373-c9d45421d0f8)

# 2024-10-15
## 게시글 상세페이지
![게시글](https://github.com/user-attachments/assets/62a84483-41ae-40df-bc8e-3d1de7f0052f)
![게시글 모달](https://github.com/user-attachments/assets/5eab1eb2-53f0-4c4c-88fb-6307b107e823)
## 스토리보드
![게시글 스토리보드](https://github.com/user-attachments/assets/c34a126d-3c21-4771-b853-df4d0d57d632)
### 게시글 하단은 마크업 에디터 라이브러리를 사용해서 넣을 예정이다.
# 2024-10-13
## 메인화면 페이징 처리
![페이징처리](https://github.com/user-attachments/assets/aebaa132-a205-4d65-967d-cf625962f35b)

# 2024-10-12
## 메인화면 
![메인페이지](https://github.com/user-attachments/assets/8370f255-27bb-4adf-aa78-3c42e60377a2)
### 게시글 작성 시 모집글들을 만들었습니다.
## 내일 할 것
### 페이징 처리를 할 것이며 메인 페이지 최종 모습은 이 사진입니다.
![Group 10](https://github.com/user-attachments/assets/0d693421-d83e-4cd8-b3b0-28b1f3a1889c)
# 2024-10-11
## 메인화면
![메인화면](https://github.com/user-attachments/assets/fb445fde-2f15-401d-96f4-6384e6680bf5)
### 로고와 슬라이드 이미지는 직접 피그마로 만들었습니다.
![피그마](https://github.com/user-attachments/assets/04171f52-a73c-43f9-a947-cc415ae80601)

# 2024-10-09
## 로그인 화면 메인 화면
![메인](https://github.com/user-attachments/assets/8fd55928-746b-4b5b-90bd-4eb664691c5b)
![로그인화면](https://github.com/user-attachments/assets/1f6f7f9f-9972-4b59-9f3a-33cf39e5d8bb)
메인 화면은 아직 content부분을 구성 중이라 비어있다.
# 2024-10-07
어제 시도한 것은 쓸데없는 짓이었다. 원래 토큰은 로그인하면 사용자에게 주어지는 것이고, 브라우저마다 쿠키에 저장되는 토큰도 달라 해킹에 대한 우려는 적다는 걸 알았다. 설사 해킹을 시도하더라도 이미 HttpOnly 쿠키를 사용해 자바스크립트 접근을 차단했기 때문에 해킹 우려도 거의 없다.
# 2024-10-06
## 시도한 것
accessToken은 접근을 관여하는 토큰이여서 클라에 보이면 안될거같아 accessToken대신 refreshToken을 클라 쿠키에 넣었다. refreshToken은 accessToken이 만료되면 재발급 해주는 토큰이여서 그렇게 햇다. <br>
지금 생각은 accessToken은 접근 권한이니까 서버에서 인증과 권한을 해결하고 클라에는 refreshToken만 보이게 하고싶었다. 하지만 accessToken으로 서버에 인증하고 권한을 얻을려하면 클라에 accessToken이 없어서 권한이 없다고 인지한다. 이 점을 해결해야할거같다.
## 오늘의 문제점
accessToken은 서버에서 인증하고 권한을 얻고싶지만 클라 쿠키에 accessToken이 없어서 권한을 못받는다 인지 그래서 로그인을 성공해도 쿠키에 refreshToken만 있으니 권한이 안들어가는 문제!
# 2024-10-05
## 토큰 갱신 에러
로그인을 하고 토큰이 만료가 되면 권한을 아에 잃어버려 login페이지 마저 401이 뜨는 현상발생 <br>
생각해본 해결방안 <br>
1. 만료되면 로그아웃 시키고 로그인 페이지로 이동시키기
2. 실시간으로 갱신시켜보기

securityConfig에 /api/** permit으로 설정했지만 오류 발생 
# 2024-09-29
오류를 콘솔로 확인해도 무엇인지 아무리 해도 모르겠다. 그래서 기본이 부족하다 생각하여 시큐리티에 대해 공부하며 다시 구글링하고 찾아보면서 따라쓰면서 익숙해질려고 노력하고있다. <br>
현재는 따로 노션으로 개념 정리하면서 나아가고있다.

# 2024-09-27
## 문제점
![오류](https://github.com/user-attachments/assets/2a3363ad-408e-4bd6-be26-9b78a0b5ea96)

# 2024-09-24
## 브라우저 구분
![크롬](https://github.com/user-attachments/assets/2cf0d235-01aa-41d0-bee7-f4f34b176620)
![시크릿](https://github.com/user-attachments/assets/9904ae3b-4bbc-4732-b775-9f616ea58182)
## 문제 해결
Joined를 하면 다른 브라우저도 같이 이벤트가 발생한다는거다 그걸 방지하기위해 checkRoom할때 브라우저도 같이 받아 구분을 시켜 다른 브라우저에서 이벤트가 발생하는걸 방지 시켰다.
## 다음에 할거
JWT통한 유저 아이디를 만들어 토큰을 받아 브라우저 아이디를 대체할 예정이다.
# 2024-09-23
## 이번 문제점
이전 코드들은 구독을 할때 필터링이 없어서 A브라우저에서 방을 들어가면 B브라우저도 같이 구독해버린다. 그래서 그걸 해결하기 위해 browserId를 만들어서 구분을 하고 방을 들어갈때 필터링 browserId를 통해 적용<br>
이전 문제는 해결했지만 이 문제를 해결하니 갑자기 채팅을 치고 보내면 보여야할 채팅 내역이 안보이기 시작
# 2024-09-21
## 현재의 문제점
로컬 서버로 하다보니 브라우저를 두개 열어서 하면 한쪽에서 이벤트가 작동하면 다른쪽에서도 그 이벤트를 발생시킨다는거다. <br>
그래서 생각한 방안은
1. 회원을 만들어 JWT을 통해 토큰을 발행시켜 로컬서버여도 토큰으로 인해 구별시키는 방법
2. 세션아이디를 줘서 세션 아이디로 구별하기
3. 개인 서버 만들기<br>
1,2는 머리속에서 생각난거라서 한번 시도 해볼려고한다. 3번은 아직 서버 만드는법을 모르기때문에 일단은 킵해두겠다.

# 2024-09-16
## 웹소캣을 이용한 개인프로젝트
### SockJS,Stomp 연결
![웹소캣테스트](https://github.com/user-attachments/assets/f3b53bbc-644f-4eca-a7ee-e022cc80d21e)




