package vn.icheck.android.activities.chat.sticker

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sticker_history")
class StickerView(
        @PrimaryKey
        @ColumnInfo(name = "id")
        val id:String,
        @ColumnInfo(name = "image")
        val image:String
){
    @ColumnInfo(name ="package_id")
    var packageId:String = ""
    var name:String = ""
    var total = 0
    @ColumnInfo(name ="lastUsed")
    var lastUse:Long = 0L
}