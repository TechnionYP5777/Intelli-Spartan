package il.org.spartan.Leonidas.auxilary_layer;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.*;
import il.org.spartan.Leonidas.plugin.leonidas.BasicBlocks.Encapsulator;
import il.org.spartan.Leonidas.plugin.leonidas.BasicBlocks.GenericEncapsulator;
import il.org.spartan.Leonidas.plugin.utils.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * General utils class
 *
 * @author 01-12-2016
 */
public enum Utils {
    ;
    static final Logger logger = new Logger(Utils.class);

    /**
     * @param candidate the element that might be in the list
     * @param list      JD
     * @param <T>       the type of the list and the element.
     * @return true iff candidate is in the list.
     */
    @SafeVarargs
    public static <T> boolean in(T candidate, T... list) {
        return list != null && Arrays.stream(list).anyMatch(elem -> elem.equals(candidate));
    }

    /**
     * @param p the current project
     * @return the PsiManager of the project.
     */
    public static PsiManager getPsiManager(Project p) {
        return PsiManager.getInstance(p);
    }

    /**
     * @param root psi element that represents code blocks.
     * @param id   identifier
     * @return list of all the appearance of the identifier.
     */
    public static List<PsiIdentifier> getAllReferences(PsiElement root, PsiIdentifier id) {
        List<PsiIdentifier> identifiers = new ArrayList<>();
        if (root != null && id != null)
            root.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitIdentifier(PsiIdentifier i) {
                    super.visitIdentifier(i);
                    if (!id.getText().equals(i.getText()))
                        return;
                    PsiElement context = i.getContext();
                    if (iz.variable(context) || iz.referenceExpression(context))
                        identifiers.add(i);
                }
            });
        return identifiers;
    }

    /**
     * @param e JD
     * @return the document in which e is.
     */
    public static Document getDocumentFromPsiElement(PsiElement e) {
        PsiFile associatedFile = e.getContainingFile();
        Project p = associatedFile.getProject();
        PsiDocumentManager pdm = PsiDocumentManager.getInstance(p);
        Document res = pdm.getDocument(associatedFile);
        return res;
    }

    /**
     * @return the project of the user.
     */
    public static Project getProject() {
        return ProjectManager.getInstance().getOpenProjects()[0];
    }

    /**
     * @param e      JD
     * @param aClass the class of wanted type
     * @param <T>    the wanted type
     * @return lists of all the children of the type T of e.
     */
    public static <T extends PsiElement> List<T> getChildrenOfType(@Nullable PsiElement e, @NotNull Class<T> aClass) {
        Wrapper<List<T>> w = new Wrapper<>(new LinkedList<T>());
        assert e != null;
        e.accept(new JavaRecursiveElementVisitor() {
            @SuppressWarnings("unchecked")
            @Override
            public void visitElement(PsiElement e) {
                super.visitElement(e);
                if (aClass.isInstance(e))
                    w.get().add((T) e);
            }
        });
        return w.get();
    }

    /**
     * @param c the wanted tipper class
     * @return the content of the file of the tipper.
     */
    public static String getSourceCode(Class<?> c) {
        try {
            InputStream is = c.getClassLoader().getResourceAsStream(c.getName().replaceAll("\\.", "/") + ".java");
            return is != null ? IOUtils.toString(new BufferedReader(new InputStreamReader(is))) : "";
        } catch (IOException e) {
            logger.error("could not read file", e);
        }
        return "";
    }

    /**
     * fixed problems on machines where the project path has spaces in it:
     * the .getPath() of getResource inserts %20 instead of spaces
     *
     * @param path the path to be fixed
     * @return fixed path. on error, returns null
     */
    public static String fixSpacesProblemOnPath(String path) {
        try {
            return URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException ignore) {
            return "";
        }
    }

    /**
     * @param cb JD
     * @return the first element inside the block that isn't white space or the
     * enclosing brackets.
     */
    @SuppressWarnings("StatementWithEmptyBody")
    public static PsiElement getFirstElementInsideBody(PsiCodeBlock cb) {
        PsiElement c;
        for (c = cb.getFirstBodyElement(); c != null && iz.whiteSpace(c); c = c.getNextSibling()) ;
        return c;
    }

    public static List<Encapsulator> wrapWithList(Encapsulator e){
        List<Encapsulator> l = new LinkedList<>();
        l.add(e);
        return l;
    }

    public static List<PsiElement> wrapWithList(PsiElement e){
        List<PsiElement> l = new LinkedList<>();
        l.add(e);
        return l;
    }

    public static PsiElement getNextActualSibling(PsiElement e){
        if (e == null) return null;
        PsiElement current = e.getNextSibling();
        while (current != null && iz.whiteSpace(current)){
            current = current.getNextSibling();
        }
        return current;
    }

    public static int getNumberOfRootsPossible(PsiElement e){
        int i = 0;
        PsiElement current = e;
        while (current != null){
            i++;
            current = getNextActualSibling(current);
        }
        return i;
    }

    public static Method getDeclaredMethod(Class<?> c, String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        if (c.equals(Object.class)) throw new NoSuchMethodException();
        try {
            return c.getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {}
        return getDeclaredMethod(c.getSuperclass(), name, parameterTypes);
    }
}
