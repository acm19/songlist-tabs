package io.github.acm19.songlist;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class TabUrlDao {
	private final CurrentSongRetriever songRetriever;
	private final SongCommentRetriever commentRetriever;

	public TabUrlDao(String queueUrl, String songDetailsUrl) {
		ObjectMapper mapper = new ObjectMapper();

		songRetriever = new CurrentSongRetriever(buildQueueClient(queueUrl), mapper);
		commentRetriever = new SongCommentRetriever(buildSongClient(songDetailsUrl), mapper);
	}

	private static Supplier<InputStream> buildQueueClient(String url) {
		Supplier<InputStream> emptyQueue = () -> {
			try {
				return new URL(url).openStream();
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		};

		return emptyQueue;
	}

	private static Function<String, InputStream> buildSongClient(String url) {
		Function<String, InputStream> emptyQueue = (songId) -> {
			try {
				return new URL(String.format(url, songId)).openStream();
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		};

		return emptyQueue;
	}

	public Optional<String> find() throws IOException {
		Optional<String> songId = songRetriever.fecthCurrentSongId();

		return songId.isPresent()
				? commentRetriever.fetchUrlFromComment(songId.get())
				: Optional.empty();
	}
}
