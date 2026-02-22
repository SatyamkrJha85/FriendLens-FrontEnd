import androidx.compose.animation.animateColorAsState
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

        Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(24.dp)) {
            Spacer(Modifier.height(16.dp))
            Text("Let's get\nstarted", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = TextDark, lineHeight = 44.sp)
            Spacer(Modifier.height(8.dp))
            Text("Create a new collection or join friends.", fontSize = 15.sp, color = TextSecondary)
            Spacer(Modifier.height(32.dp))

            Box(
                modifier = Modifier.fillMaxWidth().height(160.dp).shadow(6.dp, RoundedCornerShape(32.dp)).clip(RoundedCornerShape(32.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(painter = painterResource("drawable/group_hero.png"), contentDescription = "Group", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            }
            Spacer(Modifier.height(32.dp))

            // Toggle
            Row(
                modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(28.dp)).background(CardBackground),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val joinBg by animateColorAsState(if (isJoinMode) Color.White else Color.Transparent)
                val createBg by animateColorAsState(if (!isJoinMode) Color.White else Color.Transparent)

                Box(
                    modifier = Modifier.weight(1f).fillMaxHeight().padding(4.dp).clip(RoundedCornerShape(24.dp))
                        .shadow(if (isJoinMode) 2.dp else 0.dp, RoundedCornerShape(24.dp)).background(joinBg).clickable { isJoinMode = true },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconGroup(if (isJoinMode) BrandBlue else TextSecondary, 16f)
                        Spacer(Modifier.width(6.dp))
                        Text("Join Group", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (isJoinMode) TextDark else TextSecondary)
                    }
                }
                Box(
                    modifier = Modifier.weight(1f).fillMaxHeight().padding(4.dp).clip(RoundedCornerShape(24.dp))
                        .shadow(if (!isJoinMode) 2.dp else 0.dp, RoundedCornerShape(24.dp)).background(createBg).clickable { isJoinMode = false },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconPlus(if (!isJoinMode) BrandPurple else TextSecondary, 16f)
                        Spacer(Modifier.width(6.dp))
                        Text("Create Group", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (!isJoinMode) TextDark else TextSecondary)
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            if (message != null) {
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                        .background(if (isSuccess) SuccessGreen.copy(alpha = 0.1f) else Color.Red.copy(alpha = 0.1f)).padding(12.dp)
                ) { Text(message!!, color = if (isSuccess) SuccessGreen else Color.Red, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                Spacer(Modifier.height(16.dp))
            }

            if (isJoinMode) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Enter 6-character code", color = TextSecondary, fontSize = 14.sp)
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = joinCode, onValueChange = { if (it.length <= 6) joinCode = it.uppercase() },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true,
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, letterSpacing = 8.sp, fontSize = 22.sp, fontWeight = FontWeight.Bold),
                        colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = BrandBlue, unfocusedBorderColor = DividerColor),
                        placeholder = { Text("X Y Z 1 2 3", Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = TextSecondary.copy(alpha = 0.4f)) }
                    )
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = {
                            isLoading = true; message = null
                            scope.launch {
                                try {
                                    val resp = FriendLensApi.joinGroup(JoinGroupRequest(joinCode))
                                    if (resp.status == "success") { isSuccess = true; message = "Joined: ${resp.group?.name ?: "group"}!"; sendLocalNotification("You joined a group!", "Start sharing photos now.") }
                                    else { isSuccess = false; message = resp.message ?: "Invalid join code" }
                                } catch (e: Exception) { isSuccess = false; message = "Error: ${e.message}" }
                                isLoading = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = TextDark),
                        elevation = ButtonDefaults.elevation(defaultElevation = 6.dp),
                        enabled = joinCode.length == 6 && !isLoading
                    ) {
                        if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        else Text("Join Group", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            } else {
                Column {
                    OutlinedTextField(
                        value = groupName, onValueChange = { groupName = it }, label = { Text("Group Name") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = BrandPurple, unfocusedBorderColor = DividerColor, focusedLabelColor = BrandPurple)
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = groupDesc, onValueChange = { groupDesc = it }, label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth().height(100.dp), shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = BrandPurple, unfocusedBorderColor = DividerColor, focusedLabelColor = BrandPurple)
                    )
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = {
                            isLoading = true; message = null
                            scope.launch {
                                try {
                                    val resp = FriendLensApi.createGroup(CreateGroupRequest(groupName, groupDesc))
                                    if (resp.status == "success") { isSuccess = true; message = "Created! Code: ${resp.group?.joinCode}"; sendLocalNotification("Group Created!", "Share code ${resp.group?.joinCode} with friends."); groupName = ""; groupDesc = "" }
                                    else { isSuccess = false; message = resp.message ?: "Failed to create" }
                                } catch (e: Exception) { isSuccess = false; message = "Error: ${e.message}" }
                                isLoading = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = BrandPurple),
                        elevation = ButtonDefaults.elevation(defaultElevation = 6.dp),
                        enabled = groupName.isNotBlank() && !isLoading
                    ) {
                        if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        else Text("Create Group", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}
