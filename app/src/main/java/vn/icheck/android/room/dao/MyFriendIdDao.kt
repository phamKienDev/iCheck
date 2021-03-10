package vn.icheck.android.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import vn.icheck.android.room.entity.ICMyFriendIdUser

@Dao
interface MyFriendIdDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMyFriendIDUser(obj: ICMyFriendIdUser)

    @Query("SELECT * FROM my_friend_id_user WHERE id= :id")
    fun getUserByID(id: Long): ICMyFriendIdUser?

    @Query("SELECT * FROM my_friend_id_user")
    fun getAll(): MutableList<ICMyFriendIdUser>

    @Query("SELECT * FROM my_friend_id_user")
    fun getAllFriends(): LiveData<List<ICMyFriendIdUser>>

    @Query("DELETE FROM my_friend_id_user")
    fun deleteAll()

    @Query("DELETE FROM my_friend_id_user WHERE id=:id")
    fun deleteUserById(id: Long)
}