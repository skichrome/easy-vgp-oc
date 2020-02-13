package com.skichrome.oc.easyvgp.viewmodel

import android.content.ContentValues
import androidx.room.OnConflictStrategy
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseDataDebug
{
    fun prePopulateDatabase(db: SupportSQLiteDatabase)
    {
        val contentValues = ContentValues()

        // --- Customers --- //

        contentValues.put("customer_id", 1)
        contentValues.put("first_name", "Doe")
        contentValues.put("last_name", "John")
        contentValues.put("siret", "123456789101112")
        contentValues.put("email", "john.doe@gmail.com")
        contentValues.put("phone", 770077007)
        contentValues.put("mobile_phone", 110011001)
        contentValues.put("address", "1 Place Monseigneur de Galard")
        contentValues.put("post_code", 43000)
        contentValues.put("city", "Le Puy en Velay")
        contentValues.put("notes", "Lorem ipsum dolor sit amet")

        db.insert("Customers", OnConflictStrategy.IGNORE, contentValues)
        contentValues.clear()

        contentValues.put("customer_id", 2)
        contentValues.put("first_name", "Doe")
        contentValues.put("last_name", "Jane")
        contentValues.put("siret", "1211109876543210")
        contentValues.put("email", "jane.doe@gmail.com")
        contentValues.put("address", "1 Place Monseigneur de Galard")
        contentValues.put("post_code", 43000)
        contentValues.put("city", "Le Puy en Velay")

        db.insert("Customers", OnConflictStrategy.IGNORE, contentValues)
        contentValues.clear()

        contentValues.put("customer_id", 3)
        contentValues.put("first_name", "Dupont")
        contentValues.put("last_name", "Jean")
        contentValues.put("siret", "121110987654")
        contentValues.put("email", "jean.dupont@gmail.com")
        contentValues.put("address", "12 avenue Maréchal Foch")
        contentValues.put("post_code", 43000)
        contentValues.put("city", "Le Puy en Velay")

        db.insert("Customers", OnConflictStrategy.IGNORE, contentValues)
        contentValues.clear()

        // --- MachineType --- //

        contentValues.put("machine_type_id", 1)
        contentValues.put("machine_type_name", "R383M")

        db.insert("MachineType", OnConflictStrategy.IGNORE, contentValues)
        contentValues.clear()

        contentValues.put("machine_type_id", 2)
        contentValues.put("machine_type_name", "R390")

        db.insert("MachineType", OnConflictStrategy.IGNORE, contentValues)
        contentValues.clear()

        contentValues.put("machine_type_id", 3)
        contentValues.put("machine_type_name", "R377M")

        db.insert("MachineType", OnConflictStrategy.IGNORE, contentValues)
        contentValues.clear()

        contentValues.put("machine_type_id", 4)
        contentValues.put("machine_type_name", "R389")

        db.insert("MachineType", OnConflictStrategy.IGNORE, contentValues)
        contentValues.clear()

        // --- Machines --- //

        contentValues.put("machine_id", 1)
        contentValues.put("machine_name", "Grue untel")
        contentValues.put("machine_serial", "1448247DLRE")
        contentValues.put("machine_brand", "Potain")
        contentValues.put("customer_ref", 1)
        contentValues.put("machine_type_ref", 1)

        db.insert("MachineType", OnConflictStrategy.IGNORE, contentValues)
        contentValues.clear()

        contentValues.put("machine_id", 2)
        contentValues.put("machine_name", "Grue untel 2")
        contentValues.put("machine_serial", "1448FEFDLRE34")
        contentValues.put("machine_brand", "Potain")
        contentValues.put("customer_ref", 1)
        contentValues.put("machine_type_ref", 1)

        db.insert("MachineType", OnConflictStrategy.IGNORE, contentValues)
        contentValues.clear()

        contentValues.put("machine_id", 3)
        contentValues.put("machine_name", "Chariot untel")
        contentValues.put("machine_serial", "45DFZ44ED")
        contentValues.put("machine_brand", "Fennwick")
        contentValues.put("customer_ref", 2)
        contentValues.put("machine_type_ref", 4)

        db.insert("MachineType", OnConflictStrategy.IGNORE, contentValues)
        contentValues.clear()

        contentValues.put("machine_id", 4)
        contentValues.put("machine_name", "Chariot untel dupont")
        contentValues.put("machine_serial", "45DFZ44ETYE")
        contentValues.put("machine_brand", "Fennwick")
        contentValues.put("customer_ref", 3)
        contentValues.put("machine_type_ref", 4)

        db.insert("MachineType", OnConflictStrategy.IGNORE, contentValues)
        contentValues.clear()

        contentValues.put("machine_id", 5)
        contentValues.put("machine_name", "Grue Dupont")
        contentValues.put("machine_serial", "45DFDD1454Z44ED")
        contentValues.put("machine_brand", "Potain")
        contentValues.put("customer_ref", 3)
        contentValues.put("machine_type_ref", 3)

        db.insert("MachineType", OnConflictStrategy.IGNORE, contentValues)
        contentValues.clear()
    }
}