import java.io.File

fun File.folderIter(folderFunc: (it: File) -> Unit) {
    for (folder in listFiles()?.filter { it.isDirectory } ?: return) {
        folderFunc(folder)
    }
}