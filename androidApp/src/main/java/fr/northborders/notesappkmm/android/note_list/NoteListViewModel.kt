package fr.northborders.notesappkmm.android.note_list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.northborders.notesappkmm.domain.note.Note
import fr.northborders.notesappkmm.domain.note.NoteDataSource
import fr.northborders.notesappkmm.domain.note.SearchNotes
import fr.northborders.notesappkmm.domain.time.DateTimeUtil
import fr.northborders.notesappkmm.presentation.RedOrangeHex
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val noteDataSource: NoteDataSource,
    private val savedStateHandle: SavedStateHandle // used to help restore state after process death
): ViewModel() {

    private val searchNotes = SearchNotes() // TODO should this be injected?

    private val notes = savedStateHandle.getStateFlow("notes", emptyList<Note>())
    private val searchText = savedStateHandle.getStateFlow("searchText", "")
    private val isSearchActive = savedStateHandle.getStateFlow("isSearchActive", false)

    val state = combine(notes, searchText, isSearchActive) { notes, searchText, isSearchActive ->
        NoteListState(
            notes = searchNotes.execute(notes, searchText),
            searchText = searchText,
            isSearchActive = isSearchActive
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NoteListState()) // on screen rotation we wouldn't resubscribe, only if you leave the screen for more than 5s

    // FIXME this is just for testing
//    init {
//        viewModelScope.launch {
//            (0 .. 10).forEach {
//                noteDataSource.insertNote(
//                    Note(
//                        id = it.toLong(),
//                        title = "Note$it",
//                        content = "Content$it",
//                        colorHex = RedOrangeHex,
//                        created = DateTimeUtil.now()
//                    )
//                )
//            }
//        }
//    }

    fun loadNotes() {
        viewModelScope.launch {
            savedStateHandle["notes"] = noteDataSource.getAllNotes()
        }
    }

    fun onSearchTextChange(text: String) {
        savedStateHandle["searchText"] = text
    }

    fun onToggleSearch() {
        savedStateHandle["isSearchActive"] = !isSearchActive.value
        if (!isSearchActive.value) {
            savedStateHandle["searchText"] = ""
        }
    }

    fun deleteNoteById(id: Long) {
        viewModelScope.launch {
            noteDataSource.deleteNoteById(id)
            loadNotes()
        }
    }
}