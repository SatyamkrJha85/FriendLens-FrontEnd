import androidx.compose.runtime.Composable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.UIKit.UIImage
import platform.UIKit.UIImageWriteToSavedPhotosAlbum

@Composable
actual fun rememberImageDownloader(): (String) -> Unit {
    return { url ->
        GlobalScope.launch(Dispatchers.Default) {
            try {
                val nsUrl = NSURL(string = url)
                val data = NSData.dataWithContentsOfURL(nsUrl)
                if (data != null) {
                    val image = UIImage(data = data)
                    if (image != null) {
                        withContext(Dispatchers.Main) {
                            UIImageWriteToSavedPhotosAlbum(image, null, null, null)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
