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
        var selectedTab by remember { mutableStateOf(1) } // Default to Albums based on mockup
        var activeGroupId by remember { mutableStateOf<String?>(null) }
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            bottomBar = {
                Surface(
                    elevation = 8.dp,
                    color = Color.White,
                    border = BorderStroke(0.5.dp, DividerColor)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().height(80.dp).padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NavItem(icon = { IconHome(if (selectedTab == 0) BrandPrimary else TextSecondary, 24f) }, label = "Home", selected = selectedTab == 0) { selectedTab = 0 }
                        NavItem(icon = { IconAlbum(if (selectedTab == 1) BrandPrimary else TextSecondary, 24f) }, label = "Albums", selected = selectedTab == 1) { selectedTab = 1 }

                        // Placeholder for the gap in the bottom nav
                        Box(modifier = Modifier.weight(0.5f))

                        NavItem(icon = { IconGroup(if (selectedTab == 2) BrandPrimary else TextSecondary, 24f) }, label = "Friends", selected = selectedTab == 2) { selectedTab = 2 }
                        NavItem(icon = { IconProfile(if (selectedTab == 3) BrandPrimary else TextSecondary, 24f) }, label = "Profile", selected = selectedTab == 3) { selectedTab = 3 }
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navigator.push(PhotoCaptureScreen()) },
                    backgroundColor = BrandPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp).size(56.dp).shadow(12.dp, CircleShape)
                ) {
                    IconPlus(Color.White, 32f)
                }
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding).background(BackgroundLight)) {
                when (selectedTab) {
                    0 -> HomeTab(onGroupClick = { gid -> activeGroupId = gid; selectedTab = 1 })
                    1 -> HomeTab(onGroupClick = { gid -> activeGroupId = gid; selectedTab = 1 }) // Albums view same as home for now
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
        modifier = Modifier.clickable(onClick = onClick).padding(horizontal = 8.dp).width(64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        icon()
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.caption.copy(
                color = if (selected) BrandPrimary else TextSecondary,
                fontSize = 10.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun HomeTab(onGroupClick: (String) -> Unit) {
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
        // Sticky Header from Stitch Design
        item {
            Column(modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 24.dp, vertical = 20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Your Albums", style = MaterialTheme.typography.h1.copy(fontSize = 28.sp))
                        Text("All moments. One place.", style = MaterialTheme.typography.body2)
                    }
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFF3F4F6),
                        modifier = Modifier.size(44.dp).clickable { },
                    ) {
                        Box(contentAlignment = Alignment.Center) { IconBell(TextPrimary, 20f) }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                // Search Bar - rounded pill, no border
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search albums or friends...", style = MaterialTheme.typography.body2.copy(color = TextSecondary.copy(alpha = 0.6f))) },
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

        item {
            Column(modifier = Modifier.padding(top = 24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("RECENT FAVORITES", style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Bold, color = TextSecondary))
                    Text("View All", style = MaterialTheme.typography.caption.copy(color = BrandPrimary, fontWeight = FontWeight.Bold))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { FavoriteCard("Summer Festival", "128 photos", listOf(Color(0xFFFFEDD5), Color(0xFFFFF7ED)), { IconCamera(Color(0xFFF97316), 24f) }) }
                    item { FavoriteCard("Japan Trip", "452 photos", listOf(Color(0xFFE0F2FE), Color(0xFFF0F9FF)), { IconAlbum(Color(0xFF0EA5E9), 24f) }) }
                    item { FavoriteCard("Sam's B-Day", "89 photos", listOf(Color(0xFFF3E8FF), Color(0xFFFAF5FF)), { IconPlus(Color(0xFFA855F7), 24f) }) }
                }
            }
        }

        item {
            Text(
                "ALL COLLECTIONS",
                style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Bold, color = TextSecondary),
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 12.dp)
            )
        }

        if (isLoading) {
            item { Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = BrandPrimary) } }
        } else if (groups.isEmpty()) {
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painter = painterResource("drawable/empty_album.png"), contentDescription = null, modifier = Modifier.size(120.dp))
                    Text("No albums yet", style = MaterialTheme.typography.h3)
                }
            }
        } else {
            items(groups) { group ->
                CollectionCard(group.name, "Oct 24", "Updated by ${group.joinCode}") { onGroupClick(group.id) }
            }
        }
    }
}

@Composable
fun FavoriteCard(title: String, count: String, gradient: List<Color>, icon: @Composable () -> Unit) {
    Card(
        modifier = Modifier.width(160.dp).height(192.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = 0.dp,
        border = BorderStroke(1.dp, gradient[0])
    ) {
        Box(modifier = Modifier.background(Brush.verticalGradient(gradient)).padding(16.dp)) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                Surface(shape = CircleShape, color = Color.White, modifier = Modifier.size(40.dp), elevation = 2.dp) {
                    Box(contentAlignment = Alignment.Center) { icon() }
                }
                Column {
                    Text(title, style = MaterialTheme.typography.h3.copy(fontSize = 15.sp), maxLines = 2)
                    Text(count, style = MaterialTheme.typography.body2.copy(fontSize = 11.sp))
                }
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
        border = BorderStroke(0.5.dp, DividerColor),
        elevation = 0.dp
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF3F4F6)), contentAlignment = Alignment.Center) {
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
            IconPlus(TextSecondary, 16f, modifier = Modifier.padding(start = 8.dp)) // Chevron mock
        }
    }
}
