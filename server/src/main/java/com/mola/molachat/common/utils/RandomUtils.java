package com.mola.molachat.common.utils;

import java.util.*;

/**
 * @author : molamola
 * @Project: molachat
 * @Description:
 * @date : 2023-02-14 17:03
 **/
public class RandomUtils {

    private static Random random;

    //双重校验锁获取一个Random单例
    public static Random getRandom() {
        if(random==null){
            synchronized (RandomUtils.class) {
                if(random==null){
                    random =new Random();
                }
            }
        }

        return random;
    }

    /**
     * 获得一个[0,max)之间的整数。
     * @param max
     * @return
     */
    public static int getRandomInt(int max) {
        return Math.abs(getRandom().nextInt())%max;
    }

    /**
     * 获得一个[0,max)之间的整数。
     * @param max
     * @return
     */
    public static long getRandomLong(long max) {
        return Math.abs(getRandom().nextInt())%max;
    }

    /**
     * 从list中随机取得一个元素
     * @param list
     * @return
     */
    public static <E> E getRandomElement(List<E> list){
        return list.get(getRandomInt(list.size()));
    }

    /**
     * 从set中随机取得一个元素
     * @param set
     * @return
     */
    public static <E> E getRandomElement(Set<E> set){
        int rn = getRandomInt(set.size());
        int i = 0;
        for (E e : set) {
            if(i==rn){
                return e;
            }
            i++;
        }
        return null;
    }

    /**
     * 从map中随机取得一个key
     * @param map
     * @return
     */
    public static <K,V> K getRandomKeyFromMap(Map<K,V> map) {
        int rn = getRandomInt(map.size());
        int i = 0;
        for (K key : map.keySet()) {
            if(i==rn){
                return key;
            }
            i++;
        }
        return null;
    }

    /**
     * 从map中随机取得一个value
     * @param map
     * @return
     */
    public static <K,V> V getRandomValueFromMap(Map<K,V> map) {
        int rn = getRandomInt(map.size());
        int i = 0;
        for (V value : map.values()) {
            if(i==rn){
                return value;
            }
            i++;
        }
        return null;
    }
}
