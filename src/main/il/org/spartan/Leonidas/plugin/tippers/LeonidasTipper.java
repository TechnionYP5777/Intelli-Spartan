package il.org.spartan.Leonidas.plugin.tippers;

import com.intellij.psi.PsiElement;
import il.org.spartan.Leonidas.auxilary_layer.PsiRewrite;
import il.org.spartan.Leonidas.plugin.EncapsulatingNode;
import il.org.spartan.Leonidas.plugin.leonidas.PsiTreeMatcher;
import il.org.spartan.Leonidas.plugin.leonidas.PsiTreeReplacer;
import il.org.spartan.Leonidas.plugin.leonidas.PsiTreeTipperBuilder;
import il.org.spartan.Leonidas.plugin.leonidas.PsiTreeTipperBuilderImpl;
import il.org.spartan.Leonidas.plugin.tipping.Tip;
import il.org.spartan.Leonidas.plugin.tipping.Tipper;

import java.io.File;
import java.io.IOException;

/**
 * From this class evey leonidas tipper will be instantiated.
 *
 * @author michalcohen
 * @since 09-01-2017
 */
public class LeonidasTipper implements Tipper<PsiElement> {

    PsiTreeTipperBuilder b = new PsiTreeTipperBuilderImpl();
    File f;

    public LeonidasTipper(File f) {
        this.f = f;
        try {
            b.buildTipperPsiTree(f.getName());
        } catch (IOException ignore) {
        }
    }

    public LeonidasTipper(PsiTreeTipperBuilder builder, File file) {
        b = builder;
        f = file;
        try {
            b.buildTipperPsiTree(f.getName());
        } catch (IOException ignore) {
        }
    }

    @Override
    public boolean canTip(PsiElement e) {
        return PsiTreeMatcher.match(b.getFromPsiTree(), EncapsulatingNode.buildTreeFromPsi(e));
    }

    @Override
    public String description() {
        return f.getName();
    }

    @Override
    public String description(PsiElement e) {
        return b.getDescription();
    }

    @Override
    public Tip tip(PsiElement node) {
        return new Tip(description(node), node, this.getClass()) {
            @Override
            public void go(PsiRewrite r) {
                PsiTreeReplacer.replace(node, b, r);
            }
        };
    }

    @Override
    public Class<? extends PsiElement> getPsiClass() {
        return b.getRootElementType();
    }
}