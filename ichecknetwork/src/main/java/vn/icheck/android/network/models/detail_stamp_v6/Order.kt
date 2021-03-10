package vn.icheck.android.network.models.detail_stamp_v6


import com.google.gson.annotations.Expose
import java.io.Serializable

data class Order(
    @Expose
    var domain_check: String? = null,
    @Expose
    var expired_time: Int? = null,
    @Expose
    var last_count_id: Int? = null,
    @Expose
    var payment_status: Int? = null,
    @Expose
    var duration_month: String? = null,
    @Expose
    var gen_type: Int? = null,
    @Expose
    var show_vendor: Int? = null,
    @Expose
    var force_update: Int? = null,
    @Expose
    var task_done: Int? = null,
    @Expose
    var task_excel_done: Int? = null,
    @Expose
    var total_task_excel: Int? = null,
    @Expose
    var fields: String? = null,
    @Expose
    var records_per_file: Int? = null,
    @Expose
    var transaction_id: Long? = null,
    @Expose
    var final_price: Int? = null,
    @Expose
    var total_price: Int? = null,
    @Expose
    var unit_price: String? = null,
    @Expose
    var coupon_price: Int? = null,
    @Expose
    var coupon_id: Long? = null,
    @Expose
    var show_business: Int? = null,
    @Expose
    var name: String? = null,
    @Expose
    var guarantee_days: Int? = null,
    @Expose
    var counter: Int? = null,
    @Expose
    var total_task: Int? = null,
    @Expose
    var prefix: String? = null,
    @Expose
    var deleted_time: Int? = null,
    @Expose
    var is_deleted: Int? = null,
    @Expose
    var id_end: Int? = null,
    @Expose
    var id_start: Int? = null,
    @Expose
    var link_excel: String? = null,
    @Expose
    var status_zip_excel: Int? = null,
    @Expose
    var user_created: Int? = null,
    @Expose
    var created_time: Int? = null,
    @Expose
    var status: Int? = null,
    @Expose
    var stamp_service: String? = null,
    @Expose
    var quantity: Int? = null,
    @Expose
    var user_id: Int? = null,
    @Expose
    var id_by_user: Int? = null,
    @Expose
    var id: Int? = null
): Serializable