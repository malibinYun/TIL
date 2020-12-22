## NAT

#### 위키

> **네트워크 주소 변환**([영어](https://ko.wikipedia.org/wiki/영어): network address translation, 줄여서 **NAT**)은 컴퓨터 네트워킹에서 쓰이는 용어로서, [IP](https://ko.wikipedia.org/wiki/IP) [패킷](https://ko.wikipedia.org/wiki/패킷)의 [TCP](https://ko.wikipedia.org/wiki/전송_제어_프로토콜)/[UDP](https://ko.wikipedia.org/wiki/사용자_데이터그램_프로토콜) 포트 숫자와 소스 및 목적지의 [IP 주소](https://ko.wikipedia.org/wiki/IP_주소) 등을 재기록하면서 [라우터](https://ko.wikipedia.org/wiki/라우터)를 통해 [네트워크 트래픽](https://ko.wikipedia.org/w/index.php?title=네트워크_트래픽&action=edit&redlink=1)을 주고 받는 기술을 말한다. 패킷에 변화가 생기기 때문에 IP나 TCP/UDP의 [체크섬](https://ko.wikipedia.org/wiki/체크섬)(checksum)도 다시 계산되어 재기록해야 한다. NAT를 이용하는 이유는 대개 [사설 네트워크](https://ko.wikipedia.org/wiki/사설_네트워크)에 속한 여러 개의 호스트가 하나의 공인 IP 주소를 사용하여 [인터넷](https://ko.wikipedia.org/wiki/인터넷)에 접속하기 위함이다. 많은 네트워크 관리자들이 NAT를 편리한 기법이라고 보고 널리 사용하고 있다. NAT가 호스트 간의 통신에 있어서 복잡성을 증가시킬 수 있으므로 네트워크 성능에 영향을 줄 수 있는 것은 당연하다.



내부 네트워크에서 외부로 나가는 패킷들의 주소를 외부 네트워크 주소로 변환하고, 나갔던 패킷에 대한 응답 패킷의 목적지 주소를 다시 내부 네트워크 주소로 변환해주는 기능이다.

네트워크에서 외부망과 내부망을 나눠주는 기능을 한다

* 내부 망에연결된 기기만큼의 공인 IP주소가 없어도 연결된 기기들이 모두 인터넷 연결 가능하게 만들어 준다.
* 인터넷 공인 IP 주소의 개수를 절약할 수 있다
* 내부와 외부 네트워크가 분리되어있기 때문에 외부 네트워크의 침입으로 부터 보호할 수 있다.
* IP를 숨길 수 있다! 공인 IP만 외부로 알려지고, 내부 IP는 공개하지 않는다.
* 외부에서 먼저 내부 컴퓨터와 통신을 시도할 수 없는 제약이 큰 단점이다 !! -> 포워딩으로 해결 가능.

#### NAT의 종류

* Static NAT
  * 공인 IP주소와 내부 IP주소가 1:1로 매칭 된다. (주소 절약 도움 1도 안됨)
* Dynamic NAT
  * 공인 IP 주소 < 내부 네트워크 HOST 수인 경우 모든 내부 IP주소를 일일이 외부용 IP 주소로 고정적으로 할당할 필요가 없을 때.
  * IP Pool을 둬서 내부에서 IP를 할당 받고 통신을 한다.
  * IP Pool이 비어있는 경우 더 이상 내부 Host는 통신할 수 없다.
  * 위의 경우에도 통신을 가능하게 만드려면 PAT기능을 설정해야한다.
* PAT (Port Address Translation)
  * 하나 또는 하나 이상의 공인 IP 주소를 여러 개의 내부 Host들이 공유할 수 있게 만들어준다.
  * 같은 IP공인 주소로 변환하고 Port번호를 다르게 준다.

#### NAT 방식

* Cone NAT

  > 목적지에 관계없이 외부 Port가 변하지 않는다.
  >
  > 192.168.1.100:5060 -> malibin.com:5060 보낼 때 공유기의 1.1.1.1:1234로 매핑되면
  >
  > 192.168.1.100:5060 -> yun.malibin.com:8000 으로 보내면 공유기의 1.1.1.1:1234 으로 매핑된다.

  * Full Cone
  * Restricted Cone
  * Port Restricted Cone

* Symmetric NAT

> 목적지에 따라서 외부 Port가 각기 바뀐다.
>
> 192.168.1.100:5060 -> malibin.com:5060 보낼 때 공유기의 1.1.1.1:1234로 매핑되면
>
> 192.168.1.100:5060 -> yun.malibin.com:8000 으로 보내면 공유기의 1.1.1.1:5432 으로 매핑된다.