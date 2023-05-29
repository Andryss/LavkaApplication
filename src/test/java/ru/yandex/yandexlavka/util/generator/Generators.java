package ru.yandex.yandexlavka.util.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class Generators {

    public static <T> List<T> createListOf(int length, Supplier<T> supplier) {
        ArrayList<T> list = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            list.add(supplier.get());
        }
        return list;
    }

    public static <T> Set<T> createSetOf(int length, Supplier<T> supplier) {
        HashSet<T> set = new HashSet<>();
        for (int i = 0; i < length; i++) {
            set.add(supplier.get());
        }
        return set;
    }

}
