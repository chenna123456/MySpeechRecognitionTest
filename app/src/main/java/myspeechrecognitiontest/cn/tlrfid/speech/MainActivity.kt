package myspeechrecognitiontest.cn.tlrfid.speech

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import cn.tlrfid.activity.BaseActivity
import cn.tlrfid.activity.extend.startActivityByClass
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableMain = true
        masterContentViewResource = R.layout.activity_main
        super.onCreate(savedInstanceState)
    }

    fun speechRecognition(view: View) {
        startActivityByClass(SpeechRecognitionActivity::class.java)
    }
}
