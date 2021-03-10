package vn.icheck.android.network.models

data class ICGiftHistory(
        val image: String?,
        var nameGift: String?,
        val nameOwner: String?,
        val time: String? = null,
        val target: String? = null,
        val rewardType: String? = null
){
    var logo:String? = ""
    var id:String? = ""
    var value:Long? = null
}