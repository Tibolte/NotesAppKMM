package fr.northborders.notesappkmm.android.note_list

import fr.northborders.notesappkmm.domain.note.Note

data class NoteListState(
    val notes: List<Note> = listOf(),
    val searchText: String = "",
    val isSearchActive: Boolean = false
)
