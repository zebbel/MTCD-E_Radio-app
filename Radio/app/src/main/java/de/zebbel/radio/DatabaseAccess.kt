package de.zebbel.radio

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper


class DatabaseAccess private constructor(context: Context) {
    private val openHelper: SQLiteOpenHelper
    private var database: SQLiteDatabase? = null

    // Open the database connection.
    fun open() {
        database = openHelper.writableDatabase
    }

    // Close the database connection.
    fun close() {
        if (database != null) {
            database!!.close()
        }
    }

    // return station logo from stationId
    fun getLogo(stationId: Int): Bitmap? {
        val cursor: Cursor = database!!.rawQuery("SELECT stationLogo FROM stations WHERE stationId is $stationId", null)
        cursor.moveToFirst()
        val logo = cursor.getBlob(0)
        cursor.close()
        return BitmapFactory.decodeByteArray(logo, 0, logo!!.size)
    }

    // return station long name
    fun getLongStationName(stationId: Int): String {
        val cursor: Cursor = database!!.rawQuery("SELECT longName FROM stations WHERE stationId is $stationId", null)
        cursor.moveToFirst()
        val name = cursor.getString(0)
        cursor.close()
        return name
    }

    companion object {
        private var instance: DatabaseAccess? = null

        /**
         * Return a singleton instance of DatabaseAccess.
         *
         * @param context the Context
         * @return the instance of DabaseAccess
         */
        fun getInstance(context: Context): DatabaseAccess? {
            if (instance == null) {
                instance = DatabaseAccess(context)
            }
            return instance
        }
    }

    // Private constructor to avoid object creation from outside classes.
    init {
        openHelper = DatabaseOpenHelper(context)
        openHelper.setForcedUpgrade()
    }
}

class DatabaseOpenHelper(context: Context?) :
    SQLiteAssetHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "stations.db"
        private const val DATABASE_VERSION = 1
    }
}

