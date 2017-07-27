package com.levylin.test_module;

import android.content.Context;

import com.levylin.springrouter.lib.annotation.SRouterPath;

import java.util.List;

/**
 * Created by LinXin on 2017/7/25.
 */
public class TestRouter2 {

    @SRouterPath("test_module://ccc")
    public void test(Context context) {
        System.out.println("test_module://aaa is called");
    }

    @SRouterPath("test_module://ddd")
    public void testbbb(List<String> list) {
        System.out.println("test_module://bbb is called");
    }
}
