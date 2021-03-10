package vn.icheck.android.network.models

data class ICSuggestTopic(
        val image: String? = null,
        val avatar: String? = null,
        val createdAt: String? = null,
        val categoryDescendant: List<ICCategory?>? = null,
        val orderSort: Int? = null,
        val level: Int? = null,
        val name: String? = null,
        val id: Int? = null,
        val attributeSet: ICAttributes? = null,
        val updatedAt: String? = null,
        var selected: Boolean = false
)



