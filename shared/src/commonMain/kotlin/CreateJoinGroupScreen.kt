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
                Text("Group Space", style = MaterialTheme.typography.h1.copy(fontSize = 32.sp))
                Text("Join friends or start a new collection.", style = MaterialTheme.typography.body2)
            }

            // Hero Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(32.dp))
            ) {
                Image(
                    painter = painterResource("drawable/onboarding_adventure.png"),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.2f)))
            }

            Spacer(Modifier.height(32.dp))

            // Join Section
            Card(
                modifier = Modifier.padding(horizontal = 24.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = 0.dp,
                border = BorderStroke(1.dp, DividerColor)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = Color(0xFFEFF6FF), modifier = Modifier.size(32.dp)) {
                            Box(contentAlignment = Alignment.Center) { IconGroup(Color(0xFF3B82F6), 18f) }
                        }
                        Text("Join via Code", style = MaterialTheme.typography.h3, modifier = Modifier.padding(start = 12.dp))
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = joinCode,
                        onValueChange = { if (it.length <= 6) joinCode = it.uppercase() },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("6-digit code", style = MaterialTheme.typography.body1.copy(color = TextSecondary.copy(alpha = 0.5f))) },
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            unfocusedBorderColor = InputBorder,
                            focusedBorderColor = BrandPrimary
                        ),
                        singleLine = true
                    )
                    
                    Spacer(Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            if (joinCode.length < 6) return@Button
                            isLoading = true
                            error = null
                            scope.launch {
                                try {
                                    val resp = FriendLensApi.joinGroup(JoinGroupRequest(joinCode))
                                    if (resp.status == "success") {
                                        navigator.replaceAll(MainDashboardScreen()) 
                                    } else {
                                        error = resp.message
                                    }
                                } catch (e: Exception) { error = "Unable to join group" }
                                finally { isLoading = false }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading && joinCode.length == 6
                    ) {
                        if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        else Text("Enter Group")
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Create Section
            Card(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = 0.dp,
                border = BorderStroke(1.dp, DividerColor)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = Color(0xFFF0FDF4), modifier = Modifier.size(32.dp)) {
                            Box(contentAlignment = Alignment.Center) { IconPlus(Color(0xFF22C55E), 18f) }
                        }
                        Text("New Collection", style = MaterialTheme.typography.h3, modifier = Modifier.padding(start = 12.dp))
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = groupName,
                        onValueChange = { groupName = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Group Name", style = MaterialTheme.typography.body1.copy(color = TextSecondary.copy(alpha = 0.5f))) },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )
                    
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = groupDesc,
                        onValueChange = { groupDesc = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("What's this album about?", style = MaterialTheme.typography.body1.copy(color = TextSecondary.copy(alpha = 0.5f))) },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )
                    
                    Spacer(Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            if (groupName.isBlank()) return@Button
                            isLoading = true
                            error = null
                            scope.launch {
                                try {
                                    val resp = FriendLensApi.createGroup(CreateGroupRequest(name = groupName, description = groupDesc))
                                    if (resp.status == "success") {
                                        navigator.replaceAll(MainDashboardScreen())
                                    } else {
                                        error = resp.message
                                    }
                                } catch (e: Exception) { error = "Creation failed" }
                                finally { isLoading = false }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
                        enabled = !isLoading && groupName.isNotBlank()
                    ) {
                        if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        else Text("Create Album", color = Color.White)
                    }
                }
            }

            if (error != null) {
                Text(error!!, color = ErrorRed, modifier = Modifier.fillMaxWidth().padding(24.dp), textAlign = TextAlign.Center)
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}
