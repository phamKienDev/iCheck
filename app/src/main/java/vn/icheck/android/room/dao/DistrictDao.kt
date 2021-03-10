package vn.icheck.android.room.dao

import androidx.room.*
import vn.icheck.android.room.entity.ICDistrict

@Dao
interface DistrictDao {

    @Query("SELECT * FROM district_table WHERE city_id = :provinceID")
    fun getListDistrict(provinceID: Int) : MutableList<ICDistrict>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertListDistrict(list: MutableList<ICDistrict>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDistrict(list: MutableList<ICDistrict>)

    @Query("DELETE FROM district_table WHERE id = :districtID")
    fun deleteDistrictByID(districtID: Int)

    @Query("DELETE FROM district_table")
    fun deleteAll()
}