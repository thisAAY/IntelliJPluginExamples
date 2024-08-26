package today.thisaay.intellijpluginexamples.utils

fun getApiKey(): String {
    return requireNotNull(System.getenv()["GEMINI_API_KEY"]) {
        "GEMINI_API_KEY much be in env variables"
    }
}