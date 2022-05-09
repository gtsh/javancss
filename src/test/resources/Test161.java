
import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 *  Demonstrate some JDK 8 features: lambda
 */
final public class Test161 {

    @Target(ElementType.TYPE_PARAMETER) // Availabe for type parameter definitition since JDK 1.8
    @interface TypeParameterAnnotation {}

    @Target(ElementType.TYPE_USE) // Availabe for type specifications since JDK 1.8
    @interface TypeUseAnnotation {}

    @Target(ElementType.TYPE) // Availabe for types, around since there are annotions (since JDK 1.5)
    @interface TypeAnnotation {}

    // May be placed anywhere:
    @interface AnyAnnotation {}

    @Target({
        ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR,
        ElementType.FIELD,
        ElementType.LOCAL_VARIABLE,
        ElementType.METHOD,
        ElementType.PACKAGE,
        ElementType.PARAMETER
    })
    @interface OtherAnnotation {}
    /*
    Supplier<Object> s1 = () -> new Object();
    private final @OtherAnnotation @TypeUseAnnotation Supplier<Object> s2 = () -> null;
    
    Supplier<Object> s3 = () -> {return "A";};

    Consumer<Object> c1 = a -> {};
    Consumer<Object> c2 = (a) -> {};
    Consumer<Object> c3 = (Object a) -> {};
    Consumer<@TypeUseAnnotation Object> c4 = (final Object a) -> {};    

    BiFunction<Map, List, String> bf1 = (a, b) -> "";
    BiFunction<Long, Integer, String> bf2 = (Long a, Integer b) -> {return null;};
    BiFunction<Long, Integer, @TypeUseAnnotation String> bf3 = (final @TypeUseAnnotation Long a, @TypeUseAnnotation Integer b) -> {return null;};

    // Simple type use annotations on members: watch out for the position *between* the modifier and the members identifier!
    
    private @TypeUseAnnotation int x = 0;
    
    private @TypeUseAnnotation Test161() {
        
    }
    */
    private @TypeUseAnnotation String method(final @TypeUseAnnotation String x) /* throws @TypeUseAnnotation Exception */ {
    /*    
        try {
            
        } catch (final @TypeUseAnnotation NullPointerException | @TypeUseAnnotation Error e) {
            throw new @TypeUseAnnotation Error();
        }
       
       int[] annotatedArrayCreation = new @TypeUseAnnotation int[0];

       // Using it for a local variable and in instanceof
       @TypeUseAnnotation boolean instanceOfResult = annotatedArrayCreation instanceof @TypeUseAnnotation Object;

       // More complex generic useses, involving wildcards.
       // We are establishing a baseline here, i. e. that parsing without annotations succeeds:
       Collection<?> wildCardArgument = null;
       Collection<? extends Consumer> wildcardWithLowerBound = null;
       Collection<? super Consumer> wildcardWithUpperBound = null;
              
       // Again, with various combinations of ? with a type use annotation.
       // We are establishing a baseline here, i. e. that parsing without annotations succeeds:
       Collection<@TypeUseAnnotation ?> annotatedWildCardArgument = null;
       Collection<? super @TypeUseAnnotation Consumer> wildcardWithAnnotatedUpperBounds = null;
       List<@TypeUseAnnotation ? super Comparable<?>> annotatedwildcardWithAnnotatedUpperBounds = null;
       List<@TypeUseAnnotation ? extends Comparable<?>> wildcardWithAnnotatedLowerBounds = null;
       List<@TypeUseAnnotation ? extends @TypeUseAnnotation Comparable<?>> annotatedwildcardWithAnnotatedLowerBounds = null;
*/
       // Combining type use annotations with arrays
       // ... if the array declarator is attached to the type
       Object @TypeUseAnnotation[] annotatedArray   = null; 
       Object [] @TypeUseAnnotation[] annotated2DArray = null; 
       Object  @TypeUseAnnotation[] @TypeUseAnnotation[] doublyAnnotated2DArray = null;

       // ... if the array declarator is attached to the variable
       Object annotatedArray2 /*@TypeUseAnnotation*/[]  = null; 
       Object annotated2DArray2 [] /*@TypeUseAnnotation*/[] = null; 
       Object doublyAnnotated2DArray2 /*@TypeUseAnnotation*/[] /*@TypeUseAnnotation*/[] = null;
       
       // in a cast:
       return (@TypeUseAnnotation String) wildcardWithUpperBound.iterator().next();
    }
    
    // Annotate a type parameter A
    @TypeUseAnnotation private <@TypeParameterAnnotation A extends @TypeUseAnnotation Object & @TypeUseAnnotation Map> 
        @OtherAnnotation  A genericMethod1() {
        return null;
    }
    
    // some local classes, addorned with type annotations; note the position *after* the modifier!
    protected @TypeUseAnnotation abstract class X<@TypeParameterAnnotation T1 extends @TypeUseAnnotation Map> 
            extends @TypeUseAnnotation ArrayList<@TypeUseAnnotation T1> 
            implements @TypeUseAnnotation Cloneable, @TypeUseAnnotation Consumer<T1>
    {
         protected class Y<@TypeParameterAnnotation T2 extends T1> {
             
         }
    }

    protected @TypeUseAnnotation interface Z<@TypeParameterAnnotation T1 extends @TypeUseAnnotation Map> extends 
            @TypeUseAnnotation Cloneable, @TypeUseAnnotation Consumer<T1>
    {
    }

    protected @TypeAnnotation @TypeUseAnnotation enum E 
            implements @TypeUseAnnotation Cloneable, @TypeUseAnnotation Consumer<Object> {

        ;

        @Override
        public void accept(Object t) {
            throw new UnsupportedOperationException("Not supported yet."); 
        }
    }
    
    <A> A simpleTypeParameter(A a) {return null;}
    <A extends Map & Serializable> A typeParameterWithBounds(A a) {return null;}
    <@TypeParameterAnnotation A> A annotatedTypeParameter(A a) {return null;}
    <@TypeParameterAnnotation A extends Map<Object, @TypeUseAnnotation Object> & @TypeUseAnnotation Serializable> A annotatedTypeParameterWithBound(A a) {return null;}
    
}