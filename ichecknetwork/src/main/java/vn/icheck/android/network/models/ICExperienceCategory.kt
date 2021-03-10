package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICExperienceCategory(
        @Expose
        val id: Long = 0,
        @Expose
        var name: String? = null,
        var isSelected: Boolean = false
)