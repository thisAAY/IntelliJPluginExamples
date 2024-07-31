package today.thisaay.intellijpluginexamples.ai

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import  kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import today.thisaay.intellijpluginexamples.utils.getApiKey


// https://johnoreilly.dev/posts/gemini-kotlin-multiplatform/

@Serializable
data class Part(val text: String)

@Serializable
data class Content(val parts: List<Part>)

@Serializable
data class Candidate(val content: Content)

@Serializable
data class Error(val message: String)

@Serializable
data class GenerateContentResponse(val error: Error? = null, val candidates: List<Candidate>? = null)

@Serializable
data class GenerateContentRequest(val contents: Content)

object GeminiService {
    private const val BASE_URL = " https://generativelanguage.googleapis.com/v1beta/models"
    private val apiKey = getApiKey()

    @OptIn(ExperimentalSerializationApi::class)
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { isLenient = true; ignoreUnknownKeys = true; explicitNulls = false })
        }
    }

    suspend fun generateContent(prompt: String): GenerateContentResponse {
        val part = Part(text = prompt)
        val contents = Content(listOf(part))
        val request = GenerateContentRequest(contents)

        return client.post("$BASE_URL/gemini-1.5-flash:generateContent") {
            contentType(ContentType.Application.Json)
            url { parameters.append("key", apiKey) }
            setBody(request)
        }.body<GenerateContentResponse>()
    }
}