package com.mscg;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

public record PowerGrid(int serialNumber) {

    private static final Integer[] CELL_TO_POWER = new Integer[300 * 300];

    private static final Map<Area, PoweredSector> SECTOR_POWER = new ConcurrentHashMap<>();

    public PoweredSector findMaxPowerOf3by3Sector() {
        return findMaxPowerSectorOfSize(3);
    }

    public PoweredSector findMaxPowerSector() {
        return IntStream.rangeClosed(1, 300) //
                .parallel() //
                .mapToObj(this::findMaxPowerSectorOfSize) //
                .max(Comparator.comparingInt(PoweredSector::power)) //
                .orElseThrow();
    }

    private PoweredSector findMaxPowerSectorOfSize(final int size) {
        PoweredSector maxPowerSector = new PoweredSector(null, Integer.MIN_VALUE);

        final int maxCoordValue = 300 - size + 1;
        for (int y = 1; y <= maxCoordValue; y++) {
            for (int x = 1; x <= maxCoordValue; x++) {
                final Cell cell = new Cell(x, y);
                final Area area = new Area(cell, size);

                final PoweredSector sector;
                if (size == 1) {
                    sector = new PoweredSector(area, computeSectorPower(area));
                } else {
                    final var prevArea = new Area(cell, size - 1);
                    final PoweredSector prevPoweredSector = SECTOR_POWER.computeIfAbsent(prevArea, a -> new PoweredSector(a, computeSectorPower(a)));
                    int totalPower = prevPoweredSector.power();
                    for (int dy = 0; dy < size - 1; dy++) {
                        totalPower += computePower(x + size - 1, y + dy);
                    }
                    for (int dx = 0; dx < size; dx++) {
                        totalPower += computePower(x + dx, y + size - 1);
                    }
                    sector = new PoweredSector(area, totalPower);
                }
                SECTOR_POWER.put(sector.area(), sector);
                if (sector.power() > maxPowerSector.power()) {
                    maxPowerSector = sector;
                }
            }
        }

        return maxPowerSector;
    }

    private int computeSectorPower(final Area area) {
        int power = 0;
        for (int dy = 0; dy < area.size(); dy++) {
            for (int dx = 0; dx < area.size(); dx++) {
                if (dx == 0 && dy == 0) {
                    power += computePower(area.topLeft().x(), area.topLeft().y());
                } else {
                    power += computePower(area.topLeft().x() + dx, area.topLeft().y() + dy);
                }
            }
        }
        return power;
    }

    private int computePower(final int x, final int y) {
        Integer power = CELL_TO_POWER[(x - 1) + (y - 1) * 300];
        if (power == null) {
            final int rackId = 10 + x;
            final int startPower = rackId * y;
            final int serial = (startPower + serialNumber) * rackId;
            final int hundreds = (serial % 1000) / 100;
            power = hundreds - 5;
            CELL_TO_POWER[(x - 1) + (y - 1) * 300] = power;
        }
        return power;
    }

    public static PowerGrid parseInput(final BufferedReader in) throws IOException {
        return new PowerGrid(Integer.parseInt(in.readLine()));
    }

    @RecordBuilder
    public static record Cell(int x, int y) implements PowerGridCellBuilder.With {

        public String coord() {
            return x + "," + y;
        }

    }

    public static record Area(Cell topLeft, int size) {

    }

    public static record PoweredSector(Area area, int power) {

        @Override
        public String toString() {
            return "PoweredSector[(" + area.topLeft().coord() + "," + area.size() + "), " + power + "]";
        }

    }

}
