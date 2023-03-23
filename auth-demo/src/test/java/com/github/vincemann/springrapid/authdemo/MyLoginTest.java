package com.github.vincemann.springrapid.authdemo;

import com.github.vincemann.springrapid.authdemo.adapter.EnableProjectComponentScan;
import com.github.vincemann.springrapid.authtests.LoginTest;
import org.springframework.test.context.jdbc.Sql;

// is already done in autoconfig RapidAclSchemaAutoConfiguration
//@Sql("classpath:acl-schema.sql")
@EnableProjectComponentScan
public class MyLoginTest extends LoginTest {
}
