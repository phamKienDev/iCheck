package vn.icheck.android.activities.chat.sticker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StickerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSticker(stickerView: StickerView)

    @Query("select * from sticker_history order by lastUsed desc limit 20")
    fun getLastSticker():List<StickerView>
}