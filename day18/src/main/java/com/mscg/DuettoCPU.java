package com.mscg;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.ToLongFunction;

@RequiredArgsConstructor
public class DuettoCPU implements ToLongFunction<DuettoCPU.Register> {

    private final Map<Register, Long> registers = new HashMap<>();

    private final Channel receivedData = new Channel(new LinkedBlockingQueue<>(), new AtomicBoolean(false));

    private Channel sendChannel;

    private final List<Instruction> instructions;

    public void bind(final DuettoCPU otherCPU) {
        otherCPU.sendChannel = receivedData;
        this.sendChannel = otherCPU.receivedData;
    }

    @Override
    public long applyAsLong(final Register register) {
        return register(register);
    }

    public long register(final Register register) {
        return registers.computeIfAbsent(register, __ -> 0L);
    }

    public long register(final Register register, final long value) {
        final Long oldValue = registers.put(register, value);
        return oldValue == null ? 0 : oldValue;
    }

    private void run(final boolean stopAtRetrieved) {
        int pc = 0;
        while (pc < instructions.size()) {
            final var currentInstruction = instructions.get(pc);
            final int jump = currentInstruction.execute(this);
            pc += jump;
            if (stopAtRetrieved && (currentInstruction instanceof Rcv && registers.get(SpecialRegister.RETRIEVED) != null)) {
                return;
            }
        }
    }

    public long retrieveSound() {
        run(true);
        return register(SpecialRegister.RETRIEVED);
    }

    @SneakyThrows
    public long runDuetto() {
        register(new NamedRegister('p'), 0);
        final var otherCPU = new DuettoCPU(instructions);
        otherCPU.register(new NamedRegister('p'), 1);
        bind(otherCPU);

        final var executor = Executors.newFixedThreadPool(2);
        final Future<?> thisRun = executor.submit(() -> this.run(false));
        final Future<?> otherRun = executor.submit(() -> otherCPU.run(false));

        thisRun.get();
        otherRun.get();

        executor.shutdown();

        return otherCPU.register(SpecialRegister.SEND_COUNT);
    }

    public static DuettoCPU parseInput(final BufferedReader in, final boolean duettoMode) throws IOException {
        try {
            final List<Instruction> instructions = in.lines() //
                    .map(line -> Instruction.parse(line, duettoMode)) //
                    .toList();
            return new DuettoCPU(instructions);
        } catch (final UncheckedIOException e) {
            throw e.getCause();
        }
    }

    private static record Channel(BlockingQueue<Long> data, AtomicBoolean blocked) {

    }

    public interface Register {

    }

    public enum SpecialRegister implements Register {
        OUTPUT, RETRIEVED, SEND_COUNT
    }

    public static record NamedRegister(char name) implements Register {

    }

    public interface Value {

        long get(ToLongFunction<DuettoCPU.Register> valueExtractor);

        static Value parse(final String val) {
            try {
                return new Constant(Integer.parseInt(val));
            } catch (final NumberFormatException e) {
                return new RegisterVal(new NamedRegister(val.charAt(0)));
            }
        }

    }

    public static record Constant(int value) implements Value {

        @Override
        public long get(final ToLongFunction<DuettoCPU.Register> valueExtractor) {
            return value;
        }

    }

    public static record RegisterVal(NamedRegister register) implements Value {

        @Override
        public long get(final ToLongFunction<DuettoCPU.Register> valueExtractor) {
            return valueExtractor.applyAsLong(register);
        }

    }

    public interface Instruction {

        int execute(DuettoCPU cpu);

        static Instruction parse(final String line, final boolean duettoMode) {
            final String[] parts = line.split(" ");
            return switch (parts[0]) {
                case "snd" -> duettoMode ? new Send(Value.parse(parts[1])) : new Snd(Value.parse(parts[1]));
                case "set" -> new Set(new NamedRegister(parts[1].charAt(0)), Value.parse(parts[2]));
                case "add" -> new Add(new NamedRegister(parts[1].charAt(0)), Value.parse(parts[2]));
                case "mul" -> new Mul(new NamedRegister(parts[1].charAt(0)), Value.parse(parts[2]));
                case "mod" -> new Mod(new NamedRegister(parts[1].charAt(0)), Value.parse(parts[2]));
                case "rcv" -> duettoMode ? new Recv(new NamedRegister(parts[1].charAt(0))) : new Rcv(Value.parse(parts[1]));
                case "jgz" -> new Jgz(Value.parse(parts[1]), Value.parse(parts[2]));
                default -> throw new IllegalArgumentException("Unsupported instruction " + line);
            };
        }

    }

    public static record Snd(Value value) implements Instruction {

        @Override
        public int execute(final DuettoCPU cpu) {
            cpu.register(SpecialRegister.OUTPUT, value.get(cpu));
            return 1;
        }

    }

    public static record Send(Value value) implements Instruction {

        @Override
        public int execute(final DuettoCPU cpu) {
            cpu.register(SpecialRegister.SEND_COUNT, cpu.register(SpecialRegister.SEND_COUNT) + 1);
            cpu.sendChannel.data().add(value.get(cpu));
            return 1;
        }

    }

    public static record Set(NamedRegister target, Value value) implements Instruction {

        @Override
        public int execute(final DuettoCPU cpu) {
            cpu.register(target, value.get(cpu));
            return 1;
        }

    }

    public static record Add(NamedRegister target, Value amount) implements Instruction {

        @Override
        public int execute(final DuettoCPU cpu) {
            cpu.register(target, cpu.register(target) + amount.get(cpu));
            return 1;
        }

    }

    public static record Mul(NamedRegister target, Value amount) implements Instruction {

        @Override
        public int execute(final DuettoCPU cpu) {
            cpu.register(target, cpu.register(target) * amount.get(cpu));
            return 1;
        }

    }

    public static record Mod(NamedRegister target, Value amount) implements Instruction {

        @Override
        public int execute(final DuettoCPU cpu) {
            cpu.register(target, cpu.register(target) % amount.get(cpu));
            return 1;
        }

    }

    public static record Rcv(Value trigger) implements Instruction {

        @Override
        public int execute(final DuettoCPU cpu) {
            if (trigger.get(cpu) != 0) {
                cpu.register(SpecialRegister.RETRIEVED, cpu.registers.get(SpecialRegister.OUTPUT));
            }
            return 1;
        }

    }

    public static record Recv(Register target) implements Instruction {

        @Override
        @SneakyThrows
        public int execute(final DuettoCPU cpu) {
            if (cpu.receivedData.data().isEmpty()) {
                cpu.receivedData.blocked().set(true);
                if (cpu.sendChannel.blocked().get()) {
                    cpu.sendChannel.data().add(Long.MIN_VALUE);
                    return cpu.instructions.size();
                }
            }
            final long readData = cpu.receivedData.data().take();
            if (readData == Long.MIN_VALUE) {
                return cpu.instructions.size();
            }
            cpu.receivedData.blocked().set(false);
            cpu.register(target, readData);
            return 1;
        }

    }

    public static record Jgz(Value trigger, Value amount) implements Instruction {

        @Override
        public int execute(final DuettoCPU cpu) {
            if (trigger.get(cpu) <= 0) {
                return 1;
            }
            return (int) amount.get(cpu);
        }

    }

}
