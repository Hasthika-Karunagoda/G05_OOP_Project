package com.RockyToys.ToyManagement.dsa;

import com.RockyToys.ToyManagement.model.Toy;
import java.util.ArrayList;
import java.util.List;

public final class SelectionSort {
    private SelectionSort() {}

    public static List<Toy> sortByAgeGroup(List<Toy> input) {
        List<Toy> toys = new ArrayList<>(input);
        for (int i = 0; i < toys.size() - 1; i++) {
            int min = i;
            for (int j = i + 1; j < toys.size(); j++) {
                if (ageKey(toys.get(j).getAgeGroup()).compareTo(ageKey(toys.get(min).getAgeGroup())) < 0) min = j;
            }
            if (min != i) {
                Toy temp = toys.get(i); toys.set(i, toys.get(min)); toys.set(min, temp);
            }
        }
        return toys;
    }

    private static String ageKey(String value) {
        String v = value == null ? "" : value.toLowerCase().replace("years", "").replace("year", "").trim();
        try {
            return String.format("%08d", Integer.parseInt(v.replaceAll("[^0-9]", "")));
        } catch (NumberFormatException ex) {
            return "99999999" + v;
        }
    }
}
