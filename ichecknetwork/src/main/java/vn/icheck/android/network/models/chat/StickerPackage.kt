package vn.icheck.android.network.models.chat

data class StickerPackage(
        val id:String,
        val name:String,
        val thumbnail:String,
        val count:Int
)

data class StickerWrapper(
        val data:List<StickerPackage>
)