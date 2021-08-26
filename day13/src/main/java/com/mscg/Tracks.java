package com.mscg;

import com.codepoetics.protonpack.StreamUtils;
import io.soabase.recordbuilder.core.RecordBuilder;
import lombok.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static java.util.function.Predicate.not;

public record Tracks(List<List<Cell>> cells, List<Cart> carts) {

    public Position findFirstClash() {
        final List<Cart> carts = new ArrayList<>(this.carts);
        for (long tick = 1; tick <= 1_000_000; tick++) {
            moveCarts(carts);
            carts.sort(Comparator.naturalOrder());
            // check if there is a clash
            final Optional<Position> clashPosition = carts.stream() //
                    .filter(Cart::dead) //
                    .findFirst() //
                    .map(Cart::position);
            if (clashPosition.isPresent()) {
                return clashPosition.get();
            }
        }
        throw new IllegalStateException("Can't find a clash");
    }

    public Position findLastCartPosition() {
        final List<Cart> carts = new ArrayList<>(this.carts);
        for (long tick = 1; tick <= 1_000_000; tick++) {
            moveCarts(carts);
            carts.sort(Comparator.naturalOrder());
            final List<Cart> liveCarts = carts.stream() //
                    .filter(not(Cart::dead)) //
                    .toList();
            if (liveCarts.size() == 1) {
                return liveCarts.get(0).position();
            }
        }
        throw new IllegalStateException("Can't find a clash");
    }

    public static Tracks parseInput(final BufferedReader in) throws IOException {
        List<String> input = in.lines().toList();

        final int maxLength = input.stream().mapToInt(String::length).max().orElseThrow();
        input = input.stream() //
                .map(line -> {
                    if (line.length() == maxLength) {
                        return line;
                    } else {
                        final var str = new StringBuilder(maxLength);
                        str.append(line);
                        while (str.length() < maxLength) {
                            str.append(' ');
                        }
                        return str.toString();
                    }
                }) //
                .toList();

        final List<List<Cell>> cells = input.stream() //
                .map(line -> line.chars() //
                        .mapToObj(c -> Direction.from((char) c) //
                                .map(Direction::toCell) //
                                .orElseGet(() -> Cell.from((char) c))) //
                        .toList()) //
                .toList();

        final List<Cart> carts = StreamUtils.zipWithIndex(input.stream()) //
                .flatMap(rowIdx -> {
                    final long y = rowIdx.getIndex();
                    final String line = rowIdx.getValue();
                    return StreamUtils.zipWithIndex(line.chars().mapToObj(c -> (char) c)) //
                            .flatMap(cellIdx -> {
                                final long x = cellIdx.getIndex();
                                return Direction.from(cellIdx.getValue()) //
                                        .map(d -> new Cart(false, new Position((int) x, (int) y), d, 0)) //
                                        .stream();
                            });
                }) //
                .sorted() //
                .toList();

        return new Tracks(cells, carts);
    }

    private void moveCarts(final List<Cart> carts) {
        for (int i = 0, l = carts.size(); i < l; i++) {
            var cart = carts.get(i);
            if (cart.dead()) {
                continue;
            }

            final Cell cell = cells.get(cart.position().y()).get(cart.position().x());

            carts.set(i, switch (cell) {
                case HORIZONTAL, VERTICAL -> cart.withPosition(cart.position().move(cart.direction));
                case CURVE_RIGTH -> { // /
                    final var newDirection = switch (cart.direction()) {
                        case LEFT, RIGTH -> cart.direction().turnLeft();
                        case UP, DOWN -> cart.direction().turnRight();
                    };
                    yield cart.with(c -> {
                        c.direction(newDirection);
                        c.position(c.position().move(newDirection));
                    });
                }
                case CURVE_LEFT -> { // \
                    final var newDirection = switch (cart.direction()) {
                        case LEFT, RIGTH -> cart.direction().turnRight();
                        case UP, DOWN -> cart.direction().turnLeft();
                    };
                    yield cart.with(c -> {
                        c.direction(newDirection);
                        c.position(c.position().move(newDirection));
                    });
                }
                case INTERSECTION -> { // +
                    final var newDirection = switch (cart.turnsTaken() % 3) {
                        case 0 -> cart.direction().turnLeft();
                        case 1 -> cart.direction();
                        case 2 -> cart.direction().turnRight();
                        default -> throw new IllegalStateException("Impossible situation");
                    };
                    yield cart.with(c -> {
                        c.turnsTaken(c.turnsTaken() + 1);
                        c.direction(newDirection);
                        c.position(c.position().move(newDirection));
                    });
                }
                case EMPTY -> throw new IllegalStateException("Cart is derailed");
            });
            cart = carts.get(i);

            // check for collisions
            for (int j = 0; j < l; j++) {
                if (i == j) {
                    continue;
                }
                final var otherCart = carts.get(j);
                if (otherCart.dead()) {
                    continue;
                }

                if (otherCart.position().equals(cart.position())) {
                    carts.set(i, cart.withDead(true));
                    carts.set(j, otherCart.withDead(true));
                    break;
                }
            }
        }

    }

    @RecordBuilder
    public static record Position(int x, int y) implements TracksPositionBuilder.With, Comparable<Position> {

        private static final Comparator<Position> COMPARATOR = Comparator.comparingInt(Position::y).thenComparingInt(Position::x);

        public Position move(final Direction direction) {
            return switch (direction) {
                case UP -> this.withY(y - 1);
                case RIGTH -> this.withX(x + 1);
                case DOWN -> this.withY(y + 1);
                case LEFT -> this.withX(x - 1);
            };
        }

        @Override
        public int compareTo(@NonNull final Position other) {
            return COMPARATOR.compare(this, other);
        }

        @Override
        public String toString() {
            return x + "," + y;
        }

    }

    @RecordBuilder
    public static record Cart(boolean dead, Position position, Direction direction,
                              int turnsTaken) implements TracksCartBuilder.With, Comparable<Cart> {

        private static final Comparator<Cart> COMPARATOR = Comparator.comparing(Cart::position);

        @Override
        public int compareTo(@NonNull final Cart other) {
            return COMPARATOR.compare(this, other);
        }

    }

    public enum Direction {
        UP, RIGTH, DOWN, LEFT;

        public Cell toCell() {
            return switch (this) {
                case UP, DOWN -> Cell.VERTICAL;
                case LEFT, RIGTH -> Cell.HORIZONTAL;
            };
        }

        public Direction turnLeft() {
            return switch (this) {
                case UP -> LEFT;
                case RIGTH -> UP;
                case DOWN -> RIGTH;
                case LEFT -> DOWN;
            };
        }

        public Direction turnRight() {
            return switch (this) {
                case UP -> RIGTH;
                case RIGTH -> DOWN;
                case DOWN -> LEFT;
                case LEFT -> UP;
            };
        }

        @Override
        public String toString() {
            return switch (this) {
                case UP -> "^";
                case RIGTH -> ">";
                case DOWN -> "v";
                case LEFT -> "<";
            };
        }

        public static Optional<Direction> from(final char c) {
            return switch (c) {
                case '^' -> Optional.of(UP);
                case '>' -> Optional.of(RIGTH);
                case 'v' -> Optional.of(DOWN);
                case '<' -> Optional.of(LEFT);
                default -> Optional.empty();
            };
        }
    }

    public enum Cell {
        EMPTY, VERTICAL, HORIZONTAL, CURVE_RIGTH, CURVE_LEFT, INTERSECTION;

        @Override
        public String toString() {
            return switch (this) {
                case EMPTY -> " ";
                case VERTICAL -> "|";
                case HORIZONTAL -> "-";
                case CURVE_RIGTH -> "/";
                case CURVE_LEFT -> "\\";
                case INTERSECTION -> "+";
            };
        }

        public static Cell from(final char c) {
            return switch (c) {
                case ' ' -> EMPTY;
                case '|' -> VERTICAL;
                case '-' -> HORIZONTAL;
                case '/' -> CURVE_RIGTH;
                case '\\' -> CURVE_LEFT;
                case '+' -> INTERSECTION;
                default -> throw new IllegalArgumentException("Unsupported value \"" + c + "\"");
            };
        }
    }

}
