package ru.itis.homework

import androidx.room.*

@Dao
interface TaskDao {

    @Query("SELECT id, title, date, isDone FROM Task")
    suspend fun getAll(): List<Task>

    @Query("SELECT * FROM Task WHERE id = :id")
    suspend fun getById(id: Int): Task

    @Insert
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("DELETE FROM Task")
    suspend fun deleteAll()

}
