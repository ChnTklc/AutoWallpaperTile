package usage.personal.wallpapertile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log

class AskPermission: AppCompatActivity() {

    private val permissionCode = 0
    private val tag = "Ask Permission"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermission()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permissionCode -> {
                if (grantResults.all { it  == PackageManager.PERMISSION_GRANTED}) {
                    Log.i(tag, "All permissions granted.")
                    MainActivity.setKEY(this, MainActivity.checkPermission, "1" )
                    finish()
                } else finish()
            }
        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.SET_WALLPAPER) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.SET_WALLPAPER),
                    permissionCode)
        } else {
            Log.i(tag, "All permission already granted.")
            MainActivity.setKEY(this, MainActivity.checkPermission, "1" )
            finish()
        }
    }
}