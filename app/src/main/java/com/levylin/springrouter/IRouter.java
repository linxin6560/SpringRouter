package com.levylin.springrouter;

import android.content.Context;

import com.levylin.springrouter.lib.annotation.SRClientPath;

import java.util.HashMap;
import java.util.List;

/**
 * Created by LinXin on 2017/7/26.
 */

public interface IRouter {

    @SRClientPath("test_module://aaa")
    void testa(Context context);

    @SRClientPath("test_module://bbb")
    void testb(List<String> list);

    @SRClientPath("test://aaa")
    void testc();

    @SRClientPath("test://bbb")
    void testd(Context context, HashMap<String, String> map);
}
