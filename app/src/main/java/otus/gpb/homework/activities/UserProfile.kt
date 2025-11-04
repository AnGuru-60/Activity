package otus.gpb.homework.activities
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserInfo(
    val name: String? = null,
    val surName: String? = null,
    val age: Int? = null
) : Parcelable
