package myspeechrecognitiontest.cn.tlrfid.speech.util

import android.content.Context
import android.preference.EditTextPreference
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.Toast
import java.util.regex.Pattern

class SettingTextWatcher(private var context: Context,
                         private var mEditTextPreference: EditTextPreference,
                         private var minValue: Int, private var maxValue: Int) : TextWatcher {
    private var editStart: Int? = null
    private var editCount: Int? = null
    override fun afterTextChanged(s: Editable?) {
        if (TextUtils.isEmpty(s)) {
            return
        }
        val content = s.toString()
        if (isNumeric(content)) {
            val num = content.toInt()
            when (num > maxValue || num < minValue) {
                true -> {
                    s?.delete(editStart!!, editStart!! + editCount!!)
                    mEditTextPreference.editText.text = s
                    Toast.makeText(context, "超出有效值范围", Toast.LENGTH_LONG).show()
                }
                false -> {
                    s?.delete(editStart!!, editStart!! + editCount!!)
                    mEditTextPreference.editText.text = s
                    Toast.makeText(context, "只能输入数字哦", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        editStart = start
        editCount = count
    }

    companion object {
        fun isNumeric(str: String): Boolean {
            val pattern = Pattern.compile("[0-9]]*")
            return pattern.matcher(str).matches()
        }
    }
}