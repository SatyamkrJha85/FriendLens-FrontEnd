import androidx.compose.animation.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.PasswordVisualTransformation

class CreateJoinGroupScreen : Screen {
    @OptIn(ExperimentalResourceApi::class, ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        var joinCode by remember { mutableStateOf("") }
        var groupName by remember { mutableStateOf("") }
        var groupDesc by remember { mutableStateOf("") }
        var groupImg by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }
        var error by remember { mutableStateOf<String?>(null) }
        val scope = rememberCoroutineScope()
        val navigator = LocalNavigator.currentOrThrow

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Spaces", style = MaterialTheme.typography.h1.copy(fontSize = 36.sp, fontWeight = FontWeight.Black))
                Text("Connect with friends and start a collection.", style = MaterialTheme.typography.body1.copy(color = TextSecondary))
            }

            // High-fidelity Hero with blur effect representation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .padding(horizontal = 20.dp)
                    .shadow(16.dp, RoundedCornerShape(32.dp))
                    .clip(RoundedCornerShape(32.dp))
            ) {
                Image(
                    painter = painterResource("drawable/onboarding_adventure.png"),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.5f))))
                )
                Column(
                    modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)
                ) {
                    Text("Adventure Awaits", color = Color.White, style = MaterialTheme.typography.h2)
                    Text("Share every moment together.", color = Color.White.copy(0.8f), style = MaterialTheme.typography.caption)
                }
            }

            Spacer(Modifier.height(32.dp))

            // Join Section with improved aesthetics
            Card(
                modifier = Modifier.padding(horizontal = 24.dp),
                shape = RoundedCornerShape(28.dp),
                elevation = 0.dp,
                border = BorderStroke(1.dp, DividerColor),
                backgroundColor = Color(0xFFF9FAFB)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFEFF6FF)),
                            contentAlignment = Alignment.Center
                        ) { IconGroup(Color(0xFF3B82F6), 22f) }
                        Spacer(Modifier.width(16.dp))
                        Text("Join with Code", style = MaterialTheme.typography.h3)
                    }
                    
                    Spacer(Modifier.height(20.dp))
                    
                    OutlinedTextField(
                        value = joinCode,
                        onValueChange = { if (it.length <= 6) joinCode = it.uppercase() },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("6-digit access code", style = MaterialTheme.typography.body1.copy(color = TextSecondary.copy(0.4f))) },
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            backgroundColor = Color.White,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = BrandPrimary.copy(0.3f)
                        ),
                        singleLine = true
                    )
                    
                    Spacer(Modifier.height(20.dp))
                    
                    Button(
                        onClick = {
                            if (joinCode.length < 6) return@Button
                            isLoading = true
                            error = null
                            scope.launch {
                                try {
                                    val resp = FriendLensApi.joinGroup(JoinGroupRequest(joinCode))
                                    if (resp.status == "success") {
                                        DataCache.clear() // Invalidate cache to see new group
                                        navigator.replaceAll(MainDashboardScreen()) 
                                    } else {
                                        error = resp.message
                                    }
                                } catch (e: Exception) { error = "Unable to join group" }
                                finally { isLoading = false }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp).shadow(12.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !isLoading && joinCode.length == 6
                    ) {
                        if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        else Text("Enter Shared Space", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Create Section with more fields
            Card(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                shape = RoundedCornerShape(28.dp),
                elevation = 0.dp,
                border = BorderStroke(1.dp, DividerColor),
                backgroundColor = Color.White
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF0FDF4)),
                            contentAlignment = Alignment.Center
                        ) { IconPlus(Color(0xFF22C55E), 22f) }
                        Spacer(Modifier.width(16.dp))
                        Text("Create New Vault", style = MaterialTheme.typography.h3)
                    }
                    
                    Spacer(Modifier.height(24.dp))
                    
                    LabelStyle("ALBUM NAME")
                    OutlinedTextField(
                        value = groupName,
                        onValueChange = { groupName = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("E.g. Summer Trip 2024", style = MaterialTheme.typography.body1.copy(color = TextSecondary.copy(0.4f))) },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )
                    
                    Spacer(Modifier.height(16.dp))

                    LabelStyle("DESCRIPTION (OPTIONAL)")
                    OutlinedTextField(
                        value = groupDesc,
                        onValueChange = { groupDesc = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("What is this for?", style = MaterialTheme.typography.body1.copy(color = TextSecondary.copy(0.4f))) },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    Spacer(Modifier.height(16.dp))

                    LabelStyle("COVER IMAGE URL")
                    OutlinedTextField(
                        value = groupImg,
                        onValueChange = { groupImg = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("https://image.path/to/cover.jpg", style = MaterialTheme.typography.body1.copy(color = TextSecondary.copy(0.4f))) },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )
                    
                    Spacer(Modifier.height(24.dp))
                    
                    Button(
                        onClick = {
                            if (groupName.isBlank()) return@Button
                            isLoading = true
                            error = null
                            scope.launch {
                                try {
                                    // In a real app, we'd need to update the backend to accept groupImg in createGroup
                                    // For now we use the existing API call
                                    val resp = FriendLensApi.createGroup(CreateGroupRequest(name = groupName, description = groupDesc, groupImg = groupImg))
                                    if (resp.status == "success") {
                                        DataCache.clear() // Invalidate cache
                                        navigator.replaceAll(MainDashboardScreen())
                                    } else {
                                        error = resp.message
                                    }
                                } catch (e: Exception) { error = "Creation failed" }
                                finally { isLoading = false }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp).shadow(12.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
                        enabled = !isLoading && groupName.isNotBlank()
                    ) {
                        if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        else Text("Launch Collection", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (error != null) {
                Text(error!!, color = ErrorRed, modifier = Modifier.fillMaxWidth().padding(24.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(60.dp))
        }
    }

    @Composable
    private fun LabelStyle(text: String) {
        Text(
            text = text,
            style = MaterialTheme.typography.caption.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = TextSecondary.copy(0.6f)
            ),
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
    }
}
