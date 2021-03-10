package vn.icheck.android.network.models.demo

import com.google.gson.annotations.SerializedName

data class DemoApiRep(
     val status:Int,
     val data:Data,
     val layout:List<LayoutItem>
){
    class Data(
            @SerializedName("attachments")
            val listAttachments:List<AttachmentsItem>?,
            val info:Info?,
            val owner:Owner?
    ){
        class AttachmentsItem(
                val source:String,
                val type:String
        )
        class Info(
                val name:String,
                val barcode:String,
                val price:Long
        )
        class Owner(
                val name:String,
                val address:String,
                val phone:String,
                val tax:String
        )
    }
    class LayoutItem(
            val id:String,
            val keyword:String
    )
}