package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.codepoetics.protonpack.StreamUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TuringMachine {

    private final int maxSteps;
    private final char initialStatus;
    private final Map<State, Action> instructions;
    private final Map<Long, Integer> tape = new HashMap<>();

    public long computeChecksum() {
        char status = initialStatus;
        long headPosition = 0;
        for (int step = 0; step < maxSteps; step++) {
            final int curValue = tape.getOrDefault(headPosition, 0);
            final var action = instructions.get(new State(status, curValue));
            status = action.nextStatus();
            tape.put(headPosition, action.value());
            headPosition += switch (action.movement) {
                case RIGHT -> 1;
                case LEFT -> -1;
            };
        }

        return tape.values().stream() //
                .filter(v -> v != 0) //
                .count();
    }

    public static TuringMachine parseInput(final BufferedReader in) throws IOException {
        final char initialStatus;
        {
            final var pattern = Pattern.compile("Begin in state (.)\\.");
            final var matcher = pattern.matcher(in.readLine());
            if (!matcher.find()) {
                throw new IllegalArgumentException("Invalid input, missing initial state directive");
            }
            initialStatus = matcher.group(1).charAt(0);
        }

        final int maxSteps;
        {
            final var pattern = Pattern.compile("Perform a diagnostic checksum after (\\d+) steps\\.");
            final var matcher = pattern.matcher(in.readLine());
            if (!matcher.find()) {
                throw new IllegalArgumentException("Invalid input, missing max steps directive");
            }
            maxSteps = Integer.parseInt(matcher.group(1));
        }

        final Map<State, Action> instructions = new LinkedHashMap<>();
        {

            final var statePattern = Pattern.compile("In state (.):");
            final var currentValuePattern = Pattern.compile(" {2}If the current value is (\\d+):");
            final var writePattern = Pattern.compile(" {4}- Write the value (\\d+).");
            final var movePattern = Pattern.compile(" {4}- Move one slot to the ([^.]+).");
            final var nextStatePattern = Pattern.compile(" {4}- Continue with state (.).");
            StreamUtils.windowed(in.lines(), 10, 10) //
                    .forEach(instructionsStr -> {
                        final char stateName;
                        {
                            final var matcher = statePattern.matcher(instructionsStr.get(1));
                            if (!matcher.find()) {
                                throw new IllegalArgumentException("Invalid input, missing state directive");
                            }
                            stateName = matcher.group(1).charAt(0);
                        }
                        {

                            final int tapeValue;
                            {
                                final var matcher = currentValuePattern.matcher(instructionsStr.get(2));
                                if (!matcher.find()) {
                                    throw new IllegalArgumentException("Invalid input, missing current value directive");
                                }
                                tapeValue = Integer.parseInt(matcher.group(1));
                            }

                            final int writeValue;
                            {
                                final var matcher = writePattern.matcher(instructionsStr.get(3));
                                if (!matcher.find()) {
                                    throw new IllegalArgumentException("Invalid input, missing new value directive");
                                }
                                writeValue = Integer.parseInt(matcher.group(1));
                            }

                            final Movement movement;
                            {
                                final var matcher = movePattern.matcher(instructionsStr.get(4));
                                if (!matcher.find()) {
                                    throw new IllegalArgumentException("Invalid input, missing movement directive");
                                }
                                movement = Movement.valueOf(matcher.group(1).toUpperCase());
                            }

                            final char nextStateName;
                            {
                                final var matcher = nextStatePattern.matcher(instructionsStr.get(5));
                                if (!matcher.find()) {
                                    throw new IllegalArgumentException("Invalid input, missing next state directive");
                                }
                                nextStateName = matcher.group(1).charAt(0);
                            }

                            instructions.put(new State(stateName, tapeValue), new Action(writeValue, movement, nextStateName));
                        }
                        {

                            final int tapeValue;
                            {
                                final var matcher = currentValuePattern.matcher(instructionsStr.get(6));
                                if (!matcher.find()) {
                                    throw new IllegalArgumentException("Invalid input, missing current value directive");
                                }
                                tapeValue = Integer.parseInt(matcher.group(1));
                            }

                            final int writeValue;
                            {
                                final var matcher = writePattern.matcher(instructionsStr.get(7));
                                if (!matcher.find()) {
                                    throw new IllegalArgumentException("Invalid input, missing new value directive");
                                }
                                writeValue = Integer.parseInt(matcher.group(1));
                            }

                            final Movement movement;
                            {
                                final var matcher = movePattern.matcher(instructionsStr.get(8));
                                if (!matcher.find()) {
                                    throw new IllegalArgumentException("Invalid input, missing movement directive");
                                }
                                movement = Movement.valueOf(matcher.group(1).toUpperCase());
                            }

                            final char nextStateName;
                            {
                                final var matcher = nextStatePattern.matcher(instructionsStr.get(9));
                                if (!matcher.find()) {
                                    throw new IllegalArgumentException("Invalid input, missing next state directive");
                                }
                                nextStateName = matcher.group(1).charAt(0);
                            }

                            instructions.put(new State(stateName, tapeValue), new Action(writeValue, movement, nextStateName));
                        }
                    });
        }

        return new TuringMachine(maxSteps, initialStatus, Map.copyOf(instructions));
    }

    private enum Movement {
        LEFT, RIGHT
    }

    private static record State(char state, int tapeValue) {
    }

    private static record Action(int value, Movement movement, char nextStatus) {
    }

}
