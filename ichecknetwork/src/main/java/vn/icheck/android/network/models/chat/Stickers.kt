package vn.icheck.android.network.models.chat

data class Stickers(
        val id: String,
        val image: String,
        val packageID: String
)

data class DetailSticker(
        val data: List<Stickers>
)