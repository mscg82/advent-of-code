package com.mscg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdventDay11Test {
    
    @Test
    public void testNextPassword() {
        Assertions.assertEquals("xy", PasswordManager.next("xx"));
        Assertions.assertEquals("xz", PasswordManager.next("xy"));
        Assertions.assertEquals("ya", PasswordManager.next("xz"));
        Assertions.assertEquals("yb", PasswordManager.next("ya"));
        Assertions.assertEquals("abcdefgi", PasswordManager.next("abcdefgh"));
    }

    @Test
    public void testValid() {
        Assertions.assertFalse(PasswordManager.isValid("hijklmmn"));
        Assertions.assertFalse(PasswordManager.isValid("abbceffg"));
        Assertions.assertFalse(PasswordManager.isValid("abbcegjk"));
        Assertions.assertFalse(PasswordManager.isValid("abcdaaa"));
        Assertions.assertTrue(PasswordManager.isValid("abcdffaa"));
        Assertions.assertFalse(PasswordManager.isValid("abcdaaaa"));
        Assertions.assertTrue(PasswordManager.isValid("ghjaabcc"));
        Assertions.assertFalse(PasswordManager.isValid("ghjaaaaa"));
        Assertions.assertFalse(PasswordManager.isValid("ghjaaabb"));
        Assertions.assertFalse(PasswordManager.isValid("ghjaabba"));
    }

    @Test
    public void testNextValidPassword() {
        Assertions.assertEquals("abcdffaa", PasswordManager.nextValid("abcdefgh"));
        Assertions.assertEquals("ghjaabcc", PasswordManager.nextValid("ghijklmn"));
    }

}
