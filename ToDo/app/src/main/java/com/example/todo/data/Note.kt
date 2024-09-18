package com.example.todo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var noteText: String,
    var favorite: Boolean,
    var done: Boolean
)
