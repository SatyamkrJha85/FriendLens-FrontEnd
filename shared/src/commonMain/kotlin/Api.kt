import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email

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

    val supabase = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        install(Auth)
    }

    // ─── Auth (Direct to Supabase) ───
    suspend fun login(request: LoginRequest): AuthResponse {
        appLog("login: Attempting login for ${request.email}")
        return try {
            supabase.auth.signInWith(Email) {
                email = request.email
                password = request.password
            }
            val session = supabase.auth.currentSessionOrNull()
            if (session != null) {
                authToken = session.accessToken
                appLog("login: SUCCESS for ${request.email}")
                AuthResponse(access_token = session.accessToken, user = SupabaseUser(id = session.user?.id ?: ""))
            } else {
                appLog("login: FAILED (No session) for ${request.email}")
                AuthResponse(error_description = "Invalid login")
            }
        } catch (e: Exception) {
            appLog("login: ERROR for ${request.email}: ${e.message}")
            AuthResponse(error_description = e.message ?: "Invalid login")
        }
    }

    suspend fun signup(request: SignupRequest): AuthResponse {
        appLog("signup: Attempting signup for ${request.email}")
        return try {
            supabase.auth.signUpWith(Email) {
                email = request.email
                password = request.password
            }
            val session = supabase.auth.currentSessionOrNull()
            if (session != null) {
                authToken = session.accessToken
                appLog("signup: SUCCESS for ${request.email}")
                AuthResponse(access_token = session.accessToken, user = SupabaseUser(id = session.user?.id ?: ""))
            } else {
                appLog("signup: SUCCESS (Check email) for ${request.email}")
                AuthResponse(msg = "Signup successful. Check email.", user = SupabaseUser(id = "")) 
            }
        } catch (e: Exception) {
            appLog("signup: ERROR for ${request.email}: ${e.message}")
            AuthResponse(error_description = e.message ?: "Signup failed")
        }
    }

    suspend fun logout() {
        try {
            supabase.auth.signOut()
        } catch (e: Exception) {}
        authToken = null
    }

    // ─── User (To FriendLens Ktor Backend) ───
    suspend fun getCurrentUser(): UserResponse {
        appLog("getCurrentUser: Fetching profile from backend...")
        try {
            val res = client.get("$BACKEND_URL/api/users/me") { auth() }.body<UserResponse>()
            appLog("getCurrentUser: SUCCESS. userId=${res.userId}, username=${res.username}, email=${res.email}")
            return res
        } catch (e: Exception) {
            appLog("getCurrentUser: ERROR - ${e.message}")
            throw e
        }
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
        appLog("createGroup: Attempting to create group ${request.name}")
        return client.post("$BACKEND_URL/api/groups") {
            auth()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun createGroupWithImage(name: String, description: String, imageBytes: ByteArray?): CreateGroupResponse {
        appLog("createGroupWithImage: Attempting to create group $name with image size ${imageBytes?.size ?: 0}")
        return client.submitFormWithBinaryData(
            url = "$BACKEND_URL/api/groups",
            formData = formData {
                append("name", name)
                append("description", description)
                if (imageBytes != null) {
                    append("image", imageBytes, Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "filename=\"groupImg.jpg\"")
                    })
                }
            }
        ) { auth() }.body()
    }


    suspend fun getAllGroups(): GroupsResponse {
        appLog("getAllGroups: Fetching all groups")
        val response = client.get("$BACKEND_URL/api/groups") { auth() }.body<GroupsResponse>()
        appLog("getAllGroups: SUCCESS fetched ${response.groups.size} groups")
        response.groups.forEachIndexed { i, g ->
        appLog("  -> Group[$i]: id=${g.id}, name='${g.name}', code='${g.joinCode}', img=${g.groupImg}, s3Key=${g.s3Key}, image=${g.image}, cover=${g.coverImage}")
    }
        return response
    }

    suspend fun getGroupDetail(groupId: String): GroupDetailResponse {
        appLog("getGroupDetail: Fetching details for $groupId")
        val response = client.get("$BACKEND_URL/api/groups/$groupId") { auth() }.body<GroupDetailResponse>()
        appLog("getGroupDetail: SUCCESS. Group: name=${response.group?.name}, img=${response.group?.groupImg}")
        return response
    }

    suspend fun joinGroup(request: JoinGroupRequest): JoinGroupResponse {
        appLog("joinGroup: Attempting to join with code ${request.joinCode}")
        return client.post("$BACKEND_URL/api/groups/join") {
            auth()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    // ─── Photos ───
    suspend fun getGroupPhotos(groupId: String): PhotosResponse {
        appLog("getGroupPhotos: for groupId: $groupId")
        try {
            val response = client.get("$BACKEND_URL/api/groups/$groupId/photos") { auth() }.body<PhotosResponse>()
            appLog("getGroupPhotos: SUCCESS. Returned ${response.photos.size} photos.")
            response.photos.forEachIndexed { index, photo ->
                appLog("  -> Photo[$index]:")
                appLog("     id: ${photo.id}")
                appLog("     s3KeyOriginal: ${photo.s3KeyOriginal}")
                appLog("     s3KeyThumbnail: ${photo.s3KeyThumbnail}")
                appLog("     s3Key: ${photo.s3Key}")
                appLog("     originalUrl: ${photo.originalUrl}")
                appLog("     uploadedBy: ${photo.uploadedBy}")
                appLog("     uploadedByUsername: ${photo.uploadedByUsername}")
                appLog("     uploadedByAvatar: ${photo.uploadedByAvatar}")
                appLog("     uploadedAt: ${photo.uploadedAt}")
                appLog("     capturedAt: ${photo.capturedAt}")
                appLog("     fileSizeBytes: ${photo.fileSizeBytes}")
            }
            return response
        } catch (e: Exception) {
            appLog("getGroupPhotos: ERROR: ${e.message}")
            throw e
        }
    }

    suspend fun uploadPhoto(groupId: String, imageBytes: ByteArray, capturedAt: String? = null): UploadPhotoResponse {
        appLog("uploadPhoto: POST for groupId: $groupId, payload size: ${imageBytes.size} bytes")
        try {
            val response = client.submitFormWithBinaryData(
                url = "$BACKEND_URL/api/groups/$groupId/photos/upload",
                formData = formData {
                    append("image", imageBytes, Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "filename=\"photo.jpg\"")
                    })
                    capturedAt?.let { append("capturedAt", it) }
                }
            ) { auth() }.body<UploadPhotoResponse>()
            appLog("uploadPhoto: SUCCESS. Status: ${response.status}, new photo ID: ${response.photo?.id}")
            response.photo?.let { photo ->
                appLog("  -> UploadedPhoto deep fields:")
                appLog("     id: ${photo.id}")
                appLog("     s3KeyOriginal: ${photo.s3KeyOriginal}")
                appLog("     s3KeyThumbnail: ${photo.s3KeyThumbnail}")
                appLog("     s3Key: ${photo.s3Key}")
                appLog("     originalUrl: ${photo.originalUrl}")
                appLog("     uploadedBy: ${photo.uploadedBy}")
                appLog("     uploadedByUsername: ${photo.uploadedByUsername}")
                appLog("     uploadedByAvatar: ${photo.uploadedByAvatar}")
                appLog("     uploadedAt: ${photo.uploadedAt}")
                appLog("     capturedAt: ${photo.capturedAt}")
                appLog("     fileSizeBytes: ${photo.fileSizeBytes}")
            }
            return response
        } catch (e: Exception) {
            appLog("uploadPhoto: ERROR: ${e.message}")
            throw e
        }
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
