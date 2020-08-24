# cURL

> client URL 의 약자

커맨드라인에서 다양한 프로토콜을 이용하여 데이터를 전송하기 위한 도구.

쉽게 리눅스 Shell 에서 web url을 요청해서 원하는 데이터를 전송 또는 응답받을 수 있다.

```
<예시>
curl www.naver.com
```



### Options

* -X 

요청에 맞는 메서드를 선택할 수 있다. 기본적으로 옵션에 맞게 알아서 적용된다고 한다.

```
... -X POST ...
```

* -d

POST 요청에 특정 데이터를 넣어준다. content-type이 application/x-www-form-urlencoded로 서버로 요청한다고 한다. 

-f 는 Content-Type이 multipart/form-data 날아간다고 한다. 둘이 조금 차이점인듯!

* -H

헤더를 보낸다. 몇개든 보낼 수 있다 한다.

* -u

Basic Auth 또는 Digest Auth같은 Authentication method를 사용할때만 사용하는 듯 하다. 일반적으로 잘 사용하는 옵션은 아닌거같다. 

```
curl -u {client_id}:{client_secret} -d grant_type=client_credentials https://us.battle.net/oauth/token
```

이런식으로 블리자드 요청을 보냈다. 이때 {}:{} 이놈을 Base64로 인코딩을 해줘야한다. 그럼 Sgj3i=4 같은 이상한 문자열이 나온다. 그럼 이제 이놈앞에 "Basic " 을 붙여서 헤더에 넣어주면 된다.

헤더에 Authorization: Basic Sgj3i=4 이런식으로 들어가면 성공인거다. 에휴 왜 이걸 이렇게 삽질하고 있었는지 정말 모르겠다.

https://stackoverflow.com/questions/20737031/curlss-option-u



### 여담

Blizzard OAuth API 사용해서 access token을 얻어오는 과정에서 도당체 Retrofit으로 어떻게 요청을 해야하는지 모르겠어서 정말 이것저것 뒤적뒤적이다가 간신히 알게되었다. 공홈에 curl 예시코드가 있었고, 여태껏 다른 API들도 항상 있었던 예제 코드던데 이게 대체 뭘까? 하고 검색해보는 과정 속에서 이젠 이게 뭔지 알게되었다. 그래도 결국 토큰 받아오는거 성공해서 기쁘다 ^^;; 나의 안타까운 삽질의 시간들... 안녕ㅠ