package com.skichrome.oc.easyvgp.model.source

import com.skichrome.oc.easyvgp.model.local.database.Customers

object CustomerDataProvider
{
    const val customer1Id = 1L
    val customer1 = Customers(
        id = customer1Id,
        firstName = "first name $customer1Id",
        lastName = "last name $customer1Id",
        siret = 12345678910121L,
        postCode = 12345,
        address = "Address $customer1Id",
        city = "City$customer1Id",
        email = "test@email.com",
        mobilePhone = ("0101010110").toInt(),
        notes = "This is a note $customer1Id",
        phone = ("0404040404").toInt()
    )
    val customer1Edit = Customers(
        id = customer1Id,
        firstName = "first edited name $customer1Id",
        lastName = "last edited name $customer1Id",
        siret = 12345678910121L,
        postCode = 12345,
        address = "edited Address $customer1Id",
        city = "edited City$customer1Id",
        email = "test2@email.com",
        mobilePhone = ("0202020220").toInt(),
        notes = "This is an edited note $customer1Id",
        phone = ("0505050505").toInt()
    )

    const val customer2Id = 2L
    val customer2 = Customers(
        id = customer2Id,
        firstName = "first name $customer2Id",
        lastName = "last name $customer2Id",
        siret = 12345678910121L,
        postCode = 12345,
        address = "Address $customer2Id",
        city = "City$customer2Id",
        email = "test@email.com",
        mobilePhone = ("0101010110").toInt(),
        notes = "This is a note $customer2Id",
        phone = ("0404040404").toInt()
    )

    const val customer3Id = 3L
    val customer3 = Customers(
        id = customer3Id,
        firstName = "first name $customer3Id",
        lastName = "last name $customer3Id",
        siret = 12345678910121L,
        postCode = 12345,
        address = "Address $customer3Id",
        city = "City$customer3Id",
        email = "test@email.com",
        mobilePhone = ("0101010110").toInt(),
        notes = "This is a note $customer3Id",
        phone = ("0404040404").toInt()
    )

    val remoteCustomers = listOf(customer1, customer2).sortedBy { it.id }
    val localCustomers = listOf(customer1, customer3).sortedBy { it.id }

    val remoteCustomersMap: LinkedHashMap<Long, Customers>
        get()
        {
            val tempHashMap = LinkedHashMap<Long, Customers>()
            remoteCustomers.forEach { tempHashMap[it.id] = it }
            return tempHashMap
        }

    val localCustomersMap: LinkedHashMap<Long, Customers>
        get()
        {
            val tempHashMap = LinkedHashMap<Long, Customers>()
            localCustomers.forEach { tempHashMap[it.id] = it }
            return tempHashMap
        }
}