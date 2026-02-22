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
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

class AlbumFeedScreen(val groupId: String) : Screen {
    @OptIn(ExperimentalResourceApi::class, ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val cachedPhotosMap by DataCache.photos.collectAsState()
        val photos = cachedPhotosMap[groupId] ?: emptyList()
        var activeGroup by remember { mutableStateOf<Group?>(null) }
        var isLoading by remember { mutableStateOf(photos.isEmpty()) }
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()

        LaunchedEffect(groupId) {
            try {
                val groupResp = FriendLensApi.getGroupDetail(groupId)
                activeGroup = groupResp.group
                val photoResp = FriendLensApi.getGroupPhotos(groupId)
                DataCache.updateGroupPhotos(groupId, photoResp.photos)
            } catch (_: Exception) {}
            isLoading = false
        }

        Box(modifier = Modifier.fillMaxSize().background(BackgroundLight)) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp).background(BrandPrimary.copy(0.05f)))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
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
                        Column {
                            Text(
                                text = activeGroup?.name ?: "Album Feed",
                                style = MaterialTheme.typography.h2.copy(fontSize = 22.sp),
                                maxLines = 1
                            )
                            Text(
                                text = "${photos.size} Memories captured",
                                style = MaterialTheme.typography.caption.copy(color = TextSecondary)
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        IconMore(TextSecondary, 24f)
                    }
                }

                item {
                    activeGroup?.let { group ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(horizontal = 20.dp)
                                .shadow(20.dp, RoundedCornerShape(32.dp)),
                            shape = RoundedCornerShape(32.dp),
                            color = BrandPrimary
                        ) {
                            val imgUrl = group.getPublicUrl()
                            if (!imgUrl.isNullOrEmpty()) {
                                KamelImage(resource = asyncPainterResource(data = imgUrl), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.4f)))
                            } else {
                                Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(BrandPrimary, Color(0xFFFB7185)))))
                            }
                                
                            Column(
                                modifier = Modifier.fillMaxSize().padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconGroup(color = Color.White.copy(0.6f), 16f)
                                    Spacer(Modifier.width(8.dp))
                                    Text("INVITE FRIENDS", style = MaterialTheme.typography.caption.copy(color = Color.White, letterSpacing = 2.sp))
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(group.joinCode, style = MaterialTheme.typography.h1.copy(color = Color.White, fontSize = 42.sp, letterSpacing = 6.sp, fontWeight = FontWeight.Black))
                                Spacer(Modifier.height(12.dp))
                                Surface(shape = CircleShape, color = Color.White.copy(0.2f)) {
                                    Text("Hold to share code", modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp), style = MaterialTheme.typography.caption.copy(color = Color.White))
                                }
                            }
                        }
                    }
                }

                item {
                    Column(modifier = Modifier.padding(vertical = 16.dp)) {
                        Text("Contributors", style = MaterialTheme.typography.h3, modifier = Modifier.padding(start = 24.dp, bottom = 16.dp))
                        LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                            val contributors = photos.mapNotNull { it.uploadedByUsername }.distinct()
                            if (contributors.isEmpty()) {
                                items(3) { index -> StoryItemPlaceholder(index == 0) }
                            } else {
                                items(contributors) { name -> StoryItem(name) }
                            }
                        }
                    }
                }

                if (isLoading && photos.isEmpty()) {
                    item { Box(Modifier.fillMaxWidth().padding(60.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = BrandPrimary) } }
                } else if (photos.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(top = 80.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(160.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(0.1f))
                                    .border(2.dp, BrandPrimary.copy(0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                IconCamera(BrandPrimary.copy(0.3f), 80f)
                            }
                            Spacer(Modifier.height(24.dp))
                            Text("No memories caught yet", style = MaterialTheme.typography.h3.copy(color = TextSecondary))
                            Text("Every journey starts with one photo.", style = MaterialTheme.typography.body2.copy(color = TextSecondary.copy(0.6f)))
                        }
                    }
                } else {
                    items(photos) { photo ->
                        PhotoFeedCard(photo)
                    }
                }
            }

            FloatingActionButton(
                onClick = { navigator.push(PhotoCaptureScreen(groupId)) },
                backgroundColor = BrandPrimary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
                    .size(68.dp)
                    .shadow(16.dp, CircleShape)
            ) {
                IconPlus(Color.White, 32f)
            }
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun StoryItem(name: String) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(BrandPrimary, Color(0xFFFDBA74))))
                    .padding(3.dp)
            ) {
                Surface(modifier = Modifier.fillMaxSize(), shape = CircleShape, color = Color.White) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(name.take(1).uppercase(), fontWeight = FontWeight.Black, color = BrandPrimary, fontSize = 26.sp)
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(name, style = MaterialTheme.typography.caption.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold), modifier = Modifier.width(76.dp), textAlign = TextAlign.Center, maxLines = 1)
        }
    }

    @Composable
    fun StoryItemPlaceholder(isFirst: Boolean) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF3F4F6))
                    .padding(2.dp)
            ) {
                Surface(modifier = Modifier.fillMaxSize(), shape = CircleShape, color = Color.White) {
                    Box(contentAlignment = Alignment.Center) { IconProfile(Color(0xFFE5E7EB), 26f) }
                }
            }
            Text(if (isFirst) "Add Friend" else "Friend", style = MaterialTheme.typography.caption.copy(color = TextSecondary))
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun PhotoFeedCard(photo: Photo) {
        val scope = rememberCoroutineScope()
        var isLiked by remember(photo.id) { mutableStateOf(false) }
        var likeCount by remember(photo.id) { mutableStateOf((5..20).random()) } // Stubbed like count

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp)
                .shadow(12.dp, RoundedCornerShape(20.dp))
                .background(Color.White, RoundedCornerShape(20.dp))
        ) {
            val isMe = photo.uploadedBy == SessionManager.session.userId
            val fallbackName = SessionManager.session.username ?: SessionManager.session.email?.substringBefore("@") ?: "You"
            val resolvedName = if (photo.uploadedByUsername.isNullOrBlank() || photo.uploadedByUsername == "Unknown User") {
                if (isMe) fallbackName else "User"
            } else photo.uploadedByUsername!!
            
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(38.dp).clip(CircleShape).background(Color(0xFFEFF6FF)), contentAlignment = Alignment.Center) {
                    Text(resolvedName.take(1).uppercase(), style = MaterialTheme.typography.caption.copy(color = BrandPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp))
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(resolvedName, style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp))
                    Text(photo.uploadedAt?.split("T")?.get(0) ?: "Recently", style = MaterialTheme.typography.caption.copy(fontSize = 11.sp, color = TextSecondary))
                }
                Spacer(Modifier.weight(1f))
                IconMore(TextSecondary.copy(0.4f), 22f)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .clip(RoundedCornerShape(0.dp))
            ) {
                val urlToLoad = photo.getPublicUrl()
                if (!urlToLoad.isNullOrEmpty()) {
                    KamelImage(
                        resource = asyncPainterResource(data = urlToLoad),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        onLoading = { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = BrandPrimary) } }
                    )
                } else {
                    Image(
                        painter = painterResource("drawable/photo_sample.png"),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Row(
                    modifier = Modifier.clickable {
                        if (isLiked) {
                            isLiked = false
                            likeCount--
                            scope.launch { try { FriendLensApi.unlikePhoto(groupId, photo.id) } catch (_: Exception) {} }
                        } else {
                            isLiked = true
                            likeCount++
                            scope.launch { try { FriendLensApi.likePhoto(groupId, photo.id) } catch (_: Exception) {} }
                        }
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconHeart(if (isLiked) BrandPrimary else TextSecondary.copy(0.4f), filled = isLiked, size = 26f)
                    Spacer(Modifier.width(8.dp))
                    Text("$likeCount", style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Bold, color = if (isLiked) BrandPrimary else TextSecondary))
                }
                Spacer(Modifier.weight(1f))
                IconDownload(TextSecondary.copy(0.7f), 24f)
            }
        }
    }
}
