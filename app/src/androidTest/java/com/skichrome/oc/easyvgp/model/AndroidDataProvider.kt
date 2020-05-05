package com.skichrome.oc.easyvgp.model

import androidx.annotation.VisibleForTesting
import com.skichrome.oc.easyvgp.model.local.database.*

@Suppress("MemberVisibilityCanBePrivate")
@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
object AndroidDataProvider
{
    // --- Customers --- //

    const val customer1Id = 1L
    val customer1 = Customer(
        id = customer1Id,
        companyName = "company 1",
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
        companyName = "company 1",
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
        companyName = "company 2",
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
        companyName = "company 3",
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
    val customerList = listOf(customer1, customer2, customer3).sortedBy { it.id }
    val customerListEdit = listOf(customer1Edit, customer2, customer3).sortedBy { it.id }

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
        legalName = "testy name 1",
        name = "type1"
    )

    const val machineType2Id = 2L
    val machineType2 = MachineType(
        id = machineType2Id,
        legalName = "testy name 2",
        name = "type1"
    )

    const val machine1Id = 1L
    val machine1 = Machine(
        machineId = machine1Id,
        name = "Mach1",
        parkNumber = "park 1",
        brand = "brand1",
        model = "model1",
        manufacturingYear = 2000,
        localPhotoRef = null,
        customer = customer1Id,
        serial = "DFG123AER13333",
        type = machineType1Id
    )
    val machine1Edit = Machine(
        machineId = machine1Id,
        name = "Mach1-Edited",
        parkNumber = "park Edited",
        brand = "brand1-Edited",
        model = "model1-edited",
        manufacturingYear = 2000,
        localPhotoRef = null,
        customer = customer1Id,
        serial = "DFG123AER13333-Edited",
        type = machineType1Id
    )

    const val machine2Id = 2L
    val machine2 = Machine(
        machineId = machine2Id,
        name = "Mach2",
        parkNumber = "park 2",
        brand = "brand2",
        model = "model2",
        manufacturingYear = 2001,
        localPhotoRef = null,
        customer = customer2Id,
        serial = "DFG12ddd3AER13333",
        type = machineType2Id
    )

    const val machineToInsertId = 3L
    val machineToInsert = Machine(
        machineId = machineToInsertId,
        name = "Mach3",
        parkNumber = "park 3",
        brand = "brand3",
        model = "model3",
        manufacturingYear = 2002,
        localPhotoRef = null,
        customer = customer2Id,
        serial = "DFG12dsdz3AER13333",
        type = machineType2Id
    )

    val machineList = listOf(machine1, machine2)

    val machineHashMap: LinkedHashMap<Long, Machine>
        get()
        {
            val tempHashMap = LinkedHashMap<Long, Machine>()
            machineList.forEach { tempHashMap[it.machineId] = it }
            return tempHashMap
        }

    val machineTypeList = listOf(machineType1, machineType2)

    val machineTypeHashMap: LinkedHashMap<Long, MachineType>
        get()
        {
            val tempHashMap = LinkedHashMap<Long, MachineType>()
            machineTypeList.forEach { tempHashMap[it.id] = it }
            return tempHashMap
        }


    // --- Users And Companies --- //

    const val company1Id = 1L
    val company1 = Company(
        id = company1Id,
        name = "company1",
        siret = "fdsgerg222",
        localCompanyLogo = null,
        remoteCompanyLogo = null
    )

    const val user1Id = 1L
    val user1 = User(
        id = user1Id,
        name = "user 1",
        companyId = company1Id,
        vatNumber = "loepqgfzeg'ze",
        approval = "qfzefgze",
        email = "fakemail@mail.mail",
        firebaseUid = "fzsfrfgrzifghrghorigogjrgesgrgrgr"
    )

    const val company1IdEdit = 1L
    val company1Edit = Company(
        id = company1IdEdit,
        name = "company1Edit",
        siret = "fdsgerg222Edit",
        localCompanyLogo = null,
        remoteCompanyLogo = null
    )

    const val user1IdEdit = 1L
    val user1Edit = User(
        id = user1IdEdit,
        name = "user 1Edit",
        companyId = company1IdEdit,
        vatNumber = "loepqgfzeg'zeEdit",
        approval = "qfzefgzeEdit",
        email = "fakemail@mail.mailEdit",
        firebaseUid = "fzsfrfgrzifghrghoEditrigogjrgesgrgrgr"
    )

    const val companyIdToInsert = 4L
    val companyToInsert = Company(
        id = companyIdToInsert,
        name = "company4 to inset",
        siret = "fdsgerg2defe22",
        localCompanyLogo = null,
        remoteCompanyLogo = null
    )

    const val userIdToInsert = 4L
    val usertoInsert = User(
        id = userIdToInsert,
        name = "user 4 to insert",
        companyId = companyIdToInsert,
        vatNumber = "loepqgfzeg'zsdde",
        approval = "qfzefgzddde",
        email = "fakemail@maiddddl.mail",
        firebaseUid = "444fzsfrfgrzifghrghorigogjrgesg"
    )

    const val company2Id = 2L
    val company2 = Company(
        id = company2Id,
        name = "company2",
        siret = "fdsgerg2zr22",
        localCompanyLogo = null,
        remoteCompanyLogo = null
    )

    const val user2Id = 2L
    val user2 = User(
        id = user2Id,
        name = "user 2",
        companyId = company2Id,
        vatNumber = "loepqgfdsefzeg'ze",
        approval = "qfzefgzefe",
        email = "fakemfefeeail@mafil.mail",
        firebaseUid = "fzsfrfefefefegrzifghrghorigogjrgesgrgrgr"
    )

    const val company3Id = 3L
    val company3 = Company(
        id = company3Id,
        name = "company3",
        siret = "fdsgerg222ze",
        localCompanyLogo = null,
        remoteCompanyLogo = null
    )

    const val user3Id = 3L
    val user3 = User(
        id = user3Id,
        name = "user ",
        companyId = company3Id,
        vatNumber = "loepqgfzefefefeg'ze",
        approval = "qfzefgzeefef",
        email = "fakemail@mail.efeefmail",
        firebaseUid = "fzsfrfgrzifgefffea444hrghorigogjrgesgrgrgr"
    )

    val companyList = listOf(company1, company2, company3)
    val userList = listOf(user1, user2, user3)
    val userCompanyList = listOf(
        UserAndCompany(company = company1, user = user1),
        UserAndCompany(company = company2, user = user2),
        UserAndCompany(company = company3, user = user3)
    )

    val companyHashMap: LinkedHashMap<Long, Company>
        get()
        {
            val tempHashMap = LinkedHashMap<Long, Company>()
            companyList.forEach { tempHashMap[it.id] = it }
            return tempHashMap
        }

    val userHashMap: LinkedHashMap<Long, User>
        get()
        {
            val tempHashMap = LinkedHashMap<Long, User>()
            userList.forEach { tempHashMap[it.id] = it }
            return tempHashMap
        }

    val userCompanyHashMap: LinkedHashMap<Long, UserAndCompany>
        get()
        {
            val tempHashMap = LinkedHashMap<Long, UserAndCompany>()
            userCompanyList.forEach { tempHashMap[it.company.id] = it }
            return tempHashMap
        }

    // --- Controls Points --- //

    const val ctrlPt1Id = 1L
    val ctrlPt1 = ControlPoint(id = ctrlPt1Id, name = "ctrl pt 1", code = "code")

    const val ctrlPt2Id = 2L
    val ctrlPt2 = ControlPoint(id = ctrlPt2Id, name = "ctrl pt 1", code = "code")

    const val ctrlPt3Id = 3L
    val ctrlPt3 = ControlPoint(id = ctrlPt3Id, name = "ctrl pt 1", code = "code")

    val ctrlPtList = listOf(ctrlPt1, ctrlPt2, ctrlPt3)

    // --- ControlPointData --- //

    const val ctrlPointData1Id = 1L
    val ctrlPointData1 = ControlPointData(
        id = ctrlPointData1Id,
        ctrlPointPossibility = 1,
        ctrlPointRef = ctrlPt1Id,
        ctrlPointVerificationType = 1,
        comment = "lorem ipsum"
    )

    const val ctrlPointData2Id = 2L
    val ctrlPointData2 = ControlPointData(
        id = ctrlPointData2Id,
        ctrlPointPossibility = 1,
        ctrlPointRef = ctrlPt1Id,
        ctrlPointVerificationType = 1,
        comment = "lorem ipsum"
    )

    const val ctrlPointData3Id = 3L
    val ctrlPointData3 = ControlPointData(
        id = ctrlPointData3Id,
        ctrlPointPossibility = 1,
        ctrlPointRef = ctrlPt2Id,
        ctrlPointVerificationType = 1,
        comment = "lorem ipsum"
    )

    val ctrlPointDataList = listOf(ctrlPointData1, ctrlPointData2, ctrlPointData3)
}