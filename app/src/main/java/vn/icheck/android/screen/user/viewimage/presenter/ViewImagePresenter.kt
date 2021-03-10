package vn.icheck.android.screen.user.viewimage.presenter

import android.content.Intent
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.ICThumbnail
import vn.icheck.android.screen.user.viewimage.view.IViewImageView

/**
 * Created by VuLCL on 11/14/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class ViewImagePresenter(val view: IViewImageView) : BaseActivityPresenter(view) {

    fun getData(intent: Intent) {
        val json = try {
            intent.getStringExtra(Constant.DATA_1)
        } catch (e: Exception) {
            null
        }

        val position = try {
            intent.getIntExtra(Constant.DATA_2, 0)
        } catch (e: Exception) {
            0
        }

        val gSon = GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create()

        val list: MutableList<ICThumbnail>? = try {
            val listType = object : TypeToken<MutableList<ICThumbnail>>() {}.type
            gSon.fromJson(json, listType)
        } catch (e: Exception) {
            null
        }

        if (list.isNullOrEmpty()) {
            view.onGetDataError()
        } else {
            view.onGetDataSuccess(list, position)
        }
    }
}