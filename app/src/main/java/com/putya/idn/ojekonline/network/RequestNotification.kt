package com.putya.idn.ojekonline.network

import com.google.gson.annotations.SerializedName
import com.putya.idn.ojekonline.model.Booking

class RequestNotification {
    @SerializedName("to")
    var token: String? = null

    @SerializedName("data")
    var sendNotificationModel: Booking? = null
}