import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
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

        val backgroundBrush = if (MaterialTheme.colors.isLight) Color.White else Color(0xFF121212)

        Box(modifier = Modifier.fillMaxSize().background(backgroundBrush)) {
            // Background blur effect from Stitch Design
            Box(
                modifier = Modifier
                    .offset(x = (-40).dp, y = (-40).dp)
                    .size(256.dp)
                    .background(BrandPrimary.copy(0.1f), CircleShape)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))

                // Stitch Hero Image (4:3 Aspect Ratio)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f/3f)
                        .shadow(4.dp, MaterialTheme.shapes.medium),
                    shape = MaterialTheme.shapes.medium,
                    color = if (MaterialTheme.colors.isLight) Color(0xFFF8FAFC) else Color(0xFF1E1E1E)
                ) {
                    Image(
                        painter = painterResource("drawable/onboarding_celebration.png"),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "FriendLens",
                    style = MaterialTheme.typography.h1.copy(fontSize = 30.sp),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "All moments. One place.",
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Input Fields matching Stitch py-3.5 and xl-radius
                StitchTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    placeholder = "you@example.com",
                    icon = { IconProfile(TextSecondary, 18f) }
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                StitchTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    placeholder = "••••••••",
                    isPassword = true,
                    icon = { IconSettings(TextSecondary, 18f) }
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        "Forgot Password?",
                        style = MaterialTheme.typography.caption.copy(color = BrandPrimary),
                        modifier = Modifier.clickable { }
                    )
                }

                if (errorMsg != null) {
                    Text(errorMsg!!, color = ErrorRed, style = MaterialTheme.typography.caption, modifier = Modifier.padding(top = 12.dp))
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Primary Action Button - Solid Stitch Primary
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
                                    SessionManager.login(
                                        token = authResp.access_token,
                                        userId = userResp.userId ?: authResp.user?.id ?: "",
                                        email = userResp.email ?: email,
                                        username = userResp.username,
                                        avatarUrl = userResp.avatarUrl
                                    )
                                    navigator.replaceAll(MainDashboardScreen())
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
                    modifier = Modifier.fillMaxWidth().height(56.dp).shadow(8.dp, MaterialTheme.shapes.medium),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(backgroundColor = BrandPrimary),
                    enabled = !isLoading
                ) {
                    if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    else Text("Sign In", style = MaterialTheme.typography.button)
                }

                Spacer(modifier = Modifier.height(40.dp))
                
                Row(modifier = Modifier.padding(bottom = 40.dp)) {
                    Text("Don't have an account? ", style = MaterialTheme.typography.body2)
                    Text(
                        "Sign Up",
                        modifier = Modifier.clickable { navigator.push(SignupScreen()) },
                        style = MaterialTheme.typography.body2.copy(color = BrandPrimary, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }

    @Composable
    fun StitchTextField(
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
        placeholder: String,
        icon: @Composable () -> Unit,
        isPassword: Boolean = false
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(label, style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Bold, color = TextPrimary), modifier = Modifier.padding(start = 4.dp, bottom = 6.dp))
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(placeholder, style = MaterialTheme.typography.body1.copy(color = TextSecondary.copy(0.5f))) },
                leadingIcon = { Box(modifier = Modifier.padding(start = 12.dp)) { icon() } },
                shape = MaterialTheme.shapes.medium,
                singleLine = true,
                visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = if (MaterialTheme.colors.isLight) Color(0xFFF8FAFC) else Color(0xFF1E1E1E),
                    unfocusedBorderColor = InputBorder,
                    focusedBorderColor = BrandPrimary,
                    textColor = TextPrimary
                )
            )
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

        Column(modifier = Modifier.fillMaxSize().background(Color.White).verticalScroll(rememberScrollState())) {
            Box(modifier = Modifier.fillMaxWidth().padding(top = 48.dp, start = 24.dp)) {
                IconButton(onClick = { navigator.pop() }) { IconBack(TextPrimary, 24f) }
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Create Account", style = MaterialTheme.typography.h1)
                Text("Start your journey with friends", style = MaterialTheme.typography.body2)
                
                Spacer(modifier = Modifier.height(48.dp))

                StitchTextField(username, { username = it }, "Display Name", "Alex Morgan", { IconProfile(TextSecondary, 18f) })
                Spacer(modifier = Modifier.height(16.dp))
                StitchTextField(email, { email = it }, "Email Address", "you@example.com", { IconGroup(TextSecondary, 18f) })
                Spacer(modifier = Modifier.height(16.dp))
                StitchTextField(password, { password = it }, "Password", "••••••••", { IconSettings(TextSecondary, 18f) }, true)

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
                                    } else {
                                        errorMsg = authResp.error_description ?: "Signup successful. Please check your email to confirm."
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
                    modifier = Modifier.fillMaxWidth().height(56.dp).shadow(8.dp, MaterialTheme.shapes.medium),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(backgroundColor = BrandPrimary),
                    enabled = !isLoading
                ) {
                    if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    else Text("Get Started", style = MaterialTheme.typography.button)
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    @Composable
    private fun StitchTextField(value: String, onValueChange: (String) -> Unit, label: String, placeholder: String, icon: @Composable () -> Unit, isPassword: Boolean = false) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(label, style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Bold, color = TextPrimary), modifier = Modifier.padding(start = 4.dp, bottom = 6.dp))
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(placeholder, style = MaterialTheme.typography.body1.copy(color = TextSecondary.copy(0.5f))) },
                leadingIcon = { Box(modifier = Modifier.padding(start = 12.dp)) { icon() } },
                shape = MaterialTheme.shapes.medium,
                singleLine = true,
                visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = Color(0xFFF8FAFC),
                    unfocusedBorderColor = InputBorder,
                    focusedBorderColor = BrandPrimary,
                    textColor = TextPrimary
                )
            )
        }
    }
}
