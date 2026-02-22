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
    
    val session = SessionManager.session

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(BackgroundLight),
        contentPadding = PaddingValues(24.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Account", style = MaterialTheme.typography.h2)
                Surface(shape = CircleShape, color = Color.White, modifier = Modifier.size(40.dp).shadow(2.dp, CircleShape)) {
                    Box(contentAlignment = Alignment.Center) { IconSettings(TextDark, 20f) }
                }
            }
            Spacer(Modifier.height(32.dp))
        }

        // Profile Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                elevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .shadow(12.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Brush.sweepGradient(BrandGradientFull)),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier.size(96.dp),
                            shape = CircleShape,
                            color = Color.White
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = session.username?.firstOrNull()?.uppercase() ?: "U",
                                    style = MaterialTheme.typography.h1.copy(color = BrandPurple, fontSize = 40.sp)
                                )
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(20.dp))
                    Text(session.username ?: "Anonymous User", style = MaterialTheme.typography.h2)
                    Text(session.email ?: "no-email@friendlens.app", style = MaterialTheme.typography.body2)
                    
                    Spacer(Modifier.height(32.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProfileStat("12", "ALBUMS")
                        VerticalDivider()
                        ProfileStat("148", "PHOTOS")
                        VerticalDivider()
                        ProfileStat("4.8", "STARS")
                    }
                }
            }
            Spacer(Modifier.height(32.dp))
        }

        // Feedback Section
        item {
            Text("TELL US ANYTHING", style = MaterialTheme.typography.caption.copy(letterSpacing = 2.sp, fontWeight = FontWeight.Bold))
            Spacer(Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                elevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("How are we doing?", style = MaterialTheme.typography.h3)
                    Spacer(Modifier.height(16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        repeat(5) { index ->
                            val rating = index + 1
                            val isSelected = selectedRating >= rating
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) BrandGradientFull[index].copy(alpha = 0.1f) else BackgroundLight,
                                modifier = Modifier.size(48.dp).clickable { selectedRating = rating }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    IconHeart(
                                        color = if (isSelected) BrandGradientFull[index] else DividerColor,
                                        filled = isSelected,
                                        size = 24f
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(24.dp))
                    
                    OutlinedTextField(
                        value = feedbackText, onValueChange = { feedbackText = it },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        placeholder = { Text("Your thoughts here...", style = MaterialTheme.typography.body2) },
                        shape = MaterialTheme.shapes.medium,
                        colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = BrandBlue)
                    )
                    
                    if (feedbackMsg != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(feedbackMsg!!, color = SuccessGreen, style = MaterialTheme.typography.caption)
                    }

                    Spacer(Modifier.height(24.dp))
                    
                    Button(
                        onClick = {
                            isSubmitting = true
                            scope.launch {
                                try {
                                    val resp = FriendLensApi.submitFeedback(FeedbackRequest(content = feedbackText, rating = selectedRating.toString()))
                                    if (resp.status == "success") {
                                        feedbackMsg = "Feedback sent. Thank you!"
                                        feedbackText = ""; selectedRating = 0
                                    }
                                } catch (_: Exception) {
                                    feedbackMsg = "Failed to send."
                                }
                                isSubmitting = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = MaterialTheme.shapes.large,
                        colors = ButtonDefaults.buttonColors(backgroundColor = BrandBlue),
                        enabled = !isSubmitting && feedbackText.isNotBlank() && selectedRating > 0
                    ) {
                        if (isSubmitting) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        else Text("Submit Feedback", style = MaterialTheme.typography.button)
                    }
                }
            }
            Spacer(Modifier.height(32.dp))
        }

        item {
            OutlinedButton(
                onClick = { SessionManager.logout() },
                modifier = Modifier.fillMaxWidth().height(56.dp).padding(bottom = 32.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandCoral)
            ) {
                Text("Sign Out", style = MaterialTheme.typography.button.copy(color = BrandCoral))
            }
        }
    }
}

@Composable
fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.h2)
        Text(label, style = MaterialTheme.typography.caption.copy(letterSpacing = 1.sp))
    }
}

@Composable
fun VerticalDivider() {
    Box(modifier = Modifier.width(1.dp).height(40.dp).background(DividerColor))
}
