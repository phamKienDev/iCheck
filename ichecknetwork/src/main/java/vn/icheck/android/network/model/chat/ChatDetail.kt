package vn.icheck.android.network.model.chat

data class ChatDetail(val key:String?){
    var lastMessage = ""
    var time = System.currentTimeMillis()
}
