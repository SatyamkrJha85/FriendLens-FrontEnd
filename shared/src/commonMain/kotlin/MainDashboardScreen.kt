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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border

class MainDashboardScreen : Screen {
    @Composable
    override fun Content() {
        var selectedTab by remember { mutableStateOf(0) }
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            bottomBar = {
                Surface(
                    elevation = 24.dp,
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    border = BorderStroke(0.5.dp, DividerColor.copy(0.5f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().height(88.dp).padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NavItem(icon = { IconHome(if (selectedTab == 0) BrandPrimary else TextSecondary, 26f) }, label = "Feed", selected = selectedTab == 0) { selectedTab = 0 }
                        NavItem(icon = { IconAlbum(if (selectedTab == 1) BrandPrimary else TextSecondary, 26f) }, label = "Albums", selected = selectedTab == 1) { selectedTab = 1 }
                        NavItem(icon = { IconGroup(if (selectedTab == 2) BrandPrimary else TextSecondary, 26f) }, label = "Join", selected = selectedTab == 2) { selectedTab = 2 }
                        NavItem(icon = { IconProfile(if (selectedTab == 3) BrandPrimary else TextSecondary, 26f) }, label = "Profile", selected = selectedTab == 3) { selectedTab = 3 }
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navigator.push(PhotoCaptureScreen()) },
                    backgroundColor = BrandPrimary,
                    contentColor = Color.White,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .size(64.dp)
                        .shadow(16.dp, CircleShape)
                ) {
                    IconCamera(Color.White, 32f)
                }
            },
            floatingActionButtonPosition = FabPosition.End,
            isFloatingActionButtonDocked = false
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding).background(BackgroundLight)) {
                when (selectedTab) {
                    0 -> FeedTab()
                    1 -> AlbumsTab(onGroupClick = { gid -> navigator.push(AlbumFeedScreen(gid)) })
                    2 -> CreateJoinGroupScreen().Content()
                    3 -> ProfileTab()
                }
            }
        }
    }
}

@Composable
fun NavItem(icon: @Composable () -> Unit, label: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier.clickable(onClick = onClick).padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(BrandPrimary.copy(0.1f))
                )
            }
            icon()
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.caption.copy(
                color = if (selected) BrandPrimary else TextSecondary,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold
            )
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun FeedTab() {
    val cachedPhotos by DataCache.feedPhotos.collectAsState()
    var isLoading by remember { mutableStateOf(cachedPhotos.isEmpty()) }

    LaunchedEffect(Unit) {
        if (cachedPhotos.isEmpty()) {
            try {
                val groupsResp = FriendLensApi.getAllGroups()
                if (groupsResp.status == "success") {
                    val photos = mutableListOf<Photo>()
                    groupsResp.groups.take(5).forEach { group ->
                        val pResp = FriendLensApi.getGroupPhotos(group.id)
                        photos.addAll(pResp.photos)
                    }
                    val sorted = photos.sortedByDescending { it.uploadedAt }
                    DataCache.updateFeed(sorted)
                }
            } catch (_: Exception) {}
            isLoading = false
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Column(modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 24.dp, vertical = 24.dp)) {
                Text("Your Feed", style = MaterialTheme.typography.h1.copy(fontSize = 32.sp))
                Text("See what your friends are capturing.", style = MaterialTheme.typography.body2.copy(color = TextSecondary.copy(0.7f)))
            }
        }

        if (isLoading && cachedPhotos.isEmpty()) {
            item { Box(Modifier.fillMaxWidth().padding(80.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = BrandPrimary, strokeWidth = 3.dp) } }
        } else if (cachedPhotos.isEmpty()) {
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(60.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painter = painterResource("drawable/onboarding_capture.png"), contentDescription = null, modifier = Modifier.size(180.dp))
                    Spacer(Modifier.height(24.dp))
                    Text("Quiet here...", style = MaterialTheme.typography.h2)
                    Text("Join a group to populate your feed!", textAlign = TextAlign.Center, color = TextSecondary)
                }
            }
        } else {
            items(cachedPhotos) { photo ->
                FeedCard(photo)
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun FeedCard(photo: Photo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .shadow(12.dp, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        elevation = 0.dp,
        backgroundColor = Color.White
    ) {
        Column {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Color(0xFFFEE2E2), Color(0xFFE0E7FF))))
                        .border(1.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (photo.uploadedByUsername ?: "U").take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = BrandPrimary,
                        fontSize = 16.sp
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = photo.uploadedByUsername ?: "Anonymous",
                        style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                    )
                    Text(
                        text = photo.uploadedAt?.split("T")?.get(0) ?: "Just now",
                        style = MaterialTheme.typography.caption.copy(fontSize = 11.sp, color = TextSecondary)
                    )
                }
                Spacer(Modifier.weight(1f))
                IconMore(TextSecondary.copy(0.5f), 22f)
            }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                // Use actual sample if available, or premium placeholder
                Image(
                    painter = painterResource("drawable/photo_sample.png"),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Overlay for premium look
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.2f))))
                )
            }

            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconHeart(BrandPrimary, filled = true, size = 24f)
                    Spacer(Modifier.width(6.dp))
                    Text("24", style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Bold))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconComment(TextSecondary, 24f)
                    Spacer(Modifier.width(6.dp))
                    Text("12", style = MaterialTheme.typography.caption.copy(color = TextSecondary))
                }
                Spacer(Modifier.weight(1f))
                IconShare(TextSecondary, 22f)
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AlbumsTab(onGroupClick: (String) -> Unit) {
    val cachedGroups by DataCache.groups.collectAsState()
    var isLoading by remember { mutableStateOf(cachedGroups.isEmpty()) }
    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (cachedGroups.isEmpty()) {
            try {
                val resp = FriendLensApi.getAllGroups()
                if (resp.status == "success") DataCache.updateGroups(resp.groups)
            } catch (_: Exception) {}
            isLoading = false
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Column(modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 24.dp, vertical = 24.dp)) {
                Text("Albums", style = MaterialTheme.typography.h1.copy(fontSize = 32.sp))
                Text("Your collective memory vault.", style = MaterialTheme.typography.body2.copy(color = TextSecondary.copy(0.7f)))
                
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    placeholder = { Text("Search your collections...", style = MaterialTheme.typography.body1.copy(color = TextSecondary.copy(0.4f))) },
                    shape = CircleShape,
                    leadingIcon = { IconSearch(TextSecondary, 20f) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = Color(0xFFF3F4F6),
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = BrandPrimary.copy(0.3f)
                    ),
                    singleLine = true
                )
            }
        }

        if (isLoading && cachedGroups.isEmpty()) {
            item { Box(Modifier.fillMaxWidth().padding(80.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = BrandPrimary) } }
        } else if (cachedGroups.isEmpty()) {
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painter = painterResource("drawable/onboarding_adventure.png"), contentDescription = null, modifier = Modifier.size(140.dp))
                    Text("No albums found", style = MaterialTheme.typography.h3)
                    Text("Start by creating your first group!", textAlign = TextAlign.Center, color = TextSecondary)
                }
            }
        } else {
            items(cachedGroups.filter { it.name.contains(searchText, ignoreCase = true) }) { group ->
                CollectionCard(
                    title = group.name,
                    time = group.createdAt?.take(10) ?: "Today",
                    subtext = "Code: ${group.joinCode}",
                    imgUrl = group.groupImg
                ) { onGroupClick(group.id) }
            }
        }
    }
}

@Composable
fun CollectionCard(title: String, time: String, subtext: String, imgUrl: String? = null, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        elevation = 0.dp
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFFEF2F2)),
                contentAlignment = Alignment.Center
            ) {
                // If we had a real KMP image loader, we'd use imgUrl here
                IconAlbum(BrandPrimary, 28f)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.h3.copy(fontSize = 17.sp, fontWeight = FontWeight.ExtraBold))
                Spacer(Modifier.height(4.dp))
                Text(subtext, style = MaterialTheme.typography.body2.copy(fontSize = 13.sp, color = TextSecondary))
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(time, style = MaterialTheme.typography.caption.copy(fontSize = 11.sp, color = TextSecondary.copy(0.8f)))
                Spacer(Modifier.height(8.dp))
                Surface(
                    shape = CircleShape,
                    color = BrandPrimary.copy(0.1f),
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        IconPlus(BrandPrimary, 14f)
                    }
                }
            }
        }
    }
}
