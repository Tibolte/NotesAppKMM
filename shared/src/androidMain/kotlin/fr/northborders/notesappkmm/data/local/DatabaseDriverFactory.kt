package fr.northborders.notesappkmm.data.local

import android.content.Context
import com.squareup.sqldelight.db.SqlDriver
import fr.northborders.notesappkmm.database.NoteDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(NoteDatabase.Schema, context, "note.db")
    }
}