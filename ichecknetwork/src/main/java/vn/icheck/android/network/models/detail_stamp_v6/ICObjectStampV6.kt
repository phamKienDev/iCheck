package vn.icheck.android.network.models.detail_stamp_v6


import com.google.gson.annotations.Expose
import vn.icheck.android.network.models.detail_stamp_v6_1.ICObjectCustomer
import vn.icheck.android.network.models.detail_stamp_v6_1.ICObjectCustomerHistoryGurantee
import java.io.Serializable

data class ICObjectStampV6(
    @Expose
    var _id:String? = null,
    @Expose
    var code_id:String? = null,
    @Expose
    var active_time: Long? = null,
    @Expose
    var can_update_info: Int? = null,
    @Expose
    var count_id: Int? = null,
    @Expose
    var counter: Int? = null,
    @Expose
    var created_time: Long? = null,
    @Expose
    var expire_time: Long? = null,
    @Expose
    var item_id: Int? = null,
    @Expose
    var order_id: Int? = null,
    @Expose
    var qrm: String? = null,
    @Expose
    var serial: String? = null,
    @Expose
    var show_business: Int? = null,
    @Expose
    var show_vendor: Int? = null,
    @Expose
    var stamp_service: MutableList<String>? = null,
    @Expose
    var status: Int? = null,
    @Expose
    var to_address: String? = null,
    @Expose
    var to_city: String? = null,
    @Expose
    var to_district: String? = null,
    @Expose
    var user_id: Int? = null,
    @Expose
    var code_sms: String? = null,
    @Expose
    var task_id:String? = null,
    @Expose
    var force_update:Int? = null,
    @Expose
    var guarantee:ICObjectGuaranteeV6? = null,
    @Expose
    var store_id:Int? = null,
    @Expose
    var city_scan:String? = null,
    @Expose
    var country_scan:String? = null,
    @Expose
    var device_id:String? = null,
    @Expose
    var geo_location:String? = null,
    @Expose
    var ip:String? = null,
    @Expose
    var region_scan:String? = null,
    @Expose
    var scan_time:Long? = null,
    @Expose
    var user_scan:String? = null,
    @Expose
    var customer:ICObjectCustomerHistoryGurantee? = null

) : Serializable