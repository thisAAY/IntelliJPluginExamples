package today.thisaay.intellijpluginexamples.suggestNames

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.psi.KtProperty

class SuggestBetterNamesIntention : IntentionAction {
    override fun startInWriteAction() = false

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

        project.service<NamesService>().apply {
            updatePropertyName(property)
        }
    }
}