package com.levylin.test_module;

import android.content.Context;

import com.levylin.springrouter.lib.annotation.SRServerPath;

import java.util.List;

/**
 * Created by LinXin on 2017/7/25.
 */
public class TestRouter {

    @SRServerPath("test_module://aaa")
    public void test(Context context) {
        System.out.println("test_module://aaa is called");
    }

    @SRServerPath("test_module://bbb")
    public void testbbb(List<String> list) {
        System.out.println("test_module://bbb is called");
    }
}
