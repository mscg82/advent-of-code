package com.mscg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay12Test {

    @Test
    public void testClean() throws Exception {
        {
            String input = """
                    [1,2,3]
                    """;
            Assertions.assertEquals(6, JsonCleaner.sumValues(JsonCleaner.cleanJson(input)));
        }
        {
            String input = """
                    [1,{"c":"red","b":2},3]
                    """;
            Assertions.assertEquals(4, JsonCleaner.sumValues(JsonCleaner.cleanJson(input)));
        }
        {
            String input = """
                    {"d":"red","e":[1,2,3,4],"f":5}
                    """;
            Assertions.assertEquals(0, JsonCleaner.sumValues(JsonCleaner.cleanJson(input)));
        }
        {
            String input = """
                    [1,"red",5]
                    """;
            Assertions.assertEquals(6, JsonCleaner.sumValues(JsonCleaner.cleanJson(input)));
        }
    }

}
