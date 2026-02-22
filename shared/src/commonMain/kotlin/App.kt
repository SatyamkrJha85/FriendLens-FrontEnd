import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition

// ─── Stitch Design Tokens ───
// Core Brand Palette
val BrandPrimary = Color(0xFFF2545B) // Stitch Red/Coral
val BrandPrimaryHover = Color(0xFFD93D44)
val BrandSecondary = Color(0xFF4285F4) // Google Blue for social

// Neutrals
val BackgroundLight = Color(0xFFFAFAFA)
val SurfaceWhite = Color.White
val CardLight = Color.White
val TextPrimary = Color(0xFF1F2937) // Slate 800
val TextSecondary = Color(0xFF6B7280) // Gray 500
val DividerColor = Color(0xFFF3F4F6)
val InputBorder = Color(0xFFE2E8F0)

// Feature Colors
val SuccessGreen = Color(0xFF10B981)
val ErrorRed = Color(0xFFEF4444)
val WarningAmber = Color(0xFFF59E0B)
val ActiveZoom = Color(0xFFFFD60A) // Amber/Yellow for camera active

// Gradients
val BrandGradient = listOf(BrandPrimary, Color(0xFFFB7185)) // Coral to Rose
val BrandGradientFull = listOf(
    Color(0xFF4A90FF), // Blue
    Color(0xFF8B5CF6), // Purple
    Color(0xFFEC4899), // Pink
    BrandPrimary,      // Coral
    Color(0xFFF97316)  // Orange
)

private val FriendLensColors = lightColors(
    primary = BrandPrimary,
    primaryVariant = BrandPrimaryHover,
    secondary = BrandSecondary,
    onPrimary = Color.White,
    background = BackgroundLight,
    surface = SurfaceWhite,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = ErrorRed
)

val FriendLensShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp), // XL in Stitch
    large = RoundedCornerShape(24.dp)   // 2XL in Stitch
)

val FriendLensTypography = Typography(
    h1 = TextStyle(fontWeight = FontWeight.Bold, fontSize = 32.sp, color = TextPrimary, letterSpacing = (-0.5).sp),
    h2 = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp, color = TextPrimary),
    h3 = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary),
    body1 = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, color = TextPrimary),
    body2 = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, color = TextSecondary),
    button = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White),
    caption = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = TextSecondary, letterSpacing = 0.5.sp)
)

@Composable
fun App() {
    MaterialTheme(
        colors = FriendLensColors,
        typography = FriendLensTypography,
        shapes = FriendLensShapes
    ) {
        Navigator(SplashScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}