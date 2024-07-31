package today.thisaay.intellijpluginexamples.aiDocs

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction

class DocumentCodeAction : AnAction(
    "Document This Code"
) {

    override fun update(e: AnActionEvent) {
        val element = e.getData(CommonDataKeys.PSI_ELEMENT)

        e.presentation.isEnabledAndVisible = element is KtNamedFunction || element is KtClass
    }

    override fun actionPerformed(e: AnActionEvent) {
        val element = e.getData(CommonDataKeys.PSI_ELEMENT) ?: return
        try {
            e.project?.service<DocumentCodeService>()?.apply {
                writeDocs(element)
            }
        }catch (t: Throwable){
            println(t)
        }
    }

}