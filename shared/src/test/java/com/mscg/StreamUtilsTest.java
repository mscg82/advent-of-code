package com.mscg;

import com.mscg.utils.StreamUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StreamUtilsTest
{

	@Test
	void splitStreamWithNoEmptyAndConsumeData()
	{
		final var source = """
				a
				b
				
				c
				d
				e
				
				f
				g
				h
				i
				""";
		final List<String> result = StreamUtils.splitted(source.lines(), String::isBlank) //
				.map(block -> block.collect(Collectors.joining())) //
				.toList();
		assertEquals(List.of("ab", "cde", "fghi"), result);
	}

	@Test
	void splitStreamWithEmptiesAndConsumeData()
	{
		final var source = """
				a
				b
				
				c
				d
				e
				
				
				f
				g
				h
				i
				
				""";
		final List<String> result = StreamUtils.splitted(source.lines(), String::isBlank) //
				.map(block -> block.collect(Collectors.joining())) //
				.toList();
		assertEquals(List.of("ab", "cde", "", "fghi", ""), result);
	}

	@Test
	void splitStreamWithEmptiesAtBeginningAndConsumeData()
	{
		final var source = """
				
				a
				b
				
				c
				d
				e
				
				
				f
				g
				h
				i
				
				""";
		final List<String> result = StreamUtils.splitted(source.lines(), String::isBlank) //
				.map(block -> block.collect(Collectors.joining())) //
				.toList();
		assertEquals(List.of("", "ab", "cde", "", "fghi", ""), result);
	}

	@Test
	void splitStreamWithNoEmptyAndDontConsumeData()
	{
		final var source = """
				a
				b
				
				c
				d
				e
				
				f
				g
				h
				i
				""";
		final List<Stream<String>> result = StreamUtils.splitted(source.lines(), String::isBlank) //
				.toList();
		assertEquals(3, result.size());
	}

	@Test
	void splitStreamWithEmptiesAndDontConsumeData()
	{
		final var source = """
				a
				b
				
				c
				d
				e
				
				
				f
				g
				h
				i
				
				""";
		final List<Stream<String>> result = StreamUtils.splitted(source.lines(), String::isBlank) //
				.toList();
		assertEquals(5, result.size());
	}

	@Test
	void splitStreamWithEmptiesAtBeginningAndDontConsumeData()
	{
		final var source = """
				
				a
				b
				
				c
				d
				e
				
				
				f
				g
				h
				i
				
				""";
		final List<Stream<String>> result = StreamUtils.splitted(source.lines(), String::isBlank) //
				.toList();
		assertEquals(6, result.size());
	}

	@Test
	void splitStreamIncludingWithNoEmptyAndConsumeData()
	{
		final var source = """
				a
				b
				$
				c
				d
				e
				$
				f
				g
				h
				i
				""";
		final List<String> result = StreamUtils.splittedIncluding(source.lines(), "$"::equals) //
				.map(block -> block.collect(Collectors.joining())) //
				.toList();
		assertEquals(List.of("ab$", "cde$", "fghi"), result);
	}

	@Test
	void splitStreamIncludingWithEmptiesAndConsumeData()
	{
		final var source = """
				a
				b
				$
				c
				d
				e
				$
				$
				f
				g
				h
				i
				$
				""";
		final List<String> result = StreamUtils.splittedIncluding(source.lines(), "$"::equals) //
				.map(block -> block.collect(Collectors.joining())) //
				.toList();
		assertEquals(List.of("ab$", "cde$", "$", "fghi$", ""), result);
	}

	@Test
	void splitStreamIncludingWithEmptiesAtBeginningAndConsumeData()
	{
		final var source = """
				$
				a
				b
				$
				c
				d
				e
				$
				$
				f
				g
				h
				i
				$
				""";
		final List<String> result = StreamUtils.splittedIncluding(source.lines(), "$"::equals) //
				.map(block -> block.collect(Collectors.joining())) //
				.toList();
		assertEquals(List.of("$", "ab$", "cde$", "$", "fghi$", ""), result);
	}

	@Test
	void splitStreamIncludingWithNoEmptyAndDontConsumeData()
	{
		final var source = """
				a
				b
				$
				c
				d
				e
				$
				f
				g
				h
				i
				""";
		final List<Stream<String>> result = StreamUtils.splittedIncluding(source.lines(), "$"::equals) //
				.toList();
		assertEquals(3, result.size());
	}

	@Test
	void splitStreamIncludingWithEmptiesAndDontConsumeData()
	{
		final var source = """
				a
				b
				$
				c
				d
				e
				$
				$
				f
				g
				h
				i
				$
				""";
		final List<Stream<String>> result = StreamUtils.splitted(source.lines(), "$"::equals) //
				.toList();
		assertEquals(5, result.size());
	}

	@Test
	void splitStreamIncludingWithEmptiesAtBeginningAndDontConsumeData()
	{
		final var source = """
				$
				a
				b
				$
				c
				d
				e
				$
				$
				f
				g
				h
				i
				$
				""";
		final List<Stream<String>> result = StreamUtils.splitted(source.lines(), "$"::equals) //
				.toList();
		assertEquals(6, result.size());
	}

	@Test
	void windowedWithSmallWindow()
	{
		final var source = List.of("a", "b", "c", "d", "e", "f", "g", "h");
		final List<String> result1 = StreamUtils.windowed(source, 2) //
				.map(window -> String.join("", window)) //
				.toList();
		assertEquals(List.of("ab", "bc", "cd", "de", "ef", "fg", "gh"), result1);
		final List<String> result2 = StreamUtils.windowed(source, 2) //
				.filter(window -> window.contains("c")) //
				.map(window -> String.join("", window)) //
				.toList();
		assertEquals(List.of("bc", "cd"), result2);

		final List<String> result3 = StreamUtils.windowed(source, 3) //
				.map(window -> String.join("", window)) //
				.toList();
		assertEquals(List.of("abc", "bcd", "cde", "def", "efg", "fgh"), result3);
		final List<String> result4 = StreamUtils.windowed(source, 3) //
				.filter(window -> window.contains("c")) //
				.map(window -> String.join("", window)) //
				.toList();
		assertEquals(List.of("abc", "bcd", "cde"), result4);
	}

	@Test
	void windowedWithBigWindow()
	{
		final var source = List.of("a", "b", "c", "d", "e", "f", "g", "h");
		final List<String> result = StreamUtils.windowed(source, 10) //
				.map(window -> String.join("", window)) //
				.toList();
		assertEquals(List.of("abcdefgh"), result);
	}

	@Test
	void partitionedWithSmallSize()
	{
		final var source = List.of("a", "b", "c", "d", "e", "f", "g", "h");
		final List<String> result1 = StreamUtils.partitioned(source, 2) //
				.map(window -> String.join("", window)) //
				.toList();
		assertEquals(List.of("ab", "cd", "ef", "gh"), result1);
		final List<String> result2 = StreamUtils.partitioned(source, 2) //
				.filter(window -> window.contains("c") || window.contains("g")) //
				.map(window -> String.join("", window)) //
				.toList();
		assertEquals(List.of("cd", "gh"), result2);
		final List<String> result3 = StreamUtils.partitioned(source, 3) //
				.map(window -> String.join("", window)) //
				.toList();
		assertEquals(List.of("abc", "def", "gh"), result3);
		final List<String> result4 = StreamUtils.partitioned(source, 3) //
				.filter(window -> window.contains("c") || window.contains("g")) //
				.map(window -> String.join("", window)) //
				.toList();
		assertEquals(List.of("abc", "gh"), result4);
	}

	@Test
	void partitionedWithBigSize()
	{
		final var source = List.of("a", "b", "c", "d", "e", "f", "g", "h");
		final List<String> result = StreamUtils.partitioned(source, 10) //
				.map(window -> String.join("", window)) //
				.toList();
		assertEquals(List.of("abcdefgh"), result);
	}
}
