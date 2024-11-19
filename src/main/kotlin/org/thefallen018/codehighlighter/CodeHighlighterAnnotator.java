package org.thefallen018.codehighlighter;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.impl.source.tree.PsiCommentImpl;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.awt.Font;

public class CodeHighlighterAnnotator implements Annotator {
    private static final Logger LOG = Logger.getInstance(CodeHighlighterAnnotator.class);
    private static final TextAttributesKey BLUE_HIGHLIGHT = TextAttributesKey.createTextAttributesKey(
            "BLUE_HIGHLIGHT",
            new TextAttributes(Color.BLUE, null, null, null, Font.PLAIN)
    );

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        System.out.println("Annotate method called");

        if (!(element instanceof PsiFile)) {
            return;
        }

        PsiFile file = (PsiFile) element;
        file.accept(new PsiRecursiveElementWalkingVisitor() {
            private boolean highlight = false;

            @Override
            public void visitElement(@NotNull PsiElement element) {
                System.out.println("Visiting element: " + element.getClass().getSimpleName() + " - " + element.getText());
                System.out.println("Current highlight state: " + highlight);

                if (element instanceof PsiCommentImpl) {
                    String commentText = element.getText();
                    System.out.println("Comment found: " + commentText);

                    if (commentText.contains("##start_blue")) {
                        highlight = true;
                        System.out.println("Highlighting started");
                    } else if (commentText.contains("##end_blue")) {
                        highlight = false;
                        System.out.println("Highlighting ended");
                    }
                }

                if (highlight) {
                    holder.createInfoAnnotation(element, null).setTextAttributes(BLUE_HIGHLIGHT);
                    System.out.println("Element highlighted: " + element.getText());
                }

                super.visitElement(element);
            }
        });
    }
}