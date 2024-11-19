package org.thefallen018.codehighlighter

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(name = "CodeHighlighterSettings", storages = [Storage("CodeHighlighterSettings.xml")])
class CodeHighlighterSettings : PersistentStateComponent<CodeHighlighterSettings> {
    var highlightColors: MutableMap<String, String> = mutableMapOf()

    override fun getState(): CodeHighlighterSettings? {
        return this
    }

    override fun loadState(state: CodeHighlighterSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        fun getInstance(): CodeHighlighterSettings {
            return ServiceManager.getService(CodeHighlighterSettings::class.java)
        }
    }
}