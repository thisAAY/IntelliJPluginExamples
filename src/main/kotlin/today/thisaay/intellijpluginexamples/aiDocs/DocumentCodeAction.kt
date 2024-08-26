package today.thisaay.intellijpluginexamples.aiDocs

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction

class DocumentCodeAction : AnAction(
    "Document This Code"
) {

    override fun getActionUpdateThread() = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        val element = e.getData(CommonDataKeys.PSI_ELEMENT)


        val isVisible = element is KtNamedFunction || element is KtClass

        e.updateSession.compute(this, "Updating visibility", ActionUpdateThread.EDT) {
            e.presentation.isEnabledAndVisible = isVisible
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val element = e.getData(CommonDataKeys.PSI_ELEMENT) ?: return

        e.project?.service<DocumentCodeService>()?.apply {
            writeDocs(element)
        }
    }

}