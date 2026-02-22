import androidx.compose.foundation.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ProfileTab() {
    var userProfile by remember { mutableStateOf(DataCache.user) }
    var isLoading by remember { mutableStateOf(userProfile == null) }
    val scope = rememberCoroutineScope()
    var selectedRating by remember { mutableStateOf(5) }
    var feedbackText by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (DataCache.user == null) {
            try {
                val user = FriendLensApi.getCurrentUser()
                DataCache.user = user
                userProfile = user
            } catch (_: Exception) {}
            isLoading = false
        }
    }

    if (isLoading && userProfile == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BrandPrimary)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(Color.White),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Color(0xFFFEF2F2), Color(0xFFFEE2E2))))
                                .border(4.dp, Color.White, CircleShape)
                                .shadow(12.dp, CircleShape)
                        ) {
                            Image(
                                painter = painterResource("drawable/onboarding_celebration.png"),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Surface(
                            modifier = Modifier.size(36.dp).offset(x = 6.dp, y = 6.dp),
                            shape = CircleShape,
                            color = BrandPrimary,
                            elevation = 6.dp,
                            border = BorderStroke(3.dp, Color.White)
                        ) {
                            Box(contentAlignment = Alignment.Center) { IconPlus(Color.White, 18f) }
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    Text(userProfile?.username ?: "Explorer", style = MaterialTheme.typography.h2.copy(fontSize = 28.sp, fontWeight = FontWeight.Black))
                    Text(userProfile?.email ?: "Join a group to share moments", style = MaterialTheme.typography.body2.copy(color = TextSecondary))

                    Spacer(Modifier.height(32.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFFF9FAFB))
                            .padding(vertical = 20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatItem("12", "ALBUMS")
                        Box(Modifier.width(1.dp).height(24.dp).background(DividerColor))
                        StatItem("156", "CAPTURES")
                        Box(Modifier.width(1.dp).height(24.dp).background(DividerColor))
                        StatItem("5", "BADGES")
                    }
                }
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, DividerColor)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(BrandPrimary.copy(0.1f)),
                                contentAlignment = Alignment.Center
                            ) { IconBell(BrandPrimary, 22f) }
                            Spacer(Modifier.width(16.dp))
                            Text("Experience Feedback", style = MaterialTheme.typography.h3)
                        }
                        
                        Text(
                            "We're building FriendLens for you. Let us know how we're doing!",
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = TextSecondary
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            repeat(5) { index ->
                                val rating = index + 1
                                val isSelected = selectedRating >= rating
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) BrandPrimary.copy(0.1f) else Color.Transparent)
                                        .clickable { selectedRating = rating },
                                    contentAlignment = Alignment.Center
                                ) {
                                    IconHeart(
                                        color = if (isSelected) BrandPrimary else Color(0xFFE2E8F0),
                                        filled = isSelected,
                                        size = 32f
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        TextField(
                            value = feedbackText,
                            onValueChange = { feedbackText = it },
                            modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(20.dp)),
                            placeholder = { Text("Share your thoughts...", style = MaterialTheme.typography.body2.copy(color = TextSecondary.copy(0.5f))) },
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color(0xFFF3F4F6),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )

                        Spacer(Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (feedbackText.isBlank()) return@Button
                                isSubmitting = true
                                scope.launch {
                                    try {
                                        FriendLensApi.submitFeedback(FeedbackRequest(content = feedbackText, rating = selectedRating.toString()))
                                        feedbackText = ""
                                    } catch (_: Exception) {}
                                    finally { isSubmitting = false }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp).shadow(12.dp, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = BrandPrimary),
                            enabled = !isSubmitting
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isSubmitting) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                } else {
                                    Text("Send to Team", fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.width(12.dp))
                                    IconShare(Color.White, 18f)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 40.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Settings & Privacy", style = MaterialTheme.typography.h3.copy(fontSize = 20.sp))
                }
            }

            item {
                SettingsRow("Profile Information", { IconProfile(TextSecondary, 22f) })
                SettingsRow("Notification Hub", { IconBell(TextSecondary, 22f) })
                SettingsRow("Security Center", { IconSettings(TextSecondary, 22f) })
                
                Spacer(Modifier.height(24.dp))
                
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .clickable { 
                            DataCache.clear()
                            SessionManager.logout() 
                        },
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFFEF2F2),
                    border = BorderStroke(1.dp, Color(0xFFFEE2E2).copy(0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Log Out Account", color = BrandPrimary, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.body1)
                    }
                }
                
                Spacer(Modifier.height(40.dp))
                Text(
                    text = "FriendLens v2.0.4",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.caption.copy(color = TextSecondary.copy(0.4f))
                )
            }
        }
    }
}

@Composable
fun SettingsRow(label: String, icon: @Composable () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp)
            .clickable { },
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, DividerColor)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF3F4F6)),
                contentAlignment = Alignment.Center
            ) { icon() }
            Spacer(Modifier.width(16.dp))
            Text(label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.SemiBold))
            IconChevronRight(TextSecondary.copy(0.3f), 16f)
        }
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.h2.copy(fontSize = 24.sp, fontWeight = FontWeight.Black))
        Text(label, style = MaterialTheme.typography.caption.copy(fontSize = 10.sp, letterSpacing = 1.5.sp, fontWeight = FontWeight.Bold, color = TextSecondary))
    }
}
