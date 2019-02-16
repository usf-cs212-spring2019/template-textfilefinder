import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@SuppressWarnings("javadoc")
class TextFileFinderTest {

	public static final Path root = Path.of(".", "text", "simple");

	@BeforeAll
	public static void checkEnvironment() {
		Assumptions.assumeTrue(Files.isDirectory(root));
		Assumptions.assumeTrue(Files.exists(root.resolve("hello.txt")));
	}

	@Nested
	public class TextExtensionTests {

		@ParameterizedTest
		@ValueSource(strings = {
				"animals_copy.text",
				"capital_extension.TXT",
				"empty.txt",
				"position.teXt",
				"words.tExT"
		})
		public void testIsTextFile(String file) {
			Path path = root.resolve(file);
			Assertions.assertTrue(TextFileFinder.TEXT_EXT.test(path));
		}

		@ParameterizedTest
		@ValueSource(strings = {
				"double_extension.txt.html",
				"no_extension",
				"wrong_extension.html",
				"dir.txt",
				"nowhere.txt"
		})
		public void testIsNotTextFile(String file) {
			Path path = root.resolve(file);
			Assertions.assertFalse(TextFileFinder.TEXT_EXT.test(path));
		}
	}

	@Nested
	public class FindListTests {

		@Test
		public void testListSize() throws IOException {
			Assertions.assertEquals(14, TextFileFinder.list(root).size());
		}

		@Test
		public void testStreamSize() throws IOException {
			Assertions.assertEquals(14, TextFileFinder.find(root).count());
		}

		@Test
		public void testPaths() throws IOException {
			Set<Path> actual = TextFileFinder.find(root).collect(Collectors.toSet());

			Set<Path> expected = new HashSet<>();
			Collections.addAll(expected,
				root.resolve("symbols.txt"),
				root.resolve("dir.txt").resolve("findme.Txt"),
				root.resolve("empty.txt"),
				root.resolve(".txt").resolve("hidden.txt"),
				root.resolve("position.teXt"),
				root.resolve("animals_copy.text"),
				root.resolve("digits.txt"),
				root.resolve("capital_extension.TXT"),
				root.resolve("animals_double.text"),
				root.resolve("a").resolve("b").resolve("c").resolve("d").resolve("subdir.txt"),
				root.resolve("words.tExT"),
				root.resolve("animals.text"),
				root.resolve("hello.txt"),
				root.resolve("capitals.txt")
			);

			Assertions.assertTrue(expected.equals(actual));
		}
	}

	@Nested
	public class ApproachTests {

		/*
		 * These only approximately determine if a lambda function was used and the
		 * File class was NOT used.
		 */

		@Test
		public void testAnonymous() {
			Assertions.assertFalse(TextFileFinder.TEXT_EXT.getClass().isAnonymousClass());
		}

		@Test
		public void testEnclosingClass() {
			Assertions.assertNull(TextFileFinder.TEXT_EXT.getClass().getEnclosingClass());
		}

		@Test
		public void testSyntheticClass() {
			Assertions.assertTrue(TextFileFinder.TEXT_EXT.getClass().isSynthetic());
		}

		@Test
		public void testClassName() {
			String actual = TextFileFinder.TEXT_EXT.getClass().getCanonicalName();
			String[] parts = actual.split("[$]+");

			Assertions.assertTrue(parts[1].contentEquals("Lambda"));
		}

		@Test
		public void testFileClass() throws IOException {
			String source = Files.readString(Path.of(".", "src", "TextFileFinder.java"), StandardCharsets.UTF_8);
			Assertions.assertFalse(source.contains("import java.io.File;"));
		}
	}

}
