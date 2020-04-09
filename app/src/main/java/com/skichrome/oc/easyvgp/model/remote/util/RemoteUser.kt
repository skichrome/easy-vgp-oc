package com.skichrome.oc.easyvgp.model.remote.util

data class RemoteUser(
    private val id: Long = 0L,
    val companyId: Long = 0L,
    val firebaseUid: String = "",
    val notificationToken: String = "",
    val name: String = "",
    val email: String = "",
    val approval: String? = null,
    val vatNumber: String? = null,
    val companyName: String = "",
    val companySiret: String = ""
)
{
    constructor() : this(
        id = 0L,
        companyId = 0L,
        firebaseUid = "",
        notificationToken = "",
        name = "",
        email = "",
        approval = null,
        vatNumber = null,
        companyName = "",
        companySiret = ""
    )
}