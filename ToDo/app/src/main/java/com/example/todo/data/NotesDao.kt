package com.example.todo.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface NotesDao {
    @Query("SELECT * FROM Note WHERE (:text = '' OR noteText LIKE '%' || :text || '%') ORDER BY CASE WHEN favorite = 1 THEN 0 WHEN done = 0 THEN 1 ELSE 2 END")
    fun getNotes(text: String = "") : LiveData<List<Note>>

    @Insert
    fun addNote(note: Note)

    @Query("DELETE FROM Note WHERE id = :id")
    fun deleteNote(id: Int)

    @Update
    fun updateNote(note: Note)
}