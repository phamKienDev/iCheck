package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICCustomAttributes (
        @Expose var id:Long?=null,
        @Expose var entry_id:Long?=null,
        @Expose var attribute_id:Long?=null,
        @Expose var code:String?=null,
        @Expose var name:String?=null,
        @Expose var value_id:String?=null,
        @Expose var varchar:String?=null,
        @Expose var text:String?=null,
        @Expose var boolean:String?=null,
        @Expose var number:Int?=null

)