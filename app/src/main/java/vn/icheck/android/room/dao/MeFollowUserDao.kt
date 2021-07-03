package vn.icheck.android.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Single
import vn.icheck.android.room.entity.ICMeFollowUser
import vn.icheck.android.room.entity.ICMyFollowingPage

@Dao
interface MeFollowUserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMeFollowUser(obj: ICMeFollowUser)

    @Query("SELECT * FROM me_follow_user WHERE id= :id")
    fun getUserByID(id: Long): ICMeFollowUser?

    @Query("SELECT * FROM me_follow_user")
    fun getAll(): LiveData<MutableList<ICMeFollowUser>>

    @Query("DELETE FROM me_follow_user")
    fun deleteAll()

    @Query("DELETE FROM me_follow_user WHERE id=:id")
    fun deleteUserById(id: Long)
}