package com.mscg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay19Test {

    @Test
    public void testParse() throws Exception {
        var medicineFactory = MedicineFactory.parseInput(readInput());

        Assertions.assertEquals("HOH", medicineFactory.getMolecule());
        Assertions.assertEquals(List.of( //
                Map.entry("H", "HO"), //
                Map.entry("H", "OH"), //
                Map.entry("O", "HH") //
        ), medicineFactory.getReplacements().stream() //
                .map(r -> Map.entry(r.source().toString(), r.target())) //
                .collect(Collectors.toList()));
    }

    @Test
    public void testVariants() throws Exception {
        var medicineFactory = MedicineFactory.parseInput(readInput());

        Assertions.assertEquals(Set.of( //
                "HOOH", //
                "HOHO", //
                "OHOH", //
                "HHHH" //
        ), medicineFactory.generateVariants());
    }

    private BufferedReader readInput() {
        return new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/test-input.txt"), StandardCharsets.UTF_8));
    }

}
