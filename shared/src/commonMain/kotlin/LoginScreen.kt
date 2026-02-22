import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

class LoginScreen : Screen {
    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun Content() {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }
        var errorMsg by remember { mutableStateOf<String?>(null) }
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()

        Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
            // Hero section with brand gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                    .background(brush = Brush.horizontalGradient(listOf(BrandBlue, BrandPurple, BrandCoral))),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource("drawable/app_icon.png"),
                        contentDescription = "FriendLens",
                        modifier = Modifier.size(96.dp).clip(RoundedCornerShape(24.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("FriendLens", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text("All moments. One place.", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Welcome Back", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Sign in to continue", fontSize = 14.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = email, onValueChange = { email = it },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = BrandBlue, unfocusedBorderColor = DividerColor, cursorColor = BrandBlue, focusedLabelColor = BrandBlue)
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password, onValueChange = { password = it },
                    label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = BrandBlue, unfocusedBorderColor = DividerColor, cursorColor = BrandBlue, focusedLabelColor = BrandBlue)
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text("Forgot Password?", color = BrandCoral, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, modifier = Modifier.clickable { })
                }

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(errorMsg!!, color = Color.Red, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        isLoading = true
                        scope.launch {
                            try {
                                SessionManager.login("demo_token", "demo_user_id", email.ifEmpty { "user@friendlens.app" }, "FriendLens User", null)
                                sendLocalNotification("Welcome back!", "Let's capture some moments today.")
                                navigator.replaceAll(MainDashboardScreen())
                            } catch (e: Exception) { errorMsg = e.message ?: "Login failed" } finally { isLoading = false }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = BrandBlue),
                    elevation = ButtonDefaults.elevation(defaultElevation = 6.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    else Text("Sign In", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(28.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Divider(modifier = Modifier.weight(1f), color = DividerColor)
                    Text("  OR CONTINUE WITH  ", color = TextSecondary, fontSize = 12.sp)
                    Divider(modifier = Modifier.weight(1f), color = DividerColor)
                }
                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(
                        onClick = {}, modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.outlinedButtonColors(backgroundColor = Color.White)
                    ) { Text("G  Google", color = TextDark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp) }
                    OutlinedButton(
                        onClick = {}, modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.outlinedButtonColors(backgroundColor = Color.White)
                    ) { Text("  Apple", color = TextDark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp) }
                }

                Spacer(modifier = Modifier.weight(1f))
                Row(modifier = Modifier.padding(bottom = 32.dp), horizontalArrangement = Arrangement.Center) {
                    Text("Don't have an account? ", color = TextSecondary)
                    Text("Sign Up", color = BrandBlue, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { })
                }
            }
        }
    }
}
