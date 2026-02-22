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
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(brush = Brush.verticalGradient(listOf(Color(0xFF0F1A3E).copy(alpha = 0.6f), Color(0xFF1A0E28).copy(alpha = 0.8f)))),
                contentAlignment = Alignment.Center
            ) { IconCamera(Color.White.copy(alpha = 0.15f), 120f) }

            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 56.dp, start = 24.dp, end = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(40.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.4f)).clickable { navigator.pop() }, contentAlignment = Alignment.Center) {
                    IconClose(Color.White, 16f)
                }
                Box(Modifier.clip(RoundedCornerShape(20.dp)).background(Color.Black.copy(alpha = 0.4f)).padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text("FriendLens Album", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                Box(Modifier.size(40.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.4f)), contentAlignment = Alignment.Center) {
                    IconFlash(WarningAmber, 18f)
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(bottom = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Zoom
                Row(
                    modifier = Modifier.clip(RoundedCornerShape(24.dp)).background(Color.Black.copy(alpha = 0.5f)).padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(".5", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("1×", color = BrandBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("3", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(36.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp),
                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.size(48.dp).clip(RoundedCornerShape(10.dp)).border(2.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
                        Image(painter = painterResource("drawable/photo_sample.png"), contentDescription = "Gallery", modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                    }

                    // Shutter with icon gradient ring
                    Box(
                        modifier = Modifier.size(80.dp).clip(CircleShape)
                            .border(4.dp, Brush.sweepGradient(listOf(BrandBlue, BrandPurple, BrandPink, BrandCoral, BrandOrange, BrandBlue)), CircleShape)
                            .padding(6.dp).clip(CircleShape)
                            .background(Color.White)
                            .clickable { sendLocalNotification("Photo Captured!", "Your photo will be uploaded to the album.") }
                    )

                    Box(Modifier.size(48.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.4f)), contentAlignment = Alignment.Center) {
                        IconFlip(Color.White, 22f)
                    }
                }

                Spacer(Modifier.height(28.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(32.dp), verticalAlignment = Alignment.CenterVertically) {
                    listOf("PORTRAIT", "PHOTO", "SQUARE").forEach { mode ->
                        Text(
                            mode,
                            color = if (selectedMode == mode) Color.White else Color.White.copy(alpha = 0.4f),
                            fontSize = if (selectedMode == mode) 14.sp else 12.sp,
                            fontWeight = if (selectedMode == mode) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.clickable { selectedMode = mode }
                        )
                    }
                }
            }
        }
    }
}
