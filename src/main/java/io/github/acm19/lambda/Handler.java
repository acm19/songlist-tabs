package io.github.acm19.lambda;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.github.acm19.songlist.TabUrlDao;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

public class Handler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
	private static final Logger LOG = LogManager.getLogger(Handler.class);
	private static final String QUEUE_URL = "https://api.streamersonglist.com/v1/streamers/%s/queue";
	private static final String SONG_URL = "https://api.streamersonglist.com/v1/streamers/%s";
	private static final String SONG_URL_SUFFIX = "/songs/%s";
	private static final Map<String, String> HEADERS = Map.of(
			"Cache-Control", "no-cache, no-store, must-revalidate",
			"Pragma", "no-cache",
			"Expires", "0",
			"Content-Type", "text/html; charset=UTF-8"
	);
	private static final String ARTIST_NAME_VAR = "ARTIST_NAME";
	private final Map<String, String> redirectHeaders = new HashMap<>(HEADERS);

	private final TabUrlDao tabUrlDao;

	public Handler() {
		String artistName = System.getenv(ARTIST_NAME_VAR);
		tabUrlDao = new TabUrlDao(String.format(QUEUE_URL, artistName),
				String.format(SONG_URL, artistName + SONG_URL_SUFFIX));
	}

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
		LOG.info("received: {}", input);

		try {
			Optional<String> redirectUrl = tabUrlDao.find();

			return redirectUrl.isPresent()
					? buildRedirectHeader(redirectUrl.get())
					: ApiGatewayResponse.builder()
							.setStatusCode(200)
							.setRawBody("There are not tabs for this song.")
							.setHeaders(HEADERS)
							.build();
		} catch (IOException ex) {
			LOG.error("There was a problem reading products", ex);

			return ApiGatewayResponse.builder()
					.setStatusCode(500)
					.setHeaders(HEADERS)
					.setRawBody("There was a problem retrieving the products.")
					.build();
		}

	}

	private ApiGatewayResponse buildRedirectHeader(String redirect) {
		redirectHeaders.put("Location", redirect);

		return ApiGatewayResponse.builder()
				.setStatusCode(301)
				.setHeaders(redirectHeaders)
				.build();
	}
}
