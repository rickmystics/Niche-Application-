package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

// --- Domain Models for Courses ---

data class Subject(
    val id: String,
    val name: String,
    val icon: String, // Emoji or material code identifier
    val themeColor: Long, // Color Hex code
    val chapters: List<Chapter>
)

data class Chapter(
    val id: String,
    val name: String,
    val videoCount: Int,
    val notesCount: Int,
    val hasQuiz: Boolean,
    val videos: List<Video>,
    val notes: List<Note>,
    val quizQuestions: List<QuizQuestion>,
    val assignments: List<String>
)

data class Video(
    val id: String,
    val title: String,
    val url: String,
    val duration: String,
    val teacherName: String,
    val thumbnail: String? = null
)

data class Note(
    val id: String,
    val title: String,
    val fileSize: String,
    val contentSnippet: String
)

data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String,
    val subTopic: String
)

// --- ViewModel ---

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = AppRepository(db.appDao())

    // App User States
    val studentStats = repository.statsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StudentStats()
    )

    val doubts = repository.allDoubtsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val downloads = repository.allDownloadsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val quizResults = repository.allQuizResultsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Current Navigation Route / Screen state
    private val _currentRoute = MutableStateFlow("splash")
    val currentRoute: StateFlow<String> = _currentRoute.asStateFlow()

    // Role state
    private val _userRole = MutableStateFlow<String?>("Student") // "Student", "Teacher"
    val userRole: StateFlow<String?> = _userRole.asStateFlow()

    // Logged-in state
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // Profile Details
    val userName = MutableStateFlow("Rajesh Kumar")
    val userEmail = MutableStateFlow("rajesh.kumar@gmail.com")
    val classSelected = MutableStateFlow("Class 12 - Science")
    val schoolEntered = MutableStateFlow("St. Xavier's Academy")
    val examGoalSelected = MutableStateFlow("JEE Mains")

    // Teacher Profile Details
    val teacherName = MutableStateFlow("HC Verma")
    val teacherSubject = MutableStateFlow("Physics")
    val teacherExpt = MutableStateFlow("15 Years")
    val teacherQual = MutableStateFlow("PhD, IIT Kanpur")
    val isTeacherVerified = MutableStateFlow(false)

    // Search query in student dashboard
    val searchQuery = MutableStateFlow("")

    // Active course selections
    private val _selectedSubject = MutableStateFlow<Subject?>(null)
    val selectedSubject: StateFlow<Subject?> = _selectedSubject.asStateFlow()

    private val _selectedChapter = MutableStateFlow<Chapter?>(null)
    val selectedChapter: StateFlow<Chapter?> = _selectedChapter.asStateFlow()

    private val _activeVideo = MutableStateFlow<Video?>(null)
    val activeVideo: StateFlow<Video?> = _activeVideo.asStateFlow()

    private val _activeNote = MutableStateFlow<Note?>(null)
    val activeNote: StateFlow<Note?> = _activeNote.asStateFlow()

    // AI summary cache for the active note
    private val _noteSummary = MutableStateFlow("")
    val noteSummary: StateFlow<String> = _noteSummary.asStateFlow()
    val isSummaryLoading = MutableStateFlow(false)

    // Active room for community section
    val activeCommunityRoom = MutableStateFlow("JEE Aspirants")

    // Active Quiz state
    private val _activeQuizQuestions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val activeQuizQuestions: StateFlow<List<QuizQuestion>> = _activeQuizQuestions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _userAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val userAnswers: StateFlow<Map<Int, Int>> = _userAnswers.asStateFlow()

    private val _quizSecondsRemaining = MutableStateFlow(120) // 2 minutes
    val quizSecondsRemaining: StateFlow<Int> = _quizSecondsRemaining.asStateFlow()

    private val _quizCompletedResult = MutableStateFlow<QuizResult?>(null)
    val quizCompletedResult: StateFlow<QuizResult?> = _quizCompletedResult.asStateFlow()

    val isQuizAiLoading = MutableStateFlow(false)
    val quizAiRecommendations = MutableStateFlow("")

    // List of custom created courses (for teachers)
    val teacherCourses = MutableStateFlow<List<Subject>>(emptyList())

    // Static Prepopulated Dataset
    val subjectsList = mutableListOf<Subject>()

    init {
        loadDefaultDataset()
        teacherCourses.value = subjectsList.take(2)
        // Initialize stats
        viewModelScope.launch {
            val s = repository.getStats()
            if (s.streak == 0) {
                repository.updateStats(s.copy(streak = 5, hoursLearned = 12.5f, completionPercent = 38f))
            }
        }
    }

    fun navigateTo(route: String) {
        _currentRoute.value = route
    }

    fun setRole(role: String) {
        _userRole.value = role
    }

    fun logIn() {
        _isLoggedIn.value = true
        if (_userRole.value == "Teacher") {
            _currentRoute.value = "teacher_dashboard"
        } else {
            _currentRoute.value = "student_dashboard"
        }
    }

    fun logOut() {
        _isLoggedIn.value = false
        _currentRoute.value = "role_selection"
    }

    fun selectSubject(subject: Subject) {
        _selectedSubject.value = subject
        _currentRoute.value = "subject_detail"
    }

    fun selectChapter(chapter: Chapter) {
        _selectedChapter.value = chapter
        _currentRoute.value = "chapter_detail"
    }

    fun selectVideo(video: Video) {
        _activeVideo.value = video
        _currentRoute.value = "video_player"
    }

    fun selectNote(note: Note) {
        _activeNote.value = note
        _noteSummary.value = ""
        _currentRoute.value = "notes_viewer"
        generateNoteSummary(note)
    }

    fun startQuiz(chapter: Chapter) {
        _selectedChapter.value = chapter
        _activeQuizQuestions.value = chapter.quizQuestions
        _currentQuestionIndex.value = 0
        _userAnswers.value = emptyMap()
        _quizSecondsRemaining.value = 120
        _quizCompletedResult.value = null
        quizAiRecommendations.value = ""
        _currentRoute.value = "quiz_screen"

        // Background countdown timer
        viewModelScope.launch {
            while (_quizSecondsRemaining.value > 0 && _quizCompletedResult.value == null) {
                kotlinx.coroutines.delay(1000)
                if (_quizCompletedResult.value == null) {
                    _quizSecondsRemaining.value = _quizSecondsRemaining.value - 1
                }
            }
            if (_quizCompletedResult.value == null) {
                submitQuiz()
            }
        }
    }

    // --- Student Actions ---

    fun answerQuizQuestion(questionId: Int, optionIndex: Int) {
        val updated = _userAnswers.value.toMutableMap()
        updated[questionId] = optionIndex
        _userAnswers.value = updated
    }

    fun nextQuizQuestion() {
        if (_currentQuestionIndex.value < _activeQuizQuestions.value.size - 1) {
            _currentQuestionIndex.value += 1
        }
    }

    fun prevQuizQuestion() {
        if (_currentQuestionIndex.value > 0) {
            _currentQuestionIndex.value -= 1
        }
    }

    fun submitQuiz() {
        viewModelScope.launch {
            val questions = _activeQuizQuestions.value
            if (questions.isEmpty()) return@launch

            val answersByQuestionIndex = _userAnswers.value
            var correctCount = 0
            val weakCategoriesMap = mutableMapOf<String, Int>()

            questions.forEach { q ->
                val userAnswer = answersByQuestionIndex[q.id]
                if (userAnswer == q.correctAnswerIndex) {
                    correctCount++
                } else {
                    // Track category for failure analysis
                    weakCategoriesMap[q.subTopic] = (weakCategoriesMap[q.subTopic] ?: 0) + 1
                }
            }

            val percent = (correctCount.toFloat() / questions.size) * 100f
            val weakString = if (weakCategoriesMap.isEmpty()) "None! Execellent Performance." else weakCategoriesMap.keys.joinToString(", ")

            val result = QuizResult(
                quizTitle = "${_selectedChapter.value?.name ?: "Chapter"} Quiz",
                score = correctCount,
                totalQuestions = questions.size,
                percentage = percent,
                weakAreas = weakString
            )

            _quizCompletedResult.value = result
            repository.addQuizResult(result)

            // Trigger AI Explanation/Remediation based on weak categories
            if (weakCategoriesMap.isNotEmpty()) {
                generateQuizAiRecommendations(result, weakCategoriesMap.keys.toList())
            } else {
                quizAiRecommendations.value = "Perfect score! Outstanding mastery of all topics. Proceed to the next chapter video."
            }
        }
    }

    private fun generateQuizAiRecommendations(result: QuizResult, weakTopics: List<String>) {
        viewModelScope.launch {
            isQuizAiLoading.value = true
            val prompt = """
                The student took the quiz '${result.quizTitle}' and scored ${result.score}/${result.totalQuestions} (${result.percentage}%).
                They struggled on these specific sub-topics: ${weakTopics.joinToString(", ")}.
                Provide a short 3-sentence expert tutor review advising on:
                1. A brief explanation of why these concepts might be confusing.
                2. Explicit actionable revision tips for JEE/NEET prep.
                3. Concrete recommendation on what to review. Keep it encouraging!
            """.trimIndent()

            val aiResponse = GeminiClient.getAiResponse(
                prompt = prompt,
                systemPrompt = "You are NICHE, an elite AI tutor for competitive Indian exams like JEE/NEET."
            )
            quizAiRecommendations.value = aiResponse
            isQuizAiLoading.value = false
        }
    }

    private fun generateNoteSummary(note: Note) {
        viewModelScope.launch {
            isSummaryLoading.value = true
            val prompt = """
                Generate a concise, outstanding 4-bullet Quick Summary of this chapter study material:
                Title: ${note.title}
                Content Fragment: ${note.contentSnippet}
                And append a '🔑 Pro JEE/NEET Highlight' in a brief separate sentence.
            """.trimIndent()

            val response = GeminiClient.getAiResponse(
                prompt = prompt,
                systemPrompt = "You are NICHE AI Study Assistant. Summarize note packets with exceptional precision and clear outline tags."
            )
            _noteSummary.value = response
            isSummaryLoading.value = false
        }
    }

    // Doubt Handling
    val isDoubtAiLoading = MutableStateFlow(false)

    fun submitDoubt(text: String, mode: String) {
        viewModelScope.launch {
            val initialDoubt = Doubt(
                questionText = text,
                responseType = mode,
                status = "Pending"
            )
            repository.insertDoubt(initialDoubt)

            // Retrieve all doubts to get the ID of the newly saved one
            val allCurrent = doubts.value
            // Let's pretend or find the last doubt
            val targetId = allCurrent.firstOrNull()?.id ?: 0

            if (mode == "AI Explanation") {
                isDoubtAiLoading.value = true
                val prompt = """
                    I have a doubt regarding my studies:
                    "$text"
                    Please provide an absolute clear, step-by-step breakdown explaining the core concepts behind this doubt.
                    Structure it using easy-to-read headers:
                    - Concept Overview
                    - Step-by-Step Resolution
                    - Key Formula or Takeaway
                """.trimIndent()

                val aiResponse = GeminiClient.getAiResponse(
                    prompt = prompt,
                    systemPrompt = "You are NICHE AI Solver, a brilliant tutor specialized in clearing scientific and competitive exam doubts."
                )

                repository.resolveDoubt(targetId, aiResponse)
                isDoubtAiLoading.value = false
            } else if (mode == "Ask Teacher") {
                // Mock teacher answer after a small delay
                kotlinx.coroutines.delay(2000)
                val mockAnswers = listOf(
                    "This is an excellent question! Remember that according to Newton's Second Law, force is the rate of change of momentum. Make sure you don't confuse speed with velocity when dealing with circular motions.",
                    "Hi Rajesh! Excellent query. For chemical kinetics, the rate constant's units change depending on the reaction order. Keep an eye on standard concentrations.",
                    "Great observation. In computer science, Room uses SQLite databases behind the scenes, and the DAO acts as a safe translation layer to avoid raw SQL mistakes."
                )
                repository.resolveDoubt(targetId, mockAnswers.random())
            } else {
                // Ask Community
                kotlinx.coroutines.delay(2000)
                val communityMock = "Community Response: Hey, I had the same doubt last week! Our physics teacher verified that yes, you should assume friction is negligible unless friction coefficients are explicitly mentioned."
                repository.resolveDoubt(targetId, communityMock)
            }
        }
    }

    // Community Chat
    fun getMessagesForRoom(roomName: String): Flow<List<CommunityMessage>> {
        return repository.getMessagesForRoom(roomName)
    }

    fun postCommunityMessage(text: String) {
        viewModelScope.launch {
            val currentRole = _userRole.value ?: "Student"
            val senderName = if (currentRole == "Student") userName.value else teacherName.value
            val message = CommunityMessage(
                roomName = activeCommunityRoom.value,
                senderName = senderName,
                senderRole = currentRole,
                messageText = text
            )
            repository.addCommunityMessage(message)
        }
    }

    // Downloads
    fun downloadChapterContent(chapter: Chapter, type: String, title: String) {
        viewModelScope.launch {
            val item = DownloadItem(
                title = title,
                chapterName = chapter.name,
                type = type,
                fileSize = listOf("12.4 MB", "18.2 MB", "4.6 MB", "22.5 MB").random()
            )
            repository.addDownload(item)
        }
    }

    fun deleteDownloadedItem(id: Int) {
        viewModelScope.launch {
            repository.removeDownload(id)
        }
    }

    // Helper functions for mock updates
    fun updateGoal(goal: String) {
        examGoalSelected.value = goal
        viewModelScope.launch {
            val s = repository.getStats()
            repository.updateStats(s.copy(examGoal = goal))
        }
    }

    fun addStudyHours(hours: Float) {
        viewModelScope.launch {
            val s = repository.getStats()
            repository.updateStats(s.copy(hoursLearned = s.hoursLearned + hours))
        }
    }

    // --- Teacher Actions ---

    val isTeacherCreating = MutableStateFlow(false)

    fun createTeacherCourse(name: String, desc: String, subject: String, classLvl: String, price: String) {
        viewModelScope.launch {
            isTeacherCreating.value = true
            kotlinx.coroutines.delay(1000)

            val newSubject = Subject(
                id = "sub_${System.currentTimeMillis()}",
                name = name,
                icon = "📚",
                themeColor = 0xFF7C3AED, // Custom teacher color
                chapters = listOf(
                    Chapter(
                        id = "ch_new_1",
                        name = "Module 1 - Starter Introduction",
                        videoCount = 1,
                        notesCount = 1,
                        hasQuiz = true,
                        videos = listOf(
                            Video("v_new_1", "Orientation Lesson", "", "15:00", teacherName.value)
                        ),
                        notes = listOf(
                            Note("n_new_1", "Orientation Notes", "2.1 MB", "Welcome to the course. Here we list our targets.")
                        ),
                        quizQuestions = listOf(
                            QuizQuestion(
                                id = 9991,
                                question = "What is the primary target of this teacher module?",
                                options = listOf("Learn concepts", "Complete syllabus", "Improve grade profile", "All of the above"),
                                correctAnswerIndex = 3,
                                explanation = "All the mentioned aspects are primary goals.",
                                subTopic = "Syllabus Targets"
                            )
                        ),
                        assignments = listOf("Intro Handout Homework")
                    )
                )
            )
            subjectsList.add(newSubject)
            teacherCourses.value = teacherCourses.value + newSubject
            isTeacherCreating.value = false
            _currentRoute.value = "teacher_dashboard"
        }
    }

    val isTeacherUploading = MutableStateFlow(false)

    fun uploadTeacherContent(chapterName: String, topicName: String, type: String) {
        viewModelScope.launch {
            isTeacherUploading.value = true
            kotlinx.coroutines.delay(1000)

            // Add mock files to the first chapter of our first course
            val firstCourse = subjectsList.firstOrNull()
            if (firstCourse != null) {
                val firstChapter = firstCourse.chapters.firstOrNull()
                if (firstChapter != null) {
                    val updatedVideos = firstChapter.videos.toMutableList()
                    val updatedNotes = firstChapter.notes.toMutableList()

                    if (type == "Video") {
                        updatedVideos.add(
                            Video(
                                id = "v_" + System.currentTimeMillis(),
                                title = topicName,
                                url = "",
                                duration = "22:45",
                                teacherName = teacherName.value
                            )
                        )
                    } else if (type == "Notes") {
                        updatedNotes.add(
                            Note(
                                id = "n_" + System.currentTimeMillis(),
                                title = topicName,
                                fileSize = "3.8 MB",
                                contentSnippet = "Teacher uploaded detailed PDF content covering $topicName core equations and formula derivatives."
                            )
                        )
                    }
                }
            }

            isTeacherUploading.value = false
            _currentRoute.value = "teacher_dashboard"
        }
    }

    // Teacher Analytics Assistant
    val isAnalyticsAiLoading = MutableStateFlow(false)
    val analyticsAiInsights = MutableStateFlow("Tap 'Generate AI Insights Report' above to run deep cohort evaluations.")

    fun loadTeacherAnalyticsReport() {
        viewModelScope.launch {
            isAnalyticsAiLoading.value = true
            val prompt = """
                Analyze the mock student cohorts performance:
                - Chapter 1: Video Watch-time: 85%, Quiz Avg: 78%
                - Chapter 2: Video Watch-time: 42%, Quiz Avg: 51% (Weak Area: Organic Derivatives)
                - Chapter 3: Video Watch-time: 91%, Quiz Avg: 86%
                Identify key educational drop-off points, provide 2 targeted recommendations for the teacher, and draft a quick outline for a corrective live class.
            """.trimIndent()

            val aiResponse = GeminiClient.getAiResponse(
                prompt = prompt,
                systemPrompt = "You are NICHE AI Educator Assistant, providing elite recommendations to school and exam preparatory teachers."
            )
            analyticsAiInsights.value = aiResponse
            isAnalyticsAiLoading.value = false
        }
    }


    // --- Core Dataset Loader ---

    private fun loadDefaultDataset() {
        // --- 1. Mathematics ---
        val mathChapters = listOf(
            Chapter(
                id = "math_ch1",
                name = "Limits, Continuity & Differentiability",
                videoCount = 2,
                notesCount = 1,
                hasQuiz = true,
                videos = listOf(
                    Video("math_v1", "Introduction to Limits and L'Hopital's Rule", "", "24:12", "NV Sir (IIT Delhi)", "math_thumb1"),
                    Video("math_v2", "Continuity & Intermediate Value Theorem", "", "35:40", "NV Sir (IIT Delhi)", "math_thumb2")
                ),
                notes = listOf(
                    Note("math_n1", "Limits & Continuity Formula PDF", "4.8 MB", "Limits describe the behavior of a function near a point, rather than at that point. Real values, left-hand limits (LHL) and right-hand limits (RHL) must match for continuity. If LHL = RHL = f(c), f is continuous at c.")
                ),
                quizQuestions = listOf(
                    QuizQuestion(
                        id = 1001,
                        question = "Evaluate the limit as x approaches 0 of sin(x) / x.",
                        options = listOf("0", "1", "Infinity", "Undefined"),
                        correctAnswerIndex = 1,
                        explanation = "This is a standard fundamental limit derived using Taylor series expansion or Squeeze theorem: limit(x->0) sin(x)/x = 1.",
                        subTopic = "Fundamental Limits"
                    ),
                    QuizQuestion(
                        id = 1002,
                        question = "What condition defines intermediate continuity for a function f(x) at point x = a?",
                        options = listOf(
                            "f(a) is defined, the limit as x approaches a of f(x) exists, and they are equal",
                            "f(a) is non-zero",
                            "The derivative f'(a) exists",
                            "The function value rises and falls equally"
                        ),
                        correctAnswerIndex = 0,
                        explanation = "A function is continuous at x = a if: 1. f(a) is defined. 2. limit(x->a) f(x) exists. 3. limit equals f(a).",
                        subTopic = "Continuity Criteria"
                    )
                ),
                assignments = listOf("Limits and Differentiability Practice Problem Sheet 1", "L'Hopital Form Homework Sheet")
            ),
            Chapter(
                id = "math_ch2",
                name = "Calculus of Integrals",
                videoCount = 1,
                notesCount = 1,
                hasQuiz = false,
                videos = listOf(
                    Video("math_v3", "Integration by Parts & Special Substitutions", "", "42:15", "NV Sir (IIT Delhi)")
                ),
                notes = listOf(
                    Note("math_n2", "Indefinite & Definite Integrals Cheat Sheet", "3.2 MB", "Integration is the inverse of differentiation. Formula for integration of sec(x) is ln|sec(x)+tan(x)|+C. Use substitution to simplify tricky logarithmic or rational components.")
                ),
                quizQuestions = emptyList(),
                assignments = listOf("Integral Solvers Assignment pack")
            )
        )

        val subjectMath = Subject(
            id = "subject_math",
            name = "Mathematics",
            icon = "📐",
            themeColor = 0xFF2563EB, // Deep Blue
            chapters = mathChapters
        )
        subjectsList.add(subjectMath)

        // --- 2. Physics ---
        val physicsChapters = listOf(
            Chapter(
                id = "phy_ch1",
                name = "Electrostatics & Electric Fields",
                videoCount = 2,
                notesCount = 1,
                hasQuiz = true,
                videos = listOf(
                    Video("phy_v1", "Coulomb's Law & Electric Dipole Equations", "", "28:50", "HC Verma Sir", "phy_thumb1"),
                    Video("phy_v2", "Gauss Law and Concentric Shell Potentials", "", "31:18", "HC Verma Sir", "phy_thumb2")
                ),
                notes = listOf(
                    Note("phy_n1", "Electrostatics Core Revision Notes", "5.1 MB", "Coulomb's law defines electrostatic force F = k * (q1 * q2) / r^2. Gauss's Law states that the net electric flux through any closed surface is equal to the net charge enclosed divided by permittivity ε₀.")
                ),
                quizQuestions = listOf(
                    QuizQuestion(
                        id = 2001,
                        question = "What is the net electric field inside a hollow conducting sphere of radius R and charge Q?",
                        options = listOf("Q / (4πε₀R²)", "Zero", "Q / R", "Infinity"),
                        correctAnswerIndex = 1,
                        explanation = "By Gauss's Law, the enclosed charge of a gaussian surface inside a conductor is zero. Hence, the electric field is zero inside.",
                        subTopic = "Gauss Law & Conductors"
                    ),
                    QuizQuestion(
                        id = 2002,
                        question = "The electric file E at distance r from a long infinitely uniform wire of linear charge density λ is proportional to:",
                        options = listOf("r", "1/r", "1/r²", "Constant"),
                        correctAnswerIndex = 1,
                        explanation = "Gauss's law gives electric field around an infinite wire as E = λ / (2πε₀r), so it is proportional to 1/r.",
                        subTopic = "Charge Distribution Field"
                    )
                ),
                assignments = listOf("Charged Shells and Dipole Moment Problems", "Electrostatic Potential Sheet")
            )
        )

        val subjectPhysics = Subject(
            id = "subject_phy",
            name = "Physics",
            icon = "⚡",
            themeColor = 0xFF7C3AED, // Creative Purple
            chapters = physicsChapters
        )
        subjectsList.add(subjectPhysics)

        // --- 3. Chemistry ---
        val chemChapters = listOf(
            Chapter(
                id = "chem_ch1",
                name = "Chemical Kinetics & Rates",
                videoCount = 1,
                notesCount = 1,
                hasQuiz = true,
                videos = listOf(
                    Video("chem_v1", "First Order Reaction Kinetics & Half-Lfe", "", "22:10", "Alok Sir (Kota)")
                ),
                notes = listOf(
                    Note("chem_n1", "Kinetics rate constant notes", "3.4 MB", "For a first order reaction, rate is proportional to concentration. Integrated rate equation is ln[A]t = -kt + ln[A]0. The half life t_1/2 = 0.693 / k is independent of original concentrations.")
                ),
                quizQuestions = listOf(
                    QuizQuestion(
                        id = 3001,
                        question = "For a zero-order reaction, what are the units of rate constant k?",
                        options = listOf("s⁻¹", "M s⁻¹", "M⁻¹ s⁻¹", "Dimensionless"),
                        correctAnswerIndex = 1,
                        explanation = "In zero order, Rate = k[A]⁰ = k. Therefore, units of k are identical to units of Rate, i.e., Mol/L/sec (M s⁻¹).",
                        subTopic = "Kinetics Reaction Units"
                    )
                ),
                assignments = listOf("Reaction Mechanisms kinetic rates worksheet")
            )
        )

        val subjectChem = Subject(
            id = "subject_chem",
            name = "Chemistry",
            icon = "🧪",
            themeColor = 0xFF059669, // Soft Green
            chapters = chemChapters
        )
        subjectsList.add(subjectChem)

        // --- 4. English & Computer Science ---
        val coreChapters = listOf(
            Chapter(
                id = "cs_ch1",
                name = "Modern Databases using Room SQLite",
                videoCount = 1,
                notesCount = 1,
                hasQuiz = true,
                videos = listOf(
                    Video("cs_v1", "Room Entities, DAOs & Repository Pattern", "", "18:30", "Anil Sharma")
                ),
                notes = listOf(
                    Note("cs_n1", "Android Architecture Compendium", "2.5 MB", "Room stores app items locally. It utilizes entities to construct SQLite tables. The `@Dao` queries must return flows or suspend methods to enforce main-thread safety.")
                ),
                quizQuestions = listOf(
                    QuizQuestion(
                        id = 4001,
                        question = "Which Room Database decorator is utilized to map a Kotlin class to a database table?",
                        options = listOf("@Table", "@Database", "@Entity", "@Model"),
                        correctAnswerIndex = 2,
                        explanation = "The `@Entity` annotation tells Room compiler to construct an SQLite table layout based on the kotlin data class schema.",
                        subTopic = "Room Annotations"
                    )
                ),
                assignments = listOf("Build a custom Room SQLite Dao")
            )
        )

        val subjectCS = Subject(
            id = "subject_cs",
            name = "Computer Science",
            icon = "💻",
            themeColor = 0xFFDC2626, // Crimson Red
            chapters = coreChapters
        )
        subjectsList.add(subjectCS)
    }
}
