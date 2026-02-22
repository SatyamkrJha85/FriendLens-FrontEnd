import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.russhwolf.settings.Settings

object SessionManager {
    private val settings: Settings by lazy { Settings() }

    var session by mutableStateOf(SessionState())
        private set

    init {
        loadSession()
    }

    private fun loadSession() {
        val token = settings.getStringOrNull("auth_token")
        if (token != null) {
            FriendLensApi.authToken = token
            session = SessionState(
                token = token,
                userId = settings.getStringOrNull("user_id"),
                email = settings.getStringOrNull("user_email"),
                username = settings.getStringOrNull("user_username"),
                avatarUrl = settings.getStringOrNull("user_avatarUrl"),
                isLoggedIn = true
            )
        }
    }

    fun login(token: String, userId: String, email: String, username: String?, avatarUrl: String?) {
        FriendLensApi.authToken = token
        
        settings.putString("auth_token", token)
        settings.putString("user_id", userId)
        settings.putString("user_email", email)
        if (username != null) settings.putString("user_username", username)
        if (avatarUrl != null) settings.putString("user_avatarUrl", avatarUrl)

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
        if (username != null) settings.putString("user_username", username)
        if (avatarUrl != null) settings.putString("user_avatarUrl", avatarUrl)

        session = session.copy(
            username = username ?: session.username,
            avatarUrl = avatarUrl ?: session.avatarUrl
        )
    }

    fun logout() {
        FriendLensApi.authToken = null
        settings.clear()
        session = SessionState()
    }
}
