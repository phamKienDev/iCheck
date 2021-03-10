package vn.icheck.android.base.model

data class ICError(
        val icon: Int,
        val message: String? = null,
        val subMessage: String? = null,
        val button: Int? = null
)