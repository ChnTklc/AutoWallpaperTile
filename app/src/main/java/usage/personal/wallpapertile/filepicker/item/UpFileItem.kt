package usage.personal.wallpapertile.filepicker.item

/**
 * A [FileItem] that represents files and folders
 * @param name Name of the file. Always refers to @strings.up
*/
internal data class UpFileItem(override val name: String) : FileItem {

    /**
     * Flag for whether this is a directory or a file. Up is always a directory
     */
    override val isDirectory = true
}