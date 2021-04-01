## 카메라2 API 구성

<img src="image/image13.png" />

* **CamaraManager**
  * 시스템 서비스.
  * 사용 가능한 카메라, 카메라 기능들을 쿼리할 수 있고 카메라를 열 수 있음
* **CameraCharacteristics**
  * 카메라의 속성을 담고 있는 객체
  * 전/후면, 플래시 지원여부 등등
* **CameraDevice**
  * 카메라 객체
* **CameraRequest**
  * 촬영이나 미리보기를 요청할 때 쓰이는 객체.
  * 카메라 설정 변경할 때도 사용
* **CameraCaptureSession**
  * CaptureRequest를 보내고 카메라 하드웨어에서 결과를 받을 수 있는 세션.
  * 만들기 위해서는 camera device + surface 필요
  * surface는 camera device에서 사용 가능한 포맷과 크기가 일치하도록 미리 설정되어함.
  * 대상 surface는 SurfaceView, SurfaceTexture via Surface(SurfaceTexture), MediaCodec, MediaRecorder, Allocation, and ImageReader등 클래스들을 얻을 수 있음.
* **CaptureResult**
  * CaptureRequest의 결과. 이미지의 메타데이터도 가져올 수 있음.

<img src="image/image25.png"/>

CameraCaptureSession에 CaptureRequest를 보내는 것으로 동작함.

촬영 뿐 아니라 미리보기(Preview)도 CaptureRequest를 연속적으로 보냄으로 작동함.

그림의 buffer를 보면, SurfaceView를 사용해 바로 미리보기를 보낼 수 있고, SurfaceTexture나 RenderScript를 이용해 후처리를 하게 할 수도 있음.

사진을 찍으면 ImageReader로 Image를 줌. (기존 Camera1은 바로 ByteArray를 주었음)



* Surface
  * 카메라의 출력을 갖고있는 실제 메모리 버퍼.
  * surface의 크기를 설정하면, 매 프레임마다 얻는 이미지의 실제 사이즈를 결정한다.
  * 카메라에서 사용 가능한 각 포맷에서 이 버퍼를 만들 수 있는 사이즈의 집합을 가져올 수 있다
    (CameraCharacteristics 에서 꺼낼 수 있다는 뜻인듯.)





---



이미지 전처리를 어디서 하느냐에 따라 방법이 달라진다.

* RenderScript를 사용하는 경우
  * [HDR Viewfinder demo App](https://github.com/android/camera-samples/tree/main/HdrViewfinder) 여기에 예시가 잘 되어있음. SurfaceView 나 TextureView의 Surface를 Allocation에 연결하면 된다. 그리고 처리된 결과들을 Allocation에 write하고, Allocation의 ioSend()를 통해 보내면 된다.
* EGL shader-based 프로세싱을 하는 경우
  * EGLSurface(eglCreateWindowSurface 사용)에 surface를 연결해라. 
* native 프로세싱을 하는 경우
  * NDK 및 ANativeWindow methods를 사용해 자바로 부터 넘어온 Surface에 write 할 수 있고, ANativeWindow로 변환이 가능하다.
*  Java-level 프로세싱을 하는 경우
  * 매우 느릴것임.
  * Android M 의 ImageWriter class를 사용하거나, texture를 egl에 매 프레임마다 업로드를 할 수 있다.
* 이미지뷰에 매 프레임을 그릴 순 있지만 매우 느릴것임.

출처 : https://stackoverflow.com/questions/32725367/android-camera2-api-showing-processed-preview-image/32727163


Edit after clarification of the question; original answer at bottom

Depends on where you're doing your processing.

If you're using RenderScript, you can connect a Surface from a SurfaceView or a TextureView to an Allocation (with [setSurface](http://developer.android.com/reference/android/renderscript/Allocation.html#setSurface(android.view.Surface))), and then write your processed output to that Allocation and send it out with Allocation.ioSend(). The [HDR Viewfinder demo](https://github.com/googlesamples/android-HdrViewfinder) uses this approach.

If you're doing EGL shader-based processing, you can connect a Surface to an EGLSurface with [eglCreateWindowSurface](http://developer.android.com/reference/javax/microedition/khronos/egl/EGL10.html#eglCreateWindowSurface(javax.microedition.khronos.egl.EGLDisplay, javax.microedition.khronos.egl.EGLConfig, java.lang.Object, int[])), with the Surface as the native_window argument. Then you can render your final output to that EGLSurface and when you call eglSwapBuffers, the buffer will be sent to the screen.

If you're doing native processing, you can use the NDK [ANativeWindow methods](https://developer.android.com/ndk/reference/native__window_8h.html) to write to a Surface you pass from Java and [convert](https://developer.android.com/ndk/reference/native__window__jni_8h.html) to an ANativeWindow.

If you're doing Java-level processing, that's really slow and you probably don't want to. But can use the new Android M [ImageWriter](http://developer.android.com/reference/android/media/ImageWriter.html) class, or upload a texture to EGL every frame.

Or as you say, draw to an ImageView every frame, but that'll be slow.

------

Original answer:

If you are capturing JPEG images, you can simply copy the contents of the ByteBuffer from `Image.getPlanes()[0].getBuffer()` into a `byte[]`, and then use `BitmapFactory.decodeByteArray` to convert it to a Bitmap.

If you are capturing YUV_420_888 images, then you need to write your own conversion code from the 3-plane YCbCr 4:2:0 format to something you can display, such as a int[] of RGB values to create a Bitmap from; unfortunately there's not yet a convenient API for this.

If you are capturing RAW_SENSOR images (Bayer-pattern unprocessed sensor data), then you need to do a whole lot of image processing or just save a DNG.

