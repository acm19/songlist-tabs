package io.github.acm19.songlist;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.function.Function;
import org.apache.commons.validator.routines.UrlValidator;

class SongCommentRetriever {
	private final Function<String, InputStream> httpClient;
	private final ObjectMapper mapper;

	public SongCommentRetriever(Function<String, InputStream> httpClient, ObjectMapper mapper) {
		this.httpClient = httpClient;
		this.mapper = mapper;
	}

	Optional<String> fetchUrlFromComment(String songId) throws IOException {
		JsonNode response = mapper.readValue(new InputStreamReader(httpClient.apply(songId)), JsonNode.class);
		return response.has("comment")
				? getUrlFromComment(response)
				: Optional.empty();
	}

	private Optional<String> getUrlFromComment(JsonNode response) {
		String comment = response.get("comment").asText().trim();
		return UrlValidator.getInstance().isValid(comment)
				? Optional.of(comment)
				: Optional.empty();
	}
}
