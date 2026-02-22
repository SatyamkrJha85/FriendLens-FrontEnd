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
    @OptIn(ExperimentalFoundationApi::class, ExperimentalResourceApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val pagerState = rememberPagerState(pageCount = { 3 })
        val scope = rememberCoroutineScope()

        val pages = listOf(
            OnboardingPage(
                "Capture the vibe",
                "Relive the best moments together. Create shared albums instantly with friends nearby.",
                "drawable/onboarding_capture.png"
            ),
            OnboardingPage(
                "Group Adventures",
                "Every hike, trip and journey. Collect everyone's photos and share the memory.",
                "drawable/onboarding_adventure.png"
            ),
            OnboardingPage(
                "Special Moments",
                "Birthdays, graduations, or just a Tuesday night. Capture every memory with the people who matter most.",
                "drawable/onboarding_celebration.png"
            )
        )

        val bgColors = listOf(
            Color(0xFFF0F7FF), // Soft blue
            Color(0xFFF5F3FF), // Soft purple
            Color(0xFFFFF1F2)  // Soft pink/rose
        )
        val backgroundColor by animateColorAsState(bgColors[pagerState.currentPage], tween(600))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top skip button
                Box(modifier = Modifier.fillMaxWidth().padding(top = 24.dp, end = 24.dp)) {
                    TextButton(
                        onClick = { navigator.push(LoginScreen()) },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Text("Skip", color = TextSecondary, style = MaterialTheme.typography.body2)
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { page ->
                    Column(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .fillMaxWidth()
                                .shadow(20.dp, RoundedCornerShape(48.dp)),
                            shape = RoundedCornerShape(48.dp),
                            color = Color.White
                        ) {
                            Image(
                                painter = painterResource(pages[page].imageRes),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().padding(16.dp).clip(RoundedCornerShape(32.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(48.dp))

                        Text(
                            text = pages[page].title,
                            style = MaterialTheme.typography.h1.copy(color = TextDark, textAlign = TextAlign.Center)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = pages[page].subtitle,
                            style = MaterialTheme.typography.body1.copy(
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 24.sp
                            )
                        )
                    }
                }

                // Interaction Area
                Column(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Page Indicator dots
                    Row(
                        modifier = Modifier.padding(bottom = 32.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(3) { index ->
                            val isActive = pagerState.currentPage == index
                            val width by animateDpAsState(if (isActive) 28.dp else 8.dp, tween(300))
                            val color by animateColorAsState(
                                if (isActive) BrandGradientFull[index % BrandGradientFull.size]
                                else Color.Black.copy(alpha = 0.1f),
                                tween(300)
                            )
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .height(8.dp)
                                    .width(width)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                        }
                    }

                    // Main Action Button - Now using Brand Gradient for consistency
                    Button(
                        onClick = {
                            if (pagerState.currentPage < 2) {
                                scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                            } else {
                                navigator.push(LoginScreen())
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .shadow(12.dp, CircleShape),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(brush = Brush.linearGradient(BrandGradient)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (pagerState.currentPage == 2) "Get Started" else "Continue",
                                style = MaterialTheme.typography.button.copy(color = Color.White)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

data class OnboardingPage(val title: String, val subtitle: String, val imageRes: String)
