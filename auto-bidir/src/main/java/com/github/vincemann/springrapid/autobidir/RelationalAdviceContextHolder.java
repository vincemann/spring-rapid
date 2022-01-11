package com.github.vincemann.springrapid.autobidir;

import java.util.HashMap;
import java.util.Map;

public class RelationalAdviceContextHolder {
    private static Map<Thread,RelationalAdviceContext> context = new HashMap<>();

    public static RelationalAdviceContext getContext() {
        return context.get(Thread.currentThread());
    }

    public static void clear(){
        context.remove(Thread.currentThread());
    }

    public static void setContext(RelationalAdviceContext context){
        RelationalAdviceContextHolder.context.put(Thread.currentThread(),context);
    }
}
