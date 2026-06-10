package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// --- 1. Entities ---

@Entity(tableName = "student_stats")
data class StudentStats(
    @PrimaryKey val id: Int = 1,
    val hoursLearned: Float = 0f,
    val streak: Int = 0,
    val completionPercent: Float = 0f,
    val examGoal: String = "JEE Mains",
    val schoolCollege: String = "St. Xavier's Academy",
    val classLevel: String = "Class 12"
)

@Entity(tableName = "doubts")
data class Doubt(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val questionText: String,
    val imageUrl: String? = null,
    val responseType: String, // "AI", "Teacher", "Community"
    val status: String = "Pending", // "Pending", "Resolved"
    val responseBody: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "downloads")
data class DownloadItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val chapterName: String,
    val type: String, // "video", "notes", "quiz"
    val fileSize: String,
    val lastAccessed: String = "07 Jun 2026"
)

@Entity(tableName = "quiz_results")
data class QuizResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val quizTitle: String,
    val score: Int,
    val totalQuestions: Int,
    val percentage: Float,
    val weakAreas: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "community_messages")
data class CommunityMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val roomName: String,
    val senderName: String,
    val senderRole: String, // "Student" or "Teacher"
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis()
)

// --- 2. DAO ---

@Dao
interface AppDao {
    // Student Stats
    @Query("SELECT * FROM student_stats WHERE id = 1 LIMIT 1")
    fun getStatsFlow(): Flow<StudentStats?>

    @Query("SELECT * FROM student_stats WHERE id = 1 LIMIT 1")
    suspend fun getStats(): StudentStats?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveStats(stats: StudentStats)

    // Doubts
    @Query("SELECT * FROM doubts ORDER BY timestamp DESC")
    fun getAllDoubtsFlow(): Flow<List<Doubt>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoubt(doubt: Doubt)

    @Query("UPDATE doubts SET status = :status, responseBody = :response WHERE id = :id")
    suspend fun updateDoubtResponse(id: Int, status: String, response: String)

    // Downloads
    @Query("SELECT * FROM downloads ORDER BY id DESC")
    fun getAllDownloadsFlow(): Flow<List<DownloadItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(downloadItem: DownloadItem)

    @Query("DELETE FROM downloads WHERE id = :id")
    suspend fun deleteDownload(id: Int)

    // Quiz Results
    @Query("SELECT * FROM quiz_results ORDER BY timestamp DESC")
    fun getAllQuizResultsFlow(): Flow<List<QuizResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizResult(result: QuizResult)

    // Community Messages
    @Query("SELECT * FROM community_messages WHERE roomName = :roomName ORDER BY timestamp ASC")
    fun getMessagesForRoomFlow(roomName: String): Flow<List<CommunityMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunityMessage(message: CommunityMessage)
}

// --- 3. Database ---

@Database(
    entities = [
        StudentStats::class,
        Doubt::class,
        DownloadItem::class,
        QuizResult::class,
        CommunityMessage::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "niche_app_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// --- 4. Repository Pattern ---

class AppRepository(private val appDao: AppDao) {
    val statsFlow: Flow<StudentStats?> = appDao.getStatsFlow()
    val allDoubtsFlow: Flow<List<Doubt>> = appDao.getAllDoubtsFlow()
    val allDownloadsFlow: Flow<List<DownloadItem>> = appDao.getAllDownloadsFlow()
    val allQuizResultsFlow: Flow<List<QuizResult>> = appDao.getAllQuizResultsFlow()

    suspend fun getStats(): StudentStats {
        return appDao.getStats() ?: StudentStats()
    }

    suspend fun updateStats(stats: StudentStats) {
        appDao.saveStats(stats)
    }

    suspend fun insertDoubt(doubt: Doubt) {
        appDao.insertDoubt(doubt)
    }

    suspend fun resolveDoubt(id: Int, response: String) {
        appDao.updateDoubtResponse(id, "Resolved", response)
    }

    suspend fun addDownload(downloadItem: DownloadItem) {
        appDao.insertDownload(downloadItem)
    }

    suspend fun removeDownload(id: Int) {
        appDao.deleteDownload(id)
    }

    suspend fun addQuizResult(result: QuizResult) {
        appDao.insertQuizResult(result)
        // Also update student hours/completion slightly to simulate progress
        val current = getStats()
        val newHours = current.hoursLearned + 0.5f
        val currentPercent = current.completionPercent
        val newPercent = (currentPercent + 2f).coerceAtMost(100f)
        appDao.saveStats(current.copy(hoursLearned = newHours, completionPercent = newPercent))
    }

    fun getMessagesForRoom(roomName: String): Flow<List<CommunityMessage>> {
        return appDao.getMessagesForRoomFlow(roomName)
    }

    suspend fun addCommunityMessage(message: CommunityMessage) {
        appDao.insertCommunityMessage(message)
    }
}
