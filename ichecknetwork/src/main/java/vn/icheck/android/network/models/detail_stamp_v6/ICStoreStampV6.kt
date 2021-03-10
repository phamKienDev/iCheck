package vn.icheck.android.network.models.detail_stamp_v6


import com.google.gson.annotations.Expose

class ICStoreStampV6{
    @Expose
    var error: Boolean? = null
    @Expose
    var status: Int? = null
    @Expose
    var data: ObjectDataStore? = null

    class ObjectDataStore{
        @Expose
        var store: MutableList<ICObjectStoreV6>? = null
        @Expose
        var total: Int? = null
    }
}