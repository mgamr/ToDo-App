package com.example.todo.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.todo.data.NotesDatabase
import com.example.todo.repository.NotesRepository
import com.example.todo.viewmodel.NoteViewModel

@Composable
fun MainScreen(){
    val context = LocalContext.current
    val db = NotesDatabase.getDatabase(context)
    val repository = NotesRepository(db)
    val viewModel = NoteViewModel(repository)

    NotesView(viewModel)
}