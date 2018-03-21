package myspeechrecognitiontest.cn.tlrfid.speech

import android.annotation.SuppressLint
import android.os.Bundle
import android.speech.SpeechRecognizer
import cn.tlrfid.activity.BaseActivity
import com.iflytek.cloud.SpeechConstant
import com.iflytek.cloud.ui.RecognizerDialog

class SpeechRecognitionActivity : BaseActivity() {
    //创建语音听写对象
    private var mSRA: SpeechRecognizer? = null
    //创建语音听写UI
    private var mSRADialog: RecognizerDialog? = null
    //存储听写的结果采用HashMap
    private var mSRAResults = HashMap<String, String>()
    //引擎类型
    private val mEngineType = SpeechConstant.TYPE_CLOUD

    //指定语法类型    -----   ABNF（在线） 和 BNF（离线） 格式

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        masterContentViewResource = R.layout.activity_speech_recognition
        super.onCreate(savedInstanceState)
    }


}