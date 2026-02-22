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

        Column(modifier = Modifier.fillMaxSize().background(BackgroundLight)) {
            // Header Section with Gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(bottomStart = 48.dp, bottomEnd = 48.dp))
                    .background(brush = Brush.linearGradient(BrandGradient)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        modifier = Modifier.size(90.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White.copy(alpha = 0.2f),
                        elevation = 0.dp
                    ) {
                        Image(
                            painter = painterResource("drawable/app_icon.png"),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().padding(12.dp).clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("FriendLens", style = MaterialTheme.typography.h2.copy(color = Color.White))
                    Text("Capture your moments together", style = MaterialTheme.typography.caption.copy(color = Color.White.copy(alpha = 0.8f)))
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                Text("Welcome Back", style = MaterialTheme.typography.h2)
                Text("Sign in to your account", style = MaterialTheme.typography.body2)
                
                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = email, onValueChange = { email = it },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = BrandBlue,
                        unfocusedBorderColor = DividerColor,
                        backgroundColor = Color.White
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
                        unfocusedBorderColor = DividerColor,
                        backgroundColor = Color.White
                    )
                )

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(errorMsg!!, color = ErrorRed, style = MaterialTheme.typography.caption)
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
                    modifier = Modifier.fillMaxWidth().height(56.dp).shadow(8.dp, MaterialTheme.shapes.large),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(backgroundColor = TextDark),
                    enabled = !isLoading
                ) {
                    if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    else Text("Sign In", style = MaterialTheme.typography.button)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row {
                    Text("Don't have an account? ", style = MaterialTheme.typography.body2)
                    Text(
                        "Sign Up",
                        modifier = Modifier.clickable { navigator.push(SignupScreen()) },
                        style = MaterialTheme.typography.body2.copy(color = BrandBlue, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

class SignupScreen : Screen {
    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun Content() {
        var username by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }
        var errorMsg by remember { mutableStateOf<String?>(null) }
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()

        Column(modifier = Modifier.fillMaxSize().background(BackgroundLight)) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 48.dp, start = 24.dp)
            ) {
                IconButton(onClick = { navigator.pop() }) { IconBack(TextDark, 24f) }
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
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
                    modifier = Modifier.fillMaxWidth().height(56.dp).shadow(8.dp, MaterialTheme.shapes.large),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(backgroundColor = BrandPurple),
                    enabled = !isLoading
                ) {
                    if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    else Text("Get Started", style = MaterialTheme.typography.button)
                }
            }
        }
    }
}
