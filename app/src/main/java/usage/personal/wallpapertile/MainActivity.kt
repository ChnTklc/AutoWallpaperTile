package usage.personal.wallpapertile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.TextView
import tr.edu.iyte.filepicker.FilePicker
import tr.edu.iyte.filepicker.FilePickerMode

class MainActivity : AppCompatActivity() {

    private lateinit var filePath: TextView
    private lateinit var chooseButton: Button
    private lateinit var clearButton: Button
    private val tag = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        filePath = findViewById(R.id.textView)
        chooseButton = findViewById(R.id.chooseButton)
        clearButton = findViewById(R.id.clearButton)
        filePath.text = getKEY(applicationContext, pathKey)

        if(getKEY(this, checkPermission) == "1") goAskPermission()

        chooseButton.setOnClickListener {
            if(getKEY(this, checkPermission) == "1") {
                Log.i(tag, "Choose Button: File picker should work")
                FilePicker(this@MainActivity, FilePickerMode.FOLDER_PICK) {
                    setKEY(applicationContext, pathKey, it)
                    filePath.text = it
                    Log.i(tag, "Chosen Folder Path: " + it)
                }.show()
            } else goAskPermission()
        }

        clearButton.setOnClickListener {
            Log.i(tag, "Clear Path: path cleared")
            removeKEY(pathKey)
            filePath.text = defaultPath
        }
    }

    private fun goAskPermission() = startActivity(Intent(this, AskPermission::class.java))

    private fun removeKEY(KEY: String) {
        val editor = getSharedPreferences(sharedFileName, 0).edit()
        editor.remove(KEY)
        editor.apply()
        Log.i(tag, "Path key removed from shared preferences.")
    }

    companion object {
        val sharedFileName = "WallpaperFile"
        val defaultPath = "All files"
        val pathKey = "PathKey"
        val checkPermission = "isPermissionGranted"

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
