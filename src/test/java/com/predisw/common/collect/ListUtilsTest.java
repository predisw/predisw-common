package com.predisw.common.collect;




import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class ListUtilsTest {


    @Test
    public void getDuplicateElementsTest(){
        List<Long> longList = com.google.common.collect.Lists.newArrayList(1L,2L,30000L,30000L,1L);
        List<Long> repeatLong = ListUtils.getDuplicateElements(longList);
        System.out.println(repeatLong);

        Assertions.assertThat(repeatLong).contains(1L).contains(30000L);

    }


    @Test
    public void getDuplicateElementsWithMultiSetTest(){
        List<Long> longList = Lists.newArrayList(1L,2L,30000L,30000L,1L);
        List<Long> repeatLong = ListUtils.getDuplicateElementsWithMultiSet(longList);
        System.out.println(repeatLong);
        Assertions.assertThat(repeatLong).contains(1L).contains(30000L);
    }

    @Test
    public void getDuplicateElementsPerformanceTest(){
        getDuplicateElementsPerformanceTest(100);
        getDuplicateElementsPerformanceTest(1000);
        getDuplicateElementsPerformanceTest(10000);

        for(int i =0;i< 3;i++){
            getDuplicateElementsPerformanceTest(1000000);
        }
    }



    public void getDuplicateElementsPerformanceTest(int capacity){
        long before = System.currentTimeMillis();
        List<Long> longList = getLongListWithValue(capacity);
        long after = System.currentTimeMillis();

        System.out.println("generate list time for "+ capacity+ " : "+(after - before));

        longList.add(1L);
        longList.add(capacity/5,1L);
        longList.set(capacity/4,300000L);
        longList.add(capacity/3,300000L);
        longList.add(capacity/2,100000L);


        long beforeGetDuplicated = System.currentTimeMillis();
        List<Long> repeatLong = ListUtils.getDuplicateElements(longList);
        long afterGetDuplicated = System.currentTimeMillis();
        System.out.println("generate list time for JDK implement : "+(afterGetDuplicated - beforeGetDuplicated));
        System.out.println(repeatLong);


        long beforeGetDuplicatedWithMultiSet = System.currentTimeMillis();
        repeatLong = ListUtils.getDuplicateElementsWithMultiSet(longList);
        long afterGetDuplicatedWithMultiSet = System.currentTimeMillis();
        System.out.println("generate list time for HashMultiset implement: "+(afterGetDuplicatedWithMultiSet - beforeGetDuplicatedWithMultiSet));

        System.out.println(repeatLong);
    }


    private List<Long> getLongListWithValue(int capacity){

        List<Long> longList = new ArrayList<>(capacity);
        for(int i =0;i < capacity;i++){
            longList.add(Long.valueOf(i));
        }
        return longList;

    }


}
