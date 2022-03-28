
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 *  Demonstrate JDK 8 feature lambda
 */
public class Test161 {

    Supplier<Object> s1 = () -> new Object();
    Supplier<Object> s2 = () -> null;
    Supplier<Object> s3 = () -> {return "A";};

    Consumer<Object> c1 = a -> {};
    Consumer<Object> c2 = (a) -> {};
    Consumer<Object> c3 = (Object a) -> {};
    Consumer<Object> c4 = (final Object a) -> {};    

    BiFunction<Long, Integer, String> bf1 = (a, b) -> "";
    BiFunction<Long, Integer, String> bf2 = (Long a, Integer b) -> {return null;};
    BiFunction<Long, Integer, String> bf3 = (final Long a, Integer b) -> {return null;};

}
