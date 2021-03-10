package vn.icheck.android.network.api

import retrofit2.http.GET
import vn.icheck.android.activities.chat.v2.model.ChatBotQA

interface ChatBotApi {

    @GET("static-cdn.icheck.vn/setting/iCheckQuestionsBot")
    suspend fun getListQuestionBot(): List<ChatBotQA>
}