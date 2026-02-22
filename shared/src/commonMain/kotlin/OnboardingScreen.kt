import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val imageRes: String
)

class OnboardingScreen : Screen {
    @OptIn(ExperimentalFoundationApi::class, ExperimentalResourceApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val pagerState = rememberPagerState(pageCount = { 3 })
        val scope = rememberCoroutineScope()

        val pages = listOf(
            OnboardingPage(
                "Capture the vibe",
                "Relive the best moments together.\nCreate shared albums instantly with\nfriends nearby.",
                "drawable/onboarding_capture.png"
            ),
            OnboardingPage(
                "Group Adventures",
                "Every hike, trip and journey. Collect\neveryone's photos and share the\nmemory.",
                "drawable/onboarding_adventure.png"
            ),
            OnboardingPage(
                "Special Moments",
                "Birthdays, graduations, or just a Tuesday\nnight. Capture every memory with the\npeople who matter most.",
                "drawable/onboarding_celebration.png"
            )
        )

        // Gradient backgrounds matching icon palette
        val bgGradients = listOf(
            listOf(Color(0xFF0F1A3E), Color(0xFF1A1A2E)),  // Deep blue-navy
            listOf(Color(0xFF1A1042), Color(0xFF0F1A3E)),  // Purple-navy
            listOf(Color(0xFF2D1528), Color(0xFF1A1A2E))   // Pink-navy
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.verticalGradient(bgGradients[pagerState.currentPage]))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top bar
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (pagerState.currentPage > 0) {
                        IconButton(onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } }) {
                            IconBack(Color.White.copy(alpha = 0.7f), 20f)
                        }
                    } else Spacer(modifier = Modifier.size(48.dp))

                    TextButton(onClick = { navigator.push(LoginScreen()) }) {
                        Text("Skip", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                    }
                }

                // Pager
                HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
                    Column(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Illustration with brand gradient border
                        Box(
                            modifier = Modifier
                                .size(280.dp)
                                .clip(RoundedCornerShape(40.dp))
                                .background(Color.White.copy(alpha = 0.06f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(pages[page].imageRes),
                                contentDescription = pages[page].title,
                                modifier = Modifier.fillMaxSize().padding(8.dp).clip(RoundedCornerShape(36.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(48.dp))

                        Text(pages[page].title, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(pages[page].subtitle, fontSize = 15.sp, color = Color.White.copy(alpha = 0.6f), textAlign = TextAlign.Center, lineHeight = 24.sp)
                    }
                }

                // Dots with brand gradient colors
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), horizontalArrangement = Arrangement.Center) {
                    val dotColors = listOf(BrandBlue, BrandPurple, BrandCoral)
                    repeat(3) { index ->
                        val isActive = pagerState.currentPage == index
                        val width by animateDpAsState(if (isActive) 24.dp else 8.dp, tween(300))
                        val color by animateColorAsState(if (isActive) dotColors[index] else Color.White.copy(alpha = 0.3f), tween(300))
                        Box(
                            modifier = Modifier.padding(horizontal = 3.dp).height(8.dp).width(width)
                                .clip(CircleShape).background(color)
                        )
                    }
                }

                // CTA Button with brand gradient
                Button(
                    onClick = {
                        if (pagerState.currentPage < 2) scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        else navigator.push(LoginScreen())
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp).height(60.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = BrandBlue),
                    elevation = ButtonDefaults.elevation(defaultElevation = 8.dp)
                ) {
                    Text(
                        if (pagerState.currentPage == 2) "Get Started" else "Continue",
                        color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold
                    )
                }

                // Footer
                if (pagerState.currentPage == 0) {
                    TextButton(
                        onClick = { navigator.push(LoginScreen()) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                    ) {
                        Text("Already have account? ", color = Color.White.copy(alpha = 0.5f))
                        Text("Sign in", color = BrandCoral, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
