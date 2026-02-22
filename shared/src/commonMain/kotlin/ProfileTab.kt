import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun ProfileTab() {
    val scope = rememberCoroutineScope()
    var feedbackText by remember { mutableStateOf("") }
    var selectedRating by remember { mutableStateOf(0) }
    var feedbackMsg by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    var editingName by remember { mutableStateOf(false) }
    var newUsername by remember { mutableStateOf(SessionManager.session.username ?: "") }
    val session = SessionManager.session

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(24.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {}) { IconBack(TextDark, 20f) }
                Text("Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                IconButton(onClick = {}) { IconSettings(TextSecondary, 22f) }
            }
            Spacer(Modifier.height(32.dp))
        }

        // Avatar with brand gradient
        item {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(100.dp).shadow(8.dp, CircleShape).clip(CircleShape)
                        .background(brush = Brush.sweepGradient(listOf(BrandBlue, BrandPurple, BrandPink, BrandCoral, BrandOrange, BrandBlue))),
                    contentAlignment = Alignment.Center
                ) {
                    // Inner white circle with initial
                    Box(
                        modifier = Modifier.size(90.dp).clip(CircleShape).background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text((session.username?.firstOrNull()?.uppercase() ?: "U"), fontSize = 36.sp, color = BrandPurple, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(16.dp))

                if (editingName) {
                    OutlinedTextField(
                        value = newUsername, onValueChange = { newUsername = it },
                        modifier = Modifier.width(200.dp), shape = RoundedCornerShape(12.dp), singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = BrandBlue)
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { scope.launch { try { FriendLensApi.updateProfile(UpdateProfileRequest(username = newUsername)); SessionManager.updateProfile(newUsername, null); editingName = false } catch (_: Exception) {} } },
                        colors = ButtonDefaults.buttonColors(backgroundColor = BrandBlue), shape = RoundedCornerShape(16.dp)
                    ) { Text("Save", color = Color.White) }
                } else {
                    Text(session.username ?: "FriendLens User", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.clickable { editingName = true })
                }
                Text(session.email ?: "user@friendlens.app", fontSize = 14.sp, color = TextSecondary)
                Spacer(Modifier.height(32.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    StatBox("124", "MOMENTS")
                    Box(Modifier.width(1.dp).height(48.dp).background(DividerColor))
                    StatBox("856", "SHARES")
                    Box(Modifier.width(1.dp).height(48.dp).background(DividerColor))
                    StatBox("4.9", "RATING")
                }
            }
            Spacer(Modifier.height(32.dp))
        }

        // Feedback Card
        item {
            Box(Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp)).clip(RoundedCornerShape(20.dp)).background(Color.White).padding(24.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconComment(BrandPurple, 20f)
                        Spacer(Modifier.width(8.dp))
                        Text("Leave Feedback", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("How was your experience collaborating\nwith your friends?", fontSize = 13.sp, color = TextSecondary, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(20.dp))

                    // Star Rating — colored dots from icon
                    Row(horizontalArrangement = Arrangement.Center) {
                        val ratingColors = listOf(BrandBlue, BrandPurple, BrandPink, BrandCoral, BrandOrange)
                        repeat(5) { index ->
                            IconButton(onClick = { selectedRating = index + 1 }) {
                                IconHeart(
                                    if (index < selectedRating) ratingColors[index] else DividerColor,
                                    filled = index < selectedRating, size = 28f
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = feedbackText, onValueChange = { feedbackText = it },
                        placeholder = { Text("Tell us what you loved...", color = TextSecondary) },
                        modifier = Modifier.fillMaxWidth().height(100.dp), shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = BrandPurple, unfocusedBorderColor = DividerColor)
                    )

                    if (feedbackMsg != null) { Spacer(Modifier.height(8.dp)); Text(feedbackMsg!!, color = SuccessGreen, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (feedbackText.isNotBlank() && selectedRating > 0) {
                                isSubmitting = true
                                scope.launch {
                                    try {
                                        val resp = FriendLensApi.submitFeedback(FeedbackRequest(content = feedbackText, rating = selectedRating.toString()))
                                        if (resp.status == "success") { feedbackMsg = "Thank you for your feedback!"; feedbackText = ""; selectedRating = 0; sendLocalNotification("Feedback Received!", "Thanks for helping us improve FriendLens.") }
                                    } catch (e: Exception) { feedbackMsg = "Failed: ${e.message}" }
                                    isSubmitting = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = BrandPurple),
                        elevation = ButtonDefaults.elevation(defaultElevation = 4.dp),
                        enabled = feedbackText.isNotBlank() && selectedRating > 0 && !isSubmitting
                    ) {
                        if (isSubmitting) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        else Text("Submit Review", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(Modifier.height(32.dp))
        }

        item {
            Text("Recent Activity", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(Modifier.height(16.dp))
            ActivityItem("Added 5 photos", "Summer Festival '23", "2h ago") { IconCamera(BrandBlue, 18f) }
            ActivityItem("Joined new group", "Reunion Trip", "1d ago") { IconGroup(BrandPurple, 18f) }
            ActivityItem("Liked 3 photos", "Mountain Trek", "3d ago") { IconHeart(BrandPink, filled = true, size = 18f) }
            Spacer(Modifier.height(32.dp))
        }

        item {
            OutlinedButton(
                onClick = { SessionManager.logout() },
                modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandCoral)
            ) { Text("Sign Out", color = BrandCoral, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
fun StatBox(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Text(label, fontSize = 10.sp, color = TextSecondary, letterSpacing = 1.sp)
    }
}

@Composable
fun ActivityItem(title: String, subtitle: String, time: String, iconContent: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).shadow(2.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp)).background(Color.White).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(44.dp).clip(CircleShape).background(CardBackground), contentAlignment = Alignment.Center) { iconContent() }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, color = TextDark, fontSize = 14.sp)
            Text(subtitle, fontSize = 12.sp, color = TextSecondary)
        }
        Text(time, fontSize = 12.sp, color = TextSecondary)
    }
}
