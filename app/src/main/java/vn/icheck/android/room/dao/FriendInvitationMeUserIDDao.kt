package vn.icheck.android.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import vn.icheck.android.room.entity.ICFriendInvitationMeUserId
import vn.icheck.android.room.entity.ICMyFriendInvitationUserId

@Dao
interface FriendInvitationMeUserIDDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFriendInvitationMeUserID(obj: ICFriendInvitationMeUserId)

    @Query("SELECT * FROM friend_invitation_me_user_id WHERE id= :id")
    fun getUserByID(id: Long): ICFriendInvitationMeUserId?

    @Query("SELECT * FROM friend_invitation_me_user_id")
    fun getAll(): MutableList<ICFriendInvitationMeUserId>

    @Query("DELETE FROM friend_invitation_me_user_id")
    fun deleteAll()

    @Query("DELETE FROM friend_invitation_me_user_id WHERE id=:id")
    fun deleteUserById(id: Long)
}