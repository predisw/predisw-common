package com.predisw.common.sys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemTools {
    private static Logger logger = LoggerFactory.getLogger(SystemTools.class);


    /**
     * return Optional.empty() if error.
     * @param cmd
     * @return
     */
    public static Optional<String> callBashCmd(String cmd) {

        try {
            Process ps = Runtime.getRuntime().exec(new String[] {"/bin/sh", "-c", cmd});
            ps.waitFor();

            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            String resultStr = sb.toString();

            logger.debug("command {} output is \n{}", cmd, resultStr);

            return Optional.of(resultStr);

        } catch (IOException | InterruptedException e) {
            logger.error("Exception when execute {}",cmd,e);
        }

        return Optional.empty();
    }



}
