package com.skichrome.oc.easyvgp.model

import androidx.annotation.VisibleForTesting
import com.skichrome.oc.easyvgp.model.local.database.Customer
import com.skichrome.oc.easyvgp.model.local.database.Machine
import com.skichrome.oc.easyvgp.model.local.database.MachineType

@Suppress("MemberVisibilityCanBePrivate")
@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
object AndroidDataProvider
{
    // --- Customers --- //

    const val customer1Id = 1L
    val customer1 = Customer(
        id = customer1Id,
        firstName = "first name $customer1Id",
        lastName = "last name $customer1Id",
        siret = "12345678910121L",
        postCode = 12345,
        address = "Address $customer1Id",
        city = "City$customer1Id",
        email = "test@email.com",
        mobilePhone = ("0101010110").toInt(),
        notes = "This is a note $customer1Id",
        phone = ("0404040404").toInt()
    )
    val customer1Edit = Customer(
        id = customer1Id,
        firstName = "first edited name $customer1Id",
        lastName = "last edited name $customer1Id",
        siret = "12345678910121L",
        postCode = 12345,
        address = "edited Address $customer1Id",
        city = "edited City$customer1Id",
        email = "test2@email.com",
        mobilePhone = ("0202020220").toInt(),
        notes = "This is an edited note $customer1Id",
        phone = ("0505050505").toInt()
    )

    const val customer2Id = 2L
    val customer2 = Customer(
        id = customer2Id,
        firstName = "first name $customer2Id",
        lastName = "last name $customer2Id",
        siret = "12345678910121L",
        postCode = 12345,
        address = "Address $customer2Id",
        city = "City$customer2Id",
        email = "test@email.com",
        mobilePhone = ("0101010110").toInt(),
        notes = "This is a note $customer2Id",
        phone = ("0404040404").toInt()
    )

    const val customer3Id = 3L
    val customer3 = Customer(
        id = customer3Id,
        firstName = "first name $customer3Id",
        lastName = "last name $customer3Id",
        siret = "12345678910121L",
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

    val remoteCustomerMap: LinkedHashMap<Long, Customer>
        get()
        {
            val tempHashMap = LinkedHashMap<Long, Customer>()
            remoteCustomers.forEach { tempHashMap[it.id] = it }
            return tempHashMap
        }

    val localCustomerMap: LinkedHashMap<Long, Customer>
        get()
        {
            val tempHashMap = LinkedHashMap<Long, Customer>()
            localCustomers.forEach { tempHashMap[it.id] = it }
            return tempHashMap
        }


    // --- MachineType & Machines --- //

    const val machineType1Id = 1L
    val machineType1 = MachineType(
        id = machineType1Id,
        name = "type1"
    )

    const val machineType2Id = 2L
    val machineType2 = MachineType(
        id = machineType2Id,
        name = "type1"
    )

    val machineTypesList = listOf(machineType1, machineType2)

    const val machine1Id = 1L
    val machine1 = Machine(
        machineId = machine1Id,
        name = "Mach1",
        brand = "brand1",
        customer = customer1Id,
        serial = "DFG123AER13333",
        type = machineType1Id
    )

    const val machine2Id = 2L
    val machine2 = Machine(
        machineId = machine2Id,
        name = "Mach2",
        brand = "brand2",
        customer = customer2Id,
        serial = "DFG1245DFFFZ33",
        type = machineType2Id
    )
}