package com.predisw.common.sys;

import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class SystemToolsTest {


    @Test
    public void callBashCmdTest(){

        String cmd = "python -c \"import sys; print sys.path\"";

        Optional<String> output = SystemTools.callBashCmd(cmd);


        Assertions.assertThat(output.isPresent()).isTrue();
        Assertions.assertThat(output.get()).contains("python");

        //['', '/usr/lib/python2.7', '/usr/lib/python2.7/plat-x86_64-linux-gnu', '/usr/lib/python2.7/lib-tk', '/usr/lib/python2.7/lib-old', '/usr/lib/python2.7/lib-dynload', '/home/eggnwwg/.local/lib/python2.7/site-packages', '/usr/local/lib/python2.7/dist-packages', '/usr/lib/python2.7/dist-packages']
        System.out.println(output.get());

    }

}
