package vn.icheck.android.activities.chat.sticker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StickerPackagesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStickerPackages(obj: StickerPackages)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStickerPackages(list: MutableList<StickerPackages>)

    @Query("select * from sticker_packages")
    fun getAllStickerPackges(): MutableList<StickerPackages>

    @Query("SELECT * FROM sticker_packages WHERE `id` =:id")
    fun getStickerPackages(id: String): StickerPackages

    @Query("DELETE FROM sticker_packages WHERE `id` =:id")
    fun deleteStickPackages(id: String)
}