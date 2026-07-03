package com.hezapp.ekonomis.test_application

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.hezapp.ekonomis.base_test_utils.test_application.MyKoinTestApplication

class MyTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, MyKoinTestApplication::class.java.name, context)
    }
}