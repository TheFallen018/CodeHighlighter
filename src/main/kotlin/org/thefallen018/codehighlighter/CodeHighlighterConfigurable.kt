package org.thefallen018.codehighlighter

import com.intellij.openapi.options.Configurable
import com.intellij.ui.JBColor
import javax.swing.JButton
import javax.swing.JColorChooser
import javax.swing.JPanel
import javax.swing.JTextField

class CodeHighlighterConfigurable : Configurable {
    private var settingsPanel: JPanel? = null
    private var colorNameField: JTextField? = null
    private var colorButton: JButton? = null
    private var selectedColor: JBColor? = null

    override fun createComponent(): JPanel? {
        settingsPanel = JPanel()
        colorNameField = JTextField(20)
        colorButton = JButton("Choose Color")

        colorButton?.addActionListener {
            val color = JColorChooser.showDialog(null, "Choose Highlight Color", null)
            if (color != null) {
                selectedColor = JBColor(color, color)
            }
        }

        settingsPanel?.add(colorNameField)
        settingsPanel?.add(colorButton)
        return settingsPanel
    }

    override fun isModified(): Boolean {
        return true
    }

    override fun apply() {
        val colorName = colorNameField?.text ?: return
        val colorHex = selectedColor?.rgb?.toString(16) ?: return
        CodeHighlighterSettings.getInstance().highlightColors[colorName] = colorHex
    }

    override fun getDisplayName(): String {
        return "Code Highlighter"
    }
}