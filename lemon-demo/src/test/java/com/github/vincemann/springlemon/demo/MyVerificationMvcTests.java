package com.github.vincemann.springlemon.demo;

import com.github.vincemann.springlemon.authtests.VerificationMvcTests;
import com.github.vincemann.springlemon.demo.adapter.MyLemonTestAdapter;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.github.vincemann.springlemon.demo")
public class MyVerificationMvcTests extends VerificationMvcTests {
}
