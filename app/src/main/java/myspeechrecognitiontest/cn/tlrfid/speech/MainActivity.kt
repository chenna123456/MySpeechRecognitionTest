package myspeechrecognitiontest.cn.tlrfid.speech

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.View
import cn.tlrfid.activity.BaseActivity
import cn.tlrfid.activity.extend.startActivityByClass

class MainActivity : BaseActivity() {

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableMain = true
        masterContentViewResource = R.layout.activity_main
        requestPermissions()
        super.onCreate(savedInstanceState)
    }

    private fun requestPermissions() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val permission = ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.LOCATION_HARDWARE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_SETTINGS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_CONTACTS), 0x0010)
                }

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 0x0010)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    fun speechRecognition(view: View) {
        startActivityByClass(SpeechRecognitionActivity::class.java)
    }
}
