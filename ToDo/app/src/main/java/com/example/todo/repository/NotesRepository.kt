package com.example.todo.repository

import androidx.lifecycle.LiveData
import com.example.todo.data.Note
import com.example.todo.data.NotesDatabase

class NotesRepository(private val notesDB: NotesDatabase) {
    fun addNote(note: Note){
        notesDB.notesDao().addNote(note)
    }

    fun deleteNote(id: Int){
        notesDB.notesDao().deleteNote(id)
    }

    fun updateNote(note: Note){
        notesDB.notesDao().updateNote(note)
    }

    fun getNotes(s: String): LiveData<List<Note>>? {
        return notesDB.notesDao().getNotes(s)
    }
}