package com.example.todo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.todo.repository.NotesRepository
import com.example.todo.data.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NotesRepository) : ViewModel() {
    private val searchQuery = MutableLiveData<String>()
    val notesList: LiveData<List<Note>> = searchQuery.switchMap { query ->
        repository.getNotes(query ?: "")
    }
    init {
        searchQuery.value = ""
    }
    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun addNote(text: String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addNote(Note(noteText = text, favorite = false, done = false))
        }
    }

    fun deleteNote(id: Int){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNote(id)
        }
    }

    fun updateNote(note: Note){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateNote(note)
        }
    }
}