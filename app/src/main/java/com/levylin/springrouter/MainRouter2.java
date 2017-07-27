package com.levylin.springrouter;

import android.content.Context;

import com.levylin.springrouter.lib.annotation.SRouterPath;

import java.util.HashMap;

/**
 * Created by LinXin on 2017/7/24.
 */
public class MainRouter2 {

    @SRouterPath("test://ccc")
    public void test() {
        System.out.println("测试serverPath....");
    }

    @SRouterPath("test://ddd")
    public void test2(Context context, HashMap<String, String> map) {
        System.out.println("test://bbb is called");
    }
}
