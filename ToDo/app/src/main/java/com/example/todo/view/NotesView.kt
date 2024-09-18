package com.example.todo.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todo.R
import com.example.todo.data.Note
import com.example.todo.viewmodel.NoteViewModel
import kotlinx.coroutines.delay

@Composable
fun NotesView(viewModel: NoteViewModel){
    val notesList by viewModel.notesList.observeAsState()
    var showDialog by remember { mutableStateOf(false) }
    var newNoteText by remember { mutableStateOf("") }
    var searchInput by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Add Note") },
            text = {
                TextField(
                    value = newNoteText,
                    onValueChange = { newNoteText = it },
                    label = { Text(text = "Note Content") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newNoteText.isNotBlank()) {
                            viewModel.addNote(newNoteText)
                            newNoteText = ""
                            showDialog = false
                        }
                    }
                ) {
                    Text(text = "Add")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color.Transparent,
                actions = {},
                floatingActionButton = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .padding(start = 16.dp)
                            .padding(end = 4.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            FloatingActionButton(
                                onClick = { showDialog = true },
                                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = null)
                            }
                        }
                    }
                }
            )
        },
    ) { innerPadding ->

        Column(modifier = Modifier
            .fillMaxHeight()
            .padding(innerPadding)){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BottomAppBarDefaults.bottomAppBarFabColor)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "To Do Notes:",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Medium)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(BottomAppBarDefaults.bottomAppBarFabColor)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ){
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = searchInput,
                        onValueChange = {
                            searchInput = it
                            viewModel.setSearchQuery(it) },
                        textStyle = LocalTextStyle.current.copy(color = Color.Black),
                        shape = RoundedCornerShape(percent = 50),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent)
                    )
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }


            notesList?.let {
                LazyColumn(
                    content = {
                        itemsIndexed(it, key = { _, note -> note.id }){ _: Int, noteItem: Note ->

                            SwipeToDeleteContainer(
                                item = noteItem,
                                onDelete = { viewModel.deleteNote(noteItem.id) }
                            ) { note ->
                                NoteCard(note = note, viewModel = viewModel)
                            }
                        }
                    }
                )
            }?: Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "No items yet",
                fontSize = 16.sp
            )
        }
    }
}


@Composable
fun NoteCard(note: Note, viewModel: NoteViewModel){
    var checkState by remember { mutableStateOf(false) }
    var favoriteState by remember { mutableStateOf(note.favorite) }
    var updateDialog by remember { mutableStateOf(false) }
    var updateNoteText by remember { mutableStateOf(note.noteText) }

    val cardColor = if (checkState) BottomAppBarDefaults.bottomAppBarFabColor else MaterialTheme.colorScheme.background
    val textStyle = if (checkState) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle(textDecoration = TextDecoration.None)

    if (updateDialog) {
        AlertDialog(
            onDismissRequest = { updateDialog = false },
            title = { Text(text = "Update Note") },
            text = {
                TextField(
                    value = updateNoteText,
                    onValueChange = { updateNoteText = it },
                    label = { Text(text = "Note Content") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (updateNoteText.isNotBlank()) {
                            note.noteText = updateNoteText
                            viewModel.updateNote(note)
                            updateDialog = false
                        }
                    }
                ) {
                    Text(text = "Update")
                }
            },
            dismissButton = {
                Button(onClick = { updateDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(8.dp))
            .background(cardColor)
            .padding(8.dp)
            .clickable { updateDialog = true },
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checkState,
                onCheckedChange = {
                    checkState = it
                    if (checkState) {
                        note.done = true
                        viewModel.updateNote(note)
                    } else {
                        note.done = false
                        viewModel.updateNote(note)
                    }
                }
            )
            Text(
                text = note.noteText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(0.9f),
                style = textStyle
            )
            if (favoriteState) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    modifier = Modifier
                        .weight(0.1f)
                        .clickable {
                            favoriteState = !favoriteState
                            note.favorite = favoriteState
                            viewModel.updateNote(note)
                        }
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.star),
                    contentDescription = null,
                    modifier = Modifier
                        .weight(0.1f)
                        .clickable {
                            favoriteState = !favoriteState
                            note.favorite = favoriteState
                            viewModel.updateNote(note)
                        }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SwipeToDeleteContainer(
    item: T,
    onDelete: (T) -> Unit,
    animationDuration: Int = 500,
    content: @Composable (T) -> Unit
) {
    var isRemoved by remember {
        mutableStateOf(false)
    }
    val state = rememberDismissState(
        confirmValueChange = { value ->
            if (value == DismissValue.DismissedToStart) {
                isRemoved = true
                true
            } else {
                false
            }
        }
    )

    LaunchedEffect(key1 = isRemoved) {
        if(isRemoved) {
            delay(animationDuration.toLong())
            onDelete(item)
        }
    }

    AnimatedVisibility(
        visible = !isRemoved,
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = animationDuration),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        SwipeToDismiss(
            state = state,
            background = {
                DeleteBackground(swipeDismissState = state)
            },
            dismissContent = { content(item) },
            directions = setOf(DismissDirection.EndToStart)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteBackground(
    swipeDismissState: DismissState
) {
    val color = if (swipeDismissState.dismissDirection == DismissDirection.EndToStart) {
        Color.Red
    } else Color.Transparent

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(8.dp))
            .background(color)
            .padding(8.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = null,
            tint = Color.White
        )
    }
}