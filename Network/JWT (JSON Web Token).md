### JWT (JSON Web Token)

JWT관련 표준은 JWS, JWE가 있다.

* JWS (Json Web Signature) Json으로 전자 서명을 해 URL safe한 문자열로 만든 것.
* JWE (Json Web Encryption) Json을 암호화해 URL safe한 문자열로 만든 것.

JWT는 aaaaaaaa.bbbbbbbb.cccccccc 이런식으로 세 파트로 구분 된다. 순서대로 헤더(Header), 페이로드(Payload), 서명(Signature)으로 구성된다. 또, Url Safe 하기 위해 Base64Url 인코딩을 사용한다.



#### JWT의 구성

* Header
  
  * 토큰의 타입, 해시 암호화 알고리즘 종류를 담고 있음.
    * typ : 토큰의 타입 "JWT"
    * alg : 해싱 알고리즘 지정 "HS256" 보통 HMAC SHA256 또는 RSA가 사용된다.
* Payload
  * 여러개의 클레임 (페이로드에 담는 key value 쌍인 정보의 한 조각) 으로 구성됨.
  
  * 클레임의 종류로는 registered(등록된), public(공개), private(비공개) 세 가지가 있음.
  
    * registerd Claim
  
      * iss : 토큰 발급자
      * sub : 토큰 제목
      * aud : 토큰 대상자
      * exp : 토큰의 만료시간 (numericDate형식)
      * nbf : not before의 의미. 토큰 활성 시작 시간. 이 이전엔 처리되지 않음.
      * iat : issued at 토큰 발급 시간
      * jti : JWT 고유 식별자. 중복 처리를 방지하기 위함
  
    * public Claim
  
      * 중복이 생기지 않은 이름을 가지고 있어야함. 이름을 uri 형식으로 짓는다.
  
        ```json
        {
            "https://github.com/nightmare73": true
        }
        ```
  
    * private Claim
  
      * 나머지 클레임들. 서버 클라이언트 합의 후 사용하는 이름. 
* Signature
  
  * secret key를 포함해 암호화 되어있음.



#### 장점

* 사용자 인증에 필요한 모든 정보가 토큰 자체에 포함되어있어 별도의 인증 저장소가 필요 없다.
* 인증 서버와 데이터베이스에 의존하지 않는 쉬운 인증/인가 방법을 제공함.

#### 단점

* 많은 필드가 추가되면 토큰이 커질 수 있다.
* 토큰은 클라이언트에 저장되기 때문에 디비에서 사용자 정보를 변경해도 토큰은 변경 불가능하다.



#### 주의점

JWT 안에 중요한 정보를 넣어서는 안된다.

페이로드는 손쉽게 디코딩이 가능하기 때문에 항상 보안 키로 서명 되어있는지 검사해야한다. 유효한 토큰이 아니라면 바로 버려서 응답을 주면 안된다. 

