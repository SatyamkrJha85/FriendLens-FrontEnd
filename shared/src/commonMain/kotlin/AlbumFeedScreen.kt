import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

class AlbumFeedScreen(val groupId: String) : Screen {
    @OptIn(ExperimentalResourceApi::class, ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        var photos by remember { mutableStateOf<List<Photo>>(emptyList()) }
        var activeGroup by remember { mutableStateOf<Group?>(null) }
        var isLoading by remember { mutableStateOf(true) }
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()

        LaunchedEffect(groupId) {
            try {
                val groupResp = FriendLensApi.getGroupDetail(groupId)
                activeGroup = groupResp.group
                val photoResp = FriendLensApi.getGroupPhotos(groupId)
                photos = photoResp.photos
            } catch (_: Exception) {}
            isLoading = false
        }

        Box(modifier = Modifier.fillMaxSize().background(BackgroundLight)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // Top Header with Join Code
                item {
                    activeGroup?.let { group ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(16.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .clickable { /* Copy Code */ },
                                shape = MaterialTheme.shapes.medium,
                                color = BrandPrimary,
                                elevation = 4.dp
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    // Background Blobs from Stitch Design
                                    Box(Modifier.align(Alignment.TopEnd).offset(x=20.dp, y=(-20).dp).size(60.dp).background(Color.White.copy(alpha=0.2f), CircleShape))
                                    
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text("JOIN ALBUM", style = MaterialTheme.typography.caption.copy(color = Color.White.copy(alpha = 0.8f), letterSpacing = 2.sp))
                                        Text(group.joinCode, style = MaterialTheme.typography.h1.copy(color = Color.White, fontSize = 32.sp, letterSpacing = 2.sp))
                                        Text("Share this code with friends", style = MaterialTheme.typography.caption.copy(color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Recent Stories Row
                item {
                    Column(modifier = Modifier.padding(vertical = 16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, bottom = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("My Moments", style = MaterialTheme.typography.h2.copy(fontSize = 20.sp))
                            IconPlus(TextPrimary, 20f)
                        }
                        LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(5) { index ->
                                StoryItem(index == 0)
                            }
                        }
                    }
                }

                if (isLoading) {
                    item { Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = BrandPrimary) } }
                } else if (photos.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(top = 60.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(painter = painterResource("drawable/empty_album.png"), contentDescription = null, modifier = Modifier.size(160.dp))
                            Text("No moments yet", style = MaterialTheme.typography.h3.copy(color = TextSecondary))
                            Text("Be the first to capture something!", style = MaterialTheme.typography.body2)
                        }
                    }
                } else {
                    items(photos) { photo ->
                        PhotoFeedCard(photo)
                    }
                }
            }

            // Fixed Camera FAB
            FloatingActionButton(
                onClick = { navigator.push(PhotoCaptureScreen()) },
                backgroundColor = BrandPrimary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(start = 0.dp, top = 0.dp, end = 24.dp, bottom = 24.dp)
                    .size(56.dp)
                    .shadow(12.dp, CircleShape)
            ) {
                IconCamera(Color.White, 24f)
            }
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun StoryItem(isFirst: Boolean) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(if (isFirst) Brush.linearGradient(listOf(BrandPrimary, Color(0xFFFDBA74))) else Brush.linearGradient(listOf(Color(0xFFE5E7EB), Color(0xFFD1D5DB))) )
                    .padding(3.dp)
            ) {
                Surface(modifier = Modifier.fillMaxSize(), shape = CircleShape, color = Color.White) {
                    Image(
                        painter = painterResource("drawable/onboarding_celebration.png"),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Text(if (isFirst) "Festival" else "Hiking", style = MaterialTheme.typography.caption.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold), modifier = Modifier.padding(top = 4.dp))
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun PhotoFeedCard(photo: Photo) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .shadow(2.dp, MaterialTheme.shapes.medium)
                .background(Color.White, MaterialTheme.shapes.medium)
        ) {
            // User Header
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFDBEAFE)), contentAlignment = Alignment.Center) {
                    Text((photo.uploadedBy ?: "U").take(2).uppercase(), style = MaterialTheme.typography.caption.copy(color = Color(0xFF2563EB), fontWeight = FontWeight.Bold))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(photo.uploadedByUsername ?: "Unknown User", style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp))
                    Text(photo.uploadedAt?.take(10) ?: "Just now", style = MaterialTheme.typography.caption.copy(fontSize = 10.sp))
                }
                Spacer(Modifier.weight(1f))
                IconMore(TextSecondary, 20f)
            }

            // Main Image Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(horizontal = 12.dp)
                    .clip(MaterialTheme.shapes.medium)
            ) {
                Image(
                    painter = painterResource("drawable/onboarding_capture.png"), // Placeholder
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Interaction Bar
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconHeart(BrandPrimary, filled = true, size = 22f)
                    Text("24", style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(start = 6.dp))
                    
                    Spacer(Modifier.width(16.dp))
                    
                    IconComment(TextSecondary, 22f)
                    Text("5", style = MaterialTheme.typography.caption.copy(color = TextSecondary), modifier = Modifier.padding(start = 6.dp))
                }
                
                IconShare(TextSecondary, 20f)
            }
        }
    }
}
