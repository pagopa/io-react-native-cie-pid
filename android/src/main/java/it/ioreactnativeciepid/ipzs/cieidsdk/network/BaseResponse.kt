package it.ioreactnativeciepid.ipzs.cieidsdk.network

import com.squareup.moshi.Json


open class BaseResponse {
    @Json(name = "messaggio")
    val messaggio: String? = null
}
