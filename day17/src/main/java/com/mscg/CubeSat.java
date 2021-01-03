package com.mscg;

import com.codepoetics.protonpack.StreamUtils;
import lombok.NonNull;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CubeSat implements Cloneable {

    private final CubeState[][][][] hyperCube;
    private final int wOffset;
    private final int zOffset;

    public CubeSat(final @NonNull List<List<CubeState>> basePlane, final int hyperPlanes, final int planes, final int xPadding, int yPadding) {
        if (hyperPlanes < 0) {
            throw new IllegalArgumentException("Hyperplanes number must be non-negative");
        }
        if (planes < 0) {
            throw new IllegalArgumentException("Planes number must be non-negative");
        }
        if (basePlane.isEmpty() || basePlane.stream().anyMatch(List::isEmpty)) {
            throw new IllegalArgumentException("Base plane can't be empty or contain empty rows");
        }
        if (basePlane.stream().flatMap(Collection::stream).anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Base plane can't contain nulls");
        }

        wOffset = hyperPlanes;
        zOffset = planes;
        final int basePlaneRows = basePlane.size();
        final int basePlaneCols = basePlane.get(0).size();
        final int rows = basePlaneRows + 2 * xPadding;
        final int cols = basePlaneCols + 2 * yPadding;
        final int pln = 1 + 2 * planes;
        final int hpln = 1 + 2 * hyperPlanes;
        hyperCube = new CubeState[hpln][pln][rows][cols];
        for (int w = 0; w < hpln; w++) {
            for (int z = 0; z < pln; z++) {
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        final boolean isInBasePlane = w == wOffset &&
                                z == zOffset &&
                                (i >= xPadding && i < basePlaneRows + xPadding) &&
                                (j >= yPadding && j < basePlaneCols + yPadding);
                        hyperCube[w][z][i][j] = isInBasePlane ? basePlane.get(i - xPadding).get(j - yPadding) : CubeState.INACTIVE;
                    }
                }
            }
        }
    }

    private CubeSat(final @NonNull CubeState[][][][] hyperCube, final int wOffset, final int zOffset) {
        this.hyperCube = hyperCube;
        this.wOffset = wOffset;
        this.zOffset = zOffset;
    }

    public Stream<CubeState> stream() {
        return Arrays.stream(hyperCube)
                .flatMap(Arrays::stream)
                .flatMap(Arrays::stream)
                .flatMap(Arrays::stream);
    }

    public CubeSat next() {
        var clonedSat = (CubeSat) this.clone();

        final int hpln = this.hyperCube.length;
        final int pln = this.hyperCube[0].length;
        final int rows = this.hyperCube[0][0].length;
        final int cols = this.hyperCube[0][0][0].length;

        for (int w = 0; w < hpln; w++) {
            for (int z = 0; z < pln; z++) {
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        final List<CubeState> neighbors = getNeighbors(w, z, i, j);
                        final long activeNeighbors = neighbors.stream().filter(s -> s == CubeState.ACTIVE).count();
                        clonedSat.hyperCube[w][z][i][j] = switch (this.hyperCube[w][z][i][j]) {
                            case INACTIVE -> activeNeighbors == 3 ? CubeState.ACTIVE : CubeState.INACTIVE;
                            case ACTIVE -> activeNeighbors == 2 || activeNeighbors == 3 ? CubeState.ACTIVE : CubeState.INACTIVE;
                        };
                    }
                }
            }
        }

        return clonedSat;
    }

    public CubeSat boot() {
        var result = this;
        for (int i = 0; i < 6; i++) {
            result = result.next();
        }
        return result;
    }

    private List<CubeState> getNeighbors(int hpln, int pln, int row, int col) {
        var cells = new ArrayList<CubeState>(80);
        for (int w = Math.max(0, hpln - 1), wMax = Math.min(hyperCube.length - 1, hpln + 1); w <= wMax; w++) {
            var cube = hyperCube[w];
            for (int z = Math.max(0, pln - 1), zMax = Math.min(cube.length - 1, pln + 1); z <= zMax; z++) {
                var plane = cube[z];
                for (int i = Math.max(0, row - 1), iMax = Math.min(plane.length - 1, row + 1); i <= iMax; i++) {
                    var line = plane[i];
                    for (int j = Math.max(0, col - 1), jMax = Math.min(line.length - 1, col + 1); j <= jMax; j++) {
                        if (w == hpln && z == pln && i == row && j == col) {
                            continue;
                        }

                        cells.add(hyperCube[w][z][i][j]);
                    }
                }
            }
        }
        return cells;
    }

    @Override
    @SuppressWarnings({ "MethodDoesntCallSuperMethod", "CloneDoesntDeclareCloneNotSupportedException" })
    protected Object clone() {
        final int hpln = this.hyperCube.length;
        final int pln = this.hyperCube[0].length;
        final int rows = this.hyperCube[0][0].length;
        final int cols = this.hyperCube[0][0][0].length;

        CubeState[][][][] hyperCube = new CubeState[hpln][pln][rows][cols];
        for (int w = 0; w < hpln; w++) {
            for (int z = 0; z < pln; z++) {
                for (int i = 0; i < rows; i++) {
                    System.arraycopy(this.hyperCube[w][z][i], 0, hyperCube[w][z][i], 0, cols);
                }
            }
        }

        return new CubeSat(hyperCube, (hpln - 1) / 2, (pln - 1) / 2);
    }

    @SuppressWarnings("RedundantThrows")
    public static CubeSat parseInput(BufferedReader in, int hyperPlanes, int planes, final int xPadding, int yPadding) throws Exception {
        List<List<CubeState>> basePlane = new ArrayList<>();
        StreamUtils.zipWithIndex(in.lines())
                .forEach(idx -> {
                    String line = idx.getValue();
                    var row = new ArrayList<CubeState>(line.length());
                    for (int i = 0, l = line.length(); i < l; i++) {
                        row.add(CubeState.fromChar(line.charAt(i))
                                .orElseThrow(() -> new IllegalArgumentException("Invalid row at line " + (idx.getIndex() + 1))));
                    }
                    basePlane.add(row);
                });
        return new CubeSat(basePlane, hyperPlanes, planes, xPadding, yPadding);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (int w = 0; w < hyperCube.length; w++) {
            var cube = hyperCube[w];
            for (int z = 0; z < cube.length; z++) {
                if (!str.isEmpty()) {
                    str.append("\n\n");
                }
                str.append("z=").append(z - zOffset).append(", w=").append(w - wOffset).append('\n');
                CubeState[][] plane = cube[z];
                String planeStr = Arrays.stream(plane)
                        .map(row -> Arrays.stream(row).map(CubeState::toString).collect(Collectors.joining()))
                        .collect(Collectors.joining("\n"));
                str.append(planeStr);
            }
        }
        return str.toString();
    }

    public enum CubeState {
        ACTIVE,
        INACTIVE;

        @Override
        public String toString() {
            return switch (this) {
                case ACTIVE -> "#";
                case INACTIVE -> ".";
            };
        }

        public static Optional<CubeState> fromChar(char c) {
            return switch (c) {
                case '#' -> Optional.of(ACTIVE);
                case '.' -> Optional.of(INACTIVE);
                default -> Optional.empty();
            };
        }
    }

}
