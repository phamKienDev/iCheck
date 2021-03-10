package vn.icheck.android.di

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import vn.icheck.android.constant.FACEBOOK_AVATAR
import vn.icheck.android.constant.FACEBOOK_TOKEN
import vn.icheck.android.constant.FACEBOOK_USERNAME
import vn.icheck.android.util.DimensionUtil
import vn.icheck.android.util.ick.logDebug
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object FacebookModule {

    @Provides
    @Singleton
    fun provideFacebookCallbackManagerImpl(): CallbackManager {
        return CallbackManager.Factory.create()
    }

    @Provides
    @Singleton
    fun provideFacebookCallback(@ApplicationContext context: Context, sharedPreferences: SharedPreferences): FacebookCallback<LoginResult> {
        return object : FacebookCallback<LoginResult> {
            var mProfile:ProfileTracker? = null
            override fun onSuccess(result: LoginResult?) {
                val params = Bundle()
                params.putString("fields", "name,picture.type(large)")
                GraphRequest(result?.accessToken, "me", params, HttpMethod.GET,
                        GraphRequest.Callback {
                            val intent = Intent(FACEBOOK_TOKEN)

                            if (Profile.getCurrentProfile() != null) {
                                val pixel = DimensionUtil.convertDpToPixel(80f, context).toInt()
                                val profile = Profile.getCurrentProfile()
                                intent.putExtra(FACEBOOK_USERNAME, profile?.name)
                                sharedPreferences.edit().putString(FACEBOOK_USERNAME, profile?.name)
                                intent.putExtra(FACEBOOK_AVATAR, profile.getProfilePictureUri(pixel, pixel).toString())
                                intent.putExtra(FACEBOOK_TOKEN, result?.accessToken?.token)
                                context.sendBroadcast(intent)
                            } else {
                                mProfile = object : ProfileTracker() {
                                    override fun onCurrentProfileChanged(oldProfile: Profile?, currentProfile: Profile?) {
                                        val pixel = DimensionUtil.convertDpToPixel(80f, context).toInt()
                                        intent.putExtra(FACEBOOK_USERNAME, currentProfile?.name)
                                        sharedPreferences.edit().putString(FACEBOOK_USERNAME, currentProfile?.name)
                                        intent.putExtra(FACEBOOK_AVATAR, currentProfile?.getProfilePictureUri(pixel, pixel).toString())
                                        intent.putExtra(FACEBOOK_TOKEN, result?.accessToken?.token)
                                        context.sendBroadcast(intent)
                                        mProfile?.stopTracking()
                                    }
                                }
                            }

                        }).executeAsync()
            }

            override fun onCancel() {
            }

            override fun onError(error: FacebookException?) {
                if (error is FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut()
                    }
                }
                logDebug("FacebookException: ${error?.localizedMessage}")
            }
        }
    }

    @Provides
    @Singleton
    fun provideFacebookLoginManager(callbackManager: CallbackManager, facebookCallback: FacebookCallback<LoginResult>): LoginManager {

        val instance = LoginManager.getInstance()
        instance.registerCallback(callbackManager,facebookCallback)
        return instance
    }
}