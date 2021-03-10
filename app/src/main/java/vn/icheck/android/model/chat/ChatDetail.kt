package vn.icheck.android.model.chat

data class ChatDetail(val key:String?){
    var lastMessage = ""
    var time = System.currentTimeMillis()
}
