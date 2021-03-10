package vn.icheck.android.room.dao

import androidx.paging.DataSource
import androidx.room.*
import vn.icheck.android.activities.chat.v2.model.ICChatMessage

@Dao
interface ChatMessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChatMessage(icChatMessage: ICChatMessage)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertListChatMessage(listChat:List<ICChatMessage>)

    @Query("select * from ic_chat_messages order by sent_time desc")
    fun getChatMessages(): DataSource.Factory<Int, ICChatMessage>

    @Query("select * from ic_chat_messages order by sent_time desc")
    fun getLastMessage():List<ICChatMessage>

    @Delete
    fun deleteMessage(icChatMessage: ICChatMessage)

    @Query("delete from ic_chat_messages")
    fun delete()

    @Query("select * from ic_chat_messages where fbc_id =:id")
    fun searchMessage(id:String): ICChatMessage?

}