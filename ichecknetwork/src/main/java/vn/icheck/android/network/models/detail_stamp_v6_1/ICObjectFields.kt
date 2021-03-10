package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICObjectFields(
        @Expose
        var mucđouutien: ICObjectChildField? = null,
        @Expose
        var ngaytrabh: ICObjectChildField? = null,
        @Expose
        var nhanvien: ICObjectChildField? = null,
        @Expose
        var note: ICObjectChildField? = null,
        @Expose
        var name: String? = null,
        @Expose
        var value: String? = null
) : Serializable