package vn.icheck.android.room.dao

import androidx.room.*
import vn.icheck.android.network.model.chat.ChatConversation

@Dao
interface ConversationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addConversation(chatConversation: ChatConversation)

    @Query("select * from chat_conversation order by last_activity desc")
    fun getAllConversation():List<ChatConversation>

    @Query("select * from chat_conversation where target_name like '%' || :name ||'%'")
    fun searchConversation(name:String):List<ChatConversation>

    @Query("select * from chat_conversation where `key` = :key")
    fun getConversation(key:String):ChatConversation

    @Update
    fun updateConversation(chatConversation: ChatConversation)

    @Query("delete from chat_conversation")
    fun dropChat()
}