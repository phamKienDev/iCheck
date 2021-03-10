package vn.icheck.android.network.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

data class ICMedia(
        @Expose var content: String? = null,
        @Expose var type: String? = null
) : Serializable