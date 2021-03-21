package io.github.acm19.songlist;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.function.Supplier;

class CurrentSongRetriever {
	private final Supplier<InputStream> httpClient;
	private final ObjectMapper mapper;

	public CurrentSongRetriever(Supplier<InputStream> httpClient, ObjectMapper mapper) {
		this.httpClient = httpClient;
		this.mapper = mapper;
	}

	/**
	 * Fetches the id of the current song from the provided supplier. It is expecting a format like
	 * {@code https://api.streamersonglist.com/v1/streamers/{streamerId}/queue`}.
	 *
	 * @return the id of the current song or empty if the queue is empty
	 */
	Optional<String> fecthCurrentSongId() throws IOException {
		JsonNode response = mapper.readValue(new InputStreamReader(httpClient.get()), JsonNode.class);
		JsonNode list = response.get("list");

		Optional<String> id = Optional.empty();
		if (!list.isEmpty() && list.isArray()) {
			id = Optional.of(list.get(0).get("song").get("id").asText());
		}

		return id;
	}
}
