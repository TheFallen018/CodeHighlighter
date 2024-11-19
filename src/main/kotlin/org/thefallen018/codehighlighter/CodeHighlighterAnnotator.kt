package org.thefallen018.codehighlighter

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.*
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiRecursiveElementWalkingVisitor
import com.intellij.psi.impl.source.tree.PsiCommentImpl
import java.awt.Font

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.util.TextRange
import com.intellij.ui.JBColor
import java.awt.Color
import java.awt.Graphics

val highlightColor = Color(40, 74, 133, 255) // 50% opaque blue

class CodeHighlighterAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {

        if (element !is PsiFile) {
            return
        }

        val editor = holder.currentAnnotationSession.file.viewProvider.document?.let {
            com.intellij.openapi.editor.EditorFactory.getInstance().getEditors(it).firstOrNull()
        } ?: return

        val markupModel = editor.markupModel
        val document = editor.document
        val fontMetrics = editor.contentComponent.getFontMetrics(editor.colorsScheme.getFont(EditorFontType.PLAIN))

        // Clear all existing highlighters
        markupModel.allHighlighters.forEach { it.dispose() }

        // Find the maximum width of all lines
        var maxWidth = 0
        for (line in 0 until document.lineCount) {
            val startOffset = document.getLineStartOffset(line)
            val endOffset = document.getLineEndOffset(line)
            val lineText = document.getText(TextRange(startOffset, endOffset))
            val textWidth = fontMetrics.stringWidth(lineText)
            if (textWidth > maxWidth) {
                maxWidth = textWidth
            }
        }

        val highlighters = mutableListOf<RangeHighlighter>()
        var currentHighlighters = mutableListOf<RangeHighlighter>()

        element.accept(object : PsiRecursiveElementWalkingVisitor() {
            private val highlightRanges = mutableListOf<Pair<Int, Int>>()
            private val sectionHighlighters = mutableListOf<RangeHighlighter>()

            override fun visitElement(element: PsiElement) {
                val startLine = document.getLineNumber(element.textRange.startOffset)
                val endLine = document.getLineNumber(element.textRange.endOffset)

                for (line in startLine..endLine) {
                    val startOffset = document.getLineStartOffset(line)
                    val endOffset = document.getLineEndOffset(line)
                    val lineText = document.getText(TextRange(startOffset, endOffset))

                    if (lineText.contains("##start_highlight")) {
                        highlightRanges.add(Pair(line, -1))
                    } else if (lineText.contains("##end_highlight")) {
                        highlightRanges.lastOrNull { it.second == -1 }?.let {
                            highlightRanges[highlightRanges.indexOf(it)] = Pair(it.first, line)
                        }
                    }
                }

                highlightRanges.forEach { (start, end) ->
                    if (start != -1 && end != -1) {
                        for (line in start..end) {
                            val startOffset = document.getLineStartOffset(line)
                            val endOffset = document.getLineEndOffset(line)

                            val highlighter = markupModel.addRangeHighlighter(
                                startOffset,
                                endOffset,
                                HighlighterLayer.ADDITIONAL_SYNTAX,
                                TextAttributes(null, null, null, null, Font.PLAIN),
                                HighlighterTargetArea.LINES_IN_RANGE
                            )
                            highlighter.customRenderer = object : CustomHighlighterRenderer {
                                override fun paint(editor: Editor, highlighter: RangeHighlighter, g: Graphics) {
                                    val startPoint = editor.visualPositionToXY(editor.offsetToVisualPosition(startOffset))
                                    val y = startPoint.y
                                    val height = editor.lineHeight

                                    g.color = JBColor(highlightColor, highlightColor)
                                    g.fillRect(0, y, maxWidth, height)
                                }
                            }
                            sectionHighlighters.add(highlighter)
                        }
                    }
                }

                super.visitElement(element)
            }
        })
    }

    companion object {
        private val LOG = Logger.getInstance(CodeHighlighterAnnotator::class.java)
        private val BLUE_HIGHLIGHT = TextAttributesKey.createTextAttributesKey(
            "BLUE_HIGHLIGHT",
            TextAttributes(null, JBColor(highlightColor, highlightColor), null, null, Font.PLAIN) // 50% opaque blue
        )
    }
}