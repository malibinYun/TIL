# Camera2 API

![image/image13.png](image/image13.png)

![image/image25](image/image25.png)

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



## Camera2 활용순서

1. 권한을 먼저 물어봐야만 한다. 그렇지 않으면 모든 카메라 관련 로직에서 Permission Exception 발생

2. System Service를 통해 CameraManager를 가져온다.

   ```kotlin
   val manager = activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
   ```

   * manager를 통해 카메라 식별자들을 가져올 수 있음. (탑재된 카메라의 수에 따라 개수가 다름)

     ```kotlin
     val camaraIds: Array<String> = manager.cameraIdList
     // ["0", "1", "2"] 처럼 숫자인 문자열 배열이 떨어짐
     ```
   * manager를 통해 카메라의 속성들을 갖고있는 객체를 가져올 수 있음.
   
     ```kotlin
     val characteristics: CameraCharacteristics = manager.getCameraCharacteristics(cameraId)
     
     characteristics.get(CameraCharacteristics.LENS_FACING_FRONT)
     characteristics[CameraCharacteristics.LENS_FACING_FRONT]
     // get을 구현해뒀기 때문에 위 같은 방식으로 해당 카메라의 속성 정보를 꺼낼 수 있음.
     ```
   
     | 속성명 (CameraCharacteristics)  | 속성 정보                                                    | 반환 타입              | 비고                |
   | ------------------------------- | ------------------------------------------------------------ | ---------------------- | ------------------- |
   | LENS_FACING_FRONT               | 해당 카메라가 전면 카메라인지                                | Boolean                | 아니면 null 반환    |
   | LENS_FACING_BACK                | 해당 카메라가 후면 카메라인지                                | Boolean                | 아니면 null 반환    |
   | SCALER_STREAM_CONFIGURATION_MAP | 해당 카메라 장치에서 지원하는<br />모든 출력 형식(각 형식에 대한 크기) 목록 | StreamConfigurationMap |                     |
   | SENSOR_ORIENTATION              | 출력 이미지를 올바른 방향으로<br />돌리기 위해 필요한 각도.<br />카메라 하드웨어 자체의 회전된 값 | Int                    | 카메라 당 고정 값임 |
   
3. Camera의 속성을 가지고 그 중 원하는 크기와 형식의 image format을 받을 ImageReader객체를 만든다.

   ```kotlin
   imageReader = ImageReader.newInstance(
       MAX_PREVIEW_WIDTH,
       MAX_PREVIEW_HEIGHT,
       ImageFormat.JPEG,
       2
   ).apply {
       setOnImageAvailableListener(onImageAvailableListener, backgroundHandler)
   }
   ```

   만들때는OnImageAvailableListener, backgroundHandler가 필요함. 

   ```kotlin
   private val onImageAvailableListener = ImageReader.OnImageAvailableListener {
       val image = it.acquireNextImage()
       image.close()
   }
   ```

   카메라로부터 캡처를 하는 경우 해당 메서드로 Callback 됨.

   파라미터로는 Image 객체가 넘어오는데, 해당 객체를 acquire해서 반드시 사용해야하고, close를 마지막에 불러줌으로써 메모리 해제를 시켜주어야함. 그렇지 않으면 메모리에 쌓이고, Exception이 발생하거나, Image를 담는 Queue에 10개정도 쌓이면 카메라 자체가 멈추어버림. (앱은 죽지않음)
   

4. CameraManager를 통해 Camera를 연다

   ```kotlin
   manager.openCamera(cameraId, stateCallback, backgroundHandler)
   ```

   ```kotlin
   private val stateCallback = object : CameraDevice.StateCallback() {
       override fun onOpened(cameraDevice: CameraDevice) {
           this@ImageViewCameraActivity.cameraDevice = cameraDevice
           createCameraPreviewSession(cameraDevice)
       }
   
       override fun onDisconnected(cameraDevice: CameraDevice) {
           cameraDevice.close()
           this@ImageViewCameraActivity.cameraDevice = null
       }
   
       override fun onError(cameraDevice: CameraDevice, error: Int) {
           onDisconnected(cameraDevice)
           finish()
       }
   }
   ```

   카메라요청에 대해 상태가 콜백 메서드로 떨어짐. 일반적으로 onOpened가 실행 됨.

   카메라가 열린 다음에는, 이미지를 지속적으로 받을 수 있는 ImageReader와 카메라를 연결시켜주어야함.
   

5. 열려서 사용가능하게 된 CameraDevice로 CaptureSession을 연다.

   ```kotlin
   private fun createCameraPreviewSession(cameraDevice: CameraDevice) {
       val previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
           .apply { addTarget(imageReader?.surface!!) }
   
       cameraDevice.createCaptureSession(
           listOf(imageReader?.surface),
           object : CameraCaptureSession.StateCallback() {
   
               override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                   captureSession = cameraCaptureSession
                   previewRequestBuilder.set(
                       CaptureRequest.CONTROL_AF_MODE,
                       CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                   )
                   captureSession?.setRepeatingRequest(
                       previewRequestBuilder.build(),
                       null,
                       backgroundHandler
                   )
               }
   
               override fun onConfigureFailed(session: CameraCaptureSession) {
                   showToast("Failed")
               }
           }, null
       )
   }
   ```

   1. camera device로부터 captureRequestBuilder를 만든다.
   2. 카메라 프레임을 받을 Target은 imageReader 내부의 surface객체
   3. camera device로부터 capture session을 만든다. target으로 설정했던 imageReader의 surface도 넣는다.
   4. CaptureSession이 만들어지면 onConfigure 콜백을 실행해준다. 이 때 프레임단위로 이미지를 계속 얻어야하기 때문에 Repeating Request를 설정해준다. (하지 않으면 한 번만 이미지를 갖고올 수 있음.)
   5. 여기까지 설정이 끝나면 ImageReader Callback이 프레임단위로 호출 된다.
      

6. Surface를 사용해서 카메라를 보여주는 방법은 (SurfaceCameraActivity)

   1. 5번 처럼 CaptureSession을 여는것 까지는 똑같다.

   2. textureView 내부의 surfaceTexture를 가지고 Surface 객체를 만든다.

      ```kotlin
      val surfaceTexture = findViewById<TextureView>(R.id.textureView).surfaceTexture
      val surface = Surface(surfaceTexture)
      ```

   3. addTarget을 해당 surface로 교체한다. imageReader의 target은 지운다.

   4. imageReader객체는 필요없다. 남겨두고 target을 설정해도 상관은없다. 두 곳에서 이미지 프레임을 읽어올 수 있게 된다.



# 프로젝트 주의사항

1. 샘플 프로젝트에서는 image를 프레임단위로 받아서 해당 이미지를 bitmap으로 변환하고, imageview에 갱신하는 형태이다.
   아 방법은 java 레벨에서 이미지 프로세싱을 편하게 하기 위해서 만들어진 꼼수이기 때문에 성능이 매우 느리다. 본래 surface로 받아서 이미지 전처리를 하거나, 그 이전에 native 레벨에서 이미지 전처리를 수행해야 속도 개선을 할 수 있다.
2. 정적 값으로 해상도를 설정한다.
   * 모든 값의 결정은 동적으로 수행해야한다. -> 요즘 폰에 탑재되는 카메라는 2개 이상이며, 각 카메라마다 설정이 모두 다르기 때문이다. (가져올 수 있는 해상도, 하드웨어 회전 상태 등)
   * 현재는 camera id list 중 후면 카메라 중 가장 첫번째 리스트에 들어가있는 카메라를 불러온다.
   * 해상도는 1080 * 1920 으로 고정해두었다.
3. 이미지를 동적으로 회전해야한다. (정적으로 회전시키는중.)
   * 위 처럼 카메라 하드웨어 자체가 회전되어서 탑재 되어있는 경우, SENSOR_ORIENTATION에 해당 각도가 명시되어있다. onImageAvailableListener 에 90도 만큼 bitmap을 회전시키는 로직이 들어가있는데, 이는 후면카메라가 270도 돌아간 상태로 탑재되어있기 때문이다. 실제로 회전하는 로직을 빼면, 270도 화면이 preview에 표시된다.
4. 버그 존재
   * 분명 이미지는 1080 * 1920 으로 받아오는데, Bitmap Decode를 진행하면 1440 * 1440 으로 바뀌어서 정사각형으로 보인다. 매우 심각한 버그인데, 원인을 찾지 못하는 중...



# 용어

### Surface

* 카메라의 출력을 갖고있는 실제 메모리 버퍼.
* surface의 크기를 설정하면, 매 프레임마다 얻는 이미지의 실제 사이즈를 결정한다.
* 카메라에서 사용 가능한 각 포맷에서 이 버퍼를 만들 수 있는 사이즈의 집합을 가져올 수 있다
  (CameraCharacteristics 에서 꺼낼 수 있다.)





