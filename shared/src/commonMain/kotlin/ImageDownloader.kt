import androidx.compose.runtime.Composable

@Composable
expect fun rememberImageDownloader(): (String) -> Unit
