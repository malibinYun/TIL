# Android Emoji

### Version

* android 2.3 (API 10 / 진저브레드)
  * 장치 자체가 이모지 입출력 표기를 지원하지 않음.
* android 4.1~4.2 (API 16~17 젤리빈)
  * 특정한 이모지를 볼 수 있으나, 내장 키보드 옵션은 없으며 제3사 키보드를 사용해서 이모지를 입력해야 한다.
* android 4.3 (API 18 젤리빈)
  * 이모지를 기본적으로 지원하지 않음.
  * iWnn IME 키보드를 활성화해서 흑백 이모지를 입력할 수 있다. 혹은 제3사 키보드를 다운로드한 후 컬러 이모지 입력도 가능하다.
* android 4.4 ~ 7.1+ (API 19+ 킷캣~)
  * 4.4 이상 버전에서는 구글 키보드를 사용해서 이모지를 추가할 수 있다. 기기의 내장 키보드에 이모지 옵션이 있을 가능성도 높다. 사용 가능한 이모지와 스타일은 안드로이드 버전에 따라 결정된다.
* android 7.0 + 이상 (API 24 누가)
  * 사람 피부색 변경할 수 있는 옵션 있다고함.



### [Android Emoji Compat](https://developer.android.com/guide/topics/ui/look-and-feel/emoji-compat?hl=ko)

EmojiCompat 을 사용하려면 처음에 초기화를 해줘야함.

```kotlin
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val fontRequest = FontRequest(
                "com.example.fontprovider",
                "com.example",
                "emoji compat Font Query",
                CERTIFICATES
        )
        val config = FontRequestEmojiCompatConfig(this, fontRequest)
        EmojiCompat.init(config)
    }
}
```

내부는 비동기로 작업을 수행한다.

AppCompat 클래스의 EmojiTextView / EmojiEditText / EmojiButton 위젯을 사용할 수 있다.



#### EmojiSpan

이모지 스팬으로 사용할 수 도있대.

```kotlin
val processed = EmojiCompat.get().process("neutral face \uD83D\uDE10")
```



커스텀 위젯을 만들 때 CharSequence를 사전 처리해서 Spanned 인스턴스를 렌더링할 수 있다고 함.

```kotlin
class MyTextView(context: Context) : AppCompatTextView(context) {
    private val emojiTextViewHelper: EmojiTextViewHelper by lazy(LazyThreadSafetyMode.NONE) {
        EmojiTextViewHelper(this).apply {
            updateTransformationMethod()
        }
    }

    override fun setFilters(filters: Array<InputFilter>) {
        super.setFilters(emojiTextViewHelper.getFilters(filters))
    }

    override fun setAllCaps(allCaps: Boolean) {
        super.setAllCaps(allCaps)
        emojiTextViewHelper.setAllCaps(allCaps)
    }
}


class MyEditText(context: Context) : AppCompatEditText(context) {
    private val emojiEditTextHelper: EmojiEditTextHelper by lazy(LazyThreadSafetyMode.NONE) {
        EmojiEditTextHelper(this).also {
            super.setKeyListener(it.getKeyListener(keyListener))
        }
    }

    override fun setKeyListener(input: KeyListener?) {
        input?.also {
            super.setKeyListener(emojiEditTextHelper.getKeyListener(it))
        }
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        val inputConnection: InputConnection = super.onCreateInputConnection(outAttrs)
        return emojiEditTextHelper.onCreateInputConnection(
                inputConnection,
                outAttrs
        ) as InputConnection
    }
}
```



### Android 4.4 (API 19) 주의사항

위 버전 이하에서는 EmojiCompat이 '작업 없음' 상태가 됨. EmojiTextView 이런게 다 일반 TextView로 동작함. EmojiCompat 인스턴스의 init() 이 호출되면, 바로 LOAD_STATE_SUCCEEDED 상태가 됨. 하지만 동작하지 않음. 19버전 이하에서는 쓸모가없다...



#### strings.xml에 Emoji 쓰는 법

```xml
<string name="emoji">Hi &#128072;</string>
```

Html 코드로 쓰며, Hex -> Decimal로 바꿔서 쓴다.

mysql에 emoji를 저장하려면 UTF-8mb4 로 저장해야한다.



이모지 스코프

```
<U+1F300> - <U+1F5FF>      # symbols & pictographs
<U+1F600> - <U+1F64F>      # emoticons
<U+1F680> - <U+1F6FF>      # transport & map symbols
<U+2600>  - <U+2B55>       # other
```

https://unicode.org/Public/emoji/3.0/emoji-data.txt



[유니코드 이모지 표준](https://unicode.org/reports/tr51/index.html#emoji_data)

[이모지 유니코드 테이블](https://apps.timwhitlock.info/emoji/tables/unicode#block-6c-other-additional-symbols)

[이모지 각 상세 정보](https://www.fileformat.info/info/unicode/char/1f448/index.htm)

[이모지피디아](https://emojipedia.org/emoji/%E2%9D%A4/)