#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_socialHostDev(JNIEnv* env,jobject) {return env->NewStringUTF("https://apiv2.dev.icheck.vn/");}
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_socialHostProd(JNIEnv* env,jobject) {return env->NewStringUTF("https://api-social.icheck.com.vn/");}
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_defaultHostDev(JNIEnv* env,jobject) {return env->NewStringUTF("https://api.dev.icheck.vn/api/v1/");}
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_defaultHostProd(JNIEnv* env,jobject) {return env->NewStringUTF("https://api.icheck.com.vn/api/v1/");}
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_detailStampHostDev(JNIEnv* env,jobject /* this */) {return env->NewStringUTF("https://qrcode-test-api.icheck.vn/");}
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_detailStampHostProd(JNIEnv* env,jobject /* this */) {return env->NewStringUTF("https://api-qrcode.icheck.com.vn:7788/");}
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_adsSocialHostDev(JNIEnv* env,jobject) {return env->NewStringUTF("http://files-ads.dev.icheck.vn/");}
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_adsSocialHostProd(JNIEnv* env,jobject) {return env->NewStringUTF("https://assets.icheck.vn/");}
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_adsSocialHostOnly(JNIEnv* env,jobject) {return env->NewStringUTF("assets.icheck.vn/");}
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_adsOriginSocialHostOnly(JNIEnv* env, jobject) {return env->NewStringUTF("assets-origin.icheck.vn/");}
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_uploadFileHost(JNIEnv* env,jobject) {return env->NewStringUTF("https://upload.icheck.com.vn/");}
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_uploadFileHostV1(JNIEnv* env,jobject) {return env->NewStringUTF("https://apis.icheck.vn/upload/");}
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_detailStampV6Host(JNIEnv* env,jobject) {return env->NewStringUTF("https://api-qrcode.icheck.com.vn/");}
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_scanditLicenseKey(JNIEnv* env,jobject) {return env->NewStringUTF("2LhYrxig6g0rt//PrMHQsuVdVnq2z4XJDlYQdgBRUi0");}
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_insiderPartnerNameDev(JNIEnv* env,jobject) {return env->NewStringUTF("icheckvntest");}
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_insiderPartnerNameProd(JNIEnv* env,jobject) {return env->NewStringUTF("icheckvn");}
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_trackingTekoUrlDev(JNIEnv* env,jobject) {return env->NewStringUTF("https://footprint-ingestor.dev.tekoapis.net");}
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_trackingTekoUrlProd(JNIEnv* env,jobject) {return env->NewStringUTF("https://footprint-ingestor.tekoapis.com");}
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_trackingAppIdProd(JNIEnv* env,jobject) {return env->NewStringUTF("88c05846-5f5d-499c-8aa0-3d1592365151");}
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_trackingAppIdDev(JNIEnv* env,jobject) {return env->NewStringUTF("1189d10c-46f3-4e4e-9fa0-41eda11217e5");}
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_tripiTokenDev(JNIEnv* env,jobject) {return env->NewStringUTF("grxXh3aS6aPJSJGAqnNm7");}
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_tripiTokenProd(JNIEnv* env,jobject) {return env->NewStringUTF("Ro5LzdJErgsu8SYg3YwrPpKKBaDQ5vG2");}

extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_STAMPDETAIL(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("scan"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_STAMPMOREPRODUCTVERIFIEDDISTRIBUTOR(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("products/distributor/{id}"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_STAMPHISTORYGUARANTEE(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("guarantee/history/{serial}"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_STAMPNOTEHISTORYGUARANTEE(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("guarantee/note-inprogress/{log_id}"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_STAMPMOREPRODUCTVERIFIEDVENDOR(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("products/vendor/{id}"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_STAMPVERIFIEDNUMBERGUARANTEE(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("validate/{serial}/{phone}"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_STAMPDETAILCUSTOMERGUARANTEE(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("customers/{distributor_id}/{phone}"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_ADDRESSDETAILCITY(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("cities/{id}"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_STAMPGETNAMEDISTRICTSGUARANTEE(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("districts/{districts_id}"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_STAMPUPDATEINFORMATIONCUSTOMERGUARANTEE(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("update/serial/{serial}"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_STAMPGETSHOPVARIANT(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("shops/variants"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_STAMPGETCONFIGERROR(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("config"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_PRODUCTINFO(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("info-product/{id}"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_VARIANTPRODUCT(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("product/{product_id}/extras"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_GETFIELDLISTGUARANTEE(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("guarantee/field/list-client/{code}"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_USERSENDOTPCONFIRMPHONESTAMP(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("users/send-otp-anonymous-phone"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_USERCONFIRMPHONESTAMP(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("users/anonymous-phone-confirm"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_ADDRESSDISTRICTS(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("districts"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_ADDRESSCITIES(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("cities"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_SCAN(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("scan/{barcode}"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_CRITERIADETAIL(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("criteria/product/{id}"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_PRODUCTLISTQUESTION(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("product-questions"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_CRITERIALISTREVIEW(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("criteria/product/{id}/reviews"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_PRODUCTLIST(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("products"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_PRODUCTLISTANSWER(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("product-questions/{id}/answers"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_CARTADD(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("social/api/cms/order/cart/up-quantity"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_CRITERIAVOTEREVIEW(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("criteria/customer-review-product/{id}/action"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_CRITERIALISTCOMMENT(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("criteria/reviews/{review_id}/comment"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_SHARELINK(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("linker.php"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_CRITERIAREVIEWPRODUCT(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("criteria/customer-review-product"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_UPLOADIMAGEV1(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("image/v1/raw"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_CRITERIALISTPRODUCTCOMMENT(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("criteria/customer-review-product/{id}/comment"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_PRODUCTDETAIL(JNIEnv* env,jobject /* this */) { return env->NewStringUTF("products/{id}"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_themeSetting(JNIEnv* env, jobject /* this */) { return env->NewStringUTF("social/api/cms/system-setting-design/theme/my-theme"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_productsECommerce(JNIEnv* env, jobject /* this */) { return env->NewStringUTF("social/api/products/ecommerce//products/ecommerce/{id}"); }
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_stampDetailV61(JNIEnv* env, jobject /* this */) { return env->NewStringUTF("qr-core-aps/scan"); }

/*
 * Utilities
 * */
extern "C" JNIEXPORT jstring JNICALL Java_vn_icheck_android_network_base_APIConstants_allUtilities(JNIEnv* env, jobject /* this */) { return env->NewStringUTF("social/api/design/my-icon-more"); }