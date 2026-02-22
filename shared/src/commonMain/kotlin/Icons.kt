import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

// ─── Icon Drawing Composables ───
// Using Canvas-drawn vector icons for reliable cross-platform rendering

@Composable
fun IconHome(color: Color, size: Float = 24f) {
    Canvas(modifier = Modifier.size(size.dp)) {
        val s = this.size
        val stroke = Stroke(width = s.width * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val path = Path().apply {
            moveTo(s.width * 0.1f, s.height * 0.45f)
            lineTo(s.width * 0.5f, s.height * 0.1f)
            lineTo(s.width * 0.9f, s.height * 0.45f)
            lineTo(s.width * 0.9f, s.height * 0.9f)
            lineTo(s.width * 0.6f, s.height * 0.9f)
            lineTo(s.width * 0.6f, s.height * 0.65f)
            lineTo(s.width * 0.4f, s.height * 0.65f)
            lineTo(s.width * 0.4f, s.height * 0.9f)
            lineTo(s.width * 0.1f, s.height * 0.9f)
            close()
        }
        drawPath(path, color, style = stroke)
    }
}

@Composable
fun IconAlbum(color: Color, size: Float = 24f) {
    Canvas(modifier = Modifier.size(size.dp)) {
        val s = this.size
        val stroke = Stroke(width = s.width * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        // Back card
        drawRoundRect(color, topLeft = Offset(s.width * 0.15f, s.height * 0.05f), size = Size(s.width * 0.7f, s.height * 0.7f), cornerRadius = CornerRadius(s.width * 0.08f), style = stroke)
        // Front card
        drawRoundRect(color, topLeft = Offset(s.width * 0.2f, s.height * 0.2f), size = Size(s.width * 0.7f, s.height * 0.7f), cornerRadius = CornerRadius(s.width * 0.08f), style = stroke)
        // Mountain
        val mountain = Path().apply {
            moveTo(s.width * 0.25f, s.height * 0.75f)
            lineTo(s.width * 0.45f, s.height * 0.45f)
            lineTo(s.width * 0.55f, s.height * 0.55f)
            lineTo(s.width * 0.75f, s.height * 0.35f)
            lineTo(s.width * 0.85f, s.height * 0.75f)
        }
        drawPath(mountain, color, style = stroke)
    }
}

@Composable
fun IconCamera(color: Color, size: Float = 24f) {
    Canvas(modifier = Modifier.size(size.dp)) {
        val s = this.size
        val stroke = Stroke(width = s.width * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        // Body
        drawRoundRect(color, topLeft = Offset(s.width * 0.08f, s.height * 0.28f), size = Size(s.width * 0.84f, s.height * 0.58f), cornerRadius = CornerRadius(s.width * 0.1f), style = stroke)
        // Viewfinder bump
        val bump = Path().apply {
            moveTo(s.width * 0.32f, s.height * 0.28f)
            lineTo(s.width * 0.38f, s.height * 0.14f)
            lineTo(s.width * 0.62f, s.height * 0.14f)
            lineTo(s.width * 0.68f, s.height * 0.28f)
        }
        drawPath(bump, color, style = stroke)
        // Lens circle
        drawCircle(color, radius = s.width * 0.15f, center = Offset(s.width * 0.5f, s.height * 0.57f), style = stroke)
    }
}

@Composable
fun IconGroup(color: Color, size: Float = 24f) {
    Canvas(modifier = Modifier.size(size.dp)) {
        val s = this.size
        val stroke = Stroke(width = s.width * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        // Center person head
        drawCircle(color, radius = s.width * 0.1f, center = Offset(s.width * 0.5f, s.height * 0.25f), style = stroke)
        // Center person body
        drawArc(color, startAngle = 180f, sweepAngle = 180f, useCenter = false, topLeft = Offset(s.width * 0.3f, s.height * 0.4f), size = Size(s.width * 0.4f, s.height * 0.35f), style = stroke)
        // Left person head
        drawCircle(color, radius = s.width * 0.08f, center = Offset(s.width * 0.2f, s.height * 0.3f), style = stroke)
        // Left person body
        drawArc(color, startAngle = 180f, sweepAngle = 180f, useCenter = false, topLeft = Offset(s.width * 0.05f, s.height * 0.42f), size = Size(s.width * 0.3f, s.height * 0.3f), style = stroke)
        // Right person head
        drawCircle(color, radius = s.width * 0.08f, center = Offset(s.width * 0.8f, s.height * 0.3f), style = stroke)
        // Right person body
        drawArc(color, startAngle = 180f, sweepAngle = 180f, useCenter = false, topLeft = Offset(s.width * 0.65f, s.height * 0.42f), size = Size(s.width * 0.3f, s.height * 0.3f), style = stroke)
    }
}

@Composable
fun IconProfile(color: Color, size: Float = 24f) {
    Canvas(modifier = Modifier.size(size.dp)) {
        val s = this.size
        val stroke = Stroke(width = s.width * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        // Head
        drawCircle(color, radius = s.width * 0.15f, center = Offset(s.width * 0.5f, s.height * 0.3f), style = stroke)
        // Body
        drawArc(color, startAngle = 180f, sweepAngle = 180f, useCenter = false, topLeft = Offset(s.width * 0.2f, s.height * 0.5f), size = Size(s.width * 0.6f, s.height * 0.45f), style = stroke)
    }
}

@Composable
fun IconHeart(color: Color, filled: Boolean = false, size: Float = 24f) {
    Canvas(modifier = Modifier.size(size.dp)) {
        val s = this.size
        val heart = Path().apply {
            moveTo(s.width * 0.5f, s.height * 0.85f)
            cubicTo(s.width * 0.15f, s.height * 0.6f, s.width * 0.05f, s.height * 0.3f, s.width * 0.25f, s.height * 0.2f)
            cubicTo(s.width * 0.38f, s.height * 0.13f, s.width * 0.48f, s.height * 0.2f, s.width * 0.5f, s.height * 0.3f)
            cubicTo(s.width * 0.52f, s.height * 0.2f, s.width * 0.62f, s.height * 0.13f, s.width * 0.75f, s.height * 0.2f)
            cubicTo(s.width * 0.95f, s.height * 0.3f, s.width * 0.85f, s.height * 0.6f, s.width * 0.5f, s.height * 0.85f)
            close()
        }
        if (filled) {
            drawPath(heart, color)
        } else {
            drawPath(heart, color, style = Stroke(width = s.width * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }
}

@Composable
fun IconBell(color: Color, size: Float = 24f) {
    Canvas(modifier = Modifier.size(size.dp)) {
        val s = this.size
        val stroke = Stroke(width = s.width * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val bell = Path().apply {
            moveTo(s.width * 0.5f, s.height * 0.08f)
            lineTo(s.width * 0.5f, s.height * 0.15f)
            moveTo(s.width * 0.22f, s.height * 0.55f)
            cubicTo(s.width * 0.22f, s.height * 0.25f, s.width * 0.35f, s.height * 0.15f, s.width * 0.5f, s.height * 0.15f)
            cubicTo(s.width * 0.65f, s.height * 0.15f, s.width * 0.78f, s.height * 0.25f, s.width * 0.78f, s.height * 0.55f)
            lineTo(s.width * 0.85f, s.height * 0.72f)
            lineTo(s.width * 0.15f, s.height * 0.72f)
            lineTo(s.width * 0.22f, s.height * 0.55f)
        }
        drawPath(bell, color, style = stroke)
        // Clapper
        drawLine(color, Offset(s.width * 0.38f, s.height * 0.82f), Offset(s.width * 0.62f, s.height * 0.82f), strokeWidth = s.width * 0.08f, cap = StrokeCap.Round)
        // Dot notification
        drawCircle(BrandCoral, radius = s.width * 0.07f, center = Offset(s.width * 0.72f, s.height * 0.22f))
    }
}

@Composable
fun IconShare(color: Color, size: Float = 24f) {
    Canvas(modifier = Modifier.size(size.dp)) {
        val s = this.size
        val stroke = Stroke(width = s.width * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val path = Path().apply {
            moveTo(s.width * 0.3f, s.height * 0.45f)
            lineTo(s.width * 0.7f, s.height * 0.2f)
            lineTo(s.width * 0.7f, s.height * 0.4f)
            cubicTo(s.width * 0.9f, s.height * 0.4f, s.width * 0.9f, s.height * 0.7f, s.width * 0.7f, s.height * 0.8f)
            cubicTo(s.width * 0.8f, s.height * 0.6f, s.width * 0.75f, s.height * 0.5f, s.width * 0.7f, s.height * 0.5f)
            lineTo(s.width * 0.7f, s.height * 0.7f)
        }
        drawPath(path, color, style = stroke)
    }
}

@Composable
fun IconPlus(color: Color, size: Float = 24f) {
    Canvas(modifier = Modifier.size(size.dp)) {
        val s = this.size
        drawLine(color, Offset(s.width * 0.5f, s.height * 0.2f), Offset(s.width * 0.5f, s.height * 0.8f), strokeWidth = s.width * 0.1f, cap = StrokeCap.Round)
        drawLine(color, Offset(s.width * 0.2f, s.height * 0.5f), Offset(s.width * 0.8f, s.height * 0.5f), strokeWidth = s.width * 0.1f, cap = StrokeCap.Round)
    }
}

@Composable
fun IconBack(color: Color, size: Float = 24f) {
    Canvas(modifier = Modifier.size(size.dp)) {
        val s = this.size
        val stroke = Stroke(width = s.width * 0.1f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val path = Path().apply {
            moveTo(s.width * 0.65f, s.height * 0.15f)
            lineTo(s.width * 0.3f, s.height * 0.5f)
            lineTo(s.width * 0.65f, s.height * 0.85f)
        }
        drawPath(path, color, style = stroke)
    }
}

@Composable
fun IconSettings(color: Color, size: Float = 24f) {
    Canvas(modifier = Modifier.size(size.dp)) {
        val s = this.size
        val stroke = Stroke(width = s.width * 0.07f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        // Gear outer
        drawCircle(color, radius = s.width * 0.3f, center = Offset(s.width * 0.5f, s.height * 0.5f), style = stroke)
        // Gear inner
        drawCircle(color, radius = s.width * 0.12f, center = Offset(s.width * 0.5f, s.height * 0.5f), style = stroke)
        // Gear teeth (4 lines)
        drawLine(color, Offset(s.width * 0.5f, s.height * 0.08f), Offset(s.width * 0.5f, s.height * 0.2f), strokeWidth = s.width * 0.09f, cap = StrokeCap.Round)
        drawLine(color, Offset(s.width * 0.5f, s.height * 0.8f), Offset(s.width * 0.5f, s.height * 0.92f), strokeWidth = s.width * 0.09f, cap = StrokeCap.Round)
        drawLine(color, Offset(s.width * 0.08f, s.height * 0.5f), Offset(s.width * 0.2f, s.height * 0.5f), strokeWidth = s.width * 0.09f, cap = StrokeCap.Round)
        drawLine(color, Offset(s.width * 0.8f, s.height * 0.5f), Offset(s.width * 0.92f, s.height * 0.5f), strokeWidth = s.width * 0.09f, cap = StrokeCap.Round)
    }
}

@Composable
fun IconClose(color: Color, size: Float = 24f) {
    Canvas(modifier = Modifier.size(size.dp)) {
        val s = this.size
        drawLine(color, Offset(s.width * 0.25f, s.height * 0.25f), Offset(s.width * 0.75f, s.height * 0.75f), strokeWidth = s.width * 0.1f, cap = StrokeCap.Round)
        drawLine(color, Offset(s.width * 0.75f, s.height * 0.25f), Offset(s.width * 0.25f, s.height * 0.75f), strokeWidth = s.width * 0.1f, cap = StrokeCap.Round)
    }
}

@Composable
fun IconFlash(color: Color, size: Float = 24f) {
    Canvas(modifier = Modifier.size(size.dp)) {
        val s = this.size
        val path = Path().apply {
            moveTo(s.width * 0.6f, s.height * 0.05f)
            lineTo(s.width * 0.25f, s.height * 0.5f)
            lineTo(s.width * 0.5f, s.height * 0.5f)
            lineTo(s.width * 0.4f, s.height * 0.95f)
            lineTo(s.width * 0.75f, s.height * 0.45f)
            lineTo(s.width * 0.5f, s.height * 0.45f)
            close()
        }
        drawPath(path, color)
    }
}

@Composable
fun IconFlip(color: Color, size: Float = 24f) {
    Canvas(modifier = Modifier.size(size.dp)) {
        val s = this.size
        val stroke = Stroke(width = s.width * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        // Top arrow
        val top = Path().apply {
            moveTo(s.width * 0.3f, s.height * 0.3f)
            lineTo(s.width * 0.7f, s.height * 0.3f)
            lineTo(s.width * 0.6f, s.height * 0.15f)
        }
        drawPath(top, color, style = stroke)
        // Bottom arrow  
        val bot = Path().apply {
            moveTo(s.width * 0.7f, s.height * 0.7f)
            lineTo(s.width * 0.3f, s.height * 0.7f)
            lineTo(s.width * 0.4f, s.height * 0.85f)
        }
        drawPath(bot, color, style = stroke)
        // Connecting lines
        drawLine(color, Offset(s.width * 0.7f, s.height * 0.3f), Offset(s.width * 0.7f, s.height * 0.55f), strokeWidth = s.width * 0.08f, cap = StrokeCap.Round)
        drawLine(color, Offset(s.width * 0.3f, s.height * 0.45f), Offset(s.width * 0.3f, s.height * 0.7f), strokeWidth = s.width * 0.08f, cap = StrokeCap.Round)
    }
}

@Composable
fun IconComment(color: Color, size: Float = 24f) {
    Canvas(modifier = Modifier.size(size.dp)) {
        val s = this.size
        val stroke = Stroke(width = s.width * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val path = Path().apply {
            moveTo(s.width * 0.5f, s.height * 0.85f)
            lineTo(s.width * 0.25f, s.height * 0.7f)
            lineTo(s.width * 0.12f, s.height * 0.7f)
            cubicTo(s.width * 0.08f, s.height * 0.7f, s.width * 0.08f, s.height * 0.15f, s.width * 0.12f, s.height * 0.15f)
            lineTo(s.width * 0.88f, s.height * 0.15f)
            cubicTo(s.width * 0.92f, s.height * 0.15f, s.width * 0.92f, s.height * 0.7f, s.width * 0.88f, s.height * 0.7f)
            lineTo(s.width * 0.5f, s.height * 0.7f)
            close()
        }
        drawPath(path, color, style = stroke)
    }
}

@Composable
fun IconMore(color: Color, size: Float = 24f) {
    Canvas(modifier = Modifier.size(size.dp)) {
        val s = this.size
        val r = s.width * 0.06f
        drawCircle(color, radius = r, center = Offset(s.width * 0.5f, s.height * 0.25f))
        drawCircle(color, radius = r, center = Offset(s.width * 0.5f, s.height * 0.5f))
        drawCircle(color, radius = r, center = Offset(s.width * 0.5f, s.height * 0.75f))
    }
}
