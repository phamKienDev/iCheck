package vn.icheck.android.loyalty.model

data class ICKError(
        val icon: Int,
        val title: String,
        val message: String,
        val textButton: String,
        val backgroundButton: Int = 0,
        val colorButton: Int
)