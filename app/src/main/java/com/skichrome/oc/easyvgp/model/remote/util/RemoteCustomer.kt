package com.skichrome.oc.easyvgp.model.remote.util

data class RemoteCustomer(
    val id: Long = 0L,
    val companyName: String,
    val firstName: String,
    val lastName: String,
    val siret: String,
    val email: String?,
    val phone: Int?,
    val mobilePhone: Int?,
    val address: String,
    val postCode: Int,
    val city: String,
    val notes: String?
)
{
    constructor() : this(0L, "", "", "", "", null, null, null, "", 0, "", null)
}