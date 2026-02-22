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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch

class MainDashboardScreen : Screen {
    @Composable
    override fun Content() {
        var selectedTab by remember { mutableStateOf(0) }
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            bottomBar = {
                Surface(elevation = 24.dp, color = Color.White, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().height(72.dp).padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NavItem(icon = { IconHome(if (selectedTab == 0) BrandBlue else TextSecondary, 22f) }, label = "Home", selected = selectedTab == 0) { selectedTab = 0 }
                        NavItem(icon = { IconAlbum(if (selectedTab == 1) BrandPurple else TextSecondary, 22f) }, label = "Albums", selected = selectedTab == 1) { selectedTab = 1 }

                        // Center FAB — gradient matching icon ring
                        Box(
                            modifier = Modifier.size(56.dp).shadow(12.dp, CircleShape).clip(CircleShape)
                                .background(brush = Brush.sweepGradient(listOf(BrandBlue, BrandPurple, BrandPink, BrandCoral, BrandOrange, BrandBlue)))
                                .clickable { navigator.push(PhotoCaptureScreen()) },
                            contentAlignment = Alignment.Center
                        ) { IconCamera(Color.White, 26f) }

                        NavItem(icon = { IconGroup(if (selectedTab == 2) BrandCoral else TextSecondary, 22f) }, label = "Groups", selected = selectedTab == 2) { selectedTab = 2 }
                        NavItem(icon = { IconProfile(if (selectedTab == 3) BrandOrange else TextSecondary, 22f) }, label = "Profile", selected = selectedTab == 3) { selectedTab = 3 }
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding).background(BackgroundLight)) {
                when (selectedTab) {
                    0 -> HomeTab()
                    1 -> AlbumsTab()
                    2 -> GroupsTab()
                    3 -> ProfileTab()
                }
            }
        }
    }
}

@Composable
fun NavItem(icon: @Composable () -> Unit, label: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier.clickable(onClick = onClick).padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        icon()
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 10.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, color = if (selected) TextDark else TextSecondary)
    }
}

@Composable
fun HomeTab() {
    var groups by remember { mutableStateOf<List<Group>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        sendLocalNotification("New moments await!", "Your friends uploaded new photos.")
        try { val resp = FriendLensApi.getAllGroups(); if (resp.status == "success") groups = resp.groups } catch (_: Exception) {}
        isLoading = false
    }

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(24.dp)) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Your Albums", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text("${groups.size} collections · One place", fontSize = 14.sp, color = TextSecondary)
                }
                Box(
                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(16.dp)).background(CardBackground).clickable { },
                    contentAlignment = Alignment.Center
                ) { IconBell(TextDark, 22f) }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            OutlinedTextField(
                value = "", onValueChange = {},
                placeholder = { Text("Search albums or friends...", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = BrandBlue, unfocusedBorderColor = DividerColor, backgroundColor = Color.White)
            )
            Spacer(modifier = Modifier.height(28.dp))
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("RECENT FAVORITES", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 1.sp)
                Text("View All", fontSize = 12.sp, color = BrandBlue, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { })
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Favorite Cards with gradient from icon
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                FavoriteCard(Modifier.weight(1f), "Summer Festival", "18 photos",
                    listOf(Color(0xFFEBF4FF), Color(0xFFDBEAFE))) { IconCamera(BrandBlue, 28f) }
                FavoriteCard(Modifier.weight(1f), "Japan Trip", "450 photos",
                    listOf(Color(0xFFF5F3FF), Color(0xFFEDE9FE))) { IconAlbum(BrandPurple, 28f) }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        item {
            Text("ALL COLLECTIONS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (isLoading) {
            item { Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = BrandBlue, strokeWidth = 3.dp) } }
        } else if (groups.isEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = org.jetbrains.compose.resources.painterResource("drawable/empty_album.png"),
                        contentDescription = "Empty",
                        modifier = Modifier.size(160.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("No collections yet", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 18.sp)
                    Text("Start by joining your friends or\ncreating a new album.", color = TextSecondary, fontSize = 14.sp, textAlign = TextAlign.Center)
                }
            }
        } else {
            items(groups) { group -> CollectionCard(group.name, group.joinCode, "") { IconCamera(BrandBlue, 22f) } }
        }
    }
}

@Composable
fun AlbumsTab() { AlbumFeedScreen().Content() }
@Composable
fun GroupsTab() { CreateJoinGroupScreen().Content() }

@Composable
fun FavoriteCard(modifier: Modifier = Modifier, title: String, count: String, gradient: List<Color>, iconComposable: @Composable () -> Unit) {
    Box(
        modifier = modifier.height(150.dp).shadow(4.dp, RoundedCornerShape(20.dp)).clip(RoundedCornerShape(20.dp))
            .background(brush = Brush.verticalGradient(gradient)).padding(20.dp)
    ) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            iconComposable()
            Column {
                Text(title, fontWeight = FontWeight.Bold, color = TextDark, fontSize = 16.sp)
                Text(count, fontSize = 12.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
fun CollectionCard(title: String, code: String, count: String, iconComposable: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).shadow(2.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp)).background(Color.White).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(CardBackground), contentAlignment = Alignment.Center) { iconComposable() }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, color = TextDark, fontSize = 15.sp)
            Text("Code: $code", fontSize = 12.sp, color = TextSecondary)
        }
        if (count.isNotEmpty()) {
            Box(Modifier.clip(RoundedCornerShape(12.dp)).background(BrandBlue.copy(alpha = 0.1f)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                Text(count, fontWeight = FontWeight.Bold, color = BrandBlue, fontSize = 13.sp)
            }
        }
    }
}
