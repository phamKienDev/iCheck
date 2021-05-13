package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICStampV61(
        @Expose val serial: String?,
        @Expose val distributorId: Long?,
        @Expose val forceUpdate: Boolean?,
        @Expose val canUpdate: Boolean?,
        @Expose val onlyIdentity: Boolean?,
        @Expose val qrm: String?,
        @Expose val widgets: List<ICWidget>?
)