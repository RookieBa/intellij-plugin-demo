package cn.bawy.demo.toolwindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class DemoToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val demoToolWindow = DemoToolWindow(project)
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(demoToolWindow.getContent(), "Chat", false)
        toolWindow.contentManager.addContent(content)
    }
}