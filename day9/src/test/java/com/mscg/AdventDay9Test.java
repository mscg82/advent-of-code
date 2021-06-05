package com.mscg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay9Test {

    @Test
    public void testGetGroups() {
        {
            final var dataReader = new DataReader("{}");
            Assertions.assertEquals(1, dataReader.getGroups().size());
            Assertions.assertEquals(1, DataReader.getScore(dataReader.getGroups()));
        }
        {
            final var dataReader = new DataReader("{{{}}}");
            Assertions.assertEquals(3, dataReader.getGroups().size());
            Assertions.assertEquals(6, DataReader.getScore(dataReader.getGroups()));
        }
        {
            final var dataReader = new DataReader("{{},{}}");
            Assertions.assertEquals(3, dataReader.getGroups().size());
            Assertions.assertEquals(5, DataReader.getScore(dataReader.getGroups()));
        }
        {
            final var dataReader = new DataReader("{{{},{},{{}}}}");
            Assertions.assertEquals(6, dataReader.getGroups().size());
            Assertions.assertEquals(16, DataReader.getScore(dataReader.getGroups()));
        }
        {
            final var dataReader = new DataReader("{<{},{},{{}}>}");
            Assertions.assertEquals(1, dataReader.getGroups().size());
            Assertions.assertEquals(1, DataReader.getScore(dataReader.getGroups()));
        }
        {
            final var dataReader = new DataReader("{<a>,<a>,<a>,<a>}");
            Assertions.assertEquals(1, dataReader.getGroups().size());
            Assertions.assertEquals(1, DataReader.getScore(dataReader.getGroups()));
        }
        {
            final var dataReader = new DataReader("{{<a>},{<a>},{<a>},{<a>}}");
            Assertions.assertEquals(5, dataReader.getGroups().size());
            Assertions.assertEquals(9, DataReader.getScore(dataReader.getGroups()));
        }
        {
            final var dataReader = new DataReader("{{<!>},{<!>},{<!>},{<a>}}");
            Assertions.assertEquals(2, dataReader.getGroups().size());
            Assertions.assertEquals(3, DataReader.getScore(dataReader.getGroups()));
        }
    }

    @Test
    public void testGarbageCollection() {
        {
            final var dataReader = new DataReader("<>");
            Assertions.assertEquals(0, dataReader.cleanGarbage().garbage().length());
        }
        {
            final var dataReader = new DataReader("<random characters>");
            Assertions.assertEquals(17, dataReader.cleanGarbage().garbage().length());
        }
        {
            final var dataReader = new DataReader("<<<<>");
            Assertions.assertEquals(3, dataReader.cleanGarbage().garbage().length());
        }
        {
            final var dataReader = new DataReader("<{!>}>");
            Assertions.assertEquals(2, dataReader.cleanGarbage().garbage().length());
        }
        {
            final var dataReader = new DataReader("<!!>");
            Assertions.assertEquals(0, dataReader.cleanGarbage().garbage().length());
        }
        {
            final var dataReader = new DataReader("<!!!>>");
            Assertions.assertEquals(0, dataReader.cleanGarbage().garbage().length());
        }
        {
            final var dataReader = new DataReader("<{o\"i!a,<{i<a>");
            Assertions.assertEquals(10, dataReader.cleanGarbage().garbage().length());
        }
    }

}
