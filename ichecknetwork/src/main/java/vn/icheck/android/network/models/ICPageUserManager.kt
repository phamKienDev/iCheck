package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ICPageUserManager(

	@SerializedName("name")
	val name: String? = null,

	@SerializedName("id")
	val id: Int? = null,

	@SerializedName("avatar")
	val avatar: String? = null,

	@SerializedName("isVerify")
	val isVerify: Boolean? = null
) : Serializable
