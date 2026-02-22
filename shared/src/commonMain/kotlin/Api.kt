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
    private const val BACKEND_URL = "https://friendlens-backend.onrender.com"
    private const val SUPABASE_URL = "https://uhuyybebchznxxbsaxvd.supabase.co"
    private const val SUPABASE_ANON_KEY = "sb_publishable_NHrcR2QqwZfaN7dLicNiKA_Sq1j9Vtn"

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

    private fun HttpRequestBuilder.supabase() {
        header("apikey", SUPABASE_ANON_KEY)
    }

    // ─── Auth (Direct to Supabase) ───
    suspend fun login(request: LoginRequest): AuthResponse {
        return client.post("$SUPABASE_URL/auth/v1/token?grant_type=password") {
            supabase()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun signup(request: SignupRequest): AuthResponse {
        return client.post("$SUPABASE_URL/auth/v1/signup") {
            supabase()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    // ─── User (To FriendLens Ktor Backend) ───
    suspend fun getCurrentUser(): UserResponse {
        return client.get("$BACKEND_URL/api/users/me") { auth() }.body()
    }

    suspend fun updateProfile(request: UpdateProfileRequest): UpdateProfileResponse {
        return client.put("$BACKEND_URL/api/users/me") {
            auth()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    // ─── Groups ───
    suspend fun createGroup(request: CreateGroupRequest): CreateGroupResponse {
        return client.post("$BACKEND_URL/api/groups") {
            auth()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getAllGroups(): GroupsResponse {
        return client.get("$BACKEND_URL/api/groups") { auth() }.body()
    }

    suspend fun getGroupDetail(groupId: String): GroupDetailResponse {
        return client.get("$BACKEND_URL/api/groups/$groupId") { auth() }.body()
    }

    suspend fun joinGroup(request: JoinGroupRequest): JoinGroupResponse {
        return client.post("$BACKEND_URL/api/groups/join") {
            auth()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    // ─── Photos ───
    suspend fun getGroupPhotos(groupId: String): PhotosResponse {
        return client.get("$BACKEND_URL/api/groups/$groupId/photos") { auth() }.body()
    }

    suspend fun uploadPhoto(groupId: String, imageBytes: ByteArray, capturedAt: String? = null): UploadPhotoResponse {
        return client.submitFormWithBinaryData(
            url = "$BACKEND_URL/api/groups/$groupId/photos/upload",
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
        return client.post("$BACKEND_URL/api/groups/$groupId/photos/$photoId/like") { auth() }.body()
    }

    suspend fun unlikePhoto(groupId: String, photoId: String): LikeResponse {
        return client.delete("$BACKEND_URL/api/groups/$groupId/photos/$photoId/like") { auth() }.body()
    }

    // ─── Feedback ───
    suspend fun submitFeedback(request: FeedbackRequest): FeedbackResponse {
        return client.post("$BACKEND_URL/api/feedback") {
            auth()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
