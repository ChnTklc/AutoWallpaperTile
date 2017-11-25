package usage.personal.wallpapertile

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.TextView
import tr.edu.iyte.filepicker.FilePicker
import tr.edu.iyte.filepicker.FilePickerMode

class MainActivity : AppCompatActivity() {

    private val writePermissionCode = 0
    private val setWallpaperPermissionCode = 1
    private val tag = "MainActivity"
    private lateinit var filePath: TextView
    private lateinit var chooseButton: Button
    private lateinit var clearButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkWritePermission() // ask permission until granted
        checkSetWallpaperPermission() // ask permission until granted
        filePath = findViewById(R.id.textView)
        chooseButton = findViewById(R.id.chooseButton)
        clearButton = findViewById(R.id.clearButton)
        filePath.text = getKEY(applicationContext, pathKey)

        chooseButton.setOnClickListener {
            Log.i(tag, "Choose Button: File picker should work")
            FilePicker(this@MainActivity, FilePickerMode.FOLDER_PICK){
                setKEY(applicationContext, pathKey, it)
                filePath.text = it
            }.show()
        }

        clearButton.setOnClickListener {
            Log.i(tag, "Clear Path: path cleared")
            removeKEY(pathKey)
            filePath.text = defaultPath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode){
            writePermissionCode -> {
                if(resultCode == PackageManager.PERMISSION_GRANTED)
                    Log.i(tag, "Write file permission granted.")
                else checkWritePermission()
            }
            setWallpaperPermissionCode -> {
                if(resultCode == PackageManager.PERMISSION_GRANTED)
                    Log.i(tag, "Set wallpaper permission granted.")
                else checkSetWallpaperPermission()
            }
        }
    }

    private fun checkWritePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), writePermissionCode)
        } else Log.i(tag, "Storage permission already granted.")
    }

    private fun checkSetWallpaperPermission() {
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.SET_WALLPAPER) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SET_WALLPAPER), setWallpaperPermissionCode)
        } else Log.i(tag, "Set wallpaper permission already granted.")
    }

    private fun removeKEY(KEY: String) {
        val editor = getSharedPreferences(sharedFileName, 0).edit()
        editor.remove(KEY)
        editor.apply()
        Log.i(tag, "Path key removed from shared preferences.")
    }

    companion object {
        val sharedFileName = "WallpaperFilePath"
        val defaultPath = "All files"
        val pathKey = "PathKey"

        fun setKEY(context: Context, KEY: String, path: String) {
            val editor = context.getSharedPreferences(sharedFileName, 0).edit()
            editor.putString(KEY, path)
            editor.apply()
            Log.i("MainActivity", "Path key added to shared preferences.")
        }

        fun getKEY(context: Context, KEY: String): CharSequence =
                context.getSharedPreferences(sharedFileName, 0).getString(KEY, defaultPath)
    }
}
