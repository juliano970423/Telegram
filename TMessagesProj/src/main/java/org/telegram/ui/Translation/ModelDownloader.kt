package org.telegram.ui.Translation

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class ModelDownloader(private val context: Context) {

    companion object {
        // Xenova/nllb-200-distilled-600M quantized models
        private const val MODEL_BASE_URL = "https://huggingface.co/Xenova/nllb-200-distilled-600M/resolve/main/onnx/"
        
        val MODEL_FILES = listOf(
            "encoder_model_quantized.onnx",
            "decoder_model_quantized.onnx",
            "tokenizer_config.json",
            "sentencepiece.bpe.model"
        )
        
        val CONFIG_FILES = listOf(
            "config.json",
            "generation_config.json",
            "special_tokens_map.json"
        )
        
        private const val MODEL_VERSION = "1.0"
    }

    interface DownloadListener {
        fun onProgress(fileName: String, progress: Int)
        fun onComplete(file: File)
        fun onError(error: String)
    }

    private var listener: DownloadListener? = null

    fun setListener(listener: DownloadListener) {
        this.listener = listener
    }

    fun isModelDownloaded(): Boolean {
        val modelDir = File(context.filesDir, "nllb_model")
        if (!modelDir.exists()) return false
        
        return MODEL_FILES.all { File(modelDir, it).exists() }
    }

    suspend fun downloadModel() = withContext(Dispatchers.IO) {
        try {
            val modelDir = File(context.filesDir, "nllb_model")
            if (!modelDir.exists()) {
                modelDir.mkdirs()
            }

            // Download main model files
            for (fileName in MODEL_FILES) {
                downloadFile(fileName, modelDir)
            }

            // Download config files
            for (fileName in CONFIG_FILES) {
                downloadConfigFile(fileName, modelDir)
            }

            // Save model version
            File(modelDir, "version.txt").writeText(MODEL_VERSION)

            withContext(Dispatchers.Main) {
                listener?.onComplete(modelDir)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                listener?.onError(e.message ?: "Download failed")
            }
        }
    }

    private suspend fun downloadFile(fileName: String, destDir: File) = withContext(Dispatchers.IO) {
        val file = File(destDir, fileName)
        if (file.exists()) {
            return@withContext
        }

        val url = URL(MODEL_BASE_URL + fileName)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 30000
        connection.readTimeout = 60000

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val fileSize = connection.contentLength
            var downloadedSize = 0

            connection.inputStream.use { input ->
                FileOutputStream(file).use { output ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        downloadedSize += bytesRead

                        if (fileSize > 0) {
                            val progress = (downloadedSize * 100 / fileSize)
                            withContext(Dispatchers.Main) {
                                listener?.onProgress(fileName, progress)
                            }
                        }
                    }
                }
            }
        } else {
            throw Exception("Failed to download $fileName: $responseCode")
        }
    }

    private suspend fun downloadConfigFile(fileName: String, destDir: File) = withContext(Dispatchers.IO) {
        val file = File(destDir, fileName)
        if (file.exists()) {
            return@withContext
        }

        val url = URL("https://huggingface.co/Xenova/nllb-200-distilled-600M/resolve/main/$fileName")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            connection.inputStream.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
        }
    }

    suspend fun deleteModel() = withContext(Dispatchers.IO) {
        val modelDir = File(context.filesDir, "nllb_model")
        if (modelDir.exists()) {
            modelDir.deleteRecursively()
        }
    }

    fun getModelSize(): Long {
        val modelDir = File(context.filesDir, "nllb_model")
        return if (modelDir.exists()) {
            modelDir.walkTopDown().filter { it.isFile }.map { it.length() }.sum()
        } else {
            0
        }
    }
}