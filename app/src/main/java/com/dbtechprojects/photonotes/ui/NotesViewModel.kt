package com.dbtechprojects.photonotes.ui

import androidx.lifecycle.*
import com.dbtechprojects.photonotes.model.Note
import com.dbtechprojects.photonotes.persistence.NotesDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotesViewModel(
    private val db: NotesDao,
) : ViewModel() {

    // LiveData que contiene la lista de notas, observado por la interfaz de usuario
    val notes: LiveData<List<Note>> = db.getNotes()

    // Función para eliminar una nota en un hilo de fondo
    fun deleteNotes(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            db.deleteNote(note)
        }
    }

    // Función para actualizar una nota en un hilo de fondo
    fun updateNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            db.updateNote(note)
        }
    }

    // Función para crear una nueva nota en un hilo de fondo
    fun createNote(title: String, note: String, image: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            db.insertNote(Note(title = title, note = note, imageUri = image))
        }
    }

    // Función suspendida para obtener una nota por su ID en un hilo de fondo
    suspend fun getNote(noteId: Int): Note? {
        return db.getNoteById(noteId)
    }
}

// Factoría para crear instancias de NotesViewModel
class NotesViewModelFactory(
    private val db: NotesDao,
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NotesViewModel(
            db = db,
        ) as T
    }
}
