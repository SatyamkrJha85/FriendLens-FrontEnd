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
import androidx.compose.ui.draw.shadow
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
            // Header Image (matching mockup)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                Image(
                    painter = painterResource("drawable/onboarding_celebration.png"), // Using an existing relevant image
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.White),
                                startY = 100f
                            )
                        )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "FriendLens",
                    style = MaterialTheme.typography.h1.copy(fontSize = 32.sp),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "All moments. One place.",
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                OutlinedTextField(
                    value = email, onValueChange = { email = it },
                    label = { Text("Email") },
                    placeholder = { Text("you@example.com") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = BrandBlue,
                        unfocusedBorderColor = DividerColor
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password, onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = BrandBlue,
                        unfocusedBorderColor = DividerColor
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text("Forgot Password?", style = MaterialTheme.typography.caption.copy(color = BrandCoral))
                }

                if (errorMsg != null) {
                    Text(errorMsg!!, color = ErrorRed, style = MaterialTheme.typography.caption, modifier = Modifier.padding(top = 12.dp))
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            errorMsg = "Fields cannot be empty"
                            return@Button
                        }
                        isLoading = true
                        errorMsg = null
                        scope.launch {
                            try {
                                val authResp = FriendLensApi.login(LoginRequest(email, password))
                                if (authResp.access_token != null) {
                                    FriendLensApi.authToken = authResp.access_token
                                    val userResp = FriendLensApi.getCurrentUser()
                                    if (userResp.status == "success") {
                                        SessionManager.login(
                                            token = authResp.access_token,
                                            userId = userResp.userId ?: authResp.user?.id ?: "",
                                            email = userResp.email ?: email,
                                            username = userResp.username,
                                            avatarUrl = userResp.avatarUrl
                                        )
                                        navigator.replaceAll(MainDashboardScreen())
                                    } else {
                                        errorMsg = "Sync failed. Try again."
                                    }
                                } else {
                                    errorMsg = authResp.error_description ?: "Invalid login"
                                }
                            } catch (e: Exception) {
                                errorMsg = "Network error occurred"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp).shadow(8.dp, CircleShape),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    enabled = !isLoading
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(brush = Brush.linearGradient(listOf(BrandCoral, BrandPink))),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        else Text("Sign In", style = MaterialTheme.typography.button)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("OR CONTINUE WITH", style = MaterialTheme.typography.caption.copy(letterSpacing = 1.sp))
                Spacer(modifier = Modifier.height(16.dp))

                // Social Icons matching mockup
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    SocialButton("drawable/onboarding_celebration.png") // Placeholder for Google
                    SocialButton("drawable/onboarding_capture.png") // Placeholder for Apple
                }

                Spacer(modifier = Modifier.height(32.dp))
                Row {
                    Text("Don't have an account? ", style = MaterialTheme.typography.body2)
                    Text(
                        "Sign Up",
                        modifier = Modifier.clickable { navigator.push(SignupScreen()) },
                        style = MaterialTheme.typography.body2.copy(color = BrandCoral, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }

    @Composable
    fun SocialButton(iconRes: String) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = Color.White,
            border = BorderStroke(1.dp, DividerColor),
            elevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                // In a real app we'd use social icons, here placeholders
                Box(Modifier.size(20.dp).background(Color(0xFFEEEEEE), CircleShape))
            }
        }
    }
}

class SignupScreen : Screen {
    @Composable
    override fun Content() {
        var username by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }
        var errorMsg by remember { mutableStateOf<String?>(null) }
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()

        Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 48.dp, start = 24.dp)
            ) {
                IconButton(onClick = { navigator.pop() }) { IconBack(TextDark, 24f) }
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Create Account", style = MaterialTheme.typography.h1)
                Text("Start your journey with friends", style = MaterialTheme.typography.body2)
                
                Spacer(modifier = Modifier.height(48.dp))

                OutlinedTextField(
                    value = username, onValueChange = { username = it },
                    label = { Text("Display Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = BrandPurple, backgroundColor = Color.White)
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email, onValueChange = { email = it },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = BrandPurple, backgroundColor = Color.White)
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password, onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = BrandPurple, backgroundColor = Color.White)
                )

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(errorMsg!!, color = ErrorRed, style = MaterialTheme.typography.caption)
                }

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank() || username.isBlank()) {
                            errorMsg = "Please fill all fields"
                            return@Button
                        }
                        isLoading = true
                        errorMsg = null
                        scope.launch {
                            try {
                                val signupResp = FriendLensApi.signup(SignupRequest(email, password))
                                if (signupResp.msg != null || signupResp.user != null) {
                                    val authResp = FriendLensApi.login(LoginRequest(email, password))
                                    if (authResp.access_token != null) {
                                        FriendLensApi.authToken = authResp.access_token
                                        FriendLensApi.updateProfile(UpdateProfileRequest(username = username))
                                        val userResp = FriendLensApi.getCurrentUser()
                                        SessionManager.login(
                                            token = authResp.access_token,
                                            userId = userResp.userId ?: authResp.user?.id ?: "",
                                            email = userResp.email ?: email,
                                            username = username,
                                            avatarUrl = userResp.avatarUrl
                                        )
                                        navigator.replaceAll(MainDashboardScreen())
                                    }
                                } else {
                                    errorMsg = signupResp.error_description ?: "Signup failed"
                                }
                            } catch (e: Exception) {
                                errorMsg = "Check your connection"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(60.dp).shadow(12.dp, CircleShape),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    enabled = !isLoading
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(brush = Brush.linearGradient(listOf(BrandBlue, BrandPurple))),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        else Text("Get Started", style = MaterialTheme.typography.button)
                    }
                }
            }
        }
    }
}
