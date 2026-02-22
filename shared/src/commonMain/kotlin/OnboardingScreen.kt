import androidx.compose.foundation.BorderStroke
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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

class OnboardingScreen : Screen {
    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun Content() {
        var currentPage by remember { mutableStateOf(0) }
        val navigator = LocalNavigator.currentOrThrow

        val pages = listOf(
            OnboardingPage(
                "Capture the vibe",
                "Relive the best moments together. Create shared albums instantly with friends nearby.",
                "drawable/onboarding_capture.png",
                "FESTIVAL MODE"
            ),
            OnboardingPage(
                "Group Adventures",
                "Every trip, every party, every smile. Collective memories, perfectly organized.",
                "drawable/onboarding_adventure.png",
                "ADVENTURE MODE"
            ),
            OnboardingPage(
                "Special Moments",
                "Never miss a shot. Automatic photo syncing makes sharing effortless.",
                "drawable/onboarding_celebration.png",
                "MEMORY MODE"
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(bottom = 40.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 24.dp, end = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(modifier = Modifier.size(32.dp), shape = CircleShape, color = BrandPrimary) {
                        Box(contentAlignment = Alignment.Center) { IconSettings(Color.White, 18f) }
                    }
                    Spacer(Modifier.width(8.dp))
                    Text("FriendLens", style = MaterialTheme.typography.h3.copy(fontSize = 18.sp))
                }
                Text(
                    "Skip",
                    style = MaterialTheme.typography.body2.copy(color = TextSecondary),
                    modifier = Modifier.clickable { navigator.replaceAll(LoginScreen()) }
                )
            }

            Spacer(Modifier.weight(1f))

            // Hero section with Stitch-style float animation
            val transition = rememberInfiniteTransition()
            val translateY by transition.animateFloat(
                initialValue = 0f,
                targetValue = -12f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f/3f)
                    .padding(horizontal = 24.dp)
                    .offset(y = translateY.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background glow
                Box(Modifier.size(240.dp).background(BrandPrimary.copy(alpha = 0.1f), CircleShape).padding(20.dp))
                
                Surface(
                    modifier = Modifier.fillMaxSize().shadow(24.dp, RoundedCornerShape(32.dp)),
                    shape = RoundedCornerShape(32.dp),
                    border = BorderStroke(4.dp, Color.White)
                ) {
                    Image(
                        painter = painterResource(pages[currentPage].image),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // Content section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = BrandPrimary.copy(alpha = 0.1f),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text(
                        pages[currentPage].badge,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.caption.copy(color = BrandPrimary, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    )
                }

                Text(
                    text = pages[currentPage].title,
                    style = MaterialTheme.typography.h1.copy(fontSize = 32.sp, textAlign = TextAlign.Center),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = pages[currentPage].description,
                    style = MaterialTheme.typography.body1.copy(color = TextSecondary, textAlign = TextAlign.Center, lineHeight = 24.sp),
                    modifier = Modifier.padding(bottom = 40.dp)
                )

                // Indicators
                Row(
                    modifier = Modifier.padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(pages.size) { index ->
                        val isSelected = currentPage == index
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .width(if (isSelected) 24.dp else 6.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) BrandPrimary else Color(0xFFE5E7EB))
                        )
                    }
                }

                Button(
                    onClick = {
                        if (currentPage < pages.size - 1) currentPage++
                        else navigator.replaceAll(LoginScreen())
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp).shadow(12.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = BrandPrimary)
                ) {
                    Text(if (currentPage == pages.size - 1) "Get Started" else "Next", style = MaterialTheme.typography.button)
                    Spacer(Modifier.width(8.dp))
                    IconPlus(Color.White, 16f) // Arrow holder
                }
                
                Spacer(Modifier.height(24.dp))
                
                Row(modifier = Modifier.clickable { navigator.replaceAll(LoginScreen()) }) {
                    Text("Already have an account? ", style = MaterialTheme.typography.caption)
                    Text("Log in", style = MaterialTheme.typography.caption.copy(color = BrandPrimary, fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}

data class OnboardingPage(val title: String, val description: String, val image: String, val badge: String)
