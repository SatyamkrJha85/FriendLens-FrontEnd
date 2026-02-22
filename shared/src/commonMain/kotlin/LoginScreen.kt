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
            // Hero section
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

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(errorMsg!!, color = Color.Red, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            errorMsg = "Please fill all fields"
                            return@Button
                        }
                        isLoading = true
                        errorMsg = null
                        scope.launch {
                            try {
                                val authResp = FriendLensApi.login(LoginRequest(email, password))
                                if (authResp.access_token != null) {
                                    FriendLensApi.authToken = authResp.access_token
                                    
                                    // Sync with Ktor Backend to get/create profile
                                    val userResp = FriendLensApi.getCurrentUser()
                                    if (userResp.status == "success") {
                                        SessionManager.login(
                                            token = authResp.access_token,
                                            userId = userResp.userId ?: authResp.user?.id ?: "",
                                            email = userResp.email ?: authResp.user?.email ?: email,
                                            username = userResp.username,
                                            avatarUrl = userResp.avatarUrl
                                        )
                                        sendLocalNotification("Welcome back!", "Let's capture some moments today.")
                                        navigator.replaceAll(MainDashboardScreen())
                                    } else {
                                        errorMsg = userResp.message ?: "Failed to sync profile"
                                    }
                                } else {
                                    errorMsg = authResp.error_description ?: authResp.error ?: "Invalid credentials"
                                }
                            } catch (e: Exception) {
                                errorMsg = "Login failed: check network"
                            } finally {
                                isLoading = false
                            }
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

                Spacer(modifier = Modifier.height(32.dp))

                Row(modifier = Modifier.padding(bottom = 32.dp), horizontalArrangement = Arrangement.Center) {
                    Text("Don't have an account? ", color = TextSecondary)
                    Text("Sign Up", color = BrandBlue, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { 
                        navigator.push(SignupScreen())
                    })
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

        Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
            // Header
            Row(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalArrangement = Arrangement.Start) {
                IconButton(onClick = { navigator.pop() }) { IconBack(TextDark, 24f) }
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Create Account", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Join FriendLens and share vibes", fontSize = 15.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(48.dp))

                OutlinedTextField(
                    value = username, onValueChange = { username = it },
                    label = { Text("Display Name") },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = BrandPurple, cursorColor = BrandPurple, focusedLabelColor = BrandPurple)
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email, onValueChange = { email = it },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = BrandPurple, cursorColor = BrandPurple, focusedLabelColor = BrandPurple)
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password, onValueChange = { password = it },
                    label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = BrandPurple, cursorColor = BrandPurple, focusedLabelColor = BrandPurple)
                )

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(errorMsg!!, color = Color.Red, fontSize = 13.sp)
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
                                    // Log in immediately after signup
                                    val authResp = FriendLensApi.login(LoginRequest(email, password))
                                    if (authResp.access_token != null) {
                                        FriendLensApi.authToken = authResp.access_token
                                        
                                        // Update profile with the display name in the Ktor Backend
                                        FriendLensApi.updateProfile(UpdateProfileRequest(username = username))
                                        
                                        val userResp = FriendLensApi.getCurrentUser()
                                        SessionManager.login(
                                            token = authResp.access_token,
                                            userId = userResp.userId ?: authResp.user?.id ?: "",
                                            email = userResp.email ?: email,
                                            username = username,
                                            avatarUrl = userResp.avatarUrl
                                        )
                                        sendLocalNotification("Welcome to FriendLens!", "Start creating your first album.")
                                        navigator.replaceAll(MainDashboardScreen())
                                    } else {
                                        errorMsg = "Signup successful, but login failed"
                                    }
                                } else {
                                    errorMsg = signupResp.error_description ?: signupResp.error ?: "Signup failed"
                                }
                            } catch (e: Exception) {
                                errorMsg = "Signup failed: network error"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = BrandPurple),
                    elevation = ButtonDefaults.elevation(defaultElevation = 6.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    else Text("Create Account", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
