package com.skichrome.oc.easyvgp.util

import com.skichrome.oc.easyvgp.BuildConfig

const val RC_IMAGE_CAPTURE_INTENT = 3000
const val RC_PICK_SIGNATURE_INTENT = 3001
const val RC_PICK_LOGO_INTENT = 3002
const val RC_SEND_EMAIL_INTENT = 3003

const val RC_SIGN_IN_CODE = 2000
const val RC_FCM = 2222
const val NOTIFICATION_FCM_ID = 2223
const val RC_UPLOAD_NOTIFICATION = 2224
const val NOTIFICATION_UPLOAD_ID = 2225

// Local Storage access
const val PDF_FOLDER_NAME = "user_reports"
const val PICTURES_FOLDER_NAME = "user_medias"

// Work Manager
const val KEY_PDF_WORK = "worker_data_identifier_for_pdf_firebase_storage_location"
const val KEY_REPORT_ID_WORK = "worker_data_identifier_for_pdf_report_id"
const val KEY_REPORT__EXTRA_ID_WORK = "worker_data_identifier_for_pdf_report_extra_id"
const val KEY_REPORT_MACHINE_WORK = "worker_data_identifier_for_pdf_report_machine_name"
const val KEY_REPORT_DATE_WORK = "worker_data_identifier_for_pdf_report_date"
const val KEY_REPORT_CUSTOMER_ID = "worker_data_identifier_for_pdf_report_customer"
const val KEY_REPORT_MACHINE_ID = "worker_data_identifier_for_pdf_report_machine"
const val KEY_REPORT_MACHINE_TYPE_ID = "worker_data_identifier_for_pdf_report_machine_type"

const val KEY_LOCAL_USER_ID = "worker_data_identifier_local_user_id"
const val KEY_LOCAL_CUSTOMER_ID = "worker_data_identifier_local_customer_id"
const val KEY_LOCAL_MACHINE_ID = "worker_data_identifier_local_machine_id"
const val KEY_LOCAL_MACHINE_TYPE_ID = "worker_data_identifier_local_machine_type_id"
const val KEY_LOCAL_EXTRAS_REFERENCE = "worker_data_identifier_report_extra_reference"
const val KEY_LOCAL_REPORT_DATE = "worker_data_identifier_report_machine_name_for_notification"

const val CURRENT_LOCAL_PROFILE = "current_profile_id_from_room"

// Intents extras
const val MAIN_ACTIVITY_EXTRA_CUSTOMER = "customerId"
const val MAIN_ACTIVITY_EXTRA_MACHINE = "machineId"
const val MAIN_ACTIVITY_EXTRA_MACHINE_TYPE = "machineTypeId"
const val FRAGMENT_STATE_PICTURE_LOCATION = "where_picture_is_saved"
const val FRAGMENT_STATE_REMOTE_PICTURE_LOCATION = "where_remote_picture_is_saved"
const val FRAGMENT_STATE_REMOTE_SIGNATURE_LOCATION = "where_remote_signature_picture_is_saved"
const val FRAGMENT_STATE_REMOTE_LOGO_LOCATION = "where_remote_signature_picture_is_saved"

// Cloud Firestore and Cloud Storage
val REMOTE_ADMIN_COLLECTION = if (BuildConfig.DEBUG) "admin_${BuildConfig.FLAVOR}_debug" else "admin_${BuildConfig.FLAVOR}"
const val REMOTE_MACHINE_TYPE_DOCUMENT = "machine_type"
const val REMOTE_MACHINE_TYPE_CONTROL_POINT_DOCUMENT = "machine_type_control_points"
const val REMOTE_CONTROL_POINT_DOCUMENT = "control_points"
const val REMOTE_VERSION = "1.0"

val REMOTE_USER_COLLECTION = if (BuildConfig.DEBUG) "users_data_${BuildConfig.FLAVOR}_debug" else "users_data_${BuildConfig.FLAVOR}"
const val REMOTE_REPORT_COLLECTION = "reports"

val REMOTE_USER_STORAGE = if (BuildConfig.DEBUG) "users_data_${BuildConfig.FLAVOR}_debug" else "users_data_${BuildConfig.FLAVOR}"

// Remote config
const val KEY_ADMIN_USERS_MAP = "admin_users_email"

// Reports validity

const val SIX_MONTHS_TIME = 15_778_800_000
const val ONE_YEAR_TIME = 31_557_600_000