import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material.Surface
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

class SplashScreen : Screen {
    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scale = remember { Animatable(0.6f) }
        val alpha = remember { Animatable(0f) }

        LaunchedEffect(Unit) {
            launch {
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
            launch {
                alpha.animateTo(1f, tween(1200, easing = FastOutSlowInEasing))
            }

            delay(2800)
            
            if (SessionManager.session.isLoggedIn) {
                navigator.replaceAll(MainDashboardScreen())
            } else {
                navigator.replaceAll(OnboardingScreen())
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            // Stitch Background hint
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-100).dp, y = 100.dp)
                    .size(400.dp)
                    .background(
                        brush = Brush.radialGradient(
                            listOf(BrandPrimary.copy(0.05f), Color.Transparent)
                        )
                    )
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.scale(scale.value).alpha(alpha.value)
            ) {
                // Branded Icon Container - Clean and Minimalist
                Surface(
                    modifier = Modifier
                        .size(120.dp)
                        .shadow(12.dp, RoundedCornerShape(32.dp)),
                    shape = RoundedCornerShape(32.dp),
                    color = Color.White
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Image(
                            painter = painterResource("drawable/app_icon.png"),
                            contentDescription = "FriendLens Logo",
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "FriendLens",
                    style = MaterialTheme.typography.h1.copy(
                        color = TextPrimary,
                        fontSize = 38.sp,
                        letterSpacing = (-1.5).sp
                    )
                )

                Text(
                    text = "ALL MOMENTS. ONE PLACE.",
                    style = MaterialTheme.typography.caption.copy(
                        color = BrandPrimary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                )
            }
            
            // Minimal Loader using Brand Gradient
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp)
            ) {
                val infiniteTransition = rememberInfiniteTransition()
                val breathingAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.4f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(800, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .alpha(if (index == 1) breathingAlpha else 0.3f)
                                .clip(CircleShape)
                                .background(BrandPrimary)
                        )
                    }
                }
            }
        }
    }
}
