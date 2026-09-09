package com.victor.restart.core.utils

object BaseUrl {
    private const val PROTOCOL = "http://"
    private const val HOST =  "tracker.lentofinmark.com"//"10.0.2.2"
    private const val PORT = "5000"
    private const val API_PATH = "api/v1"

    val baseUrl: String
        get() =  "$PROTOCOL$HOST/$API_PATH/" //"$PROTOCOL$HOST:$PORT/$API_PATH/"

    fun endpoint(path: String): String =
        "$baseUrl${path.trimStart('/')}"
}