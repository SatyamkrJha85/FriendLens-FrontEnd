import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object SessionManager {
    var session by mutableStateOf(SessionState())
        private set

    fun login(token: String, userId: String, email: String, username: String?, avatarUrl: String?) {
        FriendLensApi.authToken = token
        session = SessionState(
            token = token,
            userId = userId,
            email = email,
            username = username,
            avatarUrl = avatarUrl,
            isLoggedIn = true
        )
    }

    fun updateProfile(username: String?, avatarUrl: String?) {
        session = session.copy(
            username = username ?: session.username,
            avatarUrl = avatarUrl ?: session.avatarUrl
        )
    }

    fun logout() {
        FriendLensApi.authToken = null
        session = SessionState()
    }
}
