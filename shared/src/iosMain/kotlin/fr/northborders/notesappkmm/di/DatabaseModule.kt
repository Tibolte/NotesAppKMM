package fr.northborders.notesappkmm.di

import fr.northborders.notesappkmm.data.local.DatabaseDriverFactory
import fr.northborders.notesappkmm.data.note.SqlDelightNoteDataSource
import fr.northborders.notesappkmm.database.NoteDatabase
import fr.northborders.notesappkmm.domain.note.NoteDataSource

class DatabaseModule {
    private val factory by lazy { DatabaseDriverFactory() }
    val noteDataSource: NoteDataSource by lazy {
        SqlDelightNoteDataSource(NoteDatabase(factory.createDriver()))
    }
}