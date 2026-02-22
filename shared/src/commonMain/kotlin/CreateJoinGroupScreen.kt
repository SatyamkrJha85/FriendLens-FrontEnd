import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

class CreateJoinGroupScreen : Screen {
    @OptIn(ExperimentalResourceApi::class)
    @Composable
    override fun Content() {
        var isJoinMode by remember { mutableStateOf(true) }
        var joinCode by remember { mutableStateOf("") }
        var groupName by remember { mutableStateOf("") }
        var groupDesc by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }
        var message by remember { mutableStateOf<String?>(null) }
        var isSuccess by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        Column(modifier = Modifier.fillMaxSize().background(BackgroundLight).padding(24.dp)) {
            Text("Collection\nCenter", style = MaterialTheme.typography.h1.copy(lineHeight = 40.sp))
            Text("Create or join shared photo albums.", style = MaterialTheme.typography.body2)
            
            Spacer(Modifier.height(32.dp))

            // Premium Toggle
            Surface(
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = CircleShape,
                color = Color.White,
                elevation = 2.dp
            ) {
                Row(modifier = Modifier.fillMaxSize().padding(4.dp)) {
                    val joinBg by animateColorAsState(if (isJoinMode) BrandBlue else Color.Transparent)
                    val createBg by animateColorAsState(if (!isJoinMode) BrandPurple else Color.Transparent)

                    Box(
                        modifier = Modifier
                            .weight(1f).fillMaxHeight().clip(CircleShape).background(joinBg)
                            .clickable { isJoinMode = true; message = null },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Join Existing",
                            style = MaterialTheme.typography.button.copy(
                                color = if (isJoinMode) Color.White else TextDark,
                                fontSize = 14.sp
                            )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f).fillMaxHeight().clip(CircleShape).background(createBg)
                            .clickable { isJoinMode = false; message = null },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Create New",
                            style = MaterialTheme.typography.button.copy(
                                color = if (!isJoinMode) Color.White else TextDark,
                                fontSize = 14.sp
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Form Area
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                elevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    if (isJoinMode) {
                        Text("Enter Invite Code", style = MaterialTheme.typography.h3)
                        Text("Ask your friend for their 6-digit code.", style = MaterialTheme.typography.caption)
                        Spacer(Modifier.height(24.dp))
                        
                        OutlinedTextField(
                            value = joinCode,
                            onValueChange = { if (it.length <= 6) joinCode = it.uppercase() },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.h2.copy(textAlign = TextAlign.Center, letterSpacing = 8.sp),
                            shape = MaterialTheme.shapes.medium,
                            placeholder = { Text("FRIEND", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = BrandBlue)
                        )
                    } else {
                        Text("New Album Details", style = MaterialTheme.typography.h3)
                        Spacer(Modifier.height(20.dp))
                        OutlinedTextField(
                            value = groupName, onValueChange = { groupName = it },
                            label = { Text("Album Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = BrandPurple)
                        )
                        Spacer(Modifier.height(16.dp))
                        OutlinedTextField(
                            value = groupDesc, onValueChange = { groupDesc = it },
                            label = { Text("Description (Optional)") },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            shape = MaterialTheme.shapes.medium,
                            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = BrandPurple)
                        )
                    }

                    if (message != null) {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = message!!,
                            color = if (isSuccess) SuccessGreen else ErrorRed,
                            style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = {
                            isLoading = true
                            message = null
                            scope.launch {
                                try {
                                    if (isJoinMode) {
                                        val resp = FriendLensApi.joinGroup(JoinGroupRequest(joinCode))
                                        if (resp.status == "success") {
                                            isSuccess = true
                                            message = "Welcome to ${resp.group?.name}!"
                                            joinCode = ""
                                        } else {
                                            isSuccess = false; message = resp.message ?: "Invalid code"
                                        }
                                    } else {
                                        val resp = FriendLensApi.createGroup(CreateGroupRequest(groupName, groupDesc))
                                        if (resp.status == "success") {
                                            isSuccess = true
                                            message = "Created! Share code: ${resp.group?.joinCode}"
                                            groupName = ""; groupDesc = ""
                                        } else {
                                            isSuccess = false; message = resp.message ?: "Failed"
                                        }
                                    }
                                } catch (e: Exception) {
                                    isSuccess = false; message = "Network Error"
                                }
                                isLoading = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp).shadow(8.dp, MaterialTheme.shapes.large),
                        shape = MaterialTheme.shapes.large,
                        colors = ButtonDefaults.buttonColors(backgroundColor = if (isJoinMode) BrandBlue else BrandPurple),
                        enabled = !isLoading && (if (isJoinMode) joinCode.length == 6 else groupName.isNotBlank())
                    ) {
                        if (isLoading) CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        else Text(if (isJoinMode) "Join Album" else "Create Now", style = MaterialTheme.typography.button)
                    }
                }
            }
            
            Spacer(Modifier.weight(1f))
            
            // Decorative Element
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource("drawable/group_hero.png"),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.8f
                )
                Surface(
                    color = Color.Black.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxSize()
                ) {}
                Text("Better together.", style = MaterialTheme.typography.h2.copy(color = Color.White))
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
