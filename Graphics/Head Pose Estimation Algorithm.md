# Head Pose Estimation Algorithm

컴퓨터 비전에서 물체의 포즈는 카메라를 기준으로 상대적인 방향과 위치를 가리킨다. 카메라를 기준으로 개체를 이동하거나, 개체를 기준으로 카메라를 이동해 포즈가 변경될 수 있다.

카메라에 대해서 3D 물체는 2가지 움직임이 존재한다.

1. Translation
   * 카메라를 (X, Y, Z) 위치에서 (X', Y', Z') 위치로 옮기는 것을 말한다.
   * Translation은 (X' - X, Y' - Y, Z' - Z)와 같은 벡터로 표현이 가능하다.
2. Rotation
   * 카메라를 각 X, Y, Z축을 기준으로 회전시킬 수 있다.
   * 회전을 나타내는 방법에는 오일러 각도(roll, pitch, yaw), 3*3 행렬, 회전의 방향(축) 들이 있다.

따라서, 3D 물체의 자세를 추정한다는 것은 결국 3개의 rotation, 3개의 translation, 총 6개의 숫자를 찾는 것과 같다.



## 자세 추정을 위해 필요한 것

![pose-estimation-requirements-opencv](image\pose-estimation-requirements-opencv.jpg)

자세 추정을 위해서는 3D 좌표와 2D 좌표 두 개의 좌표와 카메라의 고유 상수 값이 필요하다.

**1. 2D Coordinate (2D 좌표)**

   * Image에서 특정 몇 개의 좌표가 필요하다.
   * 얼굴의 경우 눈꼬리끝, 코끝, 입꼬리끝, 턱 등을 선택할 수 있다.
   * Dlib 라이브러리를 사용한다면 68개의 선택지 중 원하는 좌표를 사용하면 된다.

   

**2. 3D Coordinate (3D 좌표)**

   * 2D 좌표에 대응 되는 3D 좌표가 필요하다.

   * 실제 모델링을 해서 좌표를 구할 필요는 없다. 일반적인 사람의 3D 모델에서 임의의 좌표를 구해서 사용하면 된다.

   * 본인이 직접 Z축을 상상해서 입력해도 좋다. 하지만 정확도는 사용자에게 맡긴다.

   * 예시 코드에서는 아래 좌표들을 사용한다.

   * | Feature     | Coordinate                                         |
     | ----------- | -------------------------------------------------- |
     | 눈꼬리 양끝 | (-225.0f, 170.0f, -135.0), ( 225.0, 170.0, -135.0) |
     | 코 끝       | (0.0, 0.0, 0.0)                                    |
     | 입꼬리 양끝 | (-150.0, -150.0, -125.0), (150.0, -150.0, -125.0)  |
     | 턱 끝       | ( 0.0, -330.0, -65.0)                              |

   

**3. 카메라의 고유 상수**

   * 이 예제에서는 카메라가 보정되어있다고 가정하고 진행한다.
   * 본래 카메라의 렌즈 때문에 이미지의 가장자리로 갈수록 왜곡이 일어난다. 그러므로 영상 광학 중심과 왜곡 반경 상수를 알아내 카메라를 보정해야한다.
   * 여기에서는 이미지의 중앙을 기준으로 광학 중심을 근사치를 잡고, 이미지 폭 길이로 초점 길이(Focal length)를 근사치로 잡을 것이다. 또, 반경 왜곡(radial distortion)이 존재하지 않는다고 가정한다.



## 자세 추정 알고리즘의 원리

세계 좌표계, 카메라 좌표계, 이미지 좌표계. 이 3가지 좌표계를 사용해서 자세 추정을 진행할 수 있다.

![ImageFormationEquation](image\ImageFormationEquation.jpg)

위 좌표계가 어떻게 동작하는 지 이해하기 위해 이미지 형성 수식에 대해 알아볼것이다.

o는 카메라의 중심이고, 그림에 표시된 평면은 ImagePlane(영상 평면) 이다. 여기서 카메라 o로부터 점 P를 바라 볼 때 Image Plane에 투영되는 점 p가 어디에 있는지 알아내는 것이 중요하다.

세계 좌표계에서 3D 좌표인 (U, V, W)를 알고 있다고 가정했을 때, 카메라 좌표에 대한 세계 좌표인 회전 행렬 R(3*3)과 이동 벡터인 t를 알면 다음 방정식을 사용해 카메라 좌표계에 대한 P점의 위치 (X, Y, Z)를 계산할 수 있다.

쉽게 말하면 세계 좌표계의 (U, V, W)를 회전이나 이동 변형을 고려해 카메라 좌표계의 (X, Y, Z) 좌표를 구하는 것이다.

![quicklatex.com-1b208262a1d5f6acdcd254ebddec0fdc_l3](image\quicklatex.com-1b208262a1d5f6acdcd254ebddec0fdc_l3.png)

위 수식을 아래 식처럼 확장할 수 있다.

![quicklatex.com-73f41b5446719deea04d0ee6da28bd3c_l3](image\quicklatex.com-73f41b5446719deea04d0ee6da28bd3c_l3.png)



## DLT (Direct Linear Transform)

3D 모델의 점(U, V, W)들은 많이 알고 있지만 (위에서 샘플로써 다루는 6개의 얼굴 특징점들),  (X, Y, Z)좌표에 대해서는 알지 못한다. 2D 이미지를 인식해서 나오는 2D 좌표인 (x, y) 좌표만 알고 있다. 반경 왜곡(radial distortion)이 없는 경우 Image Plane 에서의 점p의 좌표 (x, y)는 다음과 같이 표현할 수 있다.

![quicklatex.com-e6ebcc605787cffb356913c8d95001d5_l3](image\quicklatex.com-e6ebcc605787cffb356913c8d95001d5_l3.png)

fx, fy는 각각 x방향과 y방향의 초점 길이이고, Cx, Cy는 광학 중심(optical center)이다.

(반경 왜곡(radial distortion)을 추가적으로 놓고 생각하면 굉장히 복잡해지기 때문에 단순한 계산을 위해 생략했다.)



어떤 이미지에서도 깊이에 대해 알 수 없기 때문에, 위 수식에 있는 "s"는 알 수 없는 척도(scale) 요인으로 존재한다. 

카메라 o 에서 P점으로 이어지는 선에서 Image Plane과 교차되는 점 p는 P의 Image이다.

카메라 중심 o와 P점을 연결하는 선을 따라 있는 모든 지점이 동일한 이미지를 생성한다. 즉, 위 수식을 사용하면 척도인 s 까지만 (X, Y, Z) 좌표를 얻을 수 있다.



위 내용까지 합치면 어떻게 풀면 될 지 바로 알았던 선형 수식을 망치는 아래의 이상한 수식이 나온다. 

![quicklatex.com-581c39f2dc31758efaeb53d8323148d6_l3](image\quicklatex.com-581c39f2dc31758efaeb53d8323148d6_l3.png)

위 형태의 수식은 직접 선형 변환(Direct Linear Transform) 이라는 선형 대수 방법을 사용해 풀 수 있다. 알 수 없는 척도(unknown scale) 때문에 선형의 수식 문제를 풀 수 없게 되는 경우 이 DLT를 사용하면 풀 수 있게 된다.



## DLT 솔루션은 정확한가?

위 DLT 방법은 그리 정확한 방법은 아니라고 한다.

그 이유는, 회전 행렬 R은 3개의 수를 갖고있지만, DLT 솔루션에서 사용되는 행렬은 9개의 숫자를 갖고 있기 때문이다. 그리고 DLT 솔루션에는 추정된 3*3 크기의 행렬을 회전행렬로 강제 하는 로직이 없다. 더 중요한건 DLT 솔루션은 올바른 목표 기능을 최소화 하지 않는 다는 점이다.

s 같은 요소가 없던 위 수식에서는 3D 점들을 2D 이미지에 투영 하므로써 3D 얼굴 점들의 2D 점의 위치를 예측할 수 있었다. 즉, R과 t만 알면 모든 3D 점에 대해 p를 찾을 수 있다.

만약 추정된 자세 (estimated pose)가 완벽하다면, 이미지에 투영된 3D 점들은 2D 얼굴 특징과 완벽하게 일치할 것이다. 그렇지 않다면, 재투영 오류(reprojection error)를 측정해 계산할 수 있다. 재투영 오류의 값은 투영된 3D 점과 2D 얼굴 특징점 사이의 거리의 제곱의 합이다.

아무튼, DLT 솔루션을 사용하면 대략적인 R과 t에 대한 추정치를 찾을 수 있다. DLT 솔류선을 개선하는 나이브한 방법중 하나는, R과 t의 값을 무작위로 변경하고 재투영 오류가 감소하는 지 체크하는 것이다. 그러면 새로운 자세 추정치를 얻을 수 있다. 더 나은 추정치를 찾기 위해 계속해서 R과 t의 값을 변경시키는 게 효과적일 수 있으나, 매우 느린 방법이다. R과 t값을 반복적으로 변경하는 (위와 같은 느린 방법) 이 원리를 원칙적으로 바꾼 이론이 있는데, 이게 바로 Levenberg-Marquardt 최적화 기법이다. 



## OpenCV solvePnP

OpenCV에서 solvePnP 와 solvePnPRansac 함수를 사용하면 자세를 추정할 수 있다.

solvePnP 메서드는 플래그를 파라미터로 자세 추정을 위한 몇 가지 알고리즘을 선택할 수 있게 구현 되어있다. SOLVEPNP_ITERATIVE 플래그를 디폴트로 사용하며, Levenberg-Marquardt 최적화 기법이 적용된 DLT 솔루션을 사용한다. SOLVEPNP_P3P 플래그는 자세 계산을 위해 3개의 점만 사용하며, solvePnPRansac 을 사용할 때만 사용해야한다.

OpenCV3에는 SOLVEPNP_DLS and SOLVEPNP_UPNP 두 메서드가 추가되었는데, SOLVEPNP_UPNP 는 내부 마라미터 또한 추정하려고 시도한다는 점에서 매우 흥미로운 함수다.



---



#### C++ 메서드 선언부

bool **solvePnP**(InputArray objectPoints, InputArray imagePoints, InputArray cameraMatrix, InputArray distCoeffs, OutputArray rvec, OutputArray tvec, bool useExtrinsicGuess=false, int flags=SOLVEPNP_ITERATIVE )

**Parameters:**

**objectPoints** – 세계 좌표계에서의 물체 좌표 배열. 3D 좌표의 벡터를 넘기면 된다. Nx3 또는 3xN 크기의 1개 채널 행렬을 넘겨도 된다. 또, 1xN 이나 Nx1 의 3채널 행렬을 넘겨도 된다. 벡터를 넘기는 게 가장 좋다.

**imagePoints** – 대응되는 image 점들의 배열. 2D 좌표의 벡터를 넘기면 된다. 위처럼 3을 2로 바꾼 형태의 행렬을 넣어도 된다.

**cameraMatrix** – Input camera matrix ![A = \begin{bmatrix} f_x & 0 & c_x \\ 0 & f_y & c_y \\ 0 & 0 & 1 \end{bmatrix}](image\cameraMatrix.png). Fx, Fy는 특정 상황에서 이미지의 너비로 근사할 수 있다. Cx, Cy 값은 이미지의 중앙값을 넣어준다. (image.cols / 2, image.rows / 2)

**distCoeffs** – 4, 5, 8, 12개 요소의 왜곡 계수의 벡터를 넣어주면 된다. [k1, k2, p1, p2, [k3, [k4, k5, k6], [s1, s2, s3, s4] ] ] 입력 벡터가 null이나 비어있다면 0괘곡 계수가 가정된다. 왜곡이 큰 Go-Pro같은 카메라로 작업하지 않는 이상 null로 설정해도 되는데, 왜곡이 심한 렌즈로 작업한다면 전체 카메라 보정이 필요하다.

**rvec** – 출력 회전 벡터 (여기에 출력받을 벡터를 넣으면 됨.)

**tvec** – 출력 이동 벡터 (여기에 출력받을 벡터를 넣으면 됨.)

**useExtrinsicGuess** – SOLVEPNP_ITERATIVE 플래그를 사용한다면 필요한 파라미터.  제공된 rvec값고 tvec 값을 각각 회전 및 변환벡터의 초기 근사치로 사용하고 이를 추가로 최적화 한다.

**flags** –
Method for solving a PnP problem:

**SOLVEPNP_ITERATIVE** Iterative method is based on Levenberg-Marquardt optimization. In this case, the function finds such a pose that minimizes reprojection error, that is the sum of squared distances between the observed projections imagePoints and the projected (using projectPoints() ) objectPoints .

**SOLVEPNP_P3P** Method is based on the paper of X.S. Gao, X.-R. Hou, J. Tang, H.-F. Chang “Complete Solution Classification for the Perspective-Three-Point Problem”. In this case, the function requires exactly four object and image points.

**SOLVEPNP_EPNP** Method has been introduced by F.Moreno-Noguer, V.Lepetit and P.Fua in the paper “EPnP: Efficient Perspective-n-Point Camera Pose Estimation”.

밑에는 **OpenCV 3**를 위한 플래그들.

**SOLVEPNP_DLS** Method is based on the paper of Joel A. Hesch and Stergios I. Roumeliotis. “A Direct Least-Squares (DLS) Method for PnP”.

**SOLVEPNP_UPNP** Method is based on the paper of A.Penate-Sanchez, J.Andrade-Cetto, F.Moreno-Noguer. “Exhaustive Linearization for Robust Camera Pose and Focal Length Estimation”. In this case the function also estimates the parameters f_x and f_y assuming that both have the same value. Then the cameraMatrix is updated with the estimated focal length.



## OpenCV solvePnPRansac

solvePnPRansac 메서드는 강력하게 pose를 추정하기 위해 [Random Sample Consensus ( RANSAC )](https://en.wikipedia.org/wiki/Random_sample_consensus) 를 사용한다는 점을 제외하고는 solvePnP와 매우 유사하다.

매우 잡음이 껴있는 일부 데이터 지점을 위해 사용한다면 매우 유용하게 쓰인다. 

예를 들어, 선을 2D 점에 적합시키는 문제를 해결할 때, 적합선에서 모든 점의 거리가 최소화되는 선형 최소 제곱을 사용하여 해결할 수 있다. 이제 완전히 잘못된 데이터 지점 하나가 생긴다면, 데이터 점이 최소 제곱 솔루션을 지배할 수 있으며 선의 추정치가 매우 잘못될 수 있다.

 RANSAC에서는 필요한 최소 점 수를 랜덤하게 선택하여 모수를 추정합니다. 선 적합 문제에서는 모든 데이터에서 랜덤하게 두 점을 선택하고 점을 통과하는 선을 찾는다. 선에 충분히 가까운 다른 데이터 점을 특이치라고 한다. 선에 대한 여러 추정치는 랜덤하게 두 점을 선택하여 얻으며, 특이치의 최대 개수가 있는 선을 올바른 추정치로 선택한다.

![255px-Line_with_outliers.svg](image\255px-Line_with_outliers.svg.png) ![255px-Fitted_line.svg](image\255px-Fitted_line.svg.png)





## 출처

https://learnopencv.com/head-pose-estimation-using-opencv-and-dlib/