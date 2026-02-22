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
        modifier = Modifier.fillMaxSize().background(Color.White),
        contentPadding = PaddingValues(24.dp)
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Avatar from Mockup
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(BackgroundLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        session.username?.firstOrNull()?.toString()?.uppercase() ?: "U",
                        style = MaterialTheme.typography.h1.copy(fontSize = 32.sp, color = BrandPurple)
                    )
                }
                
                Spacer(Modifier.height(16.dp))
                Text(session.username ?: "Alex Morgan", style = MaterialTheme.typography.h2)
                Text("Photography Enthusiast", style = MaterialTheme.typography.caption)
                
                Spacer(Modifier.height(32.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProfileStat("124", "MOMENTS")
                    ProfileStat("858", "LIKED")
                    ProfileStat("4.9", "RATING")
                }
            }
            Spacer(Modifier.height(48.dp))
        }

        // Feedback Section matching mockup style
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = BackgroundLight
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconComment(BrandCoral, 20f)
                        Spacer(Modifier.width(8.dp))
                        Text("Leave Feedback", style = MaterialTheme.typography.h3.copy(fontSize = 16.sp))
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("How was your experience collaborating with your friends on our trip?", style = MaterialTheme.typography.body2)
                    
                    Spacer(Modifier.height(20.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        repeat(5) { index ->
                            val rating = index + 1
                            val isSelected = selectedRating >= rating
                            IconHeart(
                                color = if (isSelected) BrandCoral else DividerColor,
                                filled = isSelected,
                                size = 28f,
                                modifier = Modifier.clickable { selectedRating = rating }
                            )
                        }
                    }
                    
                    Spacer(Modifier.height(24.dp))
                    
                    Button(
                        onClick = {
                            isSubmitting = true
                            scope.launch {
                                try {
                                    FriendLensApi.submitFeedback(FeedbackRequest(content = feedbackText, rating = selectedRating.toString()))
                                    feedbackMsg = "Sent! Thanks."
                                } catch (_: Exception) {}
                                isSubmitting = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp).shadow(4.dp, CircleShape),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(backgroundColor = BrandCoral),
                        enabled = !isSubmitting && selectedRating > 0
                    ) {
                        if (isSubmitting) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        else Text("Submit Review", style = MaterialTheme.typography.button)
                    }
                }
            }
            Spacer(Modifier.height(48.dp))
        }

        item {
            Text("Recent Activity", style = MaterialTheme.typography.h3.copy(fontSize = 16.sp))
            Spacer(Modifier.height(16.dp))
            ActivityItem("Added 5 photos", "Summer Festival '23", "2h ago", BrandBlue)
            ActivityItem("Joined Group", "Mountain Trip", "3h ago", BrandPurple)
            ActivityItem("Liked 12 photos", "Party Night", "1d ago", BrandPink)
            
            Spacer(Modifier.height(48.dp))
            
            TextButton(
                onClick = { SessionManager.logout() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign Out", color = BrandCoral, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun ActivityItem(title: String, group: String, time: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp))
            Text(group, style = MaterialTheme.typography.body2.copy(fontSize = 12.sp))
        }
        Text(time, style = MaterialTheme.typography.caption)
    }
}

@Composable
fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.h2.copy(fontSize = 20.sp))
        Text(label, style = MaterialTheme.typography.caption.copy(letterSpacing = 1.sp, fontSize = 10.sp))
    }
}
