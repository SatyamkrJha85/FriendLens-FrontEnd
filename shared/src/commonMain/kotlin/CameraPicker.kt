import androidx.compose.runtime.Composable

@Composable
expect fun rememberCameraPicker(onImagePicked: (ByteArray?) -> Unit): () -> Unit
