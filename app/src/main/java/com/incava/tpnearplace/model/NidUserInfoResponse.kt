package com.incava.tpnearplace.model

import retrofit2.Response

data class NidUserInfoResponse(
    var resultcode : String,
    var message : String,
    var response: NidUser
)
data class NidUser(
    var id : String,
    var email :String
)
