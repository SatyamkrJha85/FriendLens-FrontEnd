import kotlinx.serialization.Serializable

// ─── Auth / Token Session ───
data class SessionState(
    val token: String? = null,
    val userId: String? = null,
    val email: String? = null,
    val username: String? = null,
    val avatarUrl: String? = null,
    val isLoggedIn: Boolean = false
)

// ─── User API ───
@Serializable
data class UserResponse(
    val status: String,
    val userId: String? = null,
    val email: String? = null,
    val username: String? = null,
    val avatarUrl: String? = null,
    val message: String? = null
)

@Serializable
data class UpdateProfileRequest(
    val username: String? = null,
    val avatarUrl: String? = null
)

@Serializable
data class UpdateProfileResponse(
    val status: String,
    val message: String
)

// ─── Group API ───
@Serializable
data class Group(
    val id: String,
    val name: String,
    val description: String? = null,
    val joinCode: String,
    val createdAt: String? = null
)

@Serializable
data class CreateGroupRequest(
    val name: String,
    val description: String
)

@Serializable
data class CreateGroupResponse(
    val status: String,
    val group: Group? = null,
    val message: String? = null
)

@Serializable
data class GroupsResponse(
    val status: String,
    val groups: List<Group> = emptyList(),
    val message: String? = null
)

@Serializable
data class GroupDetailResponse(
    val status: String,
    val group: Group? = null,
    val message: String? = null
)

@Serializable
data class JoinGroupRequest(
    val joinCode: String
)

@Serializable
data class JoinGroupResponse(
    val status: String,
    val message: String? = null,
    val group: GroupSummary? = null
)

@Serializable
data class GroupSummary(
    val id: String,
    val name: String
)

// ─── Photo API ───
@Serializable
data class Photo(
    val id: String,
    val s3KeyOriginal: String? = null,
    val s3KeyThumbnail: String? = null,
    val originalUrl: String? = null,
    val uploadedBy: String? = null,
    val uploadedByUsername: String? = null,
    val uploadedByAvatar: String? = null,
    val uploadedAt: String? = null,
    val capturedAt: String? = null,
    val fileSizeBytes: String? = null,
    val s3Key: String? = null
)

@Serializable
data class UploadPhotoResponse(
    val status: String,
    val photo: Photo? = null,
    val message: String? = null
)

@Serializable
data class PhotosResponse(
    val status: String,
    val photos: List<Photo> = emptyList(),
    val message: String? = null
)

@Serializable
data class LikeResponse(
    val status: String,
    val message: String? = null
)

// ─── Feedback API ───
@Serializable
data class FeedbackRequest(
    val content: String,
    val rating: String
)

@Serializable
data class FeedbackResponse(
    val status: String,
    val message: String? = null
)
