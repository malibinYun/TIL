# YUV Image Format

**YUV**는 Color Encoding 시스템이다.

사람의 인식을 고려해 Color Image나 video를 인코딩 하는 시스템이다. 이 인코딩은 색상 요소에 대한 대역폭 감소를 가능하게 한다. 전송 오류나 압축 때문에 이미지가 이상하게 보일 수 있는데, 일반적으로 직접 RGB 값을 사용 한 것보다 더 효율적으로 이미지가 이상하지 않은 것 처럼 사람 눈을 속일 수 있다. 

YUV는 루마 컴포넌트(Y)와 2개의 색차 컴포넌트로 (파랑(U), 빨강(V)) 이루어져 있다.

Y 루마 성분은 밝기를 나타내며, 흑백으로 이루어져있다. 이전 흑백 TV에서 이 형식을 사용했고, 컬러 TV가 나올 때는 Y 성분에 색차 성분 2개가 추가된 형식이다.

색채 성분인 UV 성분을 빼버리고 Y의 성분으로만 사용하면 흑백사진을 바로 얻을 수 있다. 해당 이미지를 Bitmap으로 변형해야하는 경우 (3채널 컬러) 나머지 채널의 값을 모두 동일 Y값으로 채우면 된다. (회색)

![150px-Barn-yuv](image\150px-Barn-yuv.png) ![800px-Yuv420.svg](image\800px-Yuv420.svg.png)

위 그림 기준으로,

* Y가 있는 위 4행을 Y Plane이라 칭한다.
* UV가 있는 아래 2행을 U/V Plane이라 부른다.
* YUV420 기준으로, 작은 사각형 하나가 8bit 용량을 가진다. (YUV_420_888)



### YUV 형식

#### 4:4:4 Format

![aa904813.yuvformats01(en-us,vs.80)](image\aa904813.yuvformats01(en-us,vs.80).gif)

픽셀 당 32Bit 차지하는 포맷



#### 4:2:2 Format

픽셀 당 16bit를 차지하는 포맷.

* YUV2
  * ![aa904813.yuvformats02(en-us,vs.80)](image\aa904813.yuvformats02(en-us,vs.80).gif)
  * Data가 unsigned char로 취급될 수 있다.
* UYVY
  * ![aa904813.yuvformats03(en-us,vs.80)](image\aa904813.yuvformats03(en-us,vs.80).gif)
  * YUV2와 형태는 같으나, byte 순서가 반대이다. 색차 성분과 루마 성분의 byte가 뒤바뀌어있다.



#### 4:2:0 Format (픽셀 당 16bit)

* IMC1
  * ![aa904813.yuvformats04(en-us,vs.80)](image\aa904813.yuvformats04(en-us,vs.80).gif)
  * Y 성분들이 unsigned char 값 배열에서 가장 처음에 위치한다.
  * Y성분 전부 다음에 V 성분 전부, 그 다음에 U 성분 전부가 이어져있는 형태이다.
  * V, U Plane들이 Y와 같은 선상에 존재하기 때문에 위 사진 처럼 사용하지 않는 메모리 공간이 존재한다.
* IMC3
  * ![aa904813.yuvformats05(en-us,vs.80)](image\aa904813.yuvformats05(en-us,vs.80).gif)
  * U, V 성분의 위치가 서로 다르다는 점을 제외하고는 IMC1과 형태가 동일하다.



#### 4:2:0 Format (픽셀 당 12bit)

* IMC2
  * ![aa904813.yuvformats07(en-us,vs.80)](image\aa904813.yuvformats07(en-us,vs.80).gif)
  * V, U 성분이 반반으로 나뉘어 배치되어있는 것 말고는, IMC1 형식과 같다. 
  * IMC1에 비해서 메모리 공간을 효율적으로 사용한다.
  * NV12 Format 다음으로 두 번째로 많이 선호되는 포맷이다.
* IMC4
  * ![aa904813.yuvformats06(en-us,vs.80)](image\aa904813.yuvformats06(en-us,vs.80).gif)
  * 위 IMC2에서 U, V 성분의 위치가 서로 다르다는 점을 제외하고 모두 동일하다.
* YV12
  * ![aa904813.yuvformats08(en-us,vs.80)](image\aa904813.yuvformats08(en-us,vs.80).gif)
  * Y성분 전부 바로 다음에 V성분 전부가 붙어있고, 바로 다음 U성분 전부가 붙어있다.
  * 맨 위의 예시 포맷이 이 포맷
* NV12
  * ![aa904813.yuvformats09(en-us,vs.80)](image\aa904813.yuvformats09(en-us,vs.80).gif)
  * Y Plane 다음에 U,V로 이루어진 배열이 바로 붙어있다. 
  * UV로 이루어진 배열은 little-endian 의 word 값으로 주소가 매겨진다. LSB에는 U 값을, MASB에는 V 값을 포함한다.
  * DirectX의 VA에서는 NV12 4:2:0 픽셀 포맷이 가장 선호되는 포맷이다.
  * NV21 포맷은 NV12 포맷에서 U와 V의 순서가 거꾸로 되어있는 포맷이다.



### RGB888 to YUV 4:4:4 의 변환

```
Y = ( (  66 * R + 129 * G +  25 * B + 128) >> 8) +  16
U = ( ( -38 * R -  74 * G + 112 * B + 128) >> 8) + 128
V = ( ( 112 * R -  94 * G -  18 * B + 128) >> 8) + 128
```



## YUV to Bitmap 변환 예시

### YUV 4:4:4 to RGB888 의 변환

```
R = ( round( 1.164383 * (Y - 16)                          +  1.596027 * (V - 128)  ) )
G = ( round( 1.164383 * (Y - 16) - (0.391762 * (U - 128)) - (0.812968 * (V - 128)) ) )
B = ( round( 1.164383 * (Y - 16) +  2.017232 * (U - 128)                           ) )
```



#### C++ 예제 코드

```C++
void YUVImage::yuv2rgb(uint8_t yValue, uint8_t uValue, uint8_t vValue,
        uint8_t *r, uint8_t *g, uint8_t *b) const {
    int rTmp = yValue + (1.370705 * (vValue-128)); 
    // or fast integer computing with a small approximation
    // rTmp = yValue + (351*(vValue-128))>>8;
    int gTmp = yValue - (0.698001 * (vValue-128)) - (0.337633 * (uValue-128)); 
    // gTmp = yValue - (179*(vValue-128) + 86*(uValue-128))>>8;
    int bTmp = yValue + (1.732446 * (uValue-128));
    // bTmp = yValue + (443*(uValue-128))>>8;
    *r = clamp(rTmp, 0, 255);
    *g = clamp(gTmp, 0, 255);
    *b = clamp(bTmp, 0, 255);
}
```



### Android RenderScript 활용

```java
public Bitmap yuv420ToBitmap(byte[] yuvByteArray, int imageWidth, int imageHeight) {
    RenderScript renderScript = new RenderScript(context);
    Type.Builder yuvType = new Type.Builder(renderScript, Element.U8(renderScript))
            .setX(yuvByteArray.length);
    Allocation inputAllocation = Allocation.createTyped(
            renderScript, yuvType.create(), Allocation.USAGE_SCRIPT);

    Type.Builder rgbaType = new Type.Builder(renderScript, Element.RGBA_8888(renderScript))
            .setX(imageWidth)
            .setY(imageHeight);
    Allocation outputAllocation = Allocation.createTyped(
            renderScript, rgbaType.create(), Allocation.USAGE_SCRIPT);

    inputAllocation.copyFrom(yuvByteArray);
    scriptYuvToRgb.setInput(inputAllocation);
    scriptYuvToRgb.forEach(outputAllocation);

    Bitmap bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
    outputAllocation.copyTo(bitmap);
    
    return bitmap;
}
```

안드로이드에서 이미지 프로세싱 같은 단순 반복 계산 처리를 병렬적으로 처리할 수 있게 만들어 둔 툴이다.

Java 레벨에서 직접 반복문을 사용해서 처리하는 것 보다 훨씬 빠르다.



#### YUV Resize

```c++
typedef unsigned char uint8;

// yuvData는 맨 위 그림의 position of Byte Stream 그림과 같은 형태로 일자 형태의 Byte Stream임.
uint8_t* resizeYuv(const uint8_t* const yuvData, const int width, const int height, const int exponent, const int rawYuvLength) {
    int size = width / exponent * height / exponent * 3 / 2;
    uint8_t* out = new uint8_t[size];

    int i = 0;
    for (int y = 0; y < height; y += exponent) {
        for (int x = 0; x < width; x += exponent) {
            out[i] = yuvData[y * width + x];
            i++;
        }
    }
    for (int y = 0; y < height / 2; y += exponent) {
        for (int x = 0; x < width; x += (exponent * 2)) {
            if (i < rawYuvLength) {
                int index = (width * height) + (y * width) + x;
                out[i] = yuvData[index];
                i++;
                out[i] = yuvData[index + 1];
                i++;
            }
        }
    }
    return out;
}
```

exponent에 따라서 해당 배율로 이미지를 축소한다. 2, 4, 8배로 축소가 가능한 메서드이다.

Anti-aliasing이 적용되어있지 않은 순수히 가로 세로를 반토막 내는 축소 메서드이다.



#### Java Android Image 를 YUV Byte Array 추출하는 방법

```java
public static byte[] yuvBytesFromImage(Image image) {
    ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();
    ByteBuffer uBuffer = image.getPlanes()[1].getBuffer();
    ByteBuffer vBuffer = image.getPlanes()[2].getBuffer();

    int ySize = yBuffer.remaining();
    int uSize = uBuffer.remaining();
    int vSize = vBuffer.remaining();

    byte[] bytes = new byte[ySize + uSize + vSize];
    
    yBuffer.get(bytes, 0, ySize);
    vBuffer.get(bytes, ySize, vSize);
    uBuffer.get(bytes, ySize + vSize, uSize);
    return bytes;
}
```

ByteBuffer.get(bytes) 이 메서드를 호출 할 때 기기성능과 byte buffer의 크기에 비례해 시간이 걸린다.

S8+ 기준 1920*1080 이미지로 위 메서드 호출 시 약 30ms 소요



# 출처

[Microsoft YUV Format](https://docs.microsoft.com/en-us/previous-versions/aa904813(v=vs.80)?redirectedfrom=MSDN)

[WikiPedia](https://en.wikipedia.org/wiki/YUV)

[How to use YUV (YUV_420_888) Image in Android](https://blog.minhazav.dev/how-to-convert-yuv-420-sp-android.media.Image-to-Bitmap-or-jpeg/)

[YUV 포맷 정리 짧게(한국어)](https://blog.dasomoli.org/265/)