package myspeechrecognitiontest.cn.tlrfid.speech.util

import android.os.Bundle
import android.preference.EditTextPreference
import android.preference.Preference
import android.preference.PreferenceActivity
import android.view.Window
import myspeechrecognitiontest.cn.tlrfid.speech.R

@Suppress("DEPRECATION")
class IatSetting : PreferenceActivity(), Preference.OnPreferenceChangeListener {
    private var mVadbosPreference: EditTextPreference? = null
    private var mVadeosPreference: EditTextPreference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        preferenceManager.sharedPreferencesName = PREFER_NAME
        addPreferencesFromResource(R.xml.iat_setting)
        mVadbosPreference = findPreference("iat_vadbos_preference") as EditTextPreference
        mVadbosPreference?.editText?.addTextChangedListener(SettingTextWatcher(this, mVadbosPreference!!, 0, 10000))

        mVadeosPreference = findPreference("iat_vadeos_preference") as EditTextPreference?
        mVadeosPreference?.editText?.addTextChangedListener(SettingTextWatcher(this, mVadeosPreference!!, 0, 10000))
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?) = true

    companion object {
        const val PREFER_NAME = "myspeechrecognitiontest.cn.tlrfid.speech.util.setting"
    }
}