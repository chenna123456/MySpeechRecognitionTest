package myspeechrecognitiontest.cn.tlrfid.speech.util

import android.app.Application
import com.iflytek.cloud.SpeechUtility
import myspeechrecognitiontest.cn.tlrfid.speech.R

class MyApplication : Application() {
    override fun onCreate() {
        SpeechUtility.createUtility(this@MyApplication, "appid=" + getString(R.string.app_id))
        super.onCreate()
    }
}