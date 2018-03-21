package myspeechrecognitiontest.cn.tlrfid.speech

import android.annotation.SuppressLint
import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import cn.tlrfid.activity.BaseActivity
import com.iflytek.cloud.*
import com.iflytek.cloud.ui.RecognizerDialog
import com.iflytek.cloud.ui.RecognizerDialogListener
import com.iflytek.sunflower.FlowerCollector
import kotlinx.android.synthetic.main.activity_speech_recognition.*
import myspeechrecognitiontest.cn.tlrfid.speech.util.IatSetting
import myspeechrecognitiontest.cn.tlrfid.speech.util.JsonParser
import org.json.JSONException
import org.json.JSONObject

class SpeechRecognitionActivity : BaseActivity(), View.OnClickListener {

    //创建语音听写对象
    private var mSRA: SpeechRecognizer? = null
    //创建语音听写UI
    private var mSRADialog: RecognizerDialog? = null
    //存储听写的结果采用HashMap
    private var mSRAResults = HashMap<String, String>()

    var ret = 0
    private var mSharedPreferences: SharedPreferences? = null
    //引擎类型
    private val mEngineType = SpeechConstant.TYPE_CLOUD
    private var mTranslateEnable = false
    //指定语法类型    -----   ABNF（在线） 和 BNF（离线） 格式


    /**
     * 初始化监听器
     */
    private val mInitListener = InitListener {
        if (it != ErrorCode.SUCCESS) {
            showTip("初始化失败，错误码：" + it)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        masterContentViewResource = R.layout.activity_speech_recognition
        super.onCreate(savedInstanceState)
        start_speech.setOnClickListener(this)
        stop_speech.setOnClickListener(this)
        mSRA = SpeechRecognizer.createRecognizer(this, mInitListener)
        mSharedPreferences = getSharedPreferences(IatSetting.PREFER_NAME, Activity.MODE_PRIVATE)
    }

    override fun onClick(v: View?) {
        if (mSRA == null) {
            showTip("创建对象失败，请确认libmsc.so放置正确，且有调用 createUtility 进行初始化")
            return
        }
        when (v?.id) {
            R.id.start_speech -> {
                FlowerCollector.onEvent(this, "start_speech")
                your_speech_word.text = null
                mSRAResults.clear()
                setParam()
                val isShowDialog = mSharedPreferences?.getBoolean(getString(R.string.pref_key_iat_show), true)
                when (isShowDialog) {
                    true -> {
                        mSRADialog?.setListener(mRecognizerDialogListener)
                        mSRADialog?.show()
                        showTip(getString(R.string.text_begin))
                    }
                    false -> {
                        //不显示听写对话框
                        mSRA?.startListening(mRecognizerListener)
                        when (ret != ErrorCode.SUCCESS) {
                            true -> showTip("听写失败,错误码：" + ret)
                            false -> showTip(getString(R.string.text_begin))
                        }
                    }
                }
            }
            R.id.stop_speech -> {
                mSRA?.stopListening()
                showTip("停止听写")
            }
        }
    }

    private fun setParam() {
        //清空参数
        mSRA?.setParameter(SpeechConstant.PARAMS, null)
        //设置听写引擎
        mSRA?.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType)
        //设置返回结果格式
        mSRA?.setParameter(SpeechConstant.RESULT_TYPE, "json")
        mTranslateEnable = mSharedPreferences?.getBoolean(getString(R.string.pref_key_translate), false)!!
        if (mTranslateEnable) {
            mSRA?.setParameter(SpeechConstant.ASR_SCH, "1")
            mSRA?.setParameter(SpeechConstant.ADD_CAP, "translate")
            mSRA?.setParameter(SpeechConstant.TRS_SRC, "its")
        }
        val lag = mSharedPreferences?.getString("iat_language_preference", "mandarin")
        //设置语言
        when (lag == "en_us") {
            true -> {
                mSRA?.setParameter(SpeechConstant.LANGUAGE, "en_us")
                mSRA?.setParameter(SpeechConstant.ACCENT, null)
                if (mTranslateEnable) {
                    mSRA?.setParameter(SpeechConstant.ORI_LANG, "en")
                    mSRA?.setParameter(SpeechConstant.TRANS_LANG, "cn")
                }
            }
            false -> {
                mSRA?.setParameter(SpeechConstant.LANGUAGE, "en_us")
                //设置语言区域
                mSRA?.setParameter(SpeechConstant.ACCENT, lag)
                if (mTranslateEnable) {
                    mSRA?.setParameter(SpeechConstant.ORI_LANG, "en")
                    mSRA?.setParameter(SpeechConstant.TRANS_LANG, "cn")
                }
            }
        }
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mSRA?.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences?.getString("iat_vadbos_preference", "4000"))
// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mSRA?.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences?.getString("iat_vadeos_preference", "1000"))

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mSRA?.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences?.getString("iat_punc_preference", "1"))

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mSRA?.setParameter(SpeechConstant.AUDIO_FORMAT, "wav")
        mSRA?.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory().toString() + "/msc/iat.wav")

    }

    private var mRecognizerDialogListener = object : RecognizerDialogListener {
        override fun onResult(results: RecognizerResult, isLast: Boolean) {
            if (mTranslateEnable) {
                printTransResult(results)
            } else {
                printResult(results)
            }

        }

        /**
         * 识别回调错误.
         */
        override fun onError(error: SpeechError) {
            if (mTranslateEnable && error.errorCode == 14002) {
                showTip(error.getPlainDescription(true) + "\n请确认是否已开通翻译功能")
            } else {
                showTip(error.getPlainDescription(true))
            }
        }
    }

    /**
     * 听写监听器。
     */
    private val mRecognizerListener = object : RecognizerListener {

        override fun onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话")
        }

        override fun onError(error: SpeechError) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            if (mTranslateEnable && error.errorCode == 14002) {
                showTip(error.getPlainDescription(true) + "\n请确认是否已开通翻译功能")
            } else {
                showTip(error.getPlainDescription(true))
            }
        }

        override fun onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话")
        }

        override fun onResult(results: RecognizerResult, isLast: Boolean) {
            if (mTranslateEnable) {
                printTransResult(results)
            } else {
                printResult(results)
            }

            if (isLast) {
                // TODO 最后的结果
            }
        }

        override fun onVolumeChanged(volume: Int, data: ByteArray) {
            showTip("当前正在说话，音量大小：" + volume)
        }

        override fun onEvent(eventType: Int, arg1: Int, arg2: Int, obj: Bundle) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    }

    private fun printTransResult(results: RecognizerResult) {
        val trans = JsonParser.parseTransResult(results.resultString, "dst")
        val oris = JsonParser.parseTransResult(results.resultString, "src")

        if (TextUtils.isEmpty(trans) || TextUtils.isEmpty(oris)) {
            showTip("解析结果失败，请确认是否已开通翻译功能。")
        } else {
            your_speech_word.text = SpannableStringBuilder("原始语言:\n$oris\n目标语言:\n$trans")
        }

    }

    private fun printResult(results: RecognizerResult) {
        val text = JsonParser.parseIatResult(results.resultString)

        var sn: String? = null
        // 读取json结果中的sn字段
        try {
            val resultJson = JSONObject(results.resultString)
            sn = resultJson.optString("sn")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        mSRAResults[sn!!]= text

        val resultBuffer = StringBuffer()
        for (key in mSRAResults.keys) {
            resultBuffer.append(mSRAResults[key])
        }


        your_speech_word.text = SpannableStringBuilder(resultBuffer.toString())
        your_speech_word.setSelection(your_speech_word.length())
    }

    private fun showTip(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
    }

}