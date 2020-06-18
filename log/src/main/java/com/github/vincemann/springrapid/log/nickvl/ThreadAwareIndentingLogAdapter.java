package com.github.vincemann.springrapid.log.nickvl;

import lombok.Setter;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Adds padding to msg, adds ThreadId at the end of payload,
 * adds indentation depending on prior logged msg calls on same thread.
 * -> makes call stack of logged methods visible
 */
@Setter
public class ThreadAwareIndentingLogAdapter extends UniversalLogAdapter {
    private static final String EMPTY_LINE = " "+System.lineSeparator();
    private final Map<Thread, Integer> thread_amountOpenMethodCalls = new HashMap<>();
    private String HARD_START_PADDING_CHAR = "+";
    private String HARD_END_PADDING_CHAR = "=";
    private String SOFT_PADDING_CHAR = "_";
    private int LENGTH = 122;
    private int INDENTATION_LENGTH = 16;
    private String INDENTATION_CHAR = " ";

    public ThreadAwareIndentingLogAdapter(boolean skipNullFields, int cropThreshold, Set<String> excludeFieldNames) {
        super(skipNullFields, cropThreshold, excludeFieldNames);
    }

    public ThreadAwareIndentingLogAdapter(boolean skipNullFields, Set<String> excludeFieldNames) {
        super(skipNullFields, excludeFieldNames);
    }

    @Override
    public Object toMessage(String method, Object[] args, ArgumentDescriptor argumentDescriptor) {
        String msg = (String) super.toMessage(method,args,argumentDescriptor);
        int openMethodCalls = incrementOpenMethodCalls();
        return format(msg,openMethodCalls);
    }

    @Override
    public Object toMessage(String method, int argCount, Object result) {
        String msg = (String) super.toMessage(method, argCount, result);
        int openMethodCalls = decrementOpenMethodCalls();
        return format(msg,openMethodCalls);
    }

    @Override
    public Object toMessage(String method, int argCount, Exception e, boolean stackTrace) {
        String msg = (String) super.toMessage(method, argCount, e, stackTrace);
        int openMethodCalls = decrementOpenMethodCalls();
        return format(msg,openMethodCalls);
    }

    protected String format(String msg, int openMethodCalls){
        String indentation = createIdentation(openMethodCalls);
        String padding = createPadding(msg, SOFT_PADDING_CHAR);
        String endPadding = createPadding("", HARD_END_PADDING_CHAR);
        return new StringBuilder()
                .append(EMPTY_LINE)
                .append(indentation).append(padding).append(msg).append(getThreadInfoSuffix()).append(padding).append(System.lineSeparator())
                .append(indentation).append(endPadding.repeat(1)).append(System.lineSeparator())
                .append(EMPTY_LINE)
                .toString();
    }


    protected static String getThreadInfoSuffix() {
        return "  ,Thread: " + Thread.currentThread().getId() + "  ";
    }

    protected String createPadding(String middlePart, String paddingChar) {
        int paddingLength = (LENGTH - middlePart.length()) / 2;
        return paddingChar.repeat(paddingLength);
    }


    protected String createIdentation(int openMethodCalls){
        return INDENTATION_CHAR.repeat((openMethodCalls - 1) * INDENTATION_LENGTH);
    }

    private int incrementOpenMethodCalls() {
        int openMethodCalls;
        Integer currentCalls = thread_amountOpenMethodCalls.get(Thread.currentThread());
        if (currentCalls == null) {
            thread_amountOpenMethodCalls.put(Thread.currentThread(), 1);
        } else {
            int incremented = currentCalls + 1;
            thread_amountOpenMethodCalls.put(Thread.currentThread(), incremented);
        }
        openMethodCalls = thread_amountOpenMethodCalls.get(Thread.currentThread());
        return openMethodCalls;
    }

    private int decrementOpenMethodCalls() {
        int openMethodCalls;
        Integer currentCalls = thread_amountOpenMethodCalls.get(Thread.currentThread());
        Assert.notNull(currentCalls);
        int decremented = currentCalls - 1;
        //update
        thread_amountOpenMethodCalls.put(Thread.currentThread(), decremented);
        //get updated
        openMethodCalls = thread_amountOpenMethodCalls.get(Thread.currentThread());
        return openMethodCalls;
    }


}
