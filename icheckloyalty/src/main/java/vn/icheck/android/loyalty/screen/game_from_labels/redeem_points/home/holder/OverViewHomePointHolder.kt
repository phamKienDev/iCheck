package vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.home.holder

import android.content.Intent
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_header_home_redeem_point.view.*
import vn.icheck.android.ichecklibs.util.rText
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.BaseViewHolder
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.setGone
import vn.icheck.android.loyalty.base.setVisible
import vn.icheck.android.loyalty.helper.TextHelper
import vn.icheck.android.loyalty.helper.WidgetHelper
import vn.icheck.android.loyalty.model.ICKPointUser
import vn.icheck.android.loyalty.network.SessionManager
import vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.home.HomeRedeemPointActivity
import vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.point_history.PointHistoryLoyaltyActivity
import vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.the_winner_point.TheWinnerPointActivity
import vn.icheck.android.loyalty.screen.redemption_history.RedemptionHistoryActivity
import vn.icheck.android.loyalty.screen.web.WebViewActivity

class OverViewHomePointHolder(parent: ViewGroup) : BaseViewHolder<ICKPointUser>(R.layout.item_header_home_redeem_point, parent) {

    override fun bind(obj: ICKPointUser) {
        WidgetHelper.loadImageUrl(itemView.imgBanner, HomeRedeemPointActivity.banner)

        checkNullOrEmpty(itemView.tvName, SessionManager.session.user?.name)

        itemView.tvPoint.text = TextHelper.formatMoneyPhay(obj.points)

        itemView.layoutNoLogin.setGone()
        itemView.layoutLogin.setVisible()

        itemView.btnInfor.setOnClickListener {
            itemView.context.startActivity(Intent(itemView.context, WebViewActivity::class.java).apply {
                putExtra(ConstantsLoyalty.DATA_1, HomeRedeemPointActivity.description)
                putExtra(ConstantsLoyalty.DATA_3, itemView.context.rText(R.string.thong_tin_chuong_trinh))
            })
        }

        itemView.btnRanking.setOnClickListener {
            itemView.context.startActivity(Intent(itemView.context, TheWinnerPointActivity::class.java).apply {
                putExtra(ConstantsLoyalty.DATA_1, HomeRedeemPointActivity.campaignID)
            })
        }

        itemView.btnReward.setOnClickListener {
            itemView.context.startActivity(Intent(itemView.context, RedemptionHistoryActivity::class.java).apply {
                putExtra(ConstantsLoyalty.DATA_1, HomeRedeemPointActivity.campaignID)
            })
        }

        itemView.btnHistory.setOnClickListener {
            itemView.context.startActivity(Intent(itemView.context, PointHistoryLoyaltyActivity::class.java).apply {
                putExtra(ConstantsLoyalty.DATA_2, HomeRedeemPointActivity.campaignID)
            })
        }
    }
}