package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public record BingoSubsystem(int[] values, List<Board> boards) {

    private static long computeBoardPoints(ModifiableBoard winningBoard, int value) {
        final long missingValuesSum = winningBoard.rows().stream() //
                .flatMap(Collection::stream) //
                .mapToLong(Integer::longValue) //
                .sum();
        return missingValuesSum * value;
    }

    private static boolean markValueIfPresentAndCheckWinner(ModifiableBoard board, int value) {
        for (List<Integer> row : board.rows()) {
            int idx = row.indexOf(value);
            if (idx >= 0) {
                row.remove(idx);
                if (row.isEmpty()) {
                    return true;
                }
            }
        }
        for (List<Integer> col : board.cols()) {
            int idx = col.indexOf(value);
            if (idx >= 0) {
                col.remove(idx);
                if (col.isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }

    public static BingoSubsystem parseInput(BufferedReader in) throws IOException {
        final int[] values = Arrays.stream(in.readLine().split(",")) //
                .mapToInt(Integer::parseInt) //
                .toArray();

        // ignore one empty line
        in.readLine(); // NOSONAR

        List<Board> boards = new ArrayList<>();
        List<String> boardRows = new ArrayList<>();
        String line;
        while ((line = in.readLine()) != null) {
            if (line.isEmpty()) {
                boards.add(Board.fromRows(boardRows));
                boardRows.clear();
                continue;
            }
            boardRows.add(line);
        }
        if (!boardRows.isEmpty()) {
            boards.add(Board.fromRows(boardRows));
        }

        return new BingoSubsystem(values, List.copyOf(boards));
    }

    public long computeWinnerPoints() {
        var modifiableBoards = boards.stream() //
                .map(ModifiableBoard::fromBoard) //
                .toList();

        for (int value : values) {
            ModifiableBoard winningBoard = null;

            for (ModifiableBoard board : modifiableBoards) {
                if (markValueIfPresentAndCheckWinner(board, value)) {
                    winningBoard = board;
                    break;
                }
            }

            if (winningBoard != null) {
                return computeBoardPoints(winningBoard, value);
            }
        }

        throw new IllegalStateException("Unable to find winner points");
    }

    public long computeLastWinningPoints() {
        var modifiableBoards = boards.stream() //
                .map(ModifiableBoard::fromBoard) //
                .collect(Collectors.toCollection(ArrayList::new));

        ModifiableBoard lastWinningBoard = null;
        int lastValue = -1;
        for (int value : values) {
            for (var it = modifiableBoards.iterator(); it.hasNext(); ) {
                var board = it.next();
                if (markValueIfPresentAndCheckWinner(board, value)) {
                    it.remove();
                    lastWinningBoard = board;
                    lastValue = value;
                }
            }

            if (modifiableBoards.isEmpty()) {
                break;
            }
        }

        if (lastWinningBoard == null) {
            throw new IllegalStateException("Unable to find last winning board");
        }

        return computeBoardPoints(lastWinningBoard, lastValue);
    }

    public record Board(int[][] rows, int[][] cols) {

        public static Board fromRows(List<String> rowsStr) {
            final int[][] rows = rowsStr.stream() //
                    .map(row -> Arrays.stream(row.split(" ")) //
                            .filter(s -> !s.isEmpty()) //
                            .mapToInt(Integer::parseInt) //
                            .toArray()) //
                    .toArray(int[][]::new);

            final int[][] cols = new int[rows[0].length][rows.length];
            for (int i = 0; i < rows.length; i++) {
                int[] row = rows[i];
                for (int j = 0; j < row.length; j++) {
                    cols[j][i] = rows[i][j];
                }
            }

            return new Board(rows, cols);
        }

    }

    private record ModifiableBoard(List<? extends List<Integer>> rows, List<? extends List<Integer>> cols) {

        public static ModifiableBoard fromBoard(Board board) {
            List<? extends List<Integer>> rows = Arrays.stream(board.rows()) //
                    .map(row -> Arrays.stream(row).boxed().collect(Collectors.toCollection(ArrayList::new))) //
                    .toList();
            List<? extends List<Integer>> cols = Arrays.stream(board.cols()) //
                    .map(col -> Arrays.stream(col).boxed().collect(Collectors.toCollection(ArrayList::new))) //
                    .toList();
            return new ModifiableBoard(rows, cols);
        }

    }

}
