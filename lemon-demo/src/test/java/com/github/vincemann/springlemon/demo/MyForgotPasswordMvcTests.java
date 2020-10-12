package com.github.vincemann.springlemon.demo;

import com.github.vincemann.springlemon.authtests.ForgotPasswordMvcTests;
import com.github.vincemann.springlemon.demo.adapter.MyLemonTestAdapter;
import org.springframework.context.annotation.Import;

@Import(MyLemonTestAdapter.class)
public class MyForgotPasswordMvcTests extends ForgotPasswordMvcTests {
}
