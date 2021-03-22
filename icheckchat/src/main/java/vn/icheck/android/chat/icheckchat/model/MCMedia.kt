package vn.icheck.android.chat.icheckchat.model

import com.google.gson.annotations.Expose
import java.io.Serializable

data class MCMedia(@Expose var content: String?, @Expose val type: String?) : Serializable