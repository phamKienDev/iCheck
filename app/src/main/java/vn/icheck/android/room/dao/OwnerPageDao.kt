package vn.icheck.android.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Single
import vn.icheck.android.room.entity.ICMyFollowingPage
import vn.icheck.android.room.entity.ICOwnerPage

@Dao
interface OwnerPageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPageOwner(obj: ICOwnerPage)

    @Query("SELECT * FROM owner_page")
    fun getAllOwnerPage(): Single<List<ICOwnerPage>>

    @Query("SELECT * FROM owner_page WHERE id= :id")
    fun getPageById(id: Long): ICOwnerPage?


    @Query("DELETE FROM owner_page")
    fun deleteAll()
}