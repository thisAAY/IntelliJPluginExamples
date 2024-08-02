package today.thisaay.intellijpluginexamples.suggestNames

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiFile
import com.intellij.refactoring.rename.RenameProcessor
import kotlinx.coroutines.runBlocking
import org.jetbrains.kotlin.psi.KtProperty
import today.thisaay.intellijpluginexamples.ai.GeminiService

class SuggestBetterNamesIntention : IntentionAction {
    override fun startInWriteAction() = true

    override fun getFamilyName() = "Example plugins"

    override fun getText() = "Suggest better names"

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        if (file == null || editor == null) {
            return false
        }

        val offset = editor.caretModel.offset
        val element = file.findElementAt(offset)
        return element?.parent is KtProperty
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        if (file == null || editor == null) {
            return
        }

        val offset = editor.caretModel.offset
        val element = file.findElementAt(offset)
        val property = (element?.parent as? KtProperty) ?: return

        runBlocking {
            val response = GeminiService.generateContent(
                """
                For this kotlin variable suggest better names, only respond with the names in plain text, separated by ,
                Here's the variable and also the containing file: ${property.text}
            """.trimIndent()
            )

            val names = response.candidates?.first()?.content?.parts?.first()?.text
                ?.trimIndent()?.split(",")?.map { it.trimIndent() }?.toTypedArray() ?: return@runBlocking

            val selectedName = Messages.showEditableChooseDialog(
                "Select a name for variable ${property.name}:",
                "Select Name",
                Messages.getQuestionIcon(),
                names,
                names[0], // Default selection
                null // Default custom value, can be null
            ) ?: return@runBlocking


            RenameProcessor(project, property, selectedName, false, false).run()
        }
    }
}