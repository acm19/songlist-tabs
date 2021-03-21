package io.github.acm19.songlist;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

class CurrentSongRetrieverTest {
	private static final String EMPTY_QUEUE_FILE = "empty-queue-response.json";
	private static final String QUEUE_FILE = "queue-response.json";

	private final ObjectMapper mapper = new ObjectMapper();

	@Test
	void fecthEmptyQueue() throws IOException {
		CurrentSongRetriever currentSongRetriever = new CurrentSongRetriever(buildSupplier(EMPTY_QUEUE_FILE), mapper);

		assertFalse(currentSongRetriever.fecthCurrentSongId().isPresent());
	}

	private static Supplier<InputStream> buildSupplier(String fileName) {
		Supplier<InputStream> emptyQueue = () -> {
			try {
				return CurrentSongRetrieverTest.class.getResource(fileName).openStream();
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		};

		return emptyQueue;
	}

	@Test
	void fecthQueue() throws IOException {
		CurrentSongRetriever currentSongRetriever = new CurrentSongRetriever(buildSupplier(QUEUE_FILE), mapper);

		assertEquals("1142616", currentSongRetriever.fecthCurrentSongId().get());
	}
}
