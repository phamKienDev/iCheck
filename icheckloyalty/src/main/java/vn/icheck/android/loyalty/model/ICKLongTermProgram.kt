package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICKLongTermProgram : Serializable {

    @Expose
    var id: Long = 0

    @Expose
    var user_id: Long = 0

    @Expose
    var banner: ICThumbnail? = null

    @Expose
    var point_name: String? = null

    @Expose
    var point: ICKPointUser? = null

    @Expose
    var user: ICKBusiness? = null

    @Expose
    var network: ICKNetwork? = null

    @Expose
    var customer: ICKCustomer? = null
}