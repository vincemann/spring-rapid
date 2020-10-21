package com.github.vincemann.springrapid.authdemo;

import com.github.vincemann.springrapid.authtests.BasicMvcTests;
import org.springframework.context.annotation.ComponentScan;

//@Import(MyLemonTestAdapter.class)
@ComponentScan("com.github.vincemann.springrapid.authdemo")
public class MyBasicMvcTest extends BasicMvcTests {
}
