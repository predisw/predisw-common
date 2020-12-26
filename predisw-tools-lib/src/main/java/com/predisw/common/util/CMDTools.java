package com.predisw.common.util;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class CMDTools {
    private static Logger logger = LoggerFactory.getLogger(CMDTools.class);

    public static String execute(String cmd, String[] params){
        List<String> command = Lists.asList(cmd, params);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        StringBuilder builder = new StringBuilder();
        try {
            Process process = processBuilder.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            int exitCode = process.waitFor();
            logger.debug("the exitCode = {} and the response is {}", exitCode, builder.toString());
        } catch (IOException e) {
            logger.error("Exception when exec CMD", e);
        } catch (InterruptedException e) {
            logger.error("InterruptedException when exec CMD", e);
        }
        return builder.toString();
    }




}



