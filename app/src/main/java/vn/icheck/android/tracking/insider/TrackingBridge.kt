package vn.icheck.android.tracking.insider

import android.util.Log
import com.useinsider.insider.Insider
import vn.icheck.android.network.base.SessionManager
import vn.teko.hestia.trackingbridge.TrackingBridgeManager

class TrackingBridge : TrackingBridgeManager {

    override fun trackMiniAppEvent(appId: String, eventType: String, data: Map<String, String>) {
        Log.d("TrackingBridge", "$eventType - ${data.toString()}")
        when (eventType) {
            "flight_search_started" -> {
                tagFlightSearchStarted(data)
            }
            "flight_departure_viewed" -> {
                tagFlightDepartureViewed(data)
            }
            "flight_return_viewed" -> {
                tagFlightReturnViewed(data)
            }
            "flight_weight_chose" -> {
                tagFlightWeightChoose(data)
            }
            "flight_meal_chose" -> {
                tagFlightMealChoose(data)
            }
            "flight_seat_chose" -> {
                tagFlightSeatChoose(data)
            }
            "flight_payment_method_failed" -> {
                tagFlightPaymentMethodFailed(data)
            }
            "flight_payment_method_success" -> {
                tagFlightPaymentMethodSuccess(data)
            }
            "flight_checkout_success" -> {
                tagFlightCheckoutSuccess(data)
            }
            "hotel_search_started" -> {
                tagHotelSearchStarted(data)
            }
            "hotel_filter_applied" -> {
                tagHotelFilterApplied(data)
            }
            "hotel_viewed" -> {
                tagHotelViewed(data)
            }
            "hotel_cus_contact_fill_success" -> {
                tagHotelCusContactFillSuccess(data)
            }
            "hotel_checkout_initiate_start" -> {
                tagHotelCheckoutInitiateStart(data)
            }
            "hotel_checkout_coupon_applied" -> {
                tagHotelCheckoutCouponApplied(data)
            }
            "hotel_payment_method_failed" -> {
                tagHotelPaymentMethodFailed(data)
            }
            "hotel_payment_method_success" -> {
                tagHotelPaymentMethodSuccess(data)
            }
            "hotel_term_agreed" -> {
                tagHotelTermAgreed(data)
            }
            "hotel_checkout_success" -> {
                tagHotelCheckoutSuccess(data)
            }
        }
    }

    private fun tagFlightSearchStarted(data: Map<String, String>) {
        Insider.Instance.tagEvent("flight_search_started")
                .addParameterWithString("user_id", data["user_id"])
                .addParameterWithString("origination_city", data["origination_city"])
                .addParameterWithString("destination_city", data["destination_city"])
                .addParameterWithString("departure_date", data["departure_date"])
                .addParameterWithString("return_date", data["return_date"])
                .addParameterWithString("flight_class", data["flight_class"])
                .addParameterWithString("number_of_passengers", data["number_of_passengers"])
                .addParameterWithString("flight_type", data["flight_type"])
                .build()
    }

    private fun tagFlightDepartureViewed(data: Map<String, String>) {
        Insider.Instance.tagEvent("flight_departure_viewed")
                .addParameterWithString("user_id", data["user_id"])
                .addParameterWithString("flight_company", data["flight_company"])
                .addParameterWithString("flight_price", data["flight_price"])
                .addParameterWithString("flight_class", data["flight_class"])
                .addParameterWithString("departure_date", data["departure_date"])
                .addParameterWithString("return_date", data["return_date"])
                .build()
    }

    private fun tagFlightReturnViewed(data: Map<String, String>) {
        Insider.Instance.tagEvent("flight_return_viewed")
                .addParameterWithString("user_id", data["user_id"])
                .addParameterWithString("flight_company", data["flight_company"])
                .addParameterWithString("flight_price", data["flight_price"])
                .addParameterWithString("flight_class", data["flight_class"])
                .addParameterWithString("departure_date", data["departure_date"])
                .addParameterWithString("return_date", data["return_date"])
                .build()
    }

    private fun tagFlightWeightChoose(data: Map<String, String>) {
        Insider.Instance.tagEvent("flight_weight_chose")
                .addParameterWithString("user_id", data["user_id"])
                .addParameterWithString("flight_additional_weight", data["flight_additional_weight"])
                .addParameterWithString("flight_additional_weight_price", data["flight_additional_weight_price"])
                .build()
    }

    private fun tagFlightMealChoose(data: Map<String, String>) {
        Insider.Instance.tagEvent("flight_weight_chose")
                .addParameterWithString("user_id", data["user_id"])
                .addParameterWithString("flight_additional_meal", data["flight_additional_meal"])
                .addParameterWithString("flight_additional_meal_price", data["flight_additional_meal_price"])
                .build()
    }

    private fun tagFlightSeatChoose(data: Map<String, String>) {
        Insider.Instance.tagEvent("flight_seat_chose")
                .addParameterWithString("user_id", data["user_id"])
                .addParameterWithString("flight_additional_seat", data["flight_additional_seat"])
                .addParameterWithString("flight_additional_seat_price", data["flight_additional_seat_price"])
                .build()
    }

    private fun tagFlightPaymentMethodFailed(data: Map<String, String>) {
        Insider.Instance.tagEvent("flight_payment_method_failed")
                .addParameterWithString("user_id", data["user_id"])
                .addParameterWithString("flight_payment_method", data["flight_payment_method"])
                .build()
    }

    private fun tagFlightPaymentMethodSuccess(data: Map<String, String>) {
        Insider.Instance.tagEvent("flight_payment_method_success")
                .addParameterWithString("user_id", data["user_id"])
                .addParameterWithString("flight_payment_method", data["flight_payment_method"])
                .build()
    }

    private fun tagFlightCheckoutSuccess(data: Map<String, String>) {
        Insider.Instance.tagEvent("flight_checkout_success")
                .addParameterWithString("user_id", data["user_id"])
                .addParameterWithString("flight_order_total_value", data["flight_order_total_value"])
                .addParameterWithString("flight_departure_company", data["flight_departure_company"])
                .addParameterWithString("flight_return_company", data["flight_return_company"])
                .addParameterWithString("flight_departure_city", data["flight_departure_city"])
                .addParameterWithString("flight_return_city", data["flight_return_city"])
                .addParameterWithString("flight_coupon_id", data["flight_coupon_id"])
                .build()
    }

    private fun tagHotelSearchStarted(data: Map<String, String>) {
        Insider.Instance.tagEvent("hotel_search_started")
                .addParameterWithString("user_id", data["user_id"])
                .addParameterWithString("hotel_search_string", data["hotel_search_city"])
                .addParameterWithString("hotel_search_city", data["hotel_search_city"])
                .addParameterWithString("hotel_search_checkin_date", data["hotel_search_checkin_date"])
                .addParameterWithString("hotel_search_checkout_date", data["hotel_search_checkout_date"])
                .addParameterWithString("hotel_search_number_of_guests", data["hotel_search_number_of_guests"])
                .addParameterWithString("hotel_search_number_of_rooms", data["hotel_search_number_of_rooms"])
                .addParameterWithString("hotel_search_number_of_nights", data["hotel_search_number_of_nights"])
                .build()
    }

    private fun tagHotelFilterApplied(data: Map<String, String>) {
        Insider.Instance.tagEvent("hotel_filter_applied")
                .addParameterWithString("user_id", data["user_id"])
                .addParameterWithString("hotel_filter_greater_than_price", data["hotel_filter_greater_than_price"])
                .addParameterWithString("hotel_filter_lesser_than_price", data["hotel_filter_lesser_than_price"])
                .addParameterWithString("hotel_filter_rating", data["hotel_filter_rating"])
                .addParameterWithString("hotel_filter_stars", data["hotel_filter_stars"])
                .addParameterWithString("hotel_filter_types", data["hotel_filter_types"])
                .addParameterWithString("hotel_filter_location_in_city", data["hotel_filter_location_in_city"])
                .addParameterWithString("hotel_filter_additional_services", data["hotel_filter_additional_services"])
                .build()
    }

    private fun tagHotelViewed(data: Map<String, String>) {
        Insider.Instance.tagEvent("hotel_viewed")
                .addParameterWithString("user_id", data["user_id"])
                .addParameterWithString("hotel_name", data["hotel_name"])
                .addParameterWithString("hotel_rating", data["hotel_rating"])
                .addParameterWithString("hotel_stars", data["hotel_stars"])
                .addParameterWithString("hotel_types", data["hotel_types"])
                .addParameterWithString("hotel_city", data["hotel_city"])
                .addParameterWithString("hotel_lowest_price", data["hotel_lowest_price"])
                .build()
    }

    private fun tagHotelCusContactFillSuccess(data: Map<String, String>) {
        Insider.Instance.tagEvent("hotel_cus_contact_fill_success")
                .addParameterWithString("user_id", data["user_id"])
                .build()
    }

    private fun tagHotelCheckoutInitiateStart(data: Map<String, String>) {
        Insider.Instance.tagEvent("hotel_checkout_initiate_start")
                .addParameterWithString("user_id", data["user_id"])
                .build()
    }

    private fun tagHotelCheckoutCouponApplied(data: Map<String, String>) {
        Insider.Instance.tagEvent("hotel_checkout_coupon_applied")
                .addParameterWithString("user_id", data["user_id"])
                .addParameterWithString("hotel_amountBeforeDiscount", data["hotel_amountBeforeDiscount"])
                .build()
    }

    private fun tagHotelPaymentMethodFailed(data: Map<String, String>) {
        Insider.Instance.tagEvent("hotel_payment_method_failed")
                .addParameterWithString("user_id", data["user_id"])
                .addParameterWithString("hotel_payment_method", data["hotel_payment_method"])
                .build()
    }

    private fun tagHotelPaymentMethodSuccess(data: Map<String, String>) {
        Insider.Instance.tagEvent("hotel_payment_method_success")
                .addParameterWithString("user_id", data["user_id"])
                .addParameterWithString("hotel_payment_method", data["hotel_payment_method"])
                .build()
    }

    private fun tagHotelTermAgreed(data: Map<String, String>) {
        Insider.Instance.tagEvent("hotel_term_agreed")
                .addParameterWithString("user_id", data["user_id"])
                .build()
    }

    private fun tagHotelCheckoutSuccess(data: Map<String, String>) {
        Insider.Instance.tagEvent("hotel_checkout_success")
                .addParameterWithString("user_id", data["user_id"])
                .addParameterWithString("hotel_rating", data["hotel_rating"])
                .addParameterWithString("hotel_stars", data["hotel_stars"])
                .addParameterWithString("hotel_types", data["hotel_types"])
                .addParameterWithString("hotel_option_city", data["hotel_option_city"])
                .addParameterWithString("hotel_location_in_city", data["hotel_location_in_city"])
                .addParameterWithString("hotel_additional_services", data["hotel_additional_services"])
                .addParameterWithString("hoel_option_room_specific", data["hoel_option_room_specific"])
                .addParameterWithString("hotel_option_checkin_date", data["hotel_option_checkin_date"])
                .addParameterWithString("hotel_option_checkout_date", data["hotel_option_checkout_date"])
                .addParameterWithString("hotel_option_number_of_guests", data["hotel_option_number_of_guests"])
                .addParameterWithString("hotel_option_number_of_nights", data["hotel_option_number_of_nights"])
                .addParameterWithString("hotel_amountBeforeDiscount", data["hotel_amountBeforeDiscount"])
                .build()
    }


}