package com.mscg;

import com.codepoetics.protonpack.Indexed;
import com.codepoetics.protonpack.StreamUtils;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.IntPredicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record TicketDB(List<ValidityRule> validityRules, Ticket yourTicket, List<Ticket> nearbyTickets) {

    public Map<String, Integer> getMappedTicket() {
        final List<String> fields = findFields();
        final Map<Long, String> indexToField = StreamUtils.zipWithIndex(fields.stream())
                .collect(Collectors.toMap(Indexed::getIndex, Indexed::getValue));

        return StreamUtils.zipWithIndex(Arrays.stream(yourTicket.values()).boxed())
                .collect(Collectors.toMap(idx -> indexToField.get(idx.getIndex()), Indexed::getValue));
    }

    public List<String> findFields() {
        var validTickets= getValidTickets();
        var indexToValues = new TreeMap<Integer, List<Integer>>();
        Stream.concat(Stream.of(yourTicket), validTickets.stream())
                .flatMap(ticket -> StreamUtils.zipWithIndex(Arrays.stream(ticket.values()).boxed()))
                .forEach(idx -> indexToValues.computeIfAbsent((int) idx.getIndex(), __ -> new ArrayList<>()).add(idx.getValue()));

        List<int[]> rulesPossibleIndexes = new ArrayList<>(this.validityRules.size());
        for (ValidityRule validityRule : this.validityRules) {
            final int[] possibleColumns = indexToValues.entrySet().stream()
                    .filter(entry -> entry.getValue().stream().mapToInt(Integer::intValue).allMatch(validityRule))
                    .map(Map.Entry::getKey)
                    .mapToInt(Integer::intValue)
                    .toArray();
            rulesPossibleIndexes.add(possibleColumns);
        }
        final Map<Integer, Set<Integer>> ruleToColumns = StreamUtils.zipWithIndex(rulesPossibleIndexes.stream())
                .sorted(Comparator.comparingInt(idx -> idx.getValue().length))
                .map(idx -> Indexed.index(idx.getIndex(), Arrays.stream(idx.getValue()).boxed().collect(Collectors.toCollection(LinkedHashSet::new))))
                .collect(Collectors.toMap(idx -> (int) idx.getIndex(), Indexed::getValue, (v1, v2) -> v1, LinkedHashMap::new));

        int maxIndex = indexToValues.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
        List<String> fields = Arrays.asList(new String[maxIndex + 1]);

        var processedIndexes = new ArrayList<Integer>();
        for (Map.Entry<Integer, Set<Integer>> indexedRule : ruleToColumns.entrySet()) {
            int indexRule = indexedRule.getKey();
            var possibleColumns = indexedRule.getValue();

            var validityRule = this.validityRules.get(indexRule);

            possibleColumns.removeAll(processedIndexes);
            if (possibleColumns.size() != 1) {
                throw new IllegalArgumentException("Indecidable rule " + validityRule);
            }
            int column = possibleColumns.iterator().next();
            processedIndexes.add(column);

            fields.set(column, validityRule.name());
        }

        return fields;
    }

    public List<Ticket> getValidTickets() {
        final IntPredicate allInvalid = getInvalidValuePredicate();

        return nearbyTickets.stream()
                .filter(ticket -> Arrays.stream(ticket.values()).noneMatch(allInvalid))
                .collect(Collectors.toList());
    }

    public int getTicketErrorRate() {
        final IntPredicate allInvalid = getInvalidValuePredicate();


        final int[] invalidValues = nearbyTickets.stream()
                .map(Ticket::values)
                .flatMapToInt(Arrays::stream)
                .filter(allInvalid)
                .toArray();

        return Arrays.stream(invalidValues).sum();
    }

    private IntPredicate getInvalidValuePredicate() {
        return validityRules.stream()
                .map(IntPredicate::negate)
                .reduce(i -> true, IntPredicate::and);
    }

    public static TicketDB parseInput(BufferedReader in) throws Exception {
        enum State {
            RULES, MY_TICKET, NEARBY_TICKETS
        }

        var state = State.RULES;
        final List<ValidityRule> validityRules = new ArrayList<>();
        Ticket yourTicket = null;
        final List<Ticket> nearbyTickets = new ArrayList<>();

        String line;
        while ((line = in.readLine()) != null) {
            if (line.isBlank()) {
                state = switch (state) {
                    case RULES -> State.MY_TICKET;
                    case MY_TICKET -> State.NEARBY_TICKETS;
                    case NEARBY_TICKETS -> throw new IllegalArgumentException("No rule should follow the nearby tickets");
                };

                continue;
            }

            switch (state) {
                case RULES -> validityRules.add(ValidityRule.fromString(line));
                case MY_TICKET -> {
                    if ("your ticket:".equals(line)) {
                        continue;
                    }
                    yourTicket = Ticket.fromString(line);
                }
                case NEARBY_TICKETS -> {
                    if ("nearby tickets:".equals(line)) {
                        continue;
                    }
                    nearbyTickets.add(Ticket.fromString(line));
                }
            }
        }

        return new TicketDB(List.copyOf(validityRules),
                Objects.requireNonNull(yourTicket, "Missing informations about your ticket"),
                List.copyOf(nearbyTickets));
    }

    public static record Ticket(int[] values) {

        public static Ticket fromString(String s) {
            final int[] values = Arrays.stream(s.split(","))
                    .map(String::trim)
                    .mapToInt(Integer::parseInt)
                    .toArray();
            return new Ticket(values);
        }

    }

    public static record InclusiveRange(int min, int max) implements IntPredicate {
        public InclusiveRange {
            if (min > max) {
                throw new IllegalArgumentException("min must be less than or equal to max");
            }
        }

        @Override
        public boolean test(int value) {
            return min <= value && value <= max;
        }

        public static InclusiveRange fromString(String s) {
            String[] parts = s.split("-");
            return new InclusiveRange(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }

    }

    public static record ValidityRule(String name, InclusiveRange range1, InclusiveRange range2) implements IntPredicate {

        private static final Pattern FORMAT = Pattern.compile("^([^:]+):\\s*(.+)\\s+or\\s+(.+)$");

        public static ValidityRule fromString(String s) {
            var matcher = FORMAT.matcher(s);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("Invalid rule \"" + s + "\"");
            }

            return new ValidityRule(matcher.group(1),
                    InclusiveRange.fromString(matcher.group(2)),
                    InclusiveRange.fromString(matcher.group(3)));
        }

        @Override
        public boolean test(int value) {
            return range1.test(value) || range2.test(value);
        }

    }

}
