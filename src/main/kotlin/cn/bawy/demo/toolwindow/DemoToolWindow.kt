package cn.bawy.demo.toolwindow

import com.intellij.openapi.project.Project
import com.intellij.ui.jcef.JBCefBrowser
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.browser.CefMessageRouter
import org.cef.callback.CefQueryCallback
import org.cef.handler.CefMessageRouterHandlerAdapter
import java.awt.BorderLayout
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import javax.swing.JComponent
import javax.swing.JPanel

class DemoToolWindow(private val project: Project) {
    private val panel: JPanel = JPanel(BorderLayout())
    private val browser: JBCefBrowser = JBCefBrowser()

    init {
        // 获取HTML内容
        val htmlContent = getHtmlContent()

        registerBridge()
        // 加载HTML内容
        browser.loadHTML(htmlContent)
        // 将浏览器组件添加到面板
        panel.add(browser.component, BorderLayout.CENTER)
    }

    fun getContent(): JComponent {
        return panel
    }


    /**
     * 添加JavaScript与Java之间的Bridge
     *
     * @param browser
     */
    private fun registerBridge() {
        val tetrisRouter = CefMessageRouter.create(CefMessageRouter.CefMessageRouterConfig("demoQuery", "demoQueryCancel"))
        tetrisRouter.addHandler(MyCefMessageRouterHandler(project), true)
        browser.jbCefClient.cefClient.addMessageRouter(tetrisRouter)
    }

    private fun getHtmlContent(): String {
        // 获取插件资源目录中的HTML文件
        val resourceStream = javaClass.classLoader.getResourceAsStream("html/index.html")

        return if (resourceStream != null) {
            try {
                // 读取HTML文件内容
                InputStreamReader(resourceStream, StandardCharsets.UTF_8).use { reader ->
                    reader.readText()
                }
            } catch (e: Exception) {
                // 如果读取失败，返回错误信息
                "<html><body><h1>Error</h1><p>Failed to read HTML content: ${e.message}</p></body></html>"
            } finally {
                resourceStream.close()
            }
        } else {
            // 如果找不到资源，返回一个简单的HTML内容
            "<html><body><h1>Error</h1><p>Could not find the HTML file.</p></body></html>"
        }
    }

    private class MyCefMessageRouterHandler(private var project: Project) : CefMessageRouterHandlerAdapter() {

        override fun onQuery(
            browser: CefBrowser?,
            frame: CefFrame?,
            queryId: Long,
            request: String?,
            persistent: Boolean,
            callback: CefQueryCallback?
        ): Boolean {
            com.intellij.notification.NotificationGroupManager.getInstance()
                .getNotificationGroup("Demo Notification Group")
                .createNotification(request ?: "", com.intellij.notification.NotificationType.INFORMATION)
                .notify(project)
            callback?.success("")
            return true
        }


    }

}