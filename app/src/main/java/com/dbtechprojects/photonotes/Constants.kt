package com.dbtechprojects.photonotes

import com.dbtechprojects.photonotes.model.Note

object Constants {
    // Constantes para las rutas de navegación
    const val NAVIGATION_NOTES_LIST = "notesList"
    const val NAVIGATION_NOTES_CREATE = "notesCreated"
    const val NAVIGATION_NOTE_DETAIL = "noteDetail/{noteId}"
    const val NAVIGATION_NOTE_EDIT = "noteEdit/{noteId}"
    const val NAVIGATION_NOTE_ID_Argument = "noteId"

    // Constantes para la base de datos
    const val TABLE_NAME = "Notes"
    const val DATABASE_NAME = "NotesDatabase"

    // Funciones de utilidad para construir rutas de navegación
    fun noteDetailNavigation(noteId: Int) = "noteDetail/$noteId"
    fun noteEditNavigation(noteId: Int) = "noteEdit/$noteId"

    // Función de extensión para manejar casos nulos o listas vacías de notas
    fun List<Note>?.orPlaceHolderList(): List<Note> {
        // Función interna para devolver una lista de marcadores de posición cuando no hay notas
        fun placeHolderList(): List<Note> {
            return listOf(
                Note(
                    id = 0,
                    title = "No se encontraron notas",
                    note = "Crea una nota.",
                    dateUpdated = ""
                )
            )
        }

        // Devuelve la lista actual si no es nula y no está vacía; de lo contrario, devuelve la lista de marcadores de posición
        return if (this != null && this.isNotEmpty()) {
            this
        } else placeHolderList()
    }

    // Objeto de marcador de posición para detalles de nota cuando no se pueden encontrar
    val noteDetailPlaceHolder = Note(
        note = "No se encontraron notas",
        id = 0,
        title = "No se encontraron notas"
    )
}
