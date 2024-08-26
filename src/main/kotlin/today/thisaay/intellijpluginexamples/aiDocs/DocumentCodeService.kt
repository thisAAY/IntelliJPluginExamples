package today.thisaay.intellijpluginexamples.aiDocs

import com.intellij.openapi.application.EDT
import com.intellij.openapi.application.readAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPsiFactory
import today.thisaay.intellijpluginexamples.ai.GeminiService

@Service(Service.Level.PROJECT)
class DocumentCodeService(
    private val project: Project,
    private val scope: CoroutineScope,
) {
    fun writeDocs(element: PsiElement) {
        scope.launch {
            val geminiService = service<GeminiService>()
            val elementBody = readAction {
                element.text
            }

            val isFunction = readAction { element is KtNamedFunction }

            val response = withContext(Dispatchers.IO) {
                geminiService.generateContent(
                    """
                        Write a kDoc comments for this kotlin code, respond only with documentation in plain text without the markdown formatters
                        \n $elementBody
                """.trimMargin()
                )
            }

            val newText = response.candidates?.first()?.content?.parts?.first()?.text.orEmpty()

            withContext(Dispatchers.EDT) {
                WriteCommandAction.runWriteCommandAction(project) {
                    val factory = KtPsiFactory.contextual(element)
                    val docs = if (isFunction)
                        factory.createFunction(newText) else factory.createClass(newText)
                    element.replace(docs)
                }
            }

        }
    }
}