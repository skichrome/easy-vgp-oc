package com.skichrome.oc.easyvgp.model

import androidx.annotation.VisibleForTesting
import com.skichrome.oc.easyvgp.model.local.ChoicePossibility
import com.skichrome.oc.easyvgp.model.local.ControlType
import com.skichrome.oc.easyvgp.model.local.VerificationType
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

    val customersMap: LinkedHashMap<Long, Customer>
        get()
        {
            val tempHashMap = LinkedHashMap<Long, Customer>()
            customerList.forEach { tempHashMap[it.id] = it }
            return tempHashMap
        }


    // --- MachineType & Machines --- //

    const val machineType1Id = 1L
    val machineType1 = MachineType(
        id = machineType1Id,
        legalName = "testy name 1",
        name = "type1"
    )
    val machineType1Edit = MachineType(
        id = machineType1Id,
        legalName = "testy name 1 edited",
        name = "type1 edited"
    )
    const val machineTypeInsertId = 400L
    val machineTypeInsert = MachineType(
        id = machineTypeInsertId,
        legalName = "test name 400L to insert",
        name = "type 400L to insert"
    )

    const val machineType2Id = machineType1Id + 1L
    val machineType2 = MachineType(
        id = machineType2Id,
        legalName = "testy name 2",
        name = "type2"
    )

    const val machineType3Id = machineType2Id + 1L
    val machineType3 = MachineType(
        id = machineType3Id,
        legalName = "testy name 3",
        name = "type3"
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

    // --- Control Points --- //

    const val ctrlPoint1Id = 1L
    val ctrlPoint1 = ControlPoint(
        id = ctrlPoint1Id,
        name = "ctrl pt 1",
        code = "c1"
    )

    val ctrlPoint1Edit = ControlPoint(
        id = ctrlPoint1Id,
        name = "ctrl pt 1 edited",
        code = "c1 edited"
    )

    const val ctrlPointInsertId = 400L
    val ctrlPointInsert = ControlPoint(
        id = ctrlPointInsertId,
        name = "ctrl pt 1 to insert in database",
        code = "c1 to insert"
    )

    const val ctrlPoint2Id = ctrlPoint1Id + 1L
    val ctrlPoint2 = ControlPoint(
        id = ctrlPoint2Id,
        name = "ctrl pt 2",
        code = "c2"
    )

    const val ctrlPoint3Id = ctrlPoint2Id + 1L
    val ctrlPoint3 = ControlPoint(
        id = ctrlPoint3Id,
        name = "ctrl pt 3",
        code = "c3"
    )

    val ctrlPointList = listOf(ctrlPoint1, ctrlPoint2, ctrlPoint3)
    val ctrlPointHashMap: LinkedHashMap<Long, ControlPoint>
        get()
        {
            val tempHashMap = LinkedHashMap<Long, ControlPoint>()
            ctrlPointList.forEach { tempHashMap[it.id] = it }
            return tempHashMap
        }

    // --- MachineType and Control Point cross ref --- //

    val machineTypeCtrlPointCrossRef1 = MachineTypeControlPointCrossRef(
        machineTypeId = machineType1Id,
        ctrlPointId = ctrlPoint1Id
    )

    val machineTypeCtrlPointCrossRef2 = MachineTypeControlPointCrossRef(
        machineTypeId = machineType1Id,
        ctrlPointId = ctrlPoint3Id
    )

    val machineTypeCtrlPointCrossRef4 = MachineTypeControlPointCrossRef(
        machineTypeId = machineType2Id,
        ctrlPointId = ctrlPoint2Id
    )

    val machineTypeCtrlPointCrossRefList =
        listOf(machineTypeCtrlPointCrossRef1, machineTypeCtrlPointCrossRef2, machineTypeCtrlPointCrossRef4)

    val machineTypeWithControlPoint1 = MachineTypeWithControlPoints(
        machineType = machineType1,
        controlPoints = listOf(ctrlPoint1, ctrlPoint3)
    )

    val machineTypeWithControlPointEdit = MachineTypeWithControlPoints(
        machineType = machineType1,
        controlPoints = listOf(ctrlPoint1, ctrlPoint2)
    )

    val machineTypeWithControlPointInsert = MachineTypeWithControlPoints(
        machineType = machineType3,
        controlPoints = listOf(ctrlPoint2, ctrlPoint3)
    )

    val machineTypeWithControlPoint2 = MachineTypeWithControlPoints(
        machineType = machineType2,
        controlPoints = listOf(ctrlPoint2)
    )

    val machineTypeWithControlPointList = listOf(machineTypeWithControlPoint1, machineTypeWithControlPoint2)
    val machineTypeWithControlPointMap: LinkedHashMap<Long, MachineTypeWithControlPoints>
        get()
        {
            val tempMap = LinkedHashMap<Long, MachineTypeWithControlPoints>()
            tempMap[machineTypeWithControlPoint1.machineType.id] = machineTypeWithControlPoint1
            tempMap[machineTypeWithControlPoint2.machineType.id] = machineTypeWithControlPoint2
            return tempMap
        }


    // --- ControlPointData --- //

    const val ctrlPointData1Id = 1L
    val ctrlPointData1 = ControlPointData(
        id = ctrlPointData1Id,
        ctrlPointPossibility = ChoicePossibility.GOOD,
        ctrlPointRef = ctrlPoint1Id,
        ctrlPointVerificationType = VerificationType.VISUAL,
        comment = "lorem ipsum"
    )

    val ctrlPointData1Edit = ControlPointData(
        id = ctrlPointData1Id,
        ctrlPointPossibility = ChoicePossibility.GOOD,
        ctrlPointRef = ctrlPoint1Id,
        ctrlPointVerificationType = VerificationType.FUNCTIONAL,
        comment = "lorem ipsum EDITED"
    )

    const val ctrlPointData2Id = 2L
    val ctrlPointData2 = ControlPointData(
        id = ctrlPointData2Id,
        ctrlPointPossibility = ChoicePossibility.GOOD,
        ctrlPointRef = ctrlPoint1Id,
        ctrlPointVerificationType = VerificationType.VISUAL,
        comment = "lorem ipsum 2"
    )

    const val ctrlPointData3Id = 3L
    val ctrlPointData3 = ControlPointData(
        id = ctrlPointData3Id,
        ctrlPointPossibility = ChoicePossibility.MEDIUM,
        ctrlPointRef = ctrlPoint2Id,
        ctrlPointVerificationType = VerificationType.VISUAL,
        comment = "lorem ipsum 3"
    )

    const val ctrlPointData4Id = 4L
    val ctrlPointData4 = ControlPointData(
        id = ctrlPointData4Id,
        ctrlPointPossibility = ChoicePossibility.MEDIUM,
        ctrlPointRef = ctrlPoint3Id,
        ctrlPointVerificationType = VerificationType.VISUAL,
        comment = "lorem ipsum 4"
    )

    val ctrlPointDataList = listOf(ctrlPointData1, ctrlPointData2, ctrlPointData3, ctrlPointData4)

    val ctrlPointDataMap: LinkedHashMap<Long, ControlPointData>
        get()
        {
            val tmp = LinkedHashMap<Long, ControlPointData>()
            ctrlPointDataList.forEach { tmp[it.id] = it }
            return tmp
        }

    // --- CtrlPointDataExtras --- //

    const val extra1Id = 1L
    val extra1 = MachineControlPointDataExtra(
        id = extra1Id,
        loadMass = 2012,
        loadType = "peson 1",
        controlGlobalResult = ControlResult.RESULT_OK,
        testsHasTriggeredSensors = true,
        isTestsWithNominalLoad = true,
        isTestsWithLoad = true,
        reportDate = 30_000_000,
        isMachineCE = true,
        isLiftingEquip = true,
        isMachineClean = true,
        machineNotice = true,
        isValid = true,
        controlType = ControlType.VGP,
        interventionPlace = "Place 1",
        machineHours = 201,
        reportEndDate = 30_100_000,
        isReminderEmailSent = true,
        isReportValidEmailSent = true,
        reportLocalPath = null,
        reportRemotePath = null
    )
    val extra1Edit = MachineControlPointDataExtra(
        id = extra1Id,
        loadMass = 2012,
        loadType = "peson 1 update",
        controlGlobalResult = ControlResult.RESULT_OK,
        testsHasTriggeredSensors = true,
        isTestsWithNominalLoad = false,
        isTestsWithLoad = true,
        reportDate = 30_000_000,
        isMachineCE = true,
        isLiftingEquip = true,
        isMachineClean = true,
        machineNotice = true,
        isValid = true,
        controlType = ControlType.VGP,
        interventionPlace = "Place 1",
        machineHours = 2010,
        reportEndDate = 30_100_000,
        isReminderEmailSent = true,
        isReportValidEmailSent = false,
        reportLocalPath = null,
        reportRemotePath = null
    )

    const val extra2Id = 2L
    val extra2 = MachineControlPointDataExtra(
        id = extra2Id,
        loadMass = 20102,
        loadType = "peson 2",
        controlGlobalResult = ControlResult.RESULT_OK,
        testsHasTriggeredSensors = true,
        isTestsWithNominalLoad = false,
        isTestsWithLoad = true,
        reportDate = 40_000_000,
        isMachineCE = true,
        isLiftingEquip = true,
        isMachineClean = true,
        machineNotice = true,
        isValid = false,
        controlType = ControlType.VGP,
        interventionPlace = "Place 2",
        machineHours = 2010,
        reportEndDate = 40_100_000,
        isReminderEmailSent = false,
        isReportValidEmailSent = true,
        reportLocalPath = null,
        reportRemotePath = null
    )

    const val extra3Id = 3L
    val extra3 = MachineControlPointDataExtra(
        id = extra3Id,
        loadMass = 20150,
        loadType = "peson 3",
        controlGlobalResult = ControlResult.RESULT_OK_WITH_INTERVENTION_NEEDED,
        testsHasTriggeredSensors = null,
        isTestsWithNominalLoad = null,
        isTestsWithLoad = false,
        reportDate = 80_000_000,
        isMachineCE = true,
        isLiftingEquip = false,
        isMachineClean = true,
        machineNotice = true,
        isValid = true,
        controlType = ControlType.VGP,
        interventionPlace = "Place 3",
        machineHours = 2018,
        reportEndDate = 80_100_000,
        isReminderEmailSent = true,
        isReportValidEmailSent = false,
        reportLocalPath = null,
        reportRemotePath = null
    )

    val extraList = listOf(extra1, extra2, extra3)

    val extraMap: LinkedHashMap<Long, MachineControlPointDataExtra>
        get()
        {
            val tmp = LinkedHashMap<Long, MachineControlPointDataExtra>()
            extraList.forEach { tmp[it.id] = it }
            return tmp
        }

    val extraByDateMap: LinkedHashMap<Long, MachineControlPointDataExtra>
        get()
        {
            val tmp = LinkedHashMap<Long, MachineControlPointDataExtra>()
            extraList.forEach { tmp[it.reportDate] = it }
            return tmp
        }

    // --- MachineControlPointData --- //

    val machCtrlPtData1 = MachineControlPointData(
        machineId = machine1Id,
        machineCtrlPointDataExtra = extra1Id,
        ctrlPointDataId = ctrlPointData1Id
    )

    val machCtrlPtData2 = MachineControlPointData(
        machineId = machine1Id,
        machineCtrlPointDataExtra = extra2Id,
        ctrlPointDataId = ctrlPointData2Id
    )

    val machCtrlPtData3 = MachineControlPointData(
        machineId = machine2Id,
        machineCtrlPointDataExtra = extra3Id,
        ctrlPointDataId = ctrlPointData3Id
    )

    val machCtrlPtData4 = MachineControlPointData(
        machineId = machine1Id,
        machineCtrlPointDataExtra = extra1Id,
        ctrlPointDataId = ctrlPointData4Id
    )

    val machCtrlPtDataList = listOf(machCtrlPtData1, machCtrlPtData2, machCtrlPtData3, machCtrlPtData4)

    val machCtrlPtDataMap: LinkedHashMap<Long, MachineControlPointData>
        get()
        {
            val tmp = LinkedHashMap<Long, MachineControlPointData>()
            machCtrlPtDataList.forEach { tmp[it.ctrlPointDataId] = it }
            return tmp
        }

    // --- VgpListItems --- //

    val vgpListItem1 = VgpListItem(
        machineId = machCtrlPtData1.machineId,
        reportRemotePath = extraList[machCtrlPtData1.machineCtrlPointDataExtra.toInt() - 1].reportRemotePath,
        reportLocalPath = extraList[machCtrlPtData1.machineCtrlPointDataExtra.toInt() - 1].reportLocalPath,
        reportEndDate = extraList[machCtrlPtData1.machineCtrlPointDataExtra.toInt() - 1].reportEndDate,
        isValid = extraList[machCtrlPtData1.machineCtrlPointDataExtra.toInt() - 1].isValid,
        reportDate = extraList[machCtrlPtData1.machineCtrlPointDataExtra.toInt() - 1].reportDate,
        controlPointDataId = machCtrlPtData1.ctrlPointDataId,
        extrasReference = extraList[machCtrlPtData1.machineCtrlPointDataExtra.toInt() - 1].id
    )

    val vgpListItem2 = VgpListItem(
        machineId = machCtrlPtData2.machineId,
        reportRemotePath = extraList[machCtrlPtData2.machineCtrlPointDataExtra.toInt() - 1].reportRemotePath,
        reportLocalPath = extraList[machCtrlPtData2.machineCtrlPointDataExtra.toInt() - 1].reportLocalPath,
        reportEndDate = extraList[machCtrlPtData2.machineCtrlPointDataExtra.toInt() - 1].reportEndDate,
        isValid = extraList[machCtrlPtData2.machineCtrlPointDataExtra.toInt() - 1].isValid,
        reportDate = extraList[machCtrlPtData2.machineCtrlPointDataExtra.toInt() - 1].reportDate,
        controlPointDataId = machCtrlPtData2.ctrlPointDataId,
        extrasReference = extraList[machCtrlPtData2.machineCtrlPointDataExtra.toInt() - 1].id
    )

    val vgpListItem3 = VgpListItem(
        machineId = machCtrlPtData3.machineId,
        reportRemotePath = extraList[machCtrlPtData3.machineCtrlPointDataExtra.toInt() - 1].reportRemotePath,
        reportLocalPath = extraList[machCtrlPtData3.machineCtrlPointDataExtra.toInt() - 1].reportLocalPath,
        reportEndDate = extraList[machCtrlPtData3.machineCtrlPointDataExtra.toInt() - 1].reportEndDate,
        isValid = extraList[machCtrlPtData3.machineCtrlPointDataExtra.toInt() - 1].isValid,
        reportDate = extraList[machCtrlPtData3.machineCtrlPointDataExtra.toInt() - 1].reportDate,
        controlPointDataId = machCtrlPtData3.ctrlPointDataId,
        extrasReference = extraList[machCtrlPtData3.machineCtrlPointDataExtra.toInt() - 1].id
    )

    val vgpListItem4 = VgpListItem(
        machineId = machCtrlPtData4.machineId,
        reportRemotePath = extraList[machCtrlPtData4.machineCtrlPointDataExtra.toInt() - 1].reportRemotePath,
        reportLocalPath = extraList[machCtrlPtData4.machineCtrlPointDataExtra.toInt() - 1].reportLocalPath,
        reportEndDate = extraList[machCtrlPtData4.machineCtrlPointDataExtra.toInt() - 1].reportEndDate,
        isValid = extraList[machCtrlPtData4.machineCtrlPointDataExtra.toInt() - 1].isValid,
        reportDate = extraList[machCtrlPtData4.machineCtrlPointDataExtra.toInt() - 1].reportDate,
        controlPointDataId = machCtrlPtData4.ctrlPointDataId,
        extrasReference = extraList[machCtrlPtData4.machineCtrlPointDataExtra.toInt() - 1].id
    )

    val vgpListItemList = listOf(vgpListItem1, vgpListItem2, vgpListItem3, vgpListItem4)

    val vgpListItemMap: LinkedHashMap<Long, VgpListItem>
        get()
        {
            val tmp = LinkedHashMap<Long, VgpListItem>()
            vgpListItemList.forEachIndexed { index, vgpListItem -> tmp[index.toLong()] = vgpListItem }
            return tmp
        }

    // --- HomeEndValidityReportItem --- //

    val homeEndValidityReportItem1 = HomeEndValidityReportItem(
        extraId = vgpListItem1.extrasReference,
        isValid = vgpListItem1.isValid,
        reportEndDate = vgpListItem1.reportEndDate,
        reportLocalPath = vgpListItem1.reportLocalPath,
        isReminderEmailSent = extraList[machCtrlPtData1.machineCtrlPointDataExtra.toInt() - 1].isReminderEmailSent,
        companyName = customerList[machineList[machCtrlPtData1.machineId.toInt() - 1].customer.toInt() - 1].companyName,
        customerEmail = customerList[machineList[machCtrlPtData1.machineId.toInt() - 1].customer.toInt() - 1].email,
        localPicture = machineList[machCtrlPtData1.machineId.toInt() - 1].localPhotoRef,
        machineName = machineList[machCtrlPtData1.machineId.toInt() - 1].name,
        remotePicture = machineList[machCtrlPtData1.machineId.toInt() - 1].remotePhotoRef
    )

    val homeEndValidityReportItem2 = HomeEndValidityReportItem(
        extraId = vgpListItem2.extrasReference,
        isValid = vgpListItem2.isValid,
        reportEndDate = vgpListItem2.reportEndDate,
        reportLocalPath = vgpListItem2.reportLocalPath,
        isReminderEmailSent = extraList[machCtrlPtData2.machineCtrlPointDataExtra.toInt() - 1].isReminderEmailSent,
        companyName = customerList[machineList[machCtrlPtData2.machineId.toInt() - 1].customer.toInt() - 1].companyName,
        customerEmail = customerList[machineList[machCtrlPtData2.machineId.toInt() - 1].customer.toInt() - 1].email,
        localPicture = machineList[machCtrlPtData2.machineId.toInt() - 1].localPhotoRef,
        machineName = machineList[machCtrlPtData2.machineId.toInt() - 1].name,
        remotePicture = machineList[machCtrlPtData2.machineId.toInt() - 1].remotePhotoRef
    )

    val homeEndValidityReportItem3 = HomeEndValidityReportItem(
        extraId = vgpListItem3.extrasReference,
        isValid = vgpListItem3.isValid,
        reportEndDate = vgpListItem3.reportEndDate,
        reportLocalPath = vgpListItem3.reportLocalPath,
        isReminderEmailSent = extraList[machCtrlPtData3.machineCtrlPointDataExtra.toInt() - 1].isReminderEmailSent,
        companyName = customerList[machineList[machCtrlPtData3.machineId.toInt() - 1].customer.toInt() - 1].companyName,
        customerEmail = customerList[machineList[machCtrlPtData3.machineId.toInt() - 1].customer.toInt() - 1].email,
        localPicture = machineList[machCtrlPtData3.machineId.toInt() - 1].localPhotoRef,
        machineName = machineList[machCtrlPtData3.machineId.toInt() - 1].name,
        remotePicture = machineList[machCtrlPtData3.machineId.toInt() - 1].remotePhotoRef
    )

    val homeEndValidityReportItem4 = HomeEndValidityReportItem(
        extraId = vgpListItem4.extrasReference,
        isValid = vgpListItem4.isValid,
        reportEndDate = vgpListItem4.reportEndDate,
        reportLocalPath = vgpListItem4.reportLocalPath,
        isReminderEmailSent = extraList[machCtrlPtData4.machineCtrlPointDataExtra.toInt() - 1].isReminderEmailSent,
        companyName = customerList[machineList[machCtrlPtData4.machineId.toInt() - 1].customer.toInt() - 1].companyName,
        customerEmail = customerList[machineList[machCtrlPtData4.machineId.toInt() - 1].customer.toInt() - 1].email,
        localPicture = machineList[machCtrlPtData4.machineId.toInt() - 1].localPhotoRef,
        machineName = machineList[machCtrlPtData4.machineId.toInt() - 1].name,
        remotePicture = machineList[machCtrlPtData4.machineId.toInt() - 1].remotePhotoRef
    )

    val homeEndValidityReportItemList =
        listOf(homeEndValidityReportItem1, homeEndValidityReportItem2, homeEndValidityReportItem3, homeEndValidityReportItem4)

    // --- Report --- //

    val report1 = Report(
        machineId = machCtrlPtData1.machineId,
        ctrlPoint = ctrlPointList[ctrlPointDataList[machCtrlPtData1.ctrlPointDataId.toInt() - 1].ctrlPointRef.toInt() - 1],
        ctrlPointData = ctrlPointDataList[machCtrlPtData1.ctrlPointDataId.toInt() - 1],
        ctrlPointDataExtra = extraList[machCtrlPtData1.machineCtrlPointDataExtra.toInt() - 1]
    )

    val report2 = Report(
        machineId = machCtrlPtData2.machineId,
        ctrlPoint = ctrlPointList[ctrlPointDataList[machCtrlPtData2.ctrlPointDataId.toInt() - 1].ctrlPointRef.toInt() - 1],
        ctrlPointData = ctrlPointDataList[machCtrlPtData2.ctrlPointDataId.toInt() - 1],
        ctrlPointDataExtra = extraList[machCtrlPtData2.machineCtrlPointDataExtra.toInt() - 1]
    )

    val report3 = Report(
        machineId = machCtrlPtData3.machineId,
        ctrlPoint = ctrlPointList[ctrlPointDataList[machCtrlPtData3.ctrlPointDataId.toInt() - 1].ctrlPointRef.toInt() - 1],
        ctrlPointData = ctrlPointDataList[machCtrlPtData3.ctrlPointDataId.toInt() - 1],
        ctrlPointDataExtra = extraList[machCtrlPtData3.machineCtrlPointDataExtra.toInt() - 1]
    )

    val report4 = Report(
        machineId = machCtrlPtData4.machineId,
        ctrlPoint = ctrlPointList[ctrlPointDataList[machCtrlPtData4.ctrlPointDataId.toInt() - 1].ctrlPointRef.toInt() - 1],
        ctrlPointData = ctrlPointDataList[machCtrlPtData4.ctrlPointDataId.toInt() - 1],
        ctrlPointDataExtra = extraList[machCtrlPtData4.machineCtrlPointDataExtra.toInt() - 1]
    )
    val reportList = listOf(report1, report2, report3, report4)

    val reportMap: LinkedHashMap<Long, Report>
        get()
        {
            val tmp = LinkedHashMap<Long, Report>()
            reportList.forEachIndexed { index, report -> tmp[index.toLong()] = report }
            return tmp
        }
}