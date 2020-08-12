package com.example.hitschedule;

import com.example.hitschedule.util.HttpUtil;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Example local unit logo, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void wechatPostTest() throws IOException {
        for (int day = 1; day <= 7; ++day) {
            String json = HttpUtil.wechatBksKbPost("1190501001", "2019-2020;2", day);
            if (json == null) {
                System.out.println("error on day" + day);
            } else {
                System.out.println(json);
            }
        }
    }
}