package com.github.vincemann.springlemon.demo;

import com.github.vincemann.springlemon.authtests.BasicMvcTests;
import com.github.vincemann.springlemon.demo.adapter.MyLemonTestAdapter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan;

//@Import(MyLemonTestAdapter.class)
@ComponentScan("com.github.vincemann.springlemon.demo")
public class MyBasicMvcTest extends BasicMvcTests {
}
