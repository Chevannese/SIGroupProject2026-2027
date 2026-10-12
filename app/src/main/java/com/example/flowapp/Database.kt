package com.example.flowapp

import android.content.Context
import androidx.room3.*

@Database(entities = [User::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "flow_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
    abstract fun users(): UserDB
}

@Dao interface UserDB {
    @Query("SELECT * FROM user")
    suspend fun getAllUsers(): List<User>

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    suspend fun loadAllByIds(userIds: IntArray): List<User>

    @Query("SELECT * FROM user WHERE trn LIKE :trn LIMIT 1")
    suspend fun getUserByTrn(trn: String): User?

    @Insert
    suspend fun addUser(user: User)
}

@Entity data class User (
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "name") val firstName: String,
    @ColumnInfo(name = "trn") val trn: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "phone") val phone: String,
    @ColumnInfo(name = "password") val password: String
)