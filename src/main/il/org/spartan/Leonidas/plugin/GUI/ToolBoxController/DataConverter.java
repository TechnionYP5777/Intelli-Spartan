package il.org.spartan.Leonidas.plugin.GUI.ToolBoxController;

import il.org.spartan.Leonidas.auxilary_layer.Existence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Created by Sharon on 30-Jun-17.
 */
public class DataConverter<T> {
    public static final DataConverter<String> STRING = new DataConverter<>(s -> s, s -> s);
    public static final DataConverter<Integer> INTEGER = new DataConverter<>(i -> Integer.toString(i), Integer::parseInt);
    public static final DataConverter<Double> DOUBLE = new DataConverter<>(i -> Double.toString(i), Double::parseDouble);
    public static final DataConverter<Float> FLOAT = new DataConverter<>(i -> Float.toString(i), Float::parseFloat);
    public static final DataConverter<Long> LONG = new DataConverter<>(i -> Long.toString(i), Long::parseLong);
    public static final DataConverter<List<String>> STRING_LIST = new DataConverter<>(list -> String.join(",", list),
            s -> new ArrayList<>(Arrays.asList(s.split("\\s*,\\s*"))));
    public static final DataConverter<Existence> EXISTENCE = new DataConverter<>(Enum::toString, Existence::valueOf);

    private Function<T, String> toString;
    private Function<String, T> fromString;

    public DataConverter(Function<T, String> toString, Function<String, T> fromString) {
        this.toString = toString;
        this.fromString = fromString;
    }

    public String toString(T t) {
        return toString.apply(t);
    }

    public Object fromString(String s) {
        return fromString.apply(s);
    }

    public static String convert(Object o) {
        if (String.class.isAssignableFrom(o.getClass())) {
            return STRING.toString((String) o);
        } else if (Integer.class.isAssignableFrom(o.getClass())) {
            return INTEGER.toString((Integer) o);
        } else if (Integer.class.isAssignableFrom(o.getClass())) {
            return DOUBLE.toString((Double) o);
        } else if (Integer.class.isAssignableFrom(o.getClass())) {
            return FLOAT.toString((Float) o);
        } else if (Integer.class.isAssignableFrom(o.getClass())) {
            return LONG.toString((Long) o);
        } else if (List.class.isAssignableFrom(o.getClass())) {
            return STRING_LIST.toString((List<String>) o);
        } else if (Existence.class.isAssignableFrom(o.getClass())) {
            return EXISTENCE.toString((Existence) o);
        }

        return "";
    }

    public static Object decode(String s, Class<?> c) {
        if (String.class.isAssignableFrom(c)) {
            return STRING.fromString(s);
        } else if (Integer.class.isAssignableFrom(c)) {
            return INTEGER.fromString(s);
        } else if (Double.class.isAssignableFrom(c)) {
            return DOUBLE.fromString(s);
        } else if (Float.class.isAssignableFrom(c)) {
            return FLOAT.fromString(s);
        } else if (Long.class.isAssignableFrom(c)) {
            return LONG.fromString(s);
        } else if (List.class.isAssignableFrom(c)) {
            return STRING_LIST.fromString(s);
        } else if (Existence.class.isAssignableFrom(c)) {
            return EXISTENCE.fromString(s);
        }

        return "";
    }
}