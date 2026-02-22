import android.util.Log

actual fun sendLocalNotification(title: String, message: String) {
    Log.d("FriendLensLocalInfo", "Would send Android Notification: $title - $message")
}
