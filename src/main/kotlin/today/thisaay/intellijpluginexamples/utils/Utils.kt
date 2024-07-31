package today.thisaay.intellijpluginexamples.utils

fun getApiKey(): String {
    val env = System.getenv()
    return env.get("GEMINI_API_KEY")!!
}