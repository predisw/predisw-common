package com.predisw.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ModelUtil {

    private static Class[] comparableType = {String.class, Date.class, Enum.class, Byte.class, Double.class, Float.class,
            Integer.class, Long.class, Short.class};

    public static <T> Result isEqualByBasicFieldValue(T t1, T t2,String[] excludedFields){

        StringBuffer errorSb = new StringBuffer();
        boolean isEqual = true;
        Result result = new ResultBuilder().isEqual(isEqual).build();

        if(t1 == null && t2 == null){
            return new ResultBuilder().isEqual(true).build();
        }

        if(t1 == null){
            errorSb.append("one of the compared object is null but the other object of ").append(t2.getClass()).append(" is not");
            return new ResultBuilder().isEqual(false).setErrorMsg(errorSb.toString()).build();
        }

        if(t2 == null){
            errorSb.append("one of the compared object is null but the other object of ").append(t1.getClass()).append(" is not");
            return new ResultBuilder().isEqual(false).setErrorMsg(errorSb.toString()).build();
        }

        if(t1 instanceof Collection){
            if(((Collection) t1).size() != ((Collection) t2).size()){
                errorSb.append("The size of t1 is not equal to ").append("t2");
                return new ResultBuilder().isEqual(false).setErrorMsg(errorSb.toString()).build();
            }

            Iterator iterator1 = ((Collection) t1).iterator();
            Iterator iterator2 = ((Collection) t2).iterator();
            while (iterator1.hasNext()){
                result = isEqualByBasicFieldValue(iterator1.next(),iterator2.next(),excludedFields);
                if(result.isEqual() != true){
                    break;
                }
            }
            return result;
        }

        if(t1 instanceof Map){
            if(((Map) t1).size() != ((Map) t2).size()){
                errorSb.append("The size of t1 is not equal to ").append("t2");
                return new ResultBuilder().isEqual(false).setErrorMsg(errorSb.toString()).build();
            }

            Map map1 = ((Map) t1);
            Map map2 = ((Map) t2);
            for(Object key : map1.keySet()){
                result = isEqualByBasicFieldValue(map1.get(key),map2.get(key),excludedFields);
                if(result.isEqual() != true){
                    break;
                }
            }
            return result;
        }


        Field[] fields = t1.getClass().getDeclaredFields();
        List<String> fieldNames = Arrays.asList(fields).stream().map(field -> field.getName()).collect(Collectors.toList());
        Optional.ofNullable(excludedFields).ifPresent(excludes -> fieldNames.removeAll(Arrays.asList(excludes)));

        for (String fieldName : fieldNames) {
            try {
                Method getMethod1 = t1.getClass().getDeclaredMethod(combineMethodName(fieldName),null);
                Method getMethod2 = t2.getClass().getDeclaredMethod(combineMethodName(fieldName),null);
                Object value1 = getMethod1.invoke(t1,null);
                Object value2 = getMethod2.invoke(t2,null);

//                System.out.println(getMethod1.getName());
//                System.out.println(getMethod2.getName());
                if(Arrays.asList(comparableType).contains(getMethod1.getReturnType())){
                    isEqual = Objects.equals(value1,value2);
                    if(isEqual != true){
                        errorSb.append(getMethod1.getName()).append("'s value ").append(value1).append(" is not equal to ")
                                .append(getMethod2.getName()).append("'s value ").append(value2);
                        result = new ResultBuilder().isEqual(isEqual).setErrorMsg(errorSb.toString()).build();
                        break;
                    }
                }
                else {
                    result = isEqualByBasicFieldValue(value1,value2,excludedFields);
                }

            } catch (Exception e) {
                throw new IllegalStateException("Exception when compare field value which get by getMethod",e);
            }
        }
        return result;
    }


    private static String combineMethodName(String fieldName){
        String getMethodStr = new StringBuffer("get").append(fieldName.substring(0,1).toUpperCase()).append(fieldName.substring(1,fieldName.length())).toString();

        return getMethodStr;

    }

    public static class Result {
        private boolean equal;
        private String errorMsg;

        private Result(){}

        private Result(ResultBuilder builder){
            this.equal = builder.result.isEqual();
            this.errorMsg = builder.result.getErrorMsg();
        }

        public boolean isEqual() {
            return equal;
        }

        public String getErrorMsg() {
            return errorMsg;
        }
    }

    public static class ResultBuilder {
        private Result result;

        public ResultBuilder(){
            this.result = new Result();
        }

        public ResultBuilder isEqual(boolean isEqual){
            this.result.equal = isEqual;
            return this;
        }

        public ResultBuilder setErrorMsg(String errorMsg){
            this.result.errorMsg = errorMsg;
            return this;
        }

        public Result build(){
            return new Result(this);
        }
    }


}
