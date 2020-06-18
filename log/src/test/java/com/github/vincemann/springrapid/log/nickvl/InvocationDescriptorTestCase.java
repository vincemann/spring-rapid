/***********************************************************************************
 * Copyright (c) 2013. Nickolay Gerilovich. Russia.
 *   Some Rights Reserved.
 ************************************************************************************/

package com.github.vincemann.springrapid.log.nickvl;

import com.github.vincemann.springrapid.log.nickvl.annotation.*;
import org.junit.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.rules.MethodRule;
import org.junit.rules.TestWatchman;
import org.junit.runners.model.FrameworkMethod;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Tests {@link InvocationDescriptor} with log annotated methods.
 */
public class InvocationDescriptorTestCase {

    private Method currMethod;
    private AnnotationInfo<Logging> loggingAnnotationInfo;
    private AnnotationInfo<LogException> logExceptionAnnotationInfo;
    private AnnotationParser annotationParser = new HierarchicalAnnotationParser();


    @Rule
    public MethodRule watchman = new TestWatchman() {
        public void starting(FrameworkMethod method) {
            currMethod = method.getMethod();
            loggingAnnotationInfo = annotationParser.fromMethodOrClass(currMethod, Logging.class);
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
        assertNull(descriptor.getBeforeSeverity());
        assertNull(descriptor.getAfterSeverity());
        assertNull(descriptor.getExceptionAnnotation());
    }

    @Test
    @LogDebug(LogPoint.IN)
    public void testGetBeforeSeverity() throws Exception {
        InvocationDescriptor descriptor = new InvocationDescriptor.Builder(loggingAnnotationInfo,logExceptionAnnotationInfo).build();
        assertSame(Severity.DEBUG, descriptor.getBeforeSeverity());
        assertNull(descriptor.getAfterSeverity());
        assertNull(descriptor.getExceptionAnnotation());
    }

    @Test
    @LogInfo(LogPoint.IN)
    @LogDebug(LogPoint.IN)
    public void testGetBeforeSeverityByPriority() throws Exception {
        InvocationDescriptor descriptor = new InvocationDescriptor.Builder(loggingAnnotationInfo,logExceptionAnnotationInfo).build();
        assertSame(Severity.INFO, descriptor.getBeforeSeverity());
        assertNull(descriptor.getAfterSeverity());
        assertNull(descriptor.getExceptionAnnotation());
    }

    @Test
    @LogDebug(LogPoint.OUT)
    public void testGetAfterSeverity() throws Exception {
        InvocationDescriptor descriptor = new InvocationDescriptor.Builder(loggingAnnotationInfo,logExceptionAnnotationInfo).build();
        assertSame(Severity.DEBUG, descriptor.getAfterSeverity());
        assertNull(descriptor.getBeforeSeverity());
        assertNull(descriptor.getExceptionAnnotation());
    }

    @Test
    @LogInfo(LogPoint.OUT)
    @LogDebug(LogPoint.OUT)
    public void testGetAfterSeverityByPriority() throws Exception {
        InvocationDescriptor descriptor = new InvocationDescriptor.Builder(loggingAnnotationInfo,logExceptionAnnotationInfo).build();
        assertSame(Severity.INFO, descriptor.getAfterSeverity());
        assertNull(descriptor.getBeforeSeverity());
        assertNull(descriptor.getExceptionAnnotation());
    }

    @Test
    @LogDebug
    public void testGetSeverity() throws Exception {
        InvocationDescriptor descriptor = new InvocationDescriptor.Builder(loggingAnnotationInfo,logExceptionAnnotationInfo).build();
        assertSame(Severity.DEBUG, descriptor.getBeforeSeverity());
        assertSame(Severity.DEBUG, descriptor.getAfterSeverity());
        assertNull(descriptor.getExceptionAnnotation());
    }

    @Test
    @LogWarn
    @LogInfo(LogPoint.OUT)
    @LogDebug(LogPoint.IN)
    @LogTrace
    public void testGetSeverityByPriority() throws Exception {
        InvocationDescriptor descriptor = new InvocationDescriptor.Builder(loggingAnnotationInfo,logExceptionAnnotationInfo).build();
        assertSame(Severity.DEBUG, descriptor.getBeforeSeverity());
        assertSame(Severity.INFO, descriptor.getAfterSeverity());
        assertNull(descriptor.getExceptionAnnotation());
    }

    @Test
    @LogException
    public void testGetExceptionAnnotation() throws Exception {
        InvocationDescriptor descriptor = new InvocationDescriptor.Builder(loggingAnnotationInfo,logExceptionAnnotationInfo).build();
        assertNull(descriptor.getBeforeSeverity());
        assertNull(descriptor.getAfterSeverity());
        assertNotNull(descriptor.getExceptionAnnotation());
    }

    @Test
    @LogInfo
    @LogException
    public void testGetAll() throws Exception {
        InvocationDescriptor descriptor = new InvocationDescriptor.Builder(loggingAnnotationInfo,logExceptionAnnotationInfo).build();
        assertSame(Severity.INFO, descriptor.getBeforeSeverity());
        assertSame(Severity.INFO, descriptor.getAfterSeverity());
        assertNotNull(descriptor.getExceptionAnnotation());
    }
}
