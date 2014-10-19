package me.paulbgd.socketapi.sockets.utils;

import java.util.HashMap;

/**
 * @author PaulBGD
 */
public class MapBuilder<K, V> extends HashMap<K, V> {

    public MapBuilder<K, V> $(K key, V value) {
        put(key, value);
        return this;
    }

}
