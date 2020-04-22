package com.skichrome.oc.easyvgp.model.remote.util

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteConfigAdminData(@Json(name = "users") val users: List<String>)