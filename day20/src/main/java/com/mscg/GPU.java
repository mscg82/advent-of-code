package com.mscg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.codepoetics.protonpack.StreamUtils;

public record GPU(List<Particle> particles) {

    public long getClosestParticle() {
        final Map<Long, List<Particle>> distanceToParticle = particles.stream() //
                .map(p -> p.simulate(1_000_000)) //
                .collect(Collectors.groupingBy(Particle::distance, TreeMap::new, Collectors.toList()));

        return distanceToParticle.entrySet().stream() //
                .flatMap(e -> e.getValue().stream()) //
                .findFirst() //
                .orElseThrow() //
                .id();
    }

    public static record Vector3D(long x, long y, long z) {

        public Vector3D add(final Vector3D other) {
            return new Vector3D(x + other.x, y + other.y, z + other.z);
        }

        public Vector3D scale(final long val) {
            return new Vector3D(x * val, y * val, z * val);
        }

        public static Vector3D parse(final String line) {
            final String[] parts = line.split(",");
            return new Vector3D(Long.parseLong(parts[0]), Long.parseLong(parts[1]), Long.parseLong(parts[2]));
        }
    }

    public static record Particle(long id, Vector3D p, Vector3D v, Vector3D a) {

        public long distance() {
            return Math.abs(p.x()) + Math.abs(p.y()) + Math.abs(p.z());
        }

        public Particle simulate(final long t) {
            return new Particle(id, a.scale((t * t) / 2).add(v.scale(t)).add(p), a.scale(t).add(v), a);
        }

    }

    public static GPU parseInput(final BufferedReader in) throws IOException {
        try {
            final Pattern pattern = Pattern.compile("p=<([^>]+)>, v=<([^>]+)>, a=<([^>]+)>");
            final List<Particle> particles = StreamUtils.zipWithIndex(in.lines() //
                    .map(pattern::matcher) //
                    .filter(Matcher::matches)) //
                    .map(idx -> new Particle(idx.getIndex(), //
                            Vector3D.parse(idx.getValue().group(1)), //
                            Vector3D.parse(idx.getValue().group(2)),  //
                            Vector3D.parse(idx.getValue().group(3)))) //
                    .toList();
            return new GPU(particles);
        }
        catch (final UncheckedIOException e) {
            throw e.getCause();
        }
    }
}
