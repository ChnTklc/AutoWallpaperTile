package usage.personal.wallpapertile

import android.app.WallpaperManager
import android.graphics.BitmapFactory
import android.service.quicksettings.TileService
import android.util.Log
import java.io.File
import java.util.Random

/**
 * Edited by Comteng on 22/11/2017.
 */
class WallpaperTile: TileService() {

    private val tag = "WallpaperTile"

    override fun onClick() {
        super.onClick()
        Thread{
            val wpManager = WallpaperManager.getInstance(applicationContext)
            val choosenFolder =  File(MainActivity.getKEY(applicationContext, MainActivity.pathKey).toString())
            Log.i(tag, "Choosen Folder Path: "+ choosenFolder.path)
            val imagesList = choosenFolder.listFiles()
            // TODO: check file if image or not
            val randomNumber = (0..imagesList.size).random()
            Log.i(tag, "Choosen Image Path: "+ imagesList[randomNumber].absolutePath)
            val newWP = BitmapFactory.decodeFile(imagesList[randomNumber].absolutePath)
            wpManager.setBitmap(newWP)
            qsTile.updateTile()
        }.run()
    }

    private fun ClosedRange<Int>.random() = Random().nextInt(endInclusive - start) +  start
}