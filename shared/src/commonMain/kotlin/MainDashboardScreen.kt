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

class MainDashboardScreen : Screen {
    @Composable
    override fun Content() {
        var selectedTab by remember { mutableStateOf(0) }
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            bottomBar = {
                Surface(
                    elevation = 16.dp,
                    color = Color.White,
                    border = BorderStroke(0.5.dp, DividerColor)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().height(80.dp).padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NavItem(icon = { IconHome(if (selectedTab == 0) BrandPrimary else TextSecondary, 24f) }, label = "Feed", selected = selectedTab == 0) { selectedTab = 0 }
                        NavItem(icon = { IconAlbum(if (selectedTab == 1) BrandPrimary else TextSecondary, 24f) }, label = "Albums", selected = selectedTab == 1) { selectedTab = 1 }
                        NavItem(icon = { IconGroup(if (selectedTab == 2) BrandPrimary else TextSecondary, 24f) }, label = "Join", selected = selectedTab == 2) { selectedTab = 2 }
                        NavItem(icon = { IconProfile(if (selectedTab == 3) BrandPrimary else TextSecondary, 24f) }, label = "Profile", selected = selectedTab == 3) { selectedTab = 3 }
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navigator.push(PhotoCaptureScreen()) },
                    backgroundColor = BrandPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.size(56.dp).shadow(12.dp, CircleShape)
                ) {
                    IconPlus(Color.White, 32f)
                }
            },
            floatingActionButtonPosition = FabPosition.Center,
            isFloatingActionButtonDocked = true
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
        icon()
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.caption.copy(
                color = if (selected) BrandPrimary else TextSecondary,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun FeedTab() {
    var allPhotos by remember { mutableStateOf<List<Photo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val groupsResp = FriendLensApi.getAllGroups()
            if (groupsResp.status == "success") {
                val photos = mutableListOf<Photo>()
                // Fetch photos from the first 5 groups to build a feed
                groupsResp.groups.take(5).forEach { group ->
                    val pResp = FriendLensApi.getGroupPhotos(group.id)
                    photos.addAll(pResp.photos)
                }
                allPhotos = photos.sortedByDescending { it.uploadedAt }
            }
        } catch (_: Exception) {}
        isLoading = false
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            Column(modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 24.dp, vertical = 20.dp)) {
                Text("Recent Activity", style = MaterialTheme.typography.h1.copy(fontSize = 28.sp))
                Text("See what your friends are capturing.", style = MaterialTheme.typography.body2)
            }
        }

        if (isLoading) {
            item { Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = BrandPrimary) } }
        } else if (allPhotos.isEmpty()) {
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(60.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painter = painterResource("drawable/onboarding_capture.png"), contentDescription = null, modifier = Modifier.size(160.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("Your feed is empty", style = MaterialTheme.typography.h3)
                    Text("Join a group to see moments!", textAlign = TextAlign.Center)
                }
            }
        } else {
            items(allPhotos) { photo ->
                FeedCard(photo)
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun FeedCard(photo: Photo) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = 2.dp
    ) {
        Column {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(32.dp).clip(CircleShape).background(Color(0xFFE2E8F0)), contentAlignment = Alignment.Center) {
                    Text((photo.uploadedByUsername ?: "U").take(1).uppercase(), fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(photo.uploadedByUsername ?: "Anonymous", style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                    Text(photo.uploadedAt?.split("T")?.get(0) ?: "Just now", style = MaterialTheme.typography.caption.copy(fontSize = 10.sp))
                }
            }
            Box(modifier = Modifier.fillMaxWidth().height(240.dp)) {
                Image(
                    painter = painterResource("drawable/onboarding_capture.png"), // Placeholder if s3Key is not supported yet by painterResource
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // In a real app, we'd use an Image Loader for photo.originalUrl
            }
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                IconHeart(BrandPrimary, filled = true, size = 20f)
                Spacer(Modifier.width(16.dp))
                IconComment(TextSecondary, 20f)
                Spacer(Modifier.weight(1f))
                IconShare(TextSecondary, 20f)
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AlbumsTab(onGroupClick: (String) -> Unit) {
    var groups by remember { mutableStateOf<List<Group>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            val resp = FriendLensApi.getAllGroups()
            if (resp.status == "success") groups = resp.groups
        } catch (_: Exception) {}
        isLoading = false
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            Column(modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 24.dp, vertical = 20.dp)) {
                Text("Your Albums", style = MaterialTheme.typography.h1.copy(fontSize = 28.sp))
                Text("Collections of shared memories.", style = MaterialTheme.typography.body2)
                
                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search collections...", style = MaterialTheme.typography.body2.copy(color = TextSecondary.copy(alpha = 0.6f))) },
                    shape = CircleShape,
                    leadingIcon = { IconSearch(TextSecondary, 18f) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = Color(0xFFF3F4F6),
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }
        }

        if (isLoading) {
            item { Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = BrandPrimary) } }
        } else if (groups.isEmpty()) {
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painter = painterResource("drawable/onboarding_adventure.png"), contentDescription = null, modifier = Modifier.size(120.dp))
                    Text("No albums found", style = MaterialTheme.typography.h3)
                    Button(onClick = { /* Navigate to Join/Create */ }, modifier = Modifier.padding(top = 16.dp)) {
                        Text("Create Core Collection")
                    }
                }
            }
        } else {
            items(groups.filter { it.name.contains(searchText, ignoreCase = true) }) { group ->
                CollectionCard(group.name, group.createdAt?.take(10) ?: "Today", "Code: ${group.joinCode}") { onGroupClick(group.id) }
            }
        }
    }
}

@Composable
fun CollectionCard(title: String, time: String, subtext: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 6.dp).clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        elevation = 2.dp
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFFEE2E2)), contentAlignment = Alignment.Center) {
                IconAlbum(BrandPrimary, 24f)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(title, style = MaterialTheme.typography.h3.copy(fontSize = 16.sp))
                    Text(time, style = MaterialTheme.typography.caption.copy(fontSize = 10.sp))
                }
                Text(subtext, style = MaterialTheme.typography.body2.copy(fontSize = 12.sp))
            }
            IconPlus(TextSecondary, 16f, modifier = Modifier.padding(start = 8.dp))
        }
    }
}
