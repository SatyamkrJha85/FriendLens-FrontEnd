import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

class AlbumFeedScreen : Screen {
    @Composable
    override fun Content() {
        ContentWithId(null)
    }

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun ContentWithId(initialGroupId: String?) {
        var selectedGroupId by remember { mutableStateOf<String?>(initialGroupId) }
        var groups by remember { mutableStateOf<List<Group>>(emptyList()) }
        var photos by remember { mutableStateOf<List<Photo>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            try { 
                val resp = FriendLensApi.getAllGroups()
                if (resp.status == "success") { 
                    groups = resp.groups
                    if (selectedGroupId == null && groups.isNotEmpty()) {
                        selectedGroupId = groups.first().id 
                    }
                } 
            } catch (_: Exception) {}
            isLoading = false
        }

        LaunchedEffect(initialGroupId) {
            if (initialGroupId != null) selectedGroupId = initialGroupId
        }

        LaunchedEffect(selectedGroupId) {
            selectedGroupId?.let { gid -> 
                try { 
                    val resp = FriendLensApi.getGroupPhotos(gid)
                    if (resp.status == "success") photos = resp.photos 
                } catch (_: Exception) {} 
            }
        }

        Column(modifier = Modifier.fillMaxSize().background(BackgroundLight)) {
            // Group Selection Header
            Surface(elevation = 4.dp, shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp), color = Color.White) {
                Column(modifier = Modifier.padding(bottom = 16.dp)) {
                    Spacer(Modifier.height(16.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(groups) { group ->
                            val isSelected = group.id == selectedGroupId
                            Surface(
                                modifier = Modifier.clickable { selectedGroupId = group.id },
                                shape = MaterialTheme.shapes.medium,
                                color = if (isSelected) BrandBlue else BackgroundLight,
                                elevation = if (isSelected) 4.dp else 0.dp
                            ) {
                                Text(
                                    text = group.name,
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                                    style = MaterialTheme.typography.body2.copy(
                                        color = if (isSelected) Color.White else TextSecondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                // Share Code Hero Section
                item {
                    val activeGroup = groups.find { it.id == selectedGroupId }
                    if (activeGroup != null) {
                        Card(
                            modifier = Modifier.padding(24.dp).fillMaxWidth().height(100.dp),
                            shape = MaterialTheme.shapes.large,
                            elevation = 8.dp
                        ) {
                            Box(modifier = Modifier.background(Brush.horizontalGradient(BrandGradient)).padding(20.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("JOIN CODE", style = MaterialTheme.typography.caption.copy(color = Color.White.copy(alpha = 0.8f), letterSpacing = 2.sp))
                                        Text(activeGroup.joinCode, style = MaterialTheme.typography.h2.copy(color = Color.White, letterSpacing = 4.sp))
                                    }
                                    Button(
                                        onClick = { },
                                        shape = CircleShape,
                                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White.copy(alpha = 0.2f)),
                                        elevation = null
                                    ) {
                                        IconShare(Color.White, 20f)
                                    }
                                }
                            }
                        }
                    }
                }

                if (isLoading) {
                    item { Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = BrandBlue) } }
                } else if (photos.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 60.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(painter = painterResource("drawable/empty_album.png"), contentDescription = null, modifier = Modifier.size(180.dp).alpha(0.6f))
                            Spacer(Modifier.height(24.dp))
                            Text("No memories yet", style = MaterialTheme.typography.h3)
                            Text("Share the first photo in this album.", style = MaterialTheme.typography.body2)
                        }
                    }
                } else {
                    items(photos) { photo ->
                        PhotoFeedItem(
                            groupId = selectedGroupId ?: "",
                            photo = photo,
                            onLike = { 
                                scope.launch {
                                    try { 
                                        FriendLensApi.likePhoto(selectedGroupId ?: "", photo.id)
                                    } catch (_: Exception) {}
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun PhotoFeedItem(groupId: String, photo: Photo, onLike: () -> Unit) {
    var isLiked by remember { mutableStateOf(false) }
    var likesCount by remember { mutableStateOf(0) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp),
        shape = MaterialTheme.shapes.large,
        elevation = 2.dp
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = BrandGradient[(photo.uploadedByUsername?.length ?: 0) % BrandGradient.size].copy(alpha = 0.2f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = photo.uploadedByUsername?.firstOrNull()?.uppercase() ?: "U",
                            style = MaterialTheme.typography.h3.copy(fontSize = 14.sp, color = BrandBlue)
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(photo.uploadedByUsername ?: "Unknown", style = MaterialTheme.typography.h3.copy(fontSize = 15.sp))
                    Text(photo.uploadedAt?.take(16) ?: "Just now", style = MaterialTheme.typography.caption)
                }
                Spacer(Modifier.weight(1f))
                IconMore(TextSecondary, 18f)
            }

            // Image
            Box(
                modifier = Modifier.fillMaxWidth().height(280.dp).background(CardBackground)
            ) {
                Image(
                    painter = painterResource("drawable/photo_sample.png"),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    Row(
                        modifier = Modifier.clickable { 
                            isLiked = !isLiked
                            likesCount += if (isLiked) 1 else -1
                            onLike()
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconHeart(if (isLiked) BrandPink else TextSecondary, filled = isLiked, size = 24f)
                        Spacer(Modifier.width(6.dp))
                        Text(text = "$likesCount", style = MaterialTheme.typography.h3.copy(fontSize = 14.sp))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconComment(TextSecondary, 22f)
                        Spacer(Modifier.width(6.dp))
                        Text(text = "12", style = MaterialTheme.typography.h3.copy(fontSize = 14.sp))
                    }
                }
                IconShare(TextSecondary, 22f)
            }
        }
    }
}
