package com.dbtechprojects.photonotes
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.room.Room
import com.dbtechprojects.photonotes.persistence.NotesDao
import com.dbtechprojects.photonotes.persistence.NotesDatabase

class PhotoNotesApp : Application() {

    private var db: NotesDatabase? = null

    // Inicializar la instancia de la aplicación
    init {
        instance = this
    }

    // Método privado para obtener la instancia de la base de datos
    private fun getDb(): NotesDatabase {
        return if (db != null) {
            db!!
        } else {
            db = Room.databaseBuilder(
                instance!!.applicationContext,
                NotesDatabase::class.java, Constants.DATABASE_NAME
            ).fallbackToDestructiveMigration() // remove in prod
                .build()
            db!!
        }
    }

    // Métodos y propiedades de la clase compañera (companion object)
    companion object {
        private var instance: PhotoNotesApp? = null

        // Método estático para obtener el objeto DAO (Data Access Object) de la base de datos
        fun getDao(): NotesDao {
            return instance!!.getDb().NotesDao()
        }

        // Método estático para obtener los permisos de URI persistibles
        fun getUriPermission(uri: Uri) {
            instance!!.applicationContext.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
    }
}
