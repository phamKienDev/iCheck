package vn.icheck.android.activities.chat.sticker

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sticker_packages")
data class StickerPackages(
        @PrimaryKey @ColumnInfo(name = "id") val id:String,
        @ColumnInfo(name = "name") val name:String,
        @ColumnInfo(name = "thumbnail") val thumbnail:String,
        @ColumnInfo(name = "count") val count:Int
)