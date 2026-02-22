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

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ProfileTab() {
    var userProfile by remember { mutableStateOf<UserResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    var selectedRating by remember { mutableStateOf(5) }
    var feedbackText by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            userProfile = FriendLensApi.getCurrentUser()
        } catch (_: Exception) {}
        isLoading = false
    }

    if (isLoading) {
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
                                .size(112.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEF2F2))
                                .border(4.dp, Color.White, CircleShape)
                                .shadow(8.dp, CircleShape)
                        ) {
                            Image(
                                painter = painterResource("drawable/onboarding_celebration.png"),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Surface(
                            modifier = Modifier.size(32.dp).offset(x = 4.dp, y = 4.dp),
                            shape = CircleShape,
                            color = BrandPrimary,
                            elevation = 4.dp,
                            border = BorderStroke(2.dp, Color.White)
                        ) {
                            Box(contentAlignment = Alignment.Center) { IconPlus(Color.White, 16f) }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Text(userProfile?.username ?: "Anonymous User", style = MaterialTheme.typography.h2)
                    Text(userProfile?.email ?: "No email provided", style = MaterialTheme.typography.body2)

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatItem("12", "Groups")
                        Divider(modifier = Modifier.width(1.dp).height(24.dp), color = DividerColor)
                        StatItem("156", "Photos")
                        Divider(modifier = Modifier.width(1.dp).height(24.dp), color = DividerColor)
                        StatItem("5", "Badges")
                    }
                }
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFF9FAFB)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = RoundedCornerShape(8.dp), color = BrandPrimary.copy(alpha = 0.1f)) {
                                Box(modifier = Modifier.padding(8.dp)) { IconBell(BrandPrimary, 20f) }
                            }
                            Spacer(Modifier.width(12.dp))
                            Text("Share Feedback", style = MaterialTheme.typography.h3)
                        }
                        
                        Text(
                            "Help us improve FriendLens! Your feedback matters.",
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            repeat(5) { index ->
                                val rating = index + 1
                                val isSelected = selectedRating >= rating
                                IconHeart(
                                    color = if (isSelected) BrandPrimary else Color(0xFFE2E8F0),
                                    filled = isSelected,
                                    size = 32f,
                                    modifier = Modifier.clickable { selectedRating = rating }
                                )
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        TextField(
                            value = feedbackText,
                            onValueChange = { feedbackText = it },
                            modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(16.dp)),
                            placeholder = { Text("What can we do better?", style = MaterialTheme.typography.body2) },
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color.White,
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
                                        // Show success toast or similar
                                    } catch (_: Exception) {}
                                    finally { isSubmitting = false }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp).shadow(8.dp, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = BrandPrimary),
                            enabled = !isSubmitting
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Send Feedback", fontWeight = FontWeight.Bold)
                                Spacer(Modifier.width(8.dp))
                                if (isSubmitting) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                                else IconShare(Color.White, 16f)
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Account Settings", style = MaterialTheme.typography.h3)
                }
            }

            item {
                SettingsRow("Edit Profile", { IconProfile(TextSecondary, 20f) })
                SettingsRow("Notification Preference", { IconBell(TextSecondary, 20f) })
                SettingsRow("Privacy & Security", { IconSettings(TextSecondary, 20f) })
                
                Spacer(Modifier.height(20.dp))
                
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).clickable {
                        SessionManager.logout()
                    },
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFEF2F2),
                    border = BorderStroke(1.dp, Color(0xFFFEE2E2))
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Text("Log Out", color = BrandPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsRow(label: String, icon: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 6.dp).clickable { },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, DividerColor)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(24.dp), contentAlignment = Alignment.Center) { icon() }
            Spacer(Modifier.width(12.dp))
            Text(label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.body1)
            IconPlus(TextSecondary, 16f) // Chevron mock
        }
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.h2.copy(fontSize = 20.sp))
        Text(label, style = MaterialTheme.typography.caption.copy(fontSize = 10.sp, letterSpacing = 1.sp))
    }
}
