package fr.northborders.notesappkmm.android.di

import android.app.Application
import com.squareup.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.northborders.notesappkmm.data.local.DatabaseDriverFactory
import fr.northborders.notesappkmm.data.note.SqlDelightNoteDataSource
import fr.northborders.notesappkmm.database.NoteDatabase
import fr.northborders.notesappkmm.domain.note.NoteDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSqlDriver(app: Application): SqlDriver {
        return DatabaseDriverFactory(app).createDriver()
    }

    @Provides
    @Singleton
    fun provideNoteDataSource(sqlDriver: SqlDriver): NoteDataSource {
        return SqlDelightNoteDataSource(NoteDatabase(sqlDriver))
    }
}