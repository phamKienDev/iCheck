package vn.icheck.android.network.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

data class ICPrivacy(
        @Expose val privacyCode: String? = null,
        @Expose val privacyElementName: String? = null,
        @Expose val privacyElementDescription: String? = null,
        @Expose val privacyElementOrder: Int? = null,
        @Expose val privacyId: Long? = null,
        @Expose val privacyElementCode: String? = null,
        @Expose val privacyOrder: Int? = null,
        @Expose val privacyName: String? = null,
        @Expose val privacyElementId: Long = 0,
        @Expose val privacyType: String? = null,
        @Expose var selected: Boolean = false
):Serializable

