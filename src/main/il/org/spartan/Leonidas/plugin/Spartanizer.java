package il.org.spartan.Leonidas.plugin;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import il.org.spartan.Leonidas.plugin.tipping.Tipper;

/**
 * @author Oren Afek, Roey Maor
 * @since 09-12-2016
 */
public enum Spartanizer {
    ;

    static boolean canTip(PsiElement e) {
        return Toolbox.getInstance().canTip(e);
    }

    static void spartanizeElement(PsiElement e) {
        if (!"SpartanizerUtils.java".equals(e.getContainingFile().getName()))
            Toolbox.getInstance().executeAllTippers(e);
    }

    public static void spartanizeFileOnePass(PsiFile f) {
        if ("SpartanizerUtils.java".equals(f.getName()))
            return;
        Toolbox toolbox = Toolbox.getInstance();
        f.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement e) {
                super.visitElement(e);
                toolbox.executeAllTippers(e);
            }
        });
    }

    public static void spartanizeFileRecursively(PsiFile f) {
        Toolbox toolbox = Toolbox.getInstance();
        toolbox.replaced = true;
        while (toolbox.replaced) {
            toolbox.replaced = false;
            spartanizeFileOnePass(f);
        }
    }


    public static void spartanizeElementWithTipper(PsiElement e, String tipperName){
        Toolbox toolbox = Toolbox.getInstance();
        for(Tipper t : toolbox.getAllTippers()){
            System.out.println(t.name());
        }
    }
}
