# JNI(Java Native Interface)

> JVM(Java Virtual Machine)위에서 실행되고 있는 Java 코드가 native 응용프로그램, C, C++ 같은 다른언어들로 작성된 라이브러리들을 호출하거나 반대로 호출 되는 것을 가능케 하는 프로그래밍 프레임워크.
>
> \- 출처 : wikipedia

```kotlin
// Kotlin
class Foo {
    init {
        System.loadLibrary("HelloWorld")
    }
    external fun bar()
}
```



```java
// Java
public class Foo {
    static {
        System.loadLibrary("HelloWorld");
    }
    public native void bar();
}
```



```c++
extern "C" 
JNIEXPORT void JNICALL
Java_com_malibin_example_Foo_bar(JNIEnv *env, jobject obj)
{
    printf("Hello World!\n");
    return;
}
```

* 파일 확장자가 cpp라면 extern "C" 가 필요하다.



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
class Foo(val left: Int, val right: Int)
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
class Foo(val left: Int, val right: Int){
    constructor(): this(0, 0)
}
```

위 처럼 아무것도 없는 생성자를 하나 만들어준다면 해결 가능하긴 하다.



## Object Array 생성

```c++
static jobjectArray createJavaObjectArray(JNIEnv* env){
    int size = 10;
    jclass fooClass = env->FindClass("com/malibin/example/Foo");
    jobjectArray fooArray = (jobjectArray)env->NewObjectArray(size, fooClass, NULL);
    
    for(int = 0; i<size; i++){
        jmethodID constructor = env->GetMethodID(fooClass, "<init>", "()V");
        jobject fooObject = env->NewObject(detectedFaceClass, constructor);
        env->SetObjectArrayElement(fooArray, i, fooObject);
    }
    return jobjectArray
}
```

사이즈를 정해서 빈 Array를 먼저 만든 뒤

env->SetObjectArrayElement 함수를 사용해 각 Array의 내용을 채운다.



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

jint number = env->GetIntField(fooObject, fieldNumber);
jobject label = env->GetObjectField(fooObject, fieldLabel);
```

1. Signiture에 맞는 필드 아이디를 가져온다
2. 반환 Type에 맞는 필드를 호출한다.
3. Type에 맞는 필드를 가져온다.



## Java Method 접근

```kotlin
// Kotlin Code
class Foo() {
    fun isUnderZero(value: Float): Boolean
    
    fun plus(left: Int, right: Int): Int
}
```

```c++
// C++ code
jclass fooClass = env->FindClass("com/malibin/example/Foo");
jmethodID constructor = env->GetMethodID(fooClass, "<init>", "()V");
jobject fooObject = env->NewObject(fooClass, constructor);

jmethodID method_isUnderZero = env->GetMethodID(fooClass, "isUnderZero", "(F)Z");
jmethodID method_plus = env->GetMethodID(fooClass, "plus", "(II)I");

env->CallBooleanMethod(fooObject, method_isUnderZero, 0.5f);
env->CallIntMethod(fooObject, method_plus, 1, 3);
```

1. Signiture에 맞는 메서드 아이디를 가져온다
2. 반환 Type에 맞는 메서드를 호출한다.



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