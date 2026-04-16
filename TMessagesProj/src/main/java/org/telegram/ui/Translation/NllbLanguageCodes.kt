package org.telegram.ui.Translation

object NllbLanguageCodes {
    val SUPPORTED_LANGUAGES = mapOf(
        "eng_Latn" to "English",
        "zho_Hans" to "Chinese (Simplified)",
        "zho_Hant" to "Chinese (Traditional)",
        "jpn_Jpan" to "Japanese",
        "kor_Hang" to "Korean",
        "spa_Latn" to "Spanish",
        "fra_Latn" to "French",
        "deu_Latn" to "German",
        "ita_Latn" to "Italian",
        "rus_Cyrl" to "Russian",
        "por_Latn" to "Portuguese",
        "ara_Arab" to "Arabic",
        "hin_Deva" to "Hindi",
        "tha_Thai" to "Thai",
        "vie_Latn" to "Vietnamese",
        "ind_Latn" to "Indonesian",
        "tur_Latn" to "Turkish",
        "pol_Latn" to "Polish",
        "ukr_Cyrl" to "Ukrainian",
        "nld_Latn" to "Dutch",
        "vie_Hani" to "Vietnamese (Han)"
    )

    fun getLanguageName(code: String): String {
        return SUPPORTED_LANGUAGES[code] ?: code
    }

    fun getLanguageCode(name: String): String? {
        return SUPPORTED_LANGUAGES.entries.find { it.value == name }?.key
    }
}