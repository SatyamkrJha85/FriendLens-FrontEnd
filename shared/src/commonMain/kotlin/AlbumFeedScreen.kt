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
    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun Content() {
        var selectedGroupId by remember { mutableStateOf<String?>(null) }
        var groups by remember { mutableStateOf<List<Group>>(emptyList()) }
        var photos by remember { mutableStateOf<List<Photo>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            try { val resp = FriendLensApi.getAllGroups(); if (resp.status == "success") { groups = resp.groups; if (groups.isNotEmpty()) selectedGroupId = groups.first().id } } catch (_: Exception) {}
            isLoading = false
        }

        LaunchedEffect(selectedGroupId) {
            selectedGroupId?.let { gid -> try { val resp = FriendLensApi.getGroupPhotos(gid); if (resp.status == "success") photos = resp.photos } catch (_: Exception) {} }
        }

        LazyColumn(modifier = Modifier.fillMaxSize().background(BackgroundLight), contentPadding = PaddingValues(bottom = 100.dp)) {
            // Active group banner — brand gradient
            item {
                val activeGroup = groups.find { it.id == selectedGroupId }
                Box(
                    modifier = Modifier.padding(24.dp).fillMaxWidth().height(90.dp).shadow(8.dp, RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp))
                        .background(brush = Brush.horizontalGradient(listOf(BrandBlue, BrandPurple, BrandCoral)))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconShare(Color.White.copy(alpha = 0.8f), 14f)
                            Spacer(Modifier.width(6.dp))
                            Text("SHARE THIS CODE", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(activeGroup?.joinCode ?: "------", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
                    }
                }
            }

            // Group pills
            item {
                if (groups.isNotEmpty()) {
                    LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(groups) { group ->
                            val sel = group.id == selectedGroupId
                            Box(
                                modifier = Modifier.clip(RoundedCornerShape(20.dp))
                                    .background(if (sel) BrandBlue else Color.White)
                                    .clickable { selectedGroupId = group.id }
                                    .padding(horizontal = 20.dp, vertical = 10.dp)
                            ) { Text(group.name, color = if (sel) Color.White else TextDark, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }

            item {
                Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("My Moments", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text("${photos.size} photos", fontSize = 13.sp, color = TextSecondary)
                }
                Spacer(Modifier.height(8.dp))
            }

            // Story bubbles
            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    item { StoryBubble("Festival") { IconCamera(BrandBlue, 28f) } }
                    item { StoryBubble("Hiking") { IconAlbum(BrandPurple, 28f) } }
                    item { StoryBubble("Birthday") { IconHeart(BrandPink, filled = true, size = 28f) } }
                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(Modifier.size(64.dp).clip(CircleShape).background(DividerColor), contentAlignment = Alignment.Center) { IconPlus(TextSecondary, 24f) }
                            Spacer(Modifier.height(8.dp))
                            Text("Add", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                }
                Spacer(Modifier.height(28.dp))
            }

            if (isLoading) {
                item { Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = BrandBlue, strokeWidth = 3.dp) } }
            }

            if (photos.isEmpty() && !isLoading) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 80.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource("drawable/empty_album.png"),
                            contentDescription = "No photos",
                            modifier = Modifier.size(200.dp).alpha(0.8f),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(Modifier.height(24.dp))
                        Text("Your album is empty", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 20.sp)
                        Text("Be the first one to share a moment!", color = TextSecondary, fontSize = 15.sp)
                    }
                }
            } else {
                items(photos) { p -> FeedPost(selectedGroupId ?: "", p.id, p.uploadedByUsername ?: "Unknown", p.uploadedByUsername ?: "", p.uploadedAt?.take(10) ?: "", 0) }
            }
        }
    }
}

@Composable
fun StoryBubble(label: String, iconContent: @Composable () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(64.dp).clip(CircleShape).background(CardBackground), contentAlignment = Alignment.Center) { iconContent() }
        Spacer(Modifier.height(8.dp))
        Text(label, fontSize = 12.sp, color = TextDark, fontWeight = FontWeight.SemiBold)
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun FeedPost(groupId: String, photoId: String, title: String, author: String, daysAgo: String, likes: Int) {
    val scope = rememberCoroutineScope()
    var isLiked by remember { mutableStateOf(false) }
    var likeCount by remember { mutableStateOf(likes) }

    Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(40.dp).clip(CircleShape).background(BrandBlue.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                    Text(title.take(1), fontWeight = FontWeight.Bold, color = BrandBlue)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(title, fontWeight = FontWeight.Bold, color = TextDark, fontSize = 15.sp)
                    Text("$author · $daysAgo", fontSize = 12.sp, color = TextSecondary)
                }
            }
            IconButton(onClick = {}) { IconMore(TextSecondary, 18f) }
        }
        Spacer(Modifier.height(14.dp))

        Box(
            modifier = Modifier.fillMaxWidth().height(240.dp).shadow(4.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp)).background(CardBackground),
            contentAlignment = Alignment.Center
        ) {
            Image(painter = painterResource("drawable/photo_sample.png"), contentDescription = "Photo", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        }
        Spacer(Modifier.height(14.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier.clickable {
                        scope.launch {
                            try {
                                if (isLiked) { if (groupId.isNotEmpty()) FriendLensApi.unlikePhoto(groupId, photoId); likeCount-- }
                                else { if (groupId.isNotEmpty()) FriendLensApi.likePhoto(groupId, photoId); likeCount++ }
                                isLiked = !isLiked
                            } catch (_: Exception) { isLiked = !isLiked }
                        }
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconHeart(if (isLiked) BrandPink else TextSecondary, filled = isLiked, size = 22f)
                    Spacer(Modifier.width(6.dp))
                    Text("$likeCount", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 14.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconComment(TextSecondary, 20f)
                    Spacer(Modifier.width(6.dp))
                    Text("8", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 14.sp)
                }
            }
            IconButton(onClick = {}) { IconShare(TextSecondary, 20f) }
        }
        Spacer(Modifier.height(16.dp))
        Divider(color = DividerColor)
    }
}
