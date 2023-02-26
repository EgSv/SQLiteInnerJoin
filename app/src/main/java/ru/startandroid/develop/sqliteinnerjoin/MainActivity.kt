package ru.startandroid.develop.sqliteinnerjoin

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

const val LOG_TAG = "myLogs"

class MainActivity : AppCompatActivity() {

    var position_id = arrayListOf(1, 2, 3, 4)
    var position_name = arrayListOf("Директор", "Программер", "Бухгалтер", "Охранник")
    var position_salary = arrayListOf(15000, 13000, 10000, 8000)
    var people_name = arrayListOf("Иван", "Марья", "Петр", "Антон", "Даша", "Борис", "Костя", "Игорь" )
    var people_posid = arrayListOf(2, 3, 2, 2, 3, 1, 2, 4)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dbh = DBHelper(this)
        val db = dbh.writableDatabase

        var c: Cursor

        Log.d(LOG_TAG, "---Table position ---")
        c = db.query("position", null, null, null, null, null, null)
        logCursor(c)
        c.close()
        Log.d(LOG_TAG, "--- ---")

        Log.d(LOG_TAG, "---Table people ---")
        c = db.query("people", null, null, null, null, null, null)
        logCursor(c)
        c.close()
        Log.d(LOG_TAG, "--- ---")

        Log.d(LOG_TAG, "---INNER JOIN with rawQuery---")
        val sqlQuery:String = ("select PL.name as Name, PS.name as Position, salary as Salary "
                + "from people as PL "
                + "inner join position as PS "
                + "on PL.posid = PS.id "
                + "where salary > ?")
        c = db.rawQuery(sqlQuery, arrayOf("12000"))
        logCursor(c)
        c.close()
        Log.d(LOG_TAG, "--- ---")

        Log.d(LOG_TAG, "---INNER JOIN with query ---")
        val table = "people as PL inner join position as PS on PL.posid = PS.id"
        val colomns = arrayOf("PL.name as Name", "PS.name as Position", "salary as Salary")
        val selection = "salary < ?"
        val selectionArgs = arrayOf("12000")
        c = db.query(table, colomns, selection, selectionArgs, null, null, null)
        logCursor(c)
        c.close()
        Log.d(LOG_TAG, "--- ---")

        dbh.close()
    }

    private fun logCursor(c: Cursor?) {
        if (c != null) {
            if (c.moveToFirst()) {
                var str:String
                do {
                    str =""
                    for (cn in c.columnNames){
                        str = "$str + $cn = ${c.getString(c.getColumnIndexOrThrow(cn))} ;"
                    }
                    Log.d(LOG_TAG, str)
                } while (c.moveToNext())
            }
        } else
            Log.d(LOG_TAG, "Cursor is null")
    }

    internal inner class DBHelper(context: Context?) :
        SQLiteOpenHelper(context, "myDB", null, 1) {
        override fun onCreate(db: SQLiteDatabase) {
            Log.d(LOG_TAG, "---onCreate database ---")
            val cv = ContentValues()

            db.execSQL("create table position ("
                    + "id integer primary key,"
                    + "name text," + "salary integer"
                    + ");")

            for (i in position_id.indices) {
                cv.clear()
                cv.put("id", position_id[i])
                cv.put("name", position_name[i])
                cv.put("salary", position_salary[i])
                db.insert("position", null, cv)
            }
            db.execSQL("create table people ("
                    + "id integer primary key autoincrement,"
                    + "name text,"
                    + "posid integer"
                    + ");")
            for (i in people_name.indices) {
                cv.clear()
                cv.put("name", people_name[i])
                cv.put("posid", people_posid[i])
                db.insert("people", null, cv)
            }
        }
        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}
        }
}