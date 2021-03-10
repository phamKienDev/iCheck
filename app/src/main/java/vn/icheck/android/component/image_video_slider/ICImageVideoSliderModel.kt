package vn.icheck.android.component.image_video_slider

import kotlinx.coroutines.CoroutineScope
import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTags
import vn.icheck.android.component.ICViewTypes

class ICImageVideoSliderModel(val listSrc: MutableList<MediaLogic>, val coroutineScope: CoroutineScope) : ICViewModel {

    private var pageId: Long? = null

    fun setPageId(pageId: Long?) {
        this.pageId = pageId
    }

    val getPageId: Long?
        get() = pageId


    companion object {
        const val PLAY = 1
        const val RELEASE = 2
    }

    var state = PLAY

    override fun getTag(): String {
        return ICViewTags.IMAGE_VIDEO_SLIDER_COMPONENT
    }

    override fun getViewType(): Int {
        return ICViewTypes.IMAGE_VIDEO_SLIDER
    }
}

