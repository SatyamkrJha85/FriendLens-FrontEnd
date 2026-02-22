import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch
import rememberImagePicker
import rememberCameraPicker

class PhotoCaptureScreen(val groupId: String? = null) : Screen {
    override val key = uniqueScreenKey

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        var selectedMode by remember { mutableStateOf("PHOTO") }
        var isUploading by remember { mutableStateOf(false) }

        val handleImagePicked: (ByteArray?) -> Unit = { bytes ->
            appLog("ImagePicked Callback Triggered - Bytes received: ${bytes?.size} bytes")
            if (bytes != null && groupId != null) {
                appLog("ImagePicked: Valid bytes and groupId ($groupId), starting upload...")
                isUploading = true
                scope.launch {
                    try {
                        appLog("Calling FriendLensApi.uploadPhoto")
                        val response = FriendLensApi.uploadPhoto(groupId, bytes)
                        appLog("Upload success! Response status: ${response.status}")
                        
                        // Trigger a cache refresh internally
                        appLog("Refreshing group photos cache...")
                        val photoResp = FriendLensApi.getGroupPhotos(groupId)
                        DataCache.updateGroupPhotos(groupId, photoResp.photos)
                        appLog("Cache refresh complete. Found ${photoResp.photos.size} photos.")
                        
                        sendLocalNotification("Moment Captured!", "Uploaded to your shared album...")
                        navigator.pop()
                    } catch (e: Exception) {
                        appLog("ERROR during upload: ${e.message}")
                        e.printStackTrace()
                    } finally {
                        isUploading = false
                    }
                }
            } else if (bytes != null) {
                appLog("Bytes received but NO groupId! Simulating local save.")
                // If there's no group ID, just simulate or prompt user to select a group (mock for now)
                sendLocalNotification("Moment Captured!", "Select an album to share to!")
                navigator.pop()
            } else {
                appLog("bytes was NULL. Likely user cancelled picking.")
            }
        }

        val launchCamera = rememberCameraPicker(handleImagePicked)
        val launchGallery = rememberImagePicker(handleImagePicked)

        Box(modifier = Modifier.fillMaxSize().background(BackgroundLight)) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 40.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        elevation = 4.dp,
                        modifier = Modifier.size(44.dp).clickable { navigator.pop() }
                    ) {
                        Box(contentAlignment = Alignment.Center) { IconBack(TextPrimary, 20f) }
                    }
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = "Add Memory",
                        style = MaterialTheme.typography.h2.copy(fontSize = 22.sp),
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Camera Option
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .shadow(16.dp, RoundedCornerShape(24.dp))
                        .clickable { launchCamera() },
                    shape = RoundedCornerShape(24.dp),
                    color = BrandPrimary
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.linearGradient(listOf(BrandPrimary, Color(0xFFFB7185))))
                            .padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconCamera(Color.White, 48f)
                        Spacer(Modifier.width(20.dp))
                        Column {
                            Text("Capture Photo", style = MaterialTheme.typography.h3.copy(color = Color.White, fontSize = 20.sp))
                            Text("Use your device camera", style = MaterialTheme.typography.body2.copy(color = Color.White.copy(0.8f)))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Gallery Option
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .shadow(16.dp, RoundedCornerShape(24.dp))
                        .clickable { launchGallery() },
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = BrandPrimary.copy(0.1f),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                IconAlbum(BrandPrimary, 32f)
                            }
                        }
                        Spacer(Modifier.width(20.dp))
                        Column {
                            Text("Pick from Gallery", style = MaterialTheme.typography.h3.copy(color = TextPrimary, fontSize = 20.sp))
                            Text("Choose an existing photo", style = MaterialTheme.typography.body2.copy(color = TextSecondary))
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1.5f))
            }

            // Upload Overlay Map
            if (isUploading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = BrandPrimary, strokeWidth = 3.dp)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Developing...",
                            style = MaterialTheme.typography.caption.copy(color = Color.White, fontSize = 14.sp)
                        )
                    }
                }
            }
        }
    }
}
