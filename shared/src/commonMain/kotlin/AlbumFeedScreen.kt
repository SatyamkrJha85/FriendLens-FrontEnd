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
                // Professional Header with Navigation
                item {
                    Column(modifier = Modifier.fillMaxWidth().background(Color.White).padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { navigator.pop() }) { IconBack(TextPrimary, 24f) }
                            Text(activeGroup?.name ?: "Album Feed", style = MaterialTheme.typography.h2, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }

                // Join Code Section
                item {
                    activeGroup?.let { group ->
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            shape = RoundedCornerShape(24.dp),
                            color = BrandPrimary,
                            elevation = 8.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("INVITE FRIENDS", style = MaterialTheme.typography.caption.copy(color = Color.White.copy(alpha = 0.8f), letterSpacing = 2.sp))
                                Text(group.joinCode, style = MaterialTheme.typography.h1.copy(color = Color.White, fontSize = 38.sp, letterSpacing = 4.sp))
                                Text("Hold to copy and share", style = MaterialTheme.typography.caption.copy(color = Color.White.copy(alpha = 0.6f)))
                            }
                        }
                    }
                }

                // Group Members / "Stories" mock
                item {
                    Column(modifier = Modifier.padding(vertical = 16.dp)) {
                        Text("Top Contributors", style = MaterialTheme.typography.h3, modifier = Modifier.padding(start = 24.dp, bottom = 12.dp))
                        LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            // Extract unique contributors
                            val contributors = photos.mapNotNull { it.uploadedByUsername }.distinct()
                            if (contributors.isEmpty()) {
                                items(3) { index -> StoryItemPlaceholder(index == 0) }
                            } else {
                                items(contributors) { name -> StoryItem(name) }
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
                            Image(painter = painterResource("drawable/onboarding_adventure.png"), contentDescription = null, modifier = Modifier.size(160.dp))
                            Text("No memories yet", style = MaterialTheme.typography.h3.copy(color = TextSecondary))
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
                    .padding(24.dp)
                    .size(64.dp)
                    .shadow(12.dp, CircleShape)
            ) {
                IconCamera(Color.White, 28f)
            }
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun StoryItem(name: String) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(BrandPrimary, Color(0xFFFDBA74))))
                    .padding(3.dp)
            ) {
                Surface(modifier = Modifier.fillMaxSize(), shape = CircleShape, color = Color.White) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(name.take(1).uppercase(), fontWeight = FontWeight.Bold, color = BrandPrimary, fontSize = 24.sp)
                    }
                }
            }
            Text(name, style = MaterialTheme.typography.caption.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold), modifier = Modifier.padding(top = 4.dp))
        }
    }

    @Composable
    fun StoryItemPlaceholder(isFirst: Boolean) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE5E7EB))
                    .padding(3.dp)
            ) {
                Surface(modifier = Modifier.fillMaxSize(), shape = CircleShape, color = Color.White) {
                    Box(contentAlignment = Alignment.Center) { IconProfile(Color(0xFFD1D5DB), 24f) }
                }
            }
            Text(if (isFirst) "Me" else "Friend", style = MaterialTheme.typography.caption.copy(color = TextSecondary))
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun PhotoFeedCard(photo: Photo) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .shadow(4.dp, RoundedCornerShape(24.dp))
                .background(Color.White, RoundedCornerShape(24.dp))
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFDBEAFE)), contentAlignment = Alignment.Center) {
                    Text((photo.uploadedByUsername ?: "U").take(1).uppercase(), style = MaterialTheme.typography.caption.copy(color = Color(0xFF2563EB), fontWeight = FontWeight.Bold))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(photo.uploadedByUsername ?: "Unknown", style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp))
                    Text(photo.uploadedAt?.split("T")?.get(0) ?: "Recently", style = MaterialTheme.typography.caption.copy(fontSize = 10.sp))
                }
                Spacer(Modifier.weight(1f))
                IconMore(TextSecondary, 20f)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .padding(horizontal = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource("drawable/onboarding_celebration.png"), // Placeholder
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconHeart(BrandPrimary, filled = true, size = 22f)
                Text("Like", style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(start = 8.dp))
                
                Spacer(Modifier.width(20.dp))
                
                IconComment(TextSecondary, 22f)
                Text("Comment", style = MaterialTheme.typography.caption.copy(color = TextSecondary), modifier = Modifier.padding(start = 8.dp))
                
                Spacer(Modifier.weight(1f))
                
                IconShare(TextSecondary, 20f)
            }
        }
    }
}
