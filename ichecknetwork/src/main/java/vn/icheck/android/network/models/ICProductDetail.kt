package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import vn.icheck.android.network.models.product_detail.ICBasicInforProduct
import java.io.Serializable

data class ICProductDetail(
        @Expose val id: Long,
        @Expose val sourceId: Long,
        @Expose val basicInfo: ICBasicInforProduct?,
        @Expose val media: List<ICMedia>?,
        @Expose val status: String,
        @Expose val state: String,
        @Expose val verified: Boolean,
        @Expose val owner: ICOwner?,
//"certificates": [],
//"information": [],
//"categories": [],
//"alerts": []
) : Serializable