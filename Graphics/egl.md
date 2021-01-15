### EGL

> **EGL**은 ([OpenGL](https://ko.wikipedia.org/wiki/OpenGL), [OpenGL ES](https://ko.wikipedia.org/wiki/OpenGL_ES) 또는 [OpenVG](https://ko.wikipedia.org/w/index.php?title=OpenVG&action=edit&redlink=1)와 같은) [크로노스](https://ko.wikipedia.org/wiki/크로노스_그룹) [렌더링 API](https://ko.wikipedia.org/w/index.php?title=렌더링_API&action=edit&redlink=1)와 기본 네이티브 플랫폼 [윈도우 시스템](https://ko.wikipedia.org/wiki/윈도우_시스템) 간의 [인터페이스](https://ko.wikipedia.org/wiki/인터페이스_(컴퓨팅))이다. EGL은 그래픽 [컨텍스트 관리](https://ko.wikipedia.org/w/index.php?title=컨텍스트_관리&action=edit&redlink=1), [서피스](https://ko.wikipedia.org/wiki/서피스)/[버퍼](https://ko.wikipedia.org/w/index.php?title=Data_buffer&action=edit&redlink=1) 바인딩, [렌더링](https://ko.wikipedia.org/wiki/렌더링) 동기화를 처리하고 "다른 크로노스 API를 사용하여 고성능, 가속화된, 혼합모드 [2D](https://ko.wikipedia.org/wiki/2차원_컴퓨터_그래픽스) 및 [3D](https://ko.wikipedia.org/wiki/3차원_컴퓨터_그래픽스) 렌더링"을 가능하게 한다.[[2\]](https://ko.wikipedia.org/wiki/EGL_(API)#cite_note-2) EGL [비영리](https://ko.wikipedia.org/wiki/비영리_단체) 기술 컨소시엄 [크로노스 그룹](https://ko.wikipedia.org/wiki/크로노스_그룹)에 의해 관리되고 있다.
>
> 약어 *EGL*은 *Khronos Native Platform Graphics Interface*에 언급되어 EGL 버전 1.2부터 시작된 [initialism](https://ko.wikipedia.org/w/index.php?title=Initialism&action=edit&redlink=1)이다.[[3\]](https://ko.wikipedia.org/wiki/EGL_(API)#cite_note-3) 버전 1.2이전에는 EGL 스펙의 이름이 *OpenGL ES Native Platform Graphics Interface*였다.[[4\]](https://ko.wikipedia.org/wiki/EGL_(API)#cite_note-4) [X.Org](https://ko.wikipedia.org/wiki/X.Org_재단) 개발 문서 용어집에서는 EGL을 "Embedded-System Graphics Library"로 정의한다.[[5\]](https://ko.wikipedia.org/wiki/EGL_(API)#cite_note-5)
>
> 안드로이드 에서는 3D 그래픽 렌더링을 위해 EGL을 사용한다.
>
> -Wikipedia

Khronos에선 EGL을 Khronos의 렌더링 API들과 플랫폼의 윈도우 시스템 사이의 인터페이스라고 정의한다. 윈도우 시스템은 짧게 말하자면 그래픽 요소로 프로그램들을 보여주는 체계다.

Windows의 여러 윈도우를 띄우고 이 윈도우의 크기를 조절하거나 여러 윈도우를 겹쳐놓는 등의 처리부터 해상도 제어, 모니터로의 출력 등을 관장하는 것은 바로 이 윈도우 시스템을 통해서 이뤄진다. Windows의 DWM이라는 프로세스가 이를 관리한다고 알려져 있다. 이와 관련한 구체적인 내용은 별도의 위키에서 확인할 수 있다. (Wiki: [Compositing Window Manager](https://en.wikipedia.org/wiki/Compositing_window_manager))

우선 Surface란 무엇인지부터 명확히 해야 한다. EGL에서 말하는 surface란 실제 렌더링 되는 대상이라고 볼 수 있는 이미지 버퍼를 추상화한 개념이며 이를 EGLSurface라는 객체로 표현한다. OpenGL을 통해 draw를 수행한 결과는 어떤 이미지 버퍼에 그려져야 하는데, 이때 이 이미지 버퍼를 직접 사용하는 대신 surface를 쓰는 것이다. 그리고 Surface는 내부에 다음과 같은 정보들을 가지게 된다.



출처 : https://brunch.co.kr/@sixzone11/14