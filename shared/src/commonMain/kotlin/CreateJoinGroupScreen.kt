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
        var isLoading by remember { mutableStateOf(false) }
        var message by remember { mutableStateOf<String?>(null) }
        val scope = rememberCoroutineScope()
        val navigator = LocalNavigator.currentOrThrow

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with Back Button
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
                Surface(
                    onClick = { navigator.pop() },
                    shape = CircleShape,
                    color = Color(0xFFF3F4F6),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) { IconBack(TextPrimary, 20f) }
                }
                Spacer(Modifier.height(24.dp))
                Text("Let's get\nstarted", style = MaterialTheme.typography.h1.copy(fontSize = 32.sp, lineHeight = 38.sp))
                Text("Create a new collection or join friends.", style = MaterialTheme.typography.body2)
            }

            // Floating Image Graphic with Avatars
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background blur hint
                Box(Modifier.size(200.dp).background(BrandPrimary.copy(alpha = 0.1f), CircleShape))
                
                Box(modifier = Modifier.size(240.dp, 160.dp)) {
                    Surface(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        shape = RoundedCornerShape(24.dp),
                        elevation = 6.dp
                    ) {
                        Image(
                            painter = painterResource("drawable/group_hero.png"),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    
                    // Connected Badge
                    Surface(
                        modifier = Modifier.align(Alignment.BottomEnd).padding(end = 24.dp).height(44.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        elevation = 4.dp,
                        border = BorderStroke(1.dp, Color(0xFFF3F4FB))
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Row(modifier = Modifier.padding(end = 8.dp)) {
                                repeat(3) { index ->
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .offset(x = ((-index) * 4).dp)
                                            .clip(CircleShape)
                                            .background(Color.LightGray)
                                            .border(1.dp, Color.White, CircleShape)
                                    )
                                }
                            }
                            Text("Connected", style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }

            // Join Section
            SectionHeader("Join Existing Group", Color(0xFFDBEAFE), { IconHome(Color(0xFF2563EB), 18f) })
            
            Surface(
                modifier = Modifier.padding(horizontal = 24.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFF9FAFB),
                border = BorderStroke(1.dp, Color(0xFFF3F4F6))
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Enter 6-character code", style = MaterialTheme.typography.caption)
                    Spacer(Modifier.height(12.dp))
                    
                    TextField(
                        value = joinCode,
                        onValueChange = { if (it.length <= 6) joinCode = it.uppercase() },
                        modifier = Modifier.fillMaxWidth().height(72.dp),
                        textStyle = MaterialTheme.typography.h1.copy(
                            fontSize = 32.sp, 
                            textAlign = TextAlign.Center, 
                            letterSpacing = 8.sp,
                            color = BrandPrimary
                        ),
                        placeholder = { Text("XYZ123", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, style = MaterialTheme.typography.h1.copy(color = Color(0xFFD1D5DB))) },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )
                    
                    Spacer(Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            if (joinCode.length < 6) return@Button
                            isLoading = true
                            scope.launch {
                                try {
                                    val resp = FriendLensApi.joinGroup(JoinGroupRequest(joinCode))
                                    if (resp.status == "success") {
                                        navigator.pop()
                                    } else {
                                        message = resp.message
                                    }
                                } catch (e: Exception) { message = "Error joining group" }
                                finally { isLoading = false }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
                        enabled = !isLoading
                    ) {
                        Text("Join Group", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            
            // OR Divider
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f).height(1.dp).background(Color(0xFFE5E7EB)))
                Text("OR", style = MaterialTheme.typography.caption, modifier = Modifier.padding(horizontal = 16.dp))
                Box(Modifier.weight(1f).height(1.dp).background(Color(0xFFE5E7EB)))
            }

            Spacer(Modifier.height(16.dp))

            // Create Section
            SectionHeader("Start New Journey", BrandPrimary.copy(alpha = 0.1f), { IconPlus(BrandPrimary, 18f) })
            
            Surface(
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 40.dp),
                onClick = {
                    isLoading = true
                    scope.launch {
                        try {
                            val resp = FriendLensApi.createGroup(CreateGroupRequest(name = "New Adventure", description = "New Group"))
                            if (resp.status == "success") navigator.pop()
                        } catch (e: Exception) { message = "Error creating group" }
                        finally { isLoading = false }
                    }
                },
                shape = RoundedCornerShape(24.dp),
                color = BrandPrimary,
                elevation = 8.dp
            ) {
                Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Create a Group", style = MaterialTheme.typography.h3.copy(color = Color.White))
                        Text("Be the host & invite others", style = MaterialTheme.typography.body2.copy(color = Color.White.copy(alpha = 0.8f)))
                    }
                    Surface(shape = RoundedCornerShape(12.dp), color = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(44.dp)) {
                        Box(contentAlignment = Alignment.Center) { IconPlus(Color.White, 24f) }
                    }
                }
            }
        }
    }

    @Composable
    fun SectionHeader(title: String, iconBg: Color, icon: @Composable () -> Unit) {
        Row(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(8.dp), color = iconBg) {
                Box(modifier = Modifier.padding(8.dp)) { icon() }
            }
            Spacer(Modifier.width(12.dp))
            Text(title, style = MaterialTheme.typography.h3.copy(fontSize = 18.sp))
        }
    }
}
