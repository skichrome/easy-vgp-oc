package com.skichrome.oc.easyvgp.util

import com.skichrome.oc.easyvgp.BuildConfig

const val RC_SIGN_IN_CODE = 2000

const val CURRENT_LOCAL_PROFILE = "current_profile_id_from_room"

// Cloud Firestore and Cloud Stograge
val REMOTE_ADMIN_COLLECTION = if (BuildConfig.DEBUG) "admin_${BuildConfig.FLAVOR}_debug" else "admin_${BuildConfig.FLAVOR}"
const val REMOTE_MACHINE_TYPE_DOCUMENT = "machine_type"
const val REMOTE_MACHINE_TYPE_CONTROL_POINT_DOCUMENT = "machine_type_control_points"
const val REMOTE_CONTROL_POINT_DOCUMENT = "control_points"
const val REMOTE_VERSION = "1.0"

val REMOTE_USER_COLLECTION = if (BuildConfig.DEBUG) "users_data_${BuildConfig.FLAVOR}_debug" else "users_data_${BuildConfig.FLAVOR}"
const val REMOTE_REPORT_COLLECTION = "reports"

val REMOTE_USER_STORAGE = if (BuildConfig.DEBUG) "users_data_${BuildConfig.FLAVOR}_debug" else "users_data_${BuildConfig.FLAVOR}"