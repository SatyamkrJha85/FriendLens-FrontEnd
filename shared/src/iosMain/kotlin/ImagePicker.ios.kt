import androidx.compose.runtime.Composable

@Composable
actual fun rememberImagePicker(onImagePicked: (ByteArray?) -> Unit): () -> Unit {
    return {
        // Not implemented for iOS without external libs or complex UIKit integration.
        // Return null or placeholder
        onImagePicked(null) 
    }
}
