package com.skichrome.oc.easyvgp.viewmodel

import android.content.ContentValues
import androidx.room.OnConflictStrategy
import androidx.sqlite.db.SupportSQLiteDatabase
import com.skichrome.oc.easyvgp.BuildConfig

object DatabaseDataDebug
{
    fun prePopulateDatabase(db: SupportSQLiteDatabase)
    {
        if (!BuildConfig.DEBUG)
            return

        val contentValues = ContentValues()

        // --- Customers --- //

        contentValues.put("customer_id", 1)
        contentValues.put("company_name", "John's company")
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
        contentValues.put("company_name", "Jane's company")
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
        contentValues.put("company_name", "Jean's company")
        contentValues.put("first_name", "Dupont")
        contentValues.put("last_name", "Jean")
        contentValues.put("siret", "121110987654")
        contentValues.put("email", "jean.dupont@gmail.com")
        contentValues.put("address", "12 avenue Maréchal Foch")
        contentValues.put("post_code", 43000)
        contentValues.put("city", "Le Puy en Velay")

        db.insert("Customers", OnConflictStrategy.IGNORE, contentValues)
        contentValues.clear()
    }
}