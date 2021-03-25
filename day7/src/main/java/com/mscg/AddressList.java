package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AddressList {

    private final List<Address> addresses;

    public long countTLSAddresses() {
        return addresses.stream() //
                .filter(Address::hasTLS) //
                .count();
    }

    public static AddressList parseInput(BufferedReader in) throws IOException {
        List<Address> addresses = in.lines() //
                .map(line -> {
                    List<String> simpleParts = new ArrayList<>();
                    List<String> hypernets = new ArrayList<>();
                    
                    StringBuilder part = new StringBuilder();
                    for (int i = 0, l = line.length(); i < l; i++) {
                        char c = line.charAt(i);
                        switch (c) {
                            case '[' -> {
                                simpleParts.add(part.toString());
                                part.setLength(0);
                            }
                            case ']' -> {
                                hypernets.add(part.toString());
                                part.setLength(0);
                            }
                            default -> part.append(c);
                        }
                    }
                    if (part.length() != 0) {
                        simpleParts.add(part.toString());
                    }

                    return new Address(List.copyOf(simpleParts), List.copyOf(hypernets));
                }) //
                .collect(Collectors.toUnmodifiableList());
        return new AddressList(addresses);
    }

    public static record Address(List<String> simpleParts, List<String> hypernets) {

        public boolean hasTLS() {
            return hypernets.stream().noneMatch(AddressList::hasABBASequence)
                    && simpleParts.stream().anyMatch(AddressList::hasABBASequence);
        }

    }

    private static boolean hasABBASequence(String string) {
        for (int i = 0, l = string.length(); i < l - 3; i++) {
            char c1 = string.charAt(i);
            char c2 = string.charAt(i + 1);
            char c3 = string.charAt(i + 2);
            char c4 = string.charAt(i + 3);
            if (c1 == c4 && c2 == c3 && c1 != c2) {
                return true;
            }
        }
        return false;
    }
}
