//
//  NoteListViewModel.swift
//  iosApp
//
//  Created by Thibault Guégan on 13/10/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import shared

extension NoteListScreen {
    @MainActor class NoteListViewModel: ObservableObject {
        private var noteDataSource: NoteDataSource? = nil
        
        private let searchNotes = SearchNotes()
        
        private var notes = [Note]()
        @Published private(set) var filteredNotes = [Note]()
        @Published var searchText = "" {
            didSet {
                self.filteredNotes = searchNotes.execute(notes: notes, query: searchText)
            }
        }
        @Published private(set) var isSearchActive = false
        
        init(noteDataSource: NoteDataSource? = nil) {
            self.noteDataSource = noteDataSource
            
            
        }
        
        func setNoteDataSource(noteDataSource: NoteDataSource) {
            self.noteDataSource = noteDataSource
            
//            noteDataSource.insertNote(note: Note(id: nil, title: "Title", content: "Content", colorHex: 0xFF456, created: DateTimeUtil().now()), completionHandler: { error in
//
//            })
        }
        
        func loadNotes() {
            noteDataSource?.getAllNotes(completionHandler: {notes, error in
                self.notes = notes ?? []
                self.filteredNotes = self.notes
            })
        }
        
        func deleteNoteById(id: Int64?) {
            if id != nil {
                noteDataSource?.deleteNoteById(id: id!, completionHandler: { error in
                    self.loadNotes()
                })
            }
        }
        
        func toggleSearchActive() {
            isSearchActive = !isSearchActive
            if !isSearchActive {
                searchText = ""
            }
        }
     }
}
