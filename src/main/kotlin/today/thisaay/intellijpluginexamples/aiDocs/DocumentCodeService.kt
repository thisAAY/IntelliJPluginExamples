package today.thisaay.intellijpluginexamples.aiDocs

import com.intellij.openapi.application.readAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
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
            val elementBody = readAction {
                element.text
            }


            val response =
                GeminiService.generateContent(
                    """
                        Write a kDoc comments for this kotlin code, respond only with documentation in plain text without the markdown formatters
                        \n $elementBody
                """.trimMargin()
                )

            val newText = response.candidates?.first()?.content?.parts?.first()?.text.orEmpty()


            val factory = KtPsiFactory.contextual(element)
            val docs = readAction {
                if (element is KtNamedFunction) factory.createFunction(newText) else factory.createClass(newText)
            }

            WriteCommandAction.runWriteCommandAction(project) {
                // Ai returns the docs with the original code, so we need to replace the whole element with the new one
                element.replace(docs)
            }
        }
    }
}