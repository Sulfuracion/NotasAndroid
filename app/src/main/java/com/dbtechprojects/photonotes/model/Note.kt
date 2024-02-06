package com.dbtechprojects.photonotes.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.dbtechprojects.photonotes.Constants
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity(tableName = Constants.TABLE_NAME, indices = [Index(value = ["id"], unique = true)])
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "note") val note: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "dateUpdated") val dateUpdated: String = getDateCreated(),
    @ColumnInfo(name = "imageUri") val imageUri: String? = null
)

// Función que devuelve la fecha y hora actual formateada
fun getDateCreated(): String {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
}

// Extensión de la clase Note que devuelve la fecha de la nota
fun Note.getDay(): String {
    // Si la fecha de actualización está vacía, devuelve una cadena vacía
    if (this.dateUpdated.isEmpty()) return ""

    // Formatea la fecha de actualización en el formato deseado
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return LocalDateTime.parse(this.dateUpdated, formatter).toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}
