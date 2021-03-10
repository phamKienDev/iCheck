package vn.icheck.android.network.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class ICRankOfUser(

	@SerializedName("score")
	val score: Int = 0,

	@SerializedName("level")
	val level: Int? = null,

	@SerializedName("nextTarget")
	val nextTarget: Int = 0,

	@SerializedName("nextLevel")
	val nextLevel: Int? = null
):Parcelable,Serializable
