package com.mscg;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public record Passport(List<PassportField> fields) {

    public boolean isValidPart1() {
        Set<FieldType> types = fields.stream()
                .map(PassportField::type)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(FieldType.class)));
        return types.containsAll(FieldType.MANDATORY_FIELDS);
    }

    public boolean isValidPart2() {
        if (!isValidPart1()) {
            return false;
        }

        Optional<PassportField> invalidField = fields.stream()
                .filter(field -> switch (field.type()) {
                    case byr -> field.isInvalidYear(1920, 2002);
                    case iyr -> field.isInvalidYear(2010, 2020);
                    case eyr -> field.isInvalidYear(2020, 2030);
                    case hgt -> field.isInvalidHeight();
                    case hcl -> field.isInvalidRGB();
                    case ecl -> field.isInvalidEyeColor();
                    case pid -> field.isInvalidId();
                    case cid -> false;
                })
                .findAny();
        return invalidField.isEmpty();
    }

    private static Passport fromStrings(final List<String> passportLines) {
        List<PassportField> fields = passportLines.stream()
                .flatMap(s -> Arrays.stream(s.split(" ")))
                .filter(s -> !s.isBlank())
                .map(String::trim)
                .map(PassportField::fromString)
                .collect(Collectors.toList());
        return new Passport(fields);
    }

    public static List<Passport> parseInput(BufferedReader in) throws Exception {
        final var passports = new ArrayList<Passport>();
        String line;
        final var passportLines = new ArrayList<String>();
        while ((line = in.readLine()) != null) {
            if (line.isBlank()) {
                passports.add(Passport.fromStrings(passportLines));
                passportLines.clear();
            } else {
                passportLines.add(line);
            }
        }
        if (!passports.isEmpty()) {
            passports.add(Passport.fromStrings(passportLines));
        }
        return passports;
    }

    public static record PassportField(FieldType type, String value) {

        private boolean isInvalidEyeColor() {
            return !Set.of("amb", "blu", "brn", "gry", "grn", "hzl", "oth").contains(value());
        }

        public boolean isInvalidYear(int min, int max) {
            if (value.length() != 4) {
                return true;
            }
            try {
                int year = Integer.parseInt(value);
                return year < min || year > max;
            } catch (NumberFormatException e) {
                return true;
            }
        }

        public boolean isInvalidHeight() {
            if (!value.endsWith("cm") && !value.endsWith("in")) {
                return true;
            }
            try {
                int height = Integer.parseInt(value.substring(0, value.length() - 2));
                String unit = value.substring(value.length() - 2);
                return switch (unit) {
                    case "cm" -> height < 150 || height > 193;
                    case "in" -> height < 59 || height > 76;
                    default -> true; // this should never happen
                };
            } catch (NumberFormatException e) {
                return true;
            }
        }

        public boolean isInvalidId() {
            if (value.length() != 9) {
                return true;
            }
            for (int i = 0; i < 9; i++) {
                char c = value.charAt(i);
                if (c < '0' || c > '9') {
                    return true;
                }
            }
            return false;
        }

        public boolean isInvalidRGB() {
            if (value.length() != 7 && !value.startsWith("#")) {
                return true;
            }
            for (int i = 1; i < 7; i++) {
                char c = value.charAt(i);
                if ((c < '0' || c > '9') && (c < 'a' || c > 'f')) {
                    return true;
                }
            }

            return false;
        }

        public static PassportField fromString(String line) {
            String[] parts = line.split(":");
            return new PassportField(FieldType.valueOf(parts[0]), parts[1].trim());
        }

    }

    @RequiredArgsConstructor
    @Getter
    public enum FieldType {
        byr("Birth Year"),
        iyr("Issue Year"),
        eyr("Expiration Year"),
        hgt("Height"),
        hcl("Hair Color"),
        ecl("Eye Color"),
        pid("Passport ID"),
        cid("Country ID");

        private final String description;

        public static final Set<FieldType> MANDATORY_FIELDS = EnumSet.complementOf(EnumSet.of(FieldType.cid));
    }

}
