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

        Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
            // Group Tabs
            Surface(elevation = 2.dp, color = Color.White) {
                Column(modifier = Modifier.padding(vertical = 16.dp)) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(groups) { group ->
                            val isSelected = group.id == selectedGroupId
                            Column(
                                modifier = Modifier.clickable { selectedGroupId = group.id },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = group.name,
                                    style = MaterialTheme.typography.body1.copy(
                                        color = if (isSelected) TextDark else TextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                )
                                if (isSelected) {
                                    Box(Modifier.padding(top = 4.dp).size(6.dp).clip(CircleShape).background(BrandBlue))
                                }
                            }
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                // Join Code Hero - Matching Mockup (Red/Coral Card)
                item {
                    val activeGroup = groups.find { it.id == selectedGroupId }
                    if (activeGroup != null) {
                        Card(
                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            elevation = 8.dp
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(brush = Brush.linearGradient(listOf(BrandCoral, BrandPink)))
                                    .padding(vertical = 12.dp, horizontal = 20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("JOIN ALBUM", style = MaterialTheme.typography.caption.copy(color = Color.White.copy(alpha = 0.8f), letterSpacing = 2.sp))
                                        Text(activeGroup.joinCode, style = MaterialTheme.typography.h2.copy(color = Color.White, letterSpacing = 4.sp, fontSize = 28.sp))
                                    }
                                    IconShare(Color.White, 20f)
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        "My Moments",
                        style = MaterialTheme.typography.h3,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }

                if (isLoading) {
                    item { Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = BrandBlue, strokeWidth = 3.dp) } }
                } else if (photos.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 60.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            IconAlbum(TextSecondary.copy(alpha = 0.3f), 80f)
                            Spacer(Modifier.height(16.dp))
                            Text("No memories caught here", style = MaterialTheme.typography.body2)
                        }
                    }
                } else {
                    items(photos) { photo ->
                        PhotoFeedItem(photo)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun PhotoFeedItem(photo: Photo) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = 4.dp
    ) {
        Column {
            // Content
            Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                Image(
                    painter = painterResource("drawable/photo_sample.png"),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Uploader Initial Overlay - Professional look
                Box(
                    modifier = Modifier.align(Alignment.TopStart).padding(16.dp).size(36.dp)
                        .clip(CircleShape).background(Color.White.copy(alpha = 0.9f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        photo.uploadedByUsername?.firstOrNull()?.toString()?.uppercase() ?: "U",
                        style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Bold, color = BrandPurple)
                    )
                }
            }

            // Stats / Info
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconHeart(BrandPink, filled = true, size = 20f)
                Spacer(Modifier.width(8.dp))
                Text("24", style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Bold))
                Spacer(Modifier.width(20.dp))
                IconComment(TextSecondary, 18f)
                Spacer(Modifier.width(8.dp))
                Text("5", style = MaterialTheme.typography.caption)
                Spacer(Modifier.weight(1f))
                Text(photo.uploadedAt?.take(10) ?: "Today", style = MaterialTheme.typography.caption)
            }
        }
    }
}
