package com.predisw.common.collect;

import com.google.common.collect.HashMultiset;
import java.util.List;
import java.util.stream.Collectors;

public class ListUtils {


    public static <E> List<E> getDuplicateElements(List<E> list) {
        return list.stream()                              // list 对应的 Stream
                .collect(Collectors.toMap(e -> e, e -> 1, Integer::sum)) // 获得元素出现频率的 Map，键为元素，值为元素出现的次数
                .entrySet().stream()                   // 所有 entry 对应的 Stream
                .filter(entry -> entry.getValue() > 1) // 过滤出元素出现次数大于 1 的 entry
                .map(entry -> entry.getKey())          // 获得 entry 的键（重复元素）对应的 Stream
                .collect(Collectors.toList());         // 转化为 List
    }

    public static <E> List<E> getDuplicateElementsWithMultiSet(List<E> list) {
        List<E> results = HashMultiset.create(list)
                .entrySet().stream()
                .filter(w -> w.getCount() > 1)
                .map(entry -> entry.getElement())
                .collect(Collectors.toList());
        return results;
    }

}
