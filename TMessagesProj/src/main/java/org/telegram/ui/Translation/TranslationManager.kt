package org.telegram.ui.Translation

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TranslationManager private constructor(private val context: Context) {

    companion object {
        @Volatile
        private var instance: TranslationManager? = null

        private const val PREFS_NAME = "translation_prefs"
        private const val KEY_TARGET_LANGUAGE = "target_language"
        private const val KEY_AUTO_DETECT = "auto_detect"
        private const val KEY_MODEL_DOWNLOADED = "model_downloaded"
        
        // Default target language: Traditional Chinese
        const val DEFAULT_TARGET_LANGUAGE = "zho_Hant"

        fun getInstance(context: Context): TranslationManager {
            return instance ?: synchronized(this) {
                instance ?: TranslationManager(context.applicationContext).also { instance = it }
            }
        }
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val downloader = ModelDownloader(context)
    private val engine = NllbEngine.getInstance()

    var targetLanguage: String
        get() = prefs.getString(KEY_TARGET_LANGUAGE, DEFAULT_TARGET_LANGUAGE) ?: DEFAULT_TARGET_LANGUAGE
        set(value) = prefs.edit().putString(KEY_TARGET_LANGUAGE, value).apply()

    var autoDetectSource: Boolean
        get() = prefs.getBoolean(KEY_AUTO_DETECT, true)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_DETECT, value).apply()

    var isModelDownloaded: Boolean
        get() = prefs.getBoolean(KEY_MODEL_DOWNLOADED, false) && downloader.isModelDownloaded()
        private set(value) = prefs.edit().putBoolean(KEY_MODEL_DOWNLOADED, value).apply()

    fun isReady(): Boolean = isModelDownloaded && engine.isInitialized()

    suspend fun initialize(): Boolean {
        if (isModelDownloaded) {
            return engine.initialize(context)
        }
        return false
    }

    suspend fun downloadModel(): Boolean {
        return try {
            downloader.downloadModel()
            isModelDownloaded = true
            engine.initialize(context)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun translate(text: String, sourceLang: String? = null): String? {
        if (!isReady()) {
            // Try to initialize if model exists but not loaded
            if (isModelDownloaded && !engine.isInitialized()) {
                engine.initialize(context)
            }
            return null
        }

        val source = sourceLang ?: "eng_Latn" // Default to English
        return engine.translate(text, source, targetLanguage)
    }

    suspend fun deleteModel() {
        downloader.deleteModel()
        isModelDownloaded = false
        engine.release()
    }

    fun getModelSize(): Long = downloader.getModelSize()

    fun getTargetLanguageName(): String = NllbLanguageCodes.getLanguageName(targetLanguage)

    fun setListener(listener: ModelDownloader.DownloadListener) {
        downloader.setListener(listener)
    }
}