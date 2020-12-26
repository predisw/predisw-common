package com.predisw.common.util;

import com.google.common.collect.Lists;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class CMDToolsTest {

    @Test
    public void execTest(){

        String pVersion =  CMDTools.execute("python", new String[]{"--version"});
        Assertions.assertThat(pVersion).contains("Python");
        System.out.println("python version is " + pVersion);

        String spiderOut =  CMDTools.execute("python", new String[]{"-m", "fund_data.spider"});
        System.out.println("python spider output is " + spiderOut);

    }


}
