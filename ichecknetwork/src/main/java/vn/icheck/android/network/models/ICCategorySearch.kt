package vn.icheck.android.network.models

data class ICCategorySearch(
        val id: Long? = null,
        val name: String? = null,
        val content: String? = null,
        val childrenCount: Int = 0,
        var listParent: MutableList<ICCategorySearch>? = null
)




