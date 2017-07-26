package com.levylin.springrouter;

import android.content.Context;

import com.levylin.springrouter.lib.annotation.SRServerPath;

import java.util.HashMap;

/**
 * Created by LinXin on 2017/7/24.
 */
public class MainRouter {

    @SRServerPath("test://aaa")
    public void test() {
        System.out.println("测试serverPath....");
    }

    @SRServerPath("test://bbb")
    public void test2(Context context, HashMap<String, String> map) {
        System.out.println("test://bbb is called");
    }
}
