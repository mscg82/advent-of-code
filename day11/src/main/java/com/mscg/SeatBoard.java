package com.mscg;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@EqualsAndHashCode
public class SeatBoard implements Cloneable {

    private final List<List<Tile>> board = new ArrayList<>();

    @SneakyThrows
    public SeatBoard next1() {
        SeatBoard next = (SeatBoard) this.clone();

        for (int i = 0, l = board.size(); i < l; i++) {
            List<Tile> line = board.get(i);
            for (int j = 0, l2 = line.size(); j < l2; j++) {
                List<Tile> neighbors = getNeighbors1(i, j, l, l2);
                next.board.get(i).set(j, board.get(i).get(j).next(neighbors));
            }
        }

        return next;
    }

    @SneakyThrows
    public SeatBoard next2() {
        SeatBoard next = (SeatBoard) this.clone();

        for (int i = 0, l = board.size(); i < l; i++) {
            List<Tile> line = board.get(i);
            for (int j = 0, l2 = line.size(); j < l2; j++) {
                List<Tile> neighbors = getNeighbors2(i, j);
                next.board.get(i).set(j, board.get(i).get(j).next2(neighbors));
            }
        }

        return next;
    }

    private List<Tile> getNeighbors1(int i, int j, int l, int l2) {
        List<Tile> neighbors = new ArrayList<>(8);
        for (int i2 = Math.max(0, i - 1), mi2 = Math.min(l - 1, i + 1); i2 <= mi2; i2++) {
            for (int j2 = Math.max(0, j - 1), mj2 = Math.min(l2 - 1, j + 1); j2 <= mj2; j2++) {
                if (i2 == i && j2 == j) {
                    continue;
                }
                neighbors.add(board.get(i2).get(j2));
            }
        }
        return neighbors;
    }

    private List<Tile> getNeighbors2(int i, int j) {
        return List.of(
                findNonEmptyTile(i, j, 0, -1),
                findNonEmptyTile(i, j, 1, -1),
                findNonEmptyTile(i, j, 1, 0),
                findNonEmptyTile(i, j, 1, 1),
                findNonEmptyTile(i, j, 0, 1),
                findNonEmptyTile(i, j, -1, 1),
                findNonEmptyTile(i, j, -1, 0),
                findNonEmptyTile(i, j, -1, -1)
        );
    }

    private Tile findNonEmptyTile(int i, int j, int di, int dj) {
        int nextI = i + di;
        int nextJ = j + dj;
        while (nextI >= 0 && nextI < board.size() && nextJ >= 0 && nextJ < board.get(nextI).size()) {
            Tile t = board.get(nextI).get(nextJ);
            if (t != Tile.FLOOR) {
                return t;
            }
            nextI += di;
            nextJ += dj;
        }
        return Tile.FLOOR;
    }

    public SeatBoard evolveUntilHalt1() {
        var lastBoard = this;
        var nextBoard = lastBoard.next1();

        while (!nextBoard.equals(lastBoard)) {
            lastBoard = nextBoard;
            nextBoard = lastBoard.next1();
        }

        return nextBoard;
    }

    public SeatBoard evolveUntilHalt2() {
        var lastBoard = this;
        var nextBoard = lastBoard.next2();

        while (!nextBoard.equals(lastBoard)) {
            lastBoard = nextBoard;
            nextBoard = lastBoard.next2();
        }

        return nextBoard;
    }

    public long countOccupied() {
        return board.stream()
                .flatMap(List::stream)
                .filter(tile -> tile == Tile.OCCUPIED)
                .count();
    }

    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    protected Object clone() {
        SeatBoard clone = new SeatBoard();
        board.forEach(line -> clone.board.add(new ArrayList<>(line)));
        return clone;
    }

    @Override
    public String toString() {
        return board.stream()
                .map(line -> line.stream().map(tile -> String.valueOf(tile.getValue())).collect(Collectors.joining()))
                .collect(Collectors.joining("\n"));
    }

    public static SeatBoard parseInput(BufferedReader in) throws Exception {
        var seatBoard = new SeatBoard();

        int row = 0;
        String line;
        while ((line = in.readLine()) != null) {
            int currentRow = ++row;

            List<Tile> boardLine = line.codePoints()
                    .mapToObj(c -> Tile.fromValue((char) c))
                    .map(o -> o.orElseThrow(() -> new IllegalArgumentException("Invalid character found on line " + currentRow)))
                    .collect(Collectors.toList());

            seatBoard.board.add(boardLine);
        }

        return seatBoard;
    }

    @RequiredArgsConstructor
    public enum Tile {
        FLOOR('.'),
        EMPTY('L'),
        OCCUPIED('#');

        @Getter
        private final char value;

        public Tile next(List<Tile> neighbors) {
            return switch (this) {
                case FLOOR -> FLOOR;
                case EMPTY -> neighbors.stream().noneMatch(t -> t == OCCUPIED) ? OCCUPIED : EMPTY;
                case OCCUPIED -> neighbors.stream().filter(t -> t == OCCUPIED).count() >= 4 ? EMPTY : OCCUPIED;
            };
        }

        public Tile next2(List<Tile> neighbors) {
            return switch (this) {
                case FLOOR -> FLOOR;
                case EMPTY -> neighbors.stream().noneMatch(t -> t == OCCUPIED) ? OCCUPIED : EMPTY;
                case OCCUPIED -> neighbors.stream().filter(t -> t == OCCUPIED).count() >= 5 ? EMPTY : OCCUPIED;
            };
        }

        public static Optional<Tile> fromValue(char c) {
            return switch (c) {
                case '.' -> Optional.of(FLOOR);
                case 'L' -> Optional.of(EMPTY);
                case '#' -> Optional.of(OCCUPIED);
                default -> Optional.empty();
            };
        }
    }

}
