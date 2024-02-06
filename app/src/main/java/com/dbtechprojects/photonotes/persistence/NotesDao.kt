package com.dbtechprojects.photonotes.persistence
import androidx.lifecycle.LiveData
import androidx.room.*
import com.dbtechprojects.photonotes.model.Note

@Dao
interface NotesDao {

    // Consulta para obtener una nota por su ID de manera asíncrona
    @Query("SELECT * FROM Notes WHERE notes.id=:id")
    suspend fun getNoteById(id: Int): Note?

    // Consulta para obtener todas las notas ordenadas por fecha de actualización de manera asíncrona
    @Query("SELECT * FROM Notes ORDER BY dateUpdated DESC")
    fun getNotes(): LiveData<List<Note>>

    // Método para eliminar una nota
    @Delete
    fun deleteNote(note: Note): Int

    // Método para actualizar una nota
    @Update
    fun updateNote(note: Note): Int

    // Método para insertar una nueva nota
    @Insert
    fun insertNote(note: Note)
}
