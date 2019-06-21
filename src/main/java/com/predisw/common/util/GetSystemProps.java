package com.predisw.common.util;

import java.io.File;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

public class GetSystemProps {

	public static void main(String[] args) {
		
		printSysEnvAndProps();
		//System.out.println(System.getProperty("platform"));

		
	}
	
	
	public static void  printSysEnvAndProps(){
		
		Map<String,String> map=System.getenv();
		
		System.out.println("---------------System ENV--------------");
		for(Entry<String, String> entity:map.entrySet()){
			System.out.println(entity.getKey()+":"+entity.getValue());
		}
		
		
		System.out.println("\r\n---------------System Properties--------------");
		Properties props = System.getProperties();
		props.list(System.out);
		
	}
}
