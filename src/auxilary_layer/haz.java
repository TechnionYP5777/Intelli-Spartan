package auxilary_layer;

import com.intellij.psi.*;

import java.util.Arrays;


/**
 * Created by melanyc on 12/1/2016.
 */
public enum haz {
    ;
    public static boolean variableDefenition(final PsiElement p){
        final Wrapper<Boolean> b = new Wrapper<>(Boolean.FALSE);
        p.accept(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement e) {
                if(iz.declarationStatement(e)){
                    PsiDeclarationStatement d = az.declarationStatement(e);
                    super.visitElement(d);
                    b.inner = d.getDeclaredElements().length > 0;
                } else if(iz.enumConstant(e)){
                    PsiEnumConstant d = az.enumConstant(e);
                    super.visitElement(d);
                    b.inner = Boolean.TRUE;
                } else if(iz.fieldDecleration(e)){
                    PsiField d = az.fieldDeclaration(e);
                    super.visitElement(d);
                    b.inner = Boolean.TRUE;
                }

            }
        });
        return b.inner;
    }


    public static boolean centVariableDefenition(final PsiElement p){
        final Wrapper<Boolean> b = new Wrapper<>(Boolean.FALSE);
        p.accept(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement e) {
                if(iz.declarationStatement(e)){
                    PsiDeclarationStatement d = az.declarationStatement(e);
                    super.visitElement(d);
                    b.inner = Arrays.stream(d.getDeclaredElements()).anyMatch(z -> z.textContains('¢'));
                } else if(iz.enumConstant(e)){
                    PsiEnumConstant d = az.enumConstant(e);
                    super.visitElement(d);
                    b.inner = d.getName().equals("¢");
                } else if(iz.fieldDecleration(e)){
                    PsiField d = az.fieldDeclaration(e);
                    super.visitElement(d);
                    b.inner = d.getName().equals("¢");
                }
            }
        });
        return b.inner;
    }



}