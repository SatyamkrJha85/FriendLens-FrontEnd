import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object DataCache {
    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups = _groups.asStateFlow()

    private val _photos = MutableStateFlow<Map<String, List<Photo>>>(emptyMap())
    val photos = _photos.asStateFlow()

    private val _feedPhotos = MutableStateFlow<List<Photo>>(emptyList())
    val feedPhotos = _feedPhotos.asStateFlow()

    private var _user: UserResponse? = null
    var user: UserResponse?
        get() = _user
        set(value) { _user = value }

    fun updateGroups(newGroups: List<Group>) {
        _groups.value = newGroups
    }

    fun updateGroupPhotos(groupId: String, newPhotos: List<Photo>) {
        val current = _photos.value.toMutableMap()
        current[groupId] = newPhotos
        _photos.value = current
    }

    fun updateFeed(newPhotos: List<Photo>) {
        _feedPhotos.value = newPhotos
    }

    fun clear() {
        _groups.value = emptyList()
        _photos.value = emptyMap()
        _feedPhotos.value = emptyList()
        _user = null
    }
}
