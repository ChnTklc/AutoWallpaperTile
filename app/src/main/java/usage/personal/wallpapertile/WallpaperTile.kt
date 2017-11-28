package usage.personal.wallpapertile

import android.app.WallpaperManager
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.service.quicksettings.TileService
import android.util.Log
import android.widget.Toast
import java.io.File
import java.util.Random
import javax.activation.MimetypesFileTypeMap

class WallpaperTile: TileService() {

    private val tag = "WallpaperTile"
    private val mimeTypeChecker = MimetypesFileTypeMap()
    //private val imagesListWithSubDirectories: MutableList<File> = mutableListOf()

    override fun onClick() {
        super.onClick()
        if (MainActivity.getKEY(this, MainActivity.checkPermission) == "1"){
            val wpManager = WallpaperManager.getInstance(applicationContext)
            val path = MainActivity.getKEY(applicationContext, MainActivity.pathKey).toString()
            if (path == MainActivity.defaultPath) {
                val allImagesInGallery = getAllImagesFromGallery()
                if (allImagesInGallery.isNotEmpty()) {
                    val randomNumber = (0..allImagesInGallery.size).random()
                    Log.i(tag, "Random Image Path: " + allImagesInGallery[randomNumber])
                    val newWP = BitmapFactory.decodeFile(allImagesInGallery[randomNumber])
                    wpManager.setBitmap(newWP)
                } else {
                    Toast.makeText(applicationContext, "You have no image in this file!", Toast.LENGTH_SHORT).show()
                }
            } else {
                val chosenFolder = File(path)
                val imageListInChosenFolder = chosenFolder.listFiles().filter {
                    !it.isDirectory && mimeTypeChecker.getContentType(it).startsWith("image/")
                }
                if (imageListInChosenFolder.isNotEmpty()) {
                    val randomNumber = (0..imageListInChosenFolder.size).random()
                    Log.i(tag, "Random Image Path: " + imageListInChosenFolder[randomNumber].absolutePath)
                    val newWP = BitmapFactory.decodeFile(imageListInChosenFolder[randomNumber].absolutePath)
                    wpManager.setBitmap(newWP)
                } else {
                    Toast.makeText(applicationContext, "You have no image in this file!", Toast.LENGTH_SHORT).show()
                }
            }
        } else startActivity(Intent(this, AskPermission::class.java))
    }

    /*private fun selectSubFiles(chosenFolder: File) {
        imagesListWithSubDirectories.addAll(
                chosenFolder.listFiles().filter{
                    if(it.isDirectory && !it.startsWith(".")) { selectSubFiles(it) }
                    mimeTypeChecker.getContentType(it).startsWith("image/")
                })
    }*/

    private fun ClosedRange<Int>.random() = Random().nextInt(endInclusive - start) +  start

    private fun getAllImagesFromGallery() : MutableList<String>  {
        val cursor = contentResolver.query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME),
                null, null, null)

        val columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        var pathOfImage: String
        val listOfAllImages: MutableList<String> = mutableListOf()
        while (cursor.moveToNext()) {
            pathOfImage = cursor.getString(columnIndexData)
            listOfAllImages.add(pathOfImage)
        }
        cursor.close()
        return listOfAllImages
    }
}