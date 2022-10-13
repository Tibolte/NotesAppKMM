//
//  NoteListScreen.swift
//  iosApp
//
//  Created by Thibault Guégan on 13/10/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

struct NoteListScreen: View {
    
    private var noteDataSource: NoteDataSource
    @StateObject var viewModel = NoteListViewModel(noteDataSource: nil)
    
    @State private var isNoteSelected = false
    @State private var selectedNoteId: Int64? = nil
    
    init(noteDataSource: NoteDataSource) {
        self.noteDataSource = noteDataSource
    }
    
    var body: some View {
        VStack {
            ZStack {
                NavigationLink(destination: NoteDetailScreen(noteDataSource: noteDataSource, noteId: selectedNoteId), isActive: $isNoteSelected) {
                    EmptyView()
                }.hidden()
                HideableSearchTextField<NoteDetailScreen>(
                    onSearchToggled: { viewModel.toggleSearchActive() },
                    destinationProvider: {
                        NoteDetailScreen(noteDataSource: noteDataSource, noteId: selectedNoteId)
                    },
                    isSearchActive: viewModel.isSearchActive,
                    searchText: $viewModel.searchText)
                .frame(maxWidth: .infinity, minHeight: 40)
                .padding()
                
                if (!viewModel.isSearchActive) {
                    Text("All notes")
                        .font(.title2)
                }
            }
            List {
                ForEach(viewModel.filteredNotes, id: \.self.id) { note in
                    Button(action: {
                        isNoteSelected = true
                        selectedNoteId = note.id?.int64Value
                    }) {
                        NoteItem(note: note, onDeleteClicked: {
                            viewModel.deleteNoteById(id: note.id?.int64Value)
                        })
                    }
                }
            }
            .onAppear {
                viewModel.loadNotes()
            }
            .listStyle(.plain)
            .listRowSeparator(.hidden)
        }
        .onAppear {
            viewModel.setNoteDataSource(noteDataSource: noteDataSource)
        }
    }
}

struct NoteListScreen_Previews: PreviewProvider {
    static var previews: some View {
        EmptyView()
    }
}
