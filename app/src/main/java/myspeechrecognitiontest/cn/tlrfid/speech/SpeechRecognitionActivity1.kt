package myspeechrecognitiontest.cn.tlrfid.speech

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import cn.tlrfid.activity.BaseActivity
import com.iflytek.cloud.ErrorCode
import com.iflytek.cloud.InitListener
import com.iflytek.cloud.SpeechConstant
import com.iflytek.cloud.SpeechRecognizer
import kotlinx.android.synthetic.main.activity_speech_recognition.*

class SpeechRecognitionActivity1 : BaseActivity() {
    private var mSRA: SpeechRecognizer? = null
    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        masterContentViewResource = R.layout.activity_speech_recognition
        super.onCreate(savedInstanceState)
        mSRA = SpeechRecognizer.createRecognizer(this, mInitListener)
        start_speech.setOnClickListener {
            //设置语法ID和 SUBJECT 为空，以免因之前有语法调用而设置了此参数；或直接清空所有参数，具体可参考 DEMO 的示例。
            mSRA?.setParameter(SpeechConstant.CLOUD_GRAMMAR, null)
            mSRA?.setParameter(SpeechConstant.SUBJECT, null)
            mSRA?.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD)
//            mSRA?.startListening(mRecogListener);
        }

    }

    /**
     * 初始化监听器
     */
    private val mInitListener = InitListener {
        if (it != ErrorCode.SUCCESS) {
            showTip("初始化监听器失败，错误码" + it)
        }
    }

    private fun showTip(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show()
    }
}