package com.poll.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * 集合工具类
 */
public class ListUtil {

    public static <T> List<List<T>> divider(Collection<T> datas, Comparator<? super T> c) {
        List<List<T>> result = new ArrayList<List<T>>();
        for (T t : datas) {
            boolean isSameGroup = false;
            for (int j = 0; j < result.size(); j++) {
                if (c.compare(t, result.get(j).get(0)) == 0) {
                    isSameGroup = true;
                    result.get(j).add(t);
                    break;
                }
            }
            if (!isSameGroup) {
                List<T> innerList = new ArrayList<T>();
                result.add(innerList);
                innerList.add(t);
            }
        }
        return result;
    }

    /**
     * 分割list,指定分割长度
     *
     * @param resList
     * @param count
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> split(List<T> resList, int count) {
        List<List<T>> ret = new ArrayList<>();
        if (resList != null && !resList.isEmpty()) {
            int size = resList.size();
            if (size <= count || count < 1) {
                ret.add(resList);
            } else {
                int pre = size / count;
                int last = size % count;
                for (int i = 0; i < pre; i++) {
                    List<T> itemList = new ArrayList<T>();
                    for (int j = 0; j < count; j++) {
                        itemList.add(resList.get(i * count + j));
                    }
                    ret.add(itemList);
                }
                if (last > 0) {
                    List<T> itemList = new ArrayList<T>();
                    for (int i = 0; i < last; i++) {
                        itemList.add(resList.get(pre * count + i));
                    }
                    ret.add(itemList);
                }
            }
        }
        return ret;
    }
}
