package fr.northborders.notesappkmm.android.note_detail

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.northborders.notesappkmm.domain.note.Note
import fr.northborders.notesappkmm.domain.note.NoteDataSource
import fr.northborders.notesappkmm.domain.time.DateTimeUtil
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewViewModel @Inject constructor(
    private val noteDataSource: NoteDataSource,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val noteTitle = savedStateHandle.getStateFlow("noteTitle", "")
    private val isNoteTitleTextFocused =
        savedStateHandle.getStateFlow("isNoteTitleTextFocused", false)
    private val noteContent = savedStateHandle.getStateFlow("noteContent", "")
    private val isNoteContentTextFocused =
        savedStateHandle.getStateFlow("isNoteContentTextFocused", false)
    private val noteColor = savedStateHandle.getStateFlow("noteColor", Note.generateRandomColor())

    val state = combine( // if one of these changes, the block below will be executed
        noteTitle,
        isNoteTitleTextFocused,
        noteContent,
        isNoteContentTextFocused,
        noteColor
    ) { title, isTitleFocused, content, isContentFocused, color ->
        NoteDetailState(
            noteTitle = title,
            isNoteTitleHintVisible = title.isEmpty() && !isTitleFocused,
            noteContent = content,
            isNoteContentHintVisible = content.isEmpty() && !isContentFocused,
            noteColor = color
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NoteDetailState())

    private val _hasNotBeenSaved = MutableStateFlow(false)
    val hasNoteBeenSaved = _hasNotBeenSaved.asStateFlow()

    private var existingNoteId: Long? = null

    init {
        savedStateHandle.get<Long>("noteId")?.let {
            if (it == -1L) {
                return@let // the compose navigation library doesn't support null arguments
            }
            this.existingNoteId = it
            viewModelScope.launch {
                noteDataSource.getNoteById(it)?.let { note ->
                    savedStateHandle["noteTitle"] = note.title
                    savedStateHandle["noteContent"] = note.content
                    savedStateHandle["noteColor"] = note.colorHex
                }
            }
        }
    }

    fun onNoteTitleChanged(title: String) {
        savedStateHandle["noteTitle"] = title
    }

    fun onNoteContentChanged(content: String) {
        savedStateHandle["noteContent"] = content
    }

    fun onNoteTitleFocusedChanged(focus: Boolean) {
        savedStateHandle["isNoteTitleTextFocused"] = focus
    }

    fun onNoteContentFocusedChanged(focus: Boolean) {
        savedStateHandle["isNoteContentTextFocused"] = focus
    }

    fun saveNote() {
        viewModelScope.launch {
            noteDataSource.insertNote(
                Note(
                    id = existingNoteId,
                    title = noteTitle.value,
                    content = noteContent.value,
                    colorHex = noteColor.value,
                    created = DateTimeUtil.now()
                )
            )
            _hasNotBeenSaved.value = true
        }
    }

//    fun onNoteColorChanged(color: Long) {
//        savedStateHandle["noteColor"] = color
//    }
}