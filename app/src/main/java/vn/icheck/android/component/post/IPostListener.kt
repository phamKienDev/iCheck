package vn.icheck.android.component.post

import vn.icheck.android.network.models.ICPost

interface IPostListener {
    fun onEditPost(obj: ICPost)
    fun followAndUnFollowPage(obj: ICPost)
    fun onDeletePost(id: Long)
}