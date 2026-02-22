import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object FriendLensApi {
    private const val BASE_URL = "https://friendlens-backend.onrender.com/api"

    var authToken: String? = null

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.NONE
        }
    }

    private fun HttpRequestBuilder.auth() {
        authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
    }

    // ─── User ───
    suspend fun getCurrentUser(): UserResponse {
        return client.get("$BASE_URL/users/me") { auth() }.body()
    }

    suspend fun updateProfile(request: UpdateProfileRequest): UpdateProfileResponse {
        return client.put("$BASE_URL/users/me") {
            auth()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    // ─── Groups ───
    suspend fun createGroup(request: CreateGroupRequest): CreateGroupResponse {
        return client.post("$BASE_URL/groups") {
            auth()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getAllGroups(): GroupsResponse {
        return client.get("$BASE_URL/groups") { auth() }.body()
    }

    suspend fun getGroupDetail(groupId: String): GroupDetailResponse {
        return client.get("$BASE_URL/groups/$groupId") { auth() }.body()
    }

    suspend fun joinGroup(request: JoinGroupRequest): JoinGroupResponse {
        return client.post("$BASE_URL/groups/join") {
            auth()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    // ─── Photos ───
    suspend fun getGroupPhotos(groupId: String): PhotosResponse {
        return client.get("$BASE_URL/groups/$groupId/photos") { auth() }.body()
    }

    suspend fun uploadPhoto(groupId: String, imageBytes: ByteArray, capturedAt: String? = null): UploadPhotoResponse {
        return client.submitFormWithBinaryData(
            url = "$BASE_URL/groups/$groupId/photos/upload",
            formData = formData {
                append("image", imageBytes, Headers.build {
                    append(HttpHeaders.ContentType, "image/jpeg")
                    append(HttpHeaders.ContentDisposition, "filename=\"photo.jpg\"")
                })
                capturedAt?.let { append("capturedAt", it) }
            }
        ) { auth() }.body()
    }

    suspend fun likePhoto(groupId: String, photoId: String): LikeResponse {
        return client.post("$BASE_URL/groups/$groupId/photos/$photoId/like") { auth() }.body()
    }

    suspend fun unlikePhoto(groupId: String, photoId: String): LikeResponse {
        return client.delete("$BASE_URL/groups/$groupId/photos/$photoId/like") { auth() }.body()
    }

    // ─── Feedback ───
    suspend fun submitFeedback(request: FeedbackRequest): FeedbackResponse {
        return client.post("$BASE_URL/feedback") {
            auth()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
