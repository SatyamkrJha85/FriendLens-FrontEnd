import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

class PhotoCaptureScreen : Screen {
    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var selectedMode by remember { mutableStateOf("PHOTO") }

        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            // "Lens" Preview Mock
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFF0F172A).copy(alpha = 0.8f), Color(0xFF000000))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                IconCamera(Color.White.copy(alpha = 0.1f), 140f)
            }

            // Controls Overlay
            Column(modifier = Modifier.fillMaxSize()) {
                // Toolbar
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 60.dp, start = 24.dp, end = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.1f),
                        modifier = Modifier.size(44.dp).clickable { navigator.pop() }
                    ) {
                        Box(contentAlignment = Alignment.Center) { IconClose(Color.White, 20f) }
                    }
                    
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.1f),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("MOMENT ALBUM", style = MaterialTheme.typography.caption.copy(color = Color.White, letterSpacing = 2.sp))
                    }

                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.1f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) { IconFlash(WarningAmber, 20f) }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Camera UI Bottom Bar
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(bottom = 60.dp, top = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Zoom Switcher
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Text("0.5", color = Color.White.copy(alpha = 0.5f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("1x", color = ActiveZoom, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("3x", color = Color.White.copy(alpha = 0.5f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Shutter Row
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Gallery Preview
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(2.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        ) {
                            Image(
                                painter = painterResource("drawable/photo_sample.png"),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // Main Shutter Button
                        Box(
                            modifier = Modifier
                                .size(84.dp)
                                .clip(CircleShape)
                                .border(4.dp, Brush.linearGradient(BrandGradientFull), CircleShape)
                                .padding(8.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .clickable { 
                                    sendLocalNotification("Moment Captured!", "Uploading to your shared album...")
                                }
                        )

                        // Flip Cam
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.1f),
                            modifier = Modifier.size(50.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) { IconFlip(Color.White, 24f) }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Mode Selector
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(32.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("PORTRAIT", "PHOTO", "SQUARE").forEach { mode ->
                            val isActive = selectedMode == mode
                            Text(
                                text = mode,
                                modifier = Modifier.clickable { selectedMode = mode },
                                style = MaterialTheme.typography.caption.copy(
                                    color = if (isActive) BrandPrimary else Color.White.copy(alpha = 0.5f),
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                    letterSpacing = 1.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
