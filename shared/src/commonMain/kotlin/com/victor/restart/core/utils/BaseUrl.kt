package com.victor.restart.core.utils

object BaseUrl {
    private const val PROTOCOL = "http://"
    private const val HOST =  "10.0.2.2" //"tracker.lentofinmark.com"
    private const val PORT = "4000"// "5000"
    private const val API_PATH = "api/v1"

    val baseUrl: String
        get() =  "$PROTOCOL$HOST:$PORT/$API_PATH/"//"$PROTOCOL$HOST/$API_PATH/"

    fun endpoint(path: String): String =
        "$baseUrl${path.trimStart('/')}"
}