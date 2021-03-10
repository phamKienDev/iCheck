package vn.icheck.android.room.dao

import androidx.room.*
import vn.icheck.android.room.entity.ICProvince

@Dao
interface ProvinceDao {

    @Query("SELECT * FROM province_table")
    fun getAllProvince() : MutableList<ICProvince>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertListProvince(list: MutableList<ICProvince>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProvince(list: MutableList<ICProvince>)

    @Query("DELETE FROM province_table WHERE id = :provinceID")
    fun deleteProvinceByID(provinceID: Int)

    @Query("DELETE FROM province_table")
    fun deleteAll()
}