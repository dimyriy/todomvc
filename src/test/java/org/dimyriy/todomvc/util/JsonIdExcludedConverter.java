package org.dimyriy.todomvc.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author dimyriy
 * @date 23/02/16
 */
class JsonIdExcludedConverter {
    static String toJson(Object obj) {
        return createGsonProcessor().toJson(obj);
    }

    private static Gson createGsonProcessor() {
        return new GsonBuilder().setExclusionStrategies(new IdExclusionStrategy()).create();
    }
}
