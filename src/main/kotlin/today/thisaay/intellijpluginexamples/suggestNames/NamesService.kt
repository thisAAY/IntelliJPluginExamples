package today.thisaay.intellijpluginexamples.suggestNames

import com.intellij.openapi.application.readAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.refactoring.rename.RenameProcessor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.kotlin.psi.KtProperty
import today.thisaay.intellijpluginexamples.ai.GeminiService

@Service(Service.Level.PROJECT)
class NamesService(
    private val project: Project,
    private val scope: CoroutineScope,
) {

    fun updatePropertyName(element: KtProperty) {
        scope.launch {
            val (name, body) = readAction {
                Pair(element.name, element.text)
            }
            val geminiService = service<GeminiService>()
            val response = geminiService.generateContent(
                """
                For this kotlin variable suggest better names, only respond with the names in plain text, separated by ,
                Here's the variable and also the containing file: $body
            """.trimIndent()
            )

            val names = response.candidates?.first()?.content?.parts?.first()?.text
                ?.trimIndent()?.split(",")?.map { it.trimIndent() }?.toTypedArray() ?: return@launch

            val selectedName = Messages.showEditableChooseDialog(
                "Select a name for variable $name:",
                "Select Name",
                Messages.getQuestionIcon(),
                names,
                names[0],
                null,
            ) ?: return@launch


            RenameProcessor(project, element, selectedName, false, false).run()

        }
    }

}