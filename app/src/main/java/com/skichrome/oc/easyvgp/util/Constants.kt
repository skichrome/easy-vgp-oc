package com.skichrome.oc.easyvgp.util

import com.skichrome.oc.easyvgp.BuildConfig

const val RC_PICK_PICTURE_INTENT = 3000
const val RC_SIGN_IN_CODE = 2000
const val RC_FCM = 2222
const val NOTIFICATION_FCM_ID = 2223

const val CURRENT_LOCAL_PROFILE = "current_profile_id_from_room"

const val MAIN_ACTIVITY_FRAGMENT_ROUTE = "destination_to_navigate_if_coming_from_fcm_intent"
const val FRAGMENT_STATE_PICTURE_LOCATION = "where_picture_is_saved"

// Cloud Firestore and Cloud Stograge
val REMOTE_ADMIN_COLLECTION = if (BuildConfig.DEBUG) "admin_${BuildConfig.FLAVOR}_debug" else "admin_${BuildConfig.FLAVOR}"
const val REMOTE_MACHINE_TYPE_DOCUMENT = "machine_type"
const val REMOTE_MACHINE_TYPE_CONTROL_POINT_DOCUMENT = "machine_type_control_points"
const val REMOTE_CONTROL_POINT_DOCUMENT = "control_points"
const val REMOTE_VERSION = "1.0"

val REMOTE_USER_COLLECTION = if (BuildConfig.DEBUG) "users_data_${BuildConfig.FLAVOR}_debug" else "users_data_${BuildConfig.FLAVOR}"
const val REMOTE_REPORT_COLLECTION = "reports"

val REMOTE_USER_STORAGE = if (BuildConfig.DEBUG) "users_data_${BuildConfig.FLAVOR}_debug" else "users_data_${BuildConfig.FLAVOR}"