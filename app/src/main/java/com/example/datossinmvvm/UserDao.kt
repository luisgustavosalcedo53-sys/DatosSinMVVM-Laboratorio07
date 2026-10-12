
package com.example.datossinmvvm

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    // Obtener todos los usuarios
    @Query("SELECT * FROM User")
    suspend fun getAll(): List<User>

    // Agregar un usuario
    @Insert
    suspend fun insert(user: User)

    // Buscar el último usuario registrado
    @Query("SELECT * FROM User ORDER BY uid DESC LIMIT 1")
    suspend fun getLastUser(): User?

    // Eliminar un usuario por su ID
    @Query("DELETE FROM User WHERE uid = :uid")
    suspend fun deleteUserById(uid: Int)
}
