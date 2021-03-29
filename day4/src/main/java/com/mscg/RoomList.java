package com.mscg;

import static com.mscg.RoomListRoomBuilder.Room;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.soabase.recordbuilder.core.RecordBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RoomList {

    private final List<Room> rooms;

    public List<Room> findValidRooms() {
        return rooms.stream() //
                .filter(Room::isValid) //
                .collect(Collectors.toUnmodifiableList());
    }

    public long validSectorsSum() {
        return rooms.stream() //
                .filter(Room::isValid) //
                .mapToLong(Room::sector) //
                .sum();
    }

    public static RoomList parseInput(BufferedReader in) throws IOException {
        var pattern = Pattern.compile("([a-z-]+)-(\\d+)\\[([a-z]+)\\]");
        List<Room> rooms = in.lines() //
                .map(pattern::matcher) //
                .filter(Matcher::find) //
                .map(matcher -> Room(matcher.group(1), Integer.parseInt(matcher.group(2)), matcher.group(3))) //
                .collect(Collectors.toUnmodifiableList());

        return new RoomList(rooms);
    }

    @RecordBuilder
    public static record Room(String name, int sector, String checksum) implements RoomListRoomBuilder.With {

        public boolean isValid() {
            Map<Character, Long> frequency = IntStream.range(0, name.length()) //
                    .mapToObj(i -> name.charAt(i)) //
                    .filter(c -> c.charValue() != '-') //
                    .collect(Collectors.groupingBy(c -> c, Collectors.counting()));
            var elementComparator = Comparator.comparing((Entry<Character, Long> entry) -> entry.getValue()).reversed() //
                    .thenComparing(Entry::getKey);
            String checkCode = frequency.entrySet().stream() //
                    .sorted(elementComparator) //
                    .limit(checksum.length()) //
                    .map(entry -> entry.getKey().toString()) //
                    .collect(Collectors.joining());
            return checkCode.equals(checksum);
        }

        public String decodeName() {
            int range = 'z' - 'a' + 1;
            int shift = sector % range;
            return IntStream.range(0, name.length()) //
                    .mapToObj(i -> name.charAt(i)) //
                    .mapToInt(c -> switch (c.charValue()) {
                    case '-' -> (int) ' ';
                    default -> (c.charValue() - 'a' + shift) % range + 'a';
                    }) //
                    .mapToObj(c -> (char) c) //
                    .map(String::valueOf) //
                    .collect(Collectors.joining());
        }

    }

}
