package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

class ICCampaign_Reward {
    @Expose
    var image:String? = null
    @Expose
    var business_name:String? = null

    // 1-iCheck Seller , 2-nomal Seller
    @Expose
    var business_type:Int? = null


    @Expose
    var product_id:Long? = null
    @Expose
    var name:String? = null
    @Expose
    var logo:String? = null
    @Expose
    var id:String? =null

    // 1-vat pham , 2-voucher , 3-iCoin
    @Expose
    var type:Int? = null


    @Expose
    var title:String? = null

    // 1-id cua san pham , 2-id cua voucher
    @Expose
    var entity_id:Long? =null

}