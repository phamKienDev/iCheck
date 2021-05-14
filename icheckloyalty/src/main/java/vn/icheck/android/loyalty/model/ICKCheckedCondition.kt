package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICKCheckedCondition : Serializable {

    @Expose
    val status: Boolean? = null

    @Expose
    val code: String? = null

    @Expose
    val message: String? = null
}