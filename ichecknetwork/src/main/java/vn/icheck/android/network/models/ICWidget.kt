package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICWidget(
        @Expose val id: Long?,
        @Expose val name: String?,
        @Expose val index: Int?,
        @Expose val data: ICWidgetData?
)