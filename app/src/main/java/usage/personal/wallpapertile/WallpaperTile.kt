package usage.personal.wallpapertile

import android.app.WallpaperManager
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

    override fun onClick() {
        super.onClick()
        Thread{
            val wpManager = WallpaperManager.getInstance(applicationContext)
            val path = MainActivity.getKEY(applicationContext, MainActivity.pathKey).toString()
            if(path == MainActivity.defaultPath){
                val allImagesInGallery = getImagesPath()
                if(allImagesInGallery.isNotEmpty()) {
                    val randomNumber = (0..allImagesInGallery.size).random()
                    Log.i(tag, "Random Image Path: "+ allImagesInGallery[randomNumber])
                    val newWP = BitmapFactory.decodeFile(allImagesInGallery[randomNumber])
                    wpManager.setBitmap(newWP)
                } else {
                    Toast.makeText(applicationContext,"You have no image in this file!", Toast.LENGTH_SHORT).show()
                }
            } else {
                val chosenFolder = File(path)
                val imageListInChosenFolder = chosenFolder.listFiles().filter{
                    !it.isDirectory && mimeTypeChecker.getContentType(it).startsWith("image/")
                }
                Log.i(tag, "Chosen Folder Path: "+ chosenFolder.path)
                if(imageListInChosenFolder.isNotEmpty()) {
                    val randomNumber = (0..imageListInChosenFolder.size).random()
                    Log.i(tag, "Random Image Path: "+ imageListInChosenFolder[randomNumber].absolutePath)
                    val newWP = BitmapFactory.decodeFile(imageListInChosenFolder[randomNumber].absolutePath)
                    wpManager.setBitmap(newWP)
                } else {
                    Toast.makeText(applicationContext,"You have no image in this file!", Toast.LENGTH_SHORT).show()
                }
            }
        }.run()
    }
/*
    private fun selectSubFiles(chosenFolder: File) {
        imagesListWithSubDirectories.addAll(
                chosenFolder.listFiles().filter{
                    if(it.isDirectory) { selectSubFiles(it) }
                    mimeTypeChecker.getContentType(it).startsWith("image/")
                })
    }
*/
    private fun ClosedRange<Int>.random() = Random().nextInt(endInclusive - start) +  start

    private fun getImagesPath() : MutableList<String>  {
        val uri: Uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val listOfAllImages: MutableList<String> = mutableListOf()
        val cursor: Cursor
        val columnIndexData: Int
        var pathOfImage: String
        val projection: Array<String> = arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

        cursor = contentResolver.query(uri, projection, null,
                null, null)

        columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        while (cursor.moveToNext()) {
            pathOfImage = cursor.getString(columnIndexData)
            listOfAllImages.add(pathOfImage)
        }
        cursor.close()
        return listOfAllImages
    }
}