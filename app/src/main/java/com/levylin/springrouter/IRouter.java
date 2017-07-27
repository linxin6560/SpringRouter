package com.levylin.springrouter;

import android.content.Context;

import com.levylin.springrouter.lib.annotation.SRouter;

import java.util.HashMap;
import java.util.List;

/**
 * Created by LinXin on 2017/7/26.
 */

public interface IRouter {

    @SRouter("test_module://aaa")
    void testa(Context context);

    @SRouter("test_module://bbb")
    void testb(List<String> list);

    @SRouter("test://aaa")
    void testc();

    @SRouter("test://bbb")
    void testd(Context context, HashMap<String, String> map);
}
