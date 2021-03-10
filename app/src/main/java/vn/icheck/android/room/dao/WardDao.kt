package vn.icheck.android.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import vn.icheck.android.room.entity.ICWard

@Dao
interface WardDao {

    @Query("SELECT * FROM ward_table WHERE district_id = :districtID")
    fun getListDistrict(districtID: Int): MutableList<ICWard>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertListDistrict(list: MutableList<ICWard>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDistrict(list: MutableList<ICWard>)

    @Query("DELETE FROM district_table WHERE id = :districtID")
    fun deleteDistrictByID(districtID: Int)

    @Query("DELETE FROM district_table")
    fun deleteAll()
}