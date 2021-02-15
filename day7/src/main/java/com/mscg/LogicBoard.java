package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LogicBoard {

    private final List<Instruction> instructions;

    public LogicBoard patch(Instruction instruction) {
        List<Instruction> patchedInstructions = instructions.stream() //
                .map(inst -> inst.target().equals(instruction.target()) ? instruction : inst) //
                .collect(Collectors.toUnmodifiableList());
        return new LogicBoard(patchedInstructions);
    }

    public Map<String, Constant> execute() {
        Map<String, Constant> portToValue = new HashMap<>();

        Map<String, Operator> portToOperator = instructions.stream() //
                .collect(Collectors.toMap(Instruction::target, Instruction::operator));

        while (!portToOperator.isEmpty()) {
            boolean modified = false;

            Map<String, Operator> newPortToOperator = new HashMap<>(portToOperator);

            for (var it = portToOperator.entrySet().iterator(); it.hasNext();) {
                var entry = it.next();
                String port = entry.getKey();
                Operator op = entry.getValue();
                if (op instanceof Constant c) {
                    portToValue.put(port, c);
                    it.remove();
                    newPortToOperator.remove(port);
                    modified = true;

                    for (var portToRemap : portToOperator.keySet()) {
                        Operator operatorToRemap = newPortToOperator.get(portToRemap);
                        newPortToOperator.put(portToRemap, operatorToRemap.bind(port, c.value()));
                    }
                }
            }

            portToOperator = newPortToOperator;

            if (!modified) {
                throw new IllegalStateException("Can't evolve board");
            }
        }

        return Map.copyOf(portToValue);
    }

    public static LogicBoard parseInput(BufferedReader in) throws IOException {
        List<Instruction> instructions = in.lines() //
                .map(Instruction::parseLine) //
                .collect(Collectors.toUnmodifiableList());

        return new LogicBoard(instructions);
    }

    public static record Instruction(Operator operator, String target) {

        public static Instruction parseLine(String line) {
            String[] parts = line.split("->");
            return new Instruction(Operator.parse(parts[0].trim()), parts[1].trim());
        }

    }

    public sealed interface Operator {

        Operator bind(String port, int value);

        public static Operator parse(String operator) {
            String[] parts = operator.split(" ");
            if (parts.length == 1) {
                return parseSimpleOperator(parts[0]);
            }

            if (parts.length == 2) {
                if (!"NOT".equalsIgnoreCase(parts[0].trim())) {
                    throw new IllegalArgumentException("Unknows operator " + operator);
                }
                return new Not(parts[1].trim());
            }

            if (parts.length == 3) {
                return switch (parts[1].trim().toUpperCase()) {
                    case "AND" -> new And(parseSimpleOperator(parts[0]), parseSimpleOperator(parts[2]));
                    case "OR" -> new Or(parseSimpleOperator(parts[0]), parseSimpleOperator(parts[2]));
                    case "LSHIFT" -> new LShift(parts[0].trim(), Integer.parseInt(parts[2].trim()));
                    case "RSHIFT" -> new RShift(parts[0].trim(), Integer.parseInt(parts[2].trim()));
                    default -> throw new IllegalArgumentException("Unknows operator " + operator);
                };
            }

            throw new IllegalArgumentException("Unknows operator " + operator);
        }

        private static SimpleOperator parseSimpleOperator(String operator) {
            try {
                return new Constant(Integer.parseInt(operator.trim()));
            } catch (NumberFormatException e) {
                return new Line(operator);
            }
        }

    }

    public sealed interface SimpleOperator extends Operator {
    }

    public static record Constant(int value) implements SimpleOperator {
        @Override
        public Operator bind(String port, int value) {
            return this;
        }
    }

    public static record Line(String line) implements SimpleOperator {
        @Override
        public Operator bind(String port, int value) {
            if (line.equals(port)) {
                return new Constant(value);
            }
            return this;
        }
    }

    public static record Not(String line) implements Operator {
        @Override
        public Operator bind(String port, int value) {
            if (line.equals(port)) {
                return new Constant(capTo16Bit(~value));
            }

            return this;
        }
    }

    public static record And(SimpleOperator op1, SimpleOperator op2) implements Operator {
        @Override
        public Operator bind(String port, int value) {
            final SimpleOperator op1 = bindOperator(op1(), port, value);
            final SimpleOperator op2 = bindOperator(op2(), port, value);
            
            if (op1 instanceof Constant c1 && op2 instanceof Constant c2) {
                return new Constant(capTo16Bit(c1.value() & c2.value()));
            }

            if (op1 == op1() && op2 == op2()) {
                return this;
            }

            return new And(op1, op2);
        }
    }

    public static record Or(SimpleOperator op1, SimpleOperator op2) implements Operator {
        @Override
        public Operator bind(String port, int value) {
            final SimpleOperator op1 = bindOperator(op1(), port, value);
            final SimpleOperator op2 = bindOperator(op2(), port, value);
            
            if (op1 instanceof Constant c1 && op2 instanceof Constant c2) {
                return new Constant(capTo16Bit(c1.value() | c2.value()));
            }

            if (op1 == op1() && op2 == op2()) {
                return this;
            }
            
            return new Or(op1, op2);
        }
    }

    public static record LShift(String line, int amount) implements Operator {
        @Override
        public Operator bind(String port, int value) {
            if (line.equals(port)) {
                return new Constant(capTo16Bit(value << amount));
            }
            return this;
        }
    }

    public static record RShift(String line, int amount) implements Operator {
        @Override
        public Operator bind(String port, int value) {
            if (line.equals(port)) {
                return new Constant(capTo16Bit(value >> amount));
            }
            return this;
        }
    }

    private static int capTo16Bit(int value) {
        return 0xFFFF & value;
    }

    private static SimpleOperator bindOperator(SimpleOperator source, String port, int value) {
        if (source instanceof Line l && l.line().equals(port)) {
            return new Constant(value);
        }
        return source;
    }

}
