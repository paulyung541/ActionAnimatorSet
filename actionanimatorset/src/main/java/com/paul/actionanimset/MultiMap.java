package com.paul.actionanimset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yang on 2016/10/15.
 * paulyung@outlook.com
 * one key multi-values map
 */

public class MultiMap<K, V> {
    private Map<K, List<V>> map;

    public MultiMap() {
        map = new HashMap<>();
    }

    public V put(K key, V value) {
        Set<K> set = map.keySet();
        List<V> values = null;
        if (set.contains(key)) {
            values = map.get(key);
            values.add(value);
        } else {
            if ((values = map.get(key)) == null)
                values = new ArrayList<>();
            values.add(value);
            map.put(key, values);
        }
        return value;
    }

    public List<V> get(K key) {
        Set<K> set = map.keySet();
        if (set.contains(key))
            return map.get(key);
        else
            return null;
    }

    public int keySize() {
        Set<K> set = map.keySet();
        return set.size();
    }


    public int valueSize(K key) {
        Set<K> set = map.keySet();
        if (set.contains(key))
            return map.get(key).size();
        else
            return 0;
    }

    public V remove(K key, V value) {
        Set<K> set = map.keySet();
        if (set.contains(key)) {
            List<V> list = map.get(key);
            if (list.contains(value))
                list.remove(value);
            else
                return null;
        } else {
            return null;
        }
        return value;
    }

    public Set<K> keySet() {
        return map.keySet();
    }

    public void clear() {
        map.clear();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }
}
