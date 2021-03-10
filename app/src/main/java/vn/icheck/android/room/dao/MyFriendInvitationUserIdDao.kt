package vn.icheck.android.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import vn.icheck.android.room.entity.ICMyFriendInvitationUserId

@Dao
interface MyFriendInvitationUserIdDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMyFriendInvitationUserID(obj: ICMyFriendInvitationUserId)

    @Query("SELECT * FROM my_friend_invitation_user_id WHERE id= :id")
    fun getUserByID(id: Long): ICMyFriendInvitationUserId?

    @Query("SELECT * FROM my_friend_invitation_user_id")
    fun getAll(): MutableList<ICMyFriendInvitationUserId>

    @Query("DELETE FROM my_friend_invitation_user_id")
    fun deleteAll()

    @Query("DELETE FROM my_friend_invitation_user_id WHERE id=:id")
    fun deleteUserById(id: Long)
}