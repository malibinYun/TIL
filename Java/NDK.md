# NDK

## Java Object 생성

```c++
static jobject createJavaDetectedFace(....){
    jclass detectedFaceClass = env->FindClass("com/malibin/dlib/DetectedFace");
    jmethodID constructor = env->GetMethodID(detectedFaceClass, "<init>", "(Ljava/lang/String;IIII)V");
    return env->NewObject(detectedFaceClass, constructor, "string", 0, 0, 0, 0);
}
```

1. 만들 클래스를 가지고 온다
2. 해당 클래스의 생성자를 가지고 온다.
3. 위 두 값을 가지고 객체를 생성한다.

* 기본 포맷은 ()V 
  * 파라미터가 아무것도 없는 생성자이다.
  * 마지막 V는 void라는 뜻인 듯.

* () 괄호 안에 생성자의 타입 signature를 구분자 없이 넣어준다.
  * 만약 생성자의 파라미터가 String, int[], float라면  `(Ljava/lang/String;[IF)V`
  * 만약 생성자의 파라미터가 int, int, int라면  `(III)V`



#### 주의사항

class에 디폴트 생성자 (파라미터가 아무것도 없는 생성자)가 없는 경우에 ()V를 사용해 호출 하면 **NPE**가 터진다.

```kotlin
// Kotlin Code
class Foo(val left:Int, val right:Int)
```

```c++
// C++ Code
jclass fooClass = env->FindClass("com/malibin/example/Foo");
jmethodID constructor = env->GetMethodID(fooClass, "<init>", "()V");
return env->NewObject(fooClass, constructor);
```

위 Kotlin 클래스에는 디폴트 생성자가 없기 때문에 파라미터가 없는 생성자인 ()V를 사용해서 객체를 만들면 안된다.

```kotlin
// Kotlin Code
class Foo(val left:Int, val right:Int){
    constructor(): this(0, 0)
}
```

위 처럼 아무것도 없는 생성자를 하나 만들어준다면 해결 가능하긴 하다.





## Object Array 생성

```c++
static jobjectArray createJavaObjectArray(JNIEnv* env){
    int size = 10;
    jclass fooClass = env->FindClass("com/malibin/example/Foo");
    return (jobjectArray)env->NewObjectArray(size, fooClass, NULL);
}
```





## Java Field 접근

만든 객체의 Field에 값을 set하는 방법

```kotlin
// Kotlin Code
class Foo(val number: Int, val label: String)
```

```c++
// C++ code
jclass fooClass = env->FindClass("com/malibin/example/Foo");
jmethodID constructor = env->GetMethodID(fooClass, "<init>", "()V");
jobject fooObject = env->NewObject(fooClass, constructor);

jfieldID fieldNumber = env->GetFieldID(fooClass, "number", "I");
jfieldID fieldLabel = env->GetFieldID(fooClass, "label", "Ljava/lang/String;");

env->SetIntField(fooObject, fieldNumber, 10);
env->SetObjectField(fooObject, fieldLabel, "bar");
```





## Java Method 접근









## Java String 생성

만드는 방법은 여러가지인데, 발견할 때 마다 새롭게 추가한다.

```c++
static void foo(const std::string& label){
    jstring javaString = (jstring)(env->NewStringUTF(label.c_str()));
}
```





### Type Signature

| Type Signature            | Java Type             |
| ------------------------- | --------------------- |
| Z                         | boolean               |
| B                         | byte                  |
| C                         | char                  |
| S                         | short                 |
| I                         | int                   |
| J                         | long                  |
| F                         | float                 |
| D                         | double                |
| L fully-qualified-class ; | fully-qualified-class |
| [ type                    | type[]                |
| ( arg-types ) ret-type    | method type           |