package com.levylin.springrouter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.levylin.springrouter.lib.SpringRouter;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SpringRouter.init(this);
        IRouter router = SpringRouter.create(IRouter.class);
        router.testa(this);
        router.testb(new ArrayList<String>());
        router.testc();
        router.testd(this, new HashMap<String, String>());
    }
}
