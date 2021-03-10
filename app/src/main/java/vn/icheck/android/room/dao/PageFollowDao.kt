package vn.icheck.android.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Single
import vn.icheck.android.room.entity.ICMyFollowingPage

@Dao
interface PageFollowDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPageFollow(obj: ICMyFollowingPage)

    @Query("SELECT * FROM my_following_page WHERE id= :id")
    fun getPageFollowByID(id: Long): ICMyFollowingPage?

    @Query("SELECT * FROM my_following_page")
    fun getAllPageFollow(): MutableList<ICMyFollowingPage>

    @Query("DELETE FROM my_following_page")
    fun deleteAll()

    @Query("DELETE FROM my_following_page WHERE id=:id")
    fun deletePageFollowById(id: Long)
}