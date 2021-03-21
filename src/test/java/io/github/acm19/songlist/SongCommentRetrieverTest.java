package io.github.acm19.songlist;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

class SongCommentRetrieverTest {
	private static final String NON_EXISTING_SONG_FILE = "song-non-existent-response.json";
	private static final String SONG_FILE = "song-response.json";

	private final ObjectMapper mapper = new ObjectMapper();

	@Test
	void fetchNonExistingSong() throws IOException {
		SongCommentRetriever songCommentRetriever =
				new SongCommentRetriever(buildFunction(NON_EXISTING_SONG_FILE), mapper);

		assertFalse(songCommentRetriever.fetchUrlFromComment("songId").isPresent());
	}

	private static Function<String, InputStream> buildFunction(String fileName) {
		Function<String, InputStream> emptyQueue = (ignored) -> {
			try {
				return CurrentSongRetrieverTest.class.getResource(fileName).openStream();
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		};

		return emptyQueue;
	}

	@Test
	void fetchSong() throws IOException {
		SongCommentRetriever songCommentRetriever = new SongCommentRetriever(buildFunction(SONG_FILE), mapper);

		assertEquals("https://tabs.ultimate-guitar.com/user/tab/view?h=utTfy7hL5mLm6dxItOs67okB",
				songCommentRetriever.fetchUrlFromComment("songId").get());
	}
}
