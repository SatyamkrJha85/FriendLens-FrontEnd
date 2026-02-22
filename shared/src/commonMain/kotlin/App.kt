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

// ─── Design Tokens (matching app icon gradient) ───
// Icon gradient: Blue → Purple → Pink → Coral → Orange
val BrandBlue = Color(0xFF4A90FF)
val BrandPurple = Color(0xFF8B5CF6)
val BrandPink = Color(0xFFEC4899)
val BrandCoral = Color(0xFFF97066)
val BrandOrange = Color(0xFFF97316)

// Gradient stops for backgrounds/buttons
val BrandGradient = listOf(BrandBlue, BrandPurple, BrandCoral)
val BrandGradientFull = listOf(BrandBlue, BrandPurple, BrandPink, BrandCoral, BrandOrange)

// Neutrals
val BackgroundLight = Color(0xFFFAFAFA)
val SurfaceWhite = Color.White
val TextDark = Color(0xFF111827) // Darker gray for better contrast
val TextSecondary = Color(0xFF6B7280)
val DividerColor = Color(0xFFF3F4F6)
val CardBackground = Color(0xFFFFFFFF) // Using white for cards on light background

// Feature Colors
val SuccessGreen = Color(0xFF10B981)
val ErrorRed = Color(0xFFEF4444)
val WarningAmber = Color(0xFFF59E0B)

private val FriendLensColors = lightColors(
    primary = BrandBlue,
    primaryVariant = BrandPurple,
    secondary = BrandCoral,
    secondaryVariant = BrandOrange,
    onPrimary = Color.White,
    background = BackgroundLight,
    surface = SurfaceWhite,
    onBackground = TextDark,
    onSurface = TextDark,
    error = ErrorRed
)

val FriendLensShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(32.dp)
)

val FriendLensTypography = Typography(
    h1 = TextStyle(fontWeight = FontWeight.Bold, fontSize = 32.sp, color = TextDark),
    h2 = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp, color = TextDark),
    h3 = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextDark),
    body1 = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, color = TextDark),
    body2 = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, color = TextSecondary),
    button = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White),
    caption = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp, color = TextSecondary)
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