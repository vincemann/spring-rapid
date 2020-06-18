/***********************************************************************************
 * Copyright (c) 2013. Nickolay Gerilovich. Russia.
 *   Some Rights Reserved.
 ************************************************************************************/

package com.github.vincemann.springrapid.log.nickvl;

import com.github.vincemann.springrapid.log.nickvl.annotation.*;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestWatchman;
import org.junit.runners.model.FrameworkMethod;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Tests {@link com.github.vincemann.springrapid.log.nickvl.InvocationDescriptor} with log annotated methods and class.
 */
@Log(logPoint = LogPoint.OUT,level = Severity.INFO)
@LogException
public class InvocationDescriptorClassTestCase {

    private Method currMethod;
    private AnnotationInfo<Log> loggingAnnotationInfo;
    private AnnotationInfo<LogException> logExceptionAnnotationInfo;
    private AnnotationParser annotationParser = new HierarchicalAnnotationParser();


    @Rule
    public MethodRule watchman = new TestWatchman() {
        public void starting(FrameworkMethod method) {
            currMethod = method.getMethod();
            loggingAnnotationInfo = annotationParser.fromMethodOrClass(currMethod, Log.class);
            logExceptionAnnotationInfo = annotationParser.fromMethodOrClass(currMethod, LogException.class);
        }
    };

    @After
    public void tearDown() throws Exception {
        currMethod = null;
        logExceptionAnnotationInfo = null;
        loggingAnnotationInfo = null;
    }

    @Test
    public void testNoAnnotations() throws Exception {
        InvocationDescriptor descriptor = new InvocationDescriptor.Builder(loggingAnnotationInfo,logExceptionAnnotationInfo).build();
        assertSame(Severity.INFO, descriptor.getBeforeSeverity());
        assertSame(Severity.DEBUG, descriptor.getAfterSeverity());
        assertNotNull(descriptor.getExceptionAnnotation());
    }

    @Test
    @Log(Severity.TRACE)
    public void testGetSeverityByMethodPriority() throws Exception {
        InvocationDescriptor descriptor = new InvocationDescriptor.Builder(loggingAnnotationInfo,logExceptionAnnotationInfo).build();
        assertSame(Severity.TRACE, descriptor.getBeforeSeverity());
        assertSame(Severity.TRACE, descriptor.getAfterSeverity());
        assertNotNull(descriptor.getExceptionAnnotation());
    }


    @Test
    @LogException(value = {}, trace = @LogException.Exc(Exception.class))
    public void testGetExceptionAnnotationByMethodPriority() throws Exception {
        InvocationDescriptor descriptor = new InvocationDescriptor.Builder(loggingAnnotationInfo,logExceptionAnnotationInfo).build();
        assertSame(Severity.INFO, descriptor.getBeforeSeverity());
        assertSame(Severity.DEBUG, descriptor.getAfterSeverity());
        LogException exceptionAnnotation = descriptor.getExceptionAnnotation();
        assertEquals(0, exceptionAnnotation.value().length);
        assertEquals(1, exceptionAnnotation.trace().length);
        assertArrayEquals(exceptionAnnotation.trace()[0].value(), new Object[]{Exception.class});
        assertFalse(exceptionAnnotation.trace()[0].stacktrace());
    }
}
