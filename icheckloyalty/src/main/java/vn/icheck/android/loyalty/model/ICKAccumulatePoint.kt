package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICKAccumulatePoint : Serializable {
    @Expose
    val point: Long? = null

    @Expose
    val message: String? = null

    @Expose
    val statistic: ICKStatistic? = null
}