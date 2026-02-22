import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition

// ─── Design Tokens (matching app icon gradient) ───
// Icon gradient: Blue → Purple → Pink → Coral → Orange
val BrandBlue = Color(0xFF4A90FF)
val BrandPurple = Color(0xFF8B5CF6)
val BrandPink = Color(0xFFEC4899)
val BrandCoral = Color(0xFFF97066)
val BrandOrange = Color(0xFFF97316)

// Primary / accent (coral from icon — vibrant warm tone)
val FriendLensRed = Color(0xFFF97066)
val FriendLensRedDark = Color(0xFFE05560)
val FriendLensRedLight = Color(0xFFFFAFA5)

// Gradient stops matching icon ring
val GradientStart = BrandBlue
val GradientMid = BrandPurple
val GradientEnd = BrandOrange

// Neutrals
val BackgroundLight = Color(0xFFFAFAFA)
val SurfaceWhite = Color.White
val TextDark = Color(0xFF1A1A2E)
val TextSecondary = Color(0xFF6B7280)
val DividerColor = Color(0xFFE5E7EB)
val CardBackground = Color(0xFFF3F4F6)
val SuccessGreen = Color(0xFF10B981)
val WarningAmber = Color(0xFFF59E0B)

// Dot colors from icon
val DotBlue = Color(0xFF3B82F6)
val DotPink = Color(0xFFEC4899)
val DotOrange = Color(0xFFF97316)

private val FriendLensColors = lightColors(
    primary = BrandBlue,
    primaryVariant = BrandPurple,
    secondary = BrandCoral,
    secondaryVariant = BrandOrange,
    onPrimary = Color.White,
    background = BackgroundLight,
    surface = SurfaceWhite,
    onBackground = TextDark,
    onSurface = TextDark
)

@Composable
fun App() {
    MaterialTheme(colors = FriendLensColors) {
        Navigator(SplashScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}