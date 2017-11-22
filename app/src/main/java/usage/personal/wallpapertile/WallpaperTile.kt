package usage.personal.wallpapertile

import android.app.WallpaperManager
import android.graphics.BitmapFactory
import android.service.quicksettings.TileService
import android.util.Log
import java.io.File

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

            // TODO: from choosenFolder take random image and put into newWP

            val newWP = BitmapFactory.decodeFile("")
            wpManager.setBitmap(newWP)
            qsTile.updateTile()
        }.run()
    }
}