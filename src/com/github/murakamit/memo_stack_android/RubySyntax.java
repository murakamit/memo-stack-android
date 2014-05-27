
package com.github.murakamit.memo_stack_android;

public class RubySyntax {

    public static interface void_1<T> {
        void execute(T t);
    }

    public static class Array {
        public static <T> void each(T[] ary, void_1<T> f) {
            for (T t : ary) {
                f.execute(t);
            }
        }
    }

}
