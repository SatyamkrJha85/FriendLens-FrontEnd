import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

class MainDashboardScreen : Screen {
    @Composable
    override fun Content() {
        var selectedTab by remember { mutableStateOf(0) }
        var activeGroupId by remember { mutableStateOf<String?>(null) }
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            bottomBar = {
                Surface(
                    elevation = 20.dp,
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().height(84.dp).padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NavItem(icon = { IconHome(if (selectedTab == 0) BrandBlue else TextSecondary, 24f) }, label = "Home", selected = selectedTab == 0) { selectedTab = 0 }
                        NavItem(icon = { IconAlbum(if (selectedTab == 1) BrandPurple else TextSecondary, 24f) }, label = "Feed", selected = selectedTab == 1) { selectedTab = 1 }

                        // Central Highlighted FAB - Consistent with mockup (Red/Coral)
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .offset(y = (-15).dp)
                                .shadow(12.dp, CircleShape)
                                .clip(CircleShape)
                                .background(brush = Brush.linearGradient(listOf(BrandCoral, BrandPink)))
                                .clickable { navigator.push(PhotoCaptureScreen()) },
                            contentAlignment = Alignment.Center
                        ) {
                            IconCamera(Color.White, 30f)
                        }

                        NavItem(icon = { IconGroup(if (selectedTab == 2) BrandCoral else TextSecondary, 24f) }, label = "Groups", selected = selectedTab == 2) { selectedTab = 2 }
                        NavItem(icon = { IconProfile(if (selectedTab == 3) BrandOrange else TextSecondary, 24f) }, label = "Me", selected = selectedTab == 3) { selectedTab = 3 }
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding).background(Color.White)) {
                when (selectedTab) {
                    0 -> HomeTab(onGroupClick = { gid -> activeGroupId = gid; selectedTab = 1 })
                    1 -> AlbumFeedScreen().ContentWithId(activeGroupId)
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
        modifier = Modifier.clickable(onClick = onClick).padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        icon()
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.caption.copy(
                color = if (selected) TextDark else TextSecondary,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.sp
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
        contentPadding = PaddingValues(24.dp)
    ) {
        item {
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
                    color = BackgroundLight,
                    modifier = Modifier.size(44.dp).clickable { },
                ) {
                    Box(contentAlignment = Alignment.Center) { IconBell(TextDark, 20f) }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Search Bar matching mockup
        item {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search albums or friends...") },
                shape = CircleShape,
                leadingIcon = { IconSearch(TextSecondary, 18f) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = BackgroundLight,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = BrandBlue.copy(alpha = 0.3f)
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(32.dp))
        }

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("RECENT FAVORITES", style = MaterialTheme.typography.caption.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold))
                Text("View All", style = MaterialTheme.typography.caption.copy(color = BrandCoral))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                FavoriteCard(Modifier.weight(1f), "Summer Festival", "12 photos", listOf(Color(0xFFFFF7ED), Color(0xFFFFEDD5))) { IconCamera(BrandOrange, 28f) }
                FavoriteCard(Modifier.weight(1f), "Japan Trip", "45 photos", listOf(Color(0xFFF0F9FF), Color(0xFFE0F2FE))) { IconAlbum(BrandBlue, 28f) }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        item {
            Text("ALL COLLECTIONS", style = MaterialTheme.typography.caption.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (isLoading) {
            item { Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = BrandBlue, strokeWidth = 3.dp) } }
        } else if (groups.isEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(painter = painterResource("drawable/empty_album.png"), contentDescription = null, modifier = Modifier.size(120.dp), contentScale = ContentScale.Fit)
                    Spacer(Modifier.height(16.dp))
                    Text("No albums yet", style = MaterialTheme.typography.h3)
                    Text("Start by creating your first group.", style = MaterialTheme.typography.body2, textAlign = TextAlign.Center)
                }
            }
        } else {
            items(groups) { group ->
                CollectionCard(group.name, group.joinCode) { onGroupClick(group.id) }
            }
        }
        
        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}

@Composable
fun FavoriteCard(modifier: Modifier, title: String, count: String, gradient: List<Color>, icon: @Composable () -> Unit) {
    Card(
        modifier = modifier.height(160.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = 0.dp
    ) {
        Box(modifier = Modifier.background(Brush.verticalGradient(gradient)).padding(20.dp)) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(40.dp)) {
                    Box(contentAlignment = Alignment.Center) { icon() }
                }
                Column {
                    Text(title, style = MaterialTheme.typography.h3.copy(fontSize = 15.sp))
                    Text(count, style = MaterialTheme.typography.caption)
                }
            }
        }
    }
}

@Composable
fun CollectionCard(title: String, code: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = BackgroundLight
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = RoundedCornerShape(12.dp), color = Color.White, modifier = Modifier.size(50.dp)) {
                Box(contentAlignment = Alignment.Center) { IconAlbum(BrandBlue, 20f) }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.h3.copy(fontSize = 16.sp))
                Text("Join Code: $code", style = MaterialTheme.typography.body2)
            }
            // Add Button matching mockup (Red circle with plus)
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape).background(BrandCoral),
                contentAlignment = Alignment.Center
            ) {
                Text("+", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
