package vn.icheck.android.network.models.point_user

import com.google.gson.annotations.Expose
import java.io.Serializable

public class ICKAccumulatePoint : Serializable {
    @Expose
    val point: Long? = null

    @Expose
    val message: String? = null

    @Expose
    val statistic: ICKStatistic? = null
}