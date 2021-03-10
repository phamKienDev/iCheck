package vn.icheck.android.fragments.message.model

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import retrofit2.http.DELETE

@Dao
interface MessageDao {
    @Query("select * from ic_messages order by last_seen desc")
    fun getMessageByLastSeen(): DataSource.Factory<Int, ICMessage>

    @Query("select * from ic_messages where message_type=:type")
    fun getMessageLiveData(type:Int): List<ICMessage>

    @Query("select * from ic_messages where fb_id=:fbId")
    fun searchMsg(fbId: String):ICMessage

    @Query("update ic_messages set unread_count=:unread where fb_id=:fbId and unread_count != :unread")
    fun updateUnread(unread:Int, fbId:String)

    @Query("update ic_messages set is_online=:isOnline where fb_id=:fbId")
    fun updateOnline(isOnline:Boolean, fbId:String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(vararg icMessage: ICMessage)

    @Query("update ic_messages set last_seen=:lastSeen and last_message=:lastMessage where fb_id=:fbId")
    fun updateLastMessage(lastSeen: Long, lastMessage:String?, fbId: String)

    @Query("select * from ic_messages where fb_id =:fbId and last_seen =:lastSeen limit 1")
    fun existMsg(fbId: String, lastSeen:Long): ICMessage?

    @Query("update ic_messages set room_name=:name and avatar=:avatar where fb_id=:fbId")
    fun updateGroupChat(name:String?, avatar:String, fbId: String)

    @Query("delete from ic_messages where fb_id=:fbId")
    fun deleteRow(fbId: String)

    @Query("delete from ic_messages")
    fun delete()
}