package pl.dragon.marcel.monitorowaniejednostekmorskich.service;

import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import pl.dragon.marcel.monitorowaniejednostekmorskich.model.AccessToken;

@Service
public class TokenService {
	static Logger log = LogManager.getLogger(TokenService.class);
	private final String AUTHENTICATION_URL = "https://id.barentswatch.no/connect/token";
	private final long DELAY = 60000;
	private final long EXPISRES_IN = 3600000;
	@Value("${security.client-id}")
	private String clientId;
	@Value("${security.client-secret}")
	private String clientSecret;
	private AccessToken accessToken;
	private Long timeExpired = 0L;

	public String getAccessToken() {

		if (accessToken == null || timeExpired.compareTo(getTimeMiliSec()) < 0) {
			accessToken = generateAccessToken();
			log.info("Create new acces token.");
			setTimeExpiredc();
			return accessToken.getAccessToken();
		} else {
			log.info("Get old acces token.");
			return accessToken.getAccessToken();
		}
	}

	public AccessToken generateAccessToken() {

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		RestTemplate restTemplate = new RestTemplate();
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("client_id", clientId);
		map.add("scope", "api");
		map.add("client_secret", clientSecret);
		map.add("grant_type", "client_credentials");

		HttpEntity<MultiValueMap<String, String>> formEntity = new HttpEntity<MultiValueMap<String, String>>(map,
				httpHeaders);

		ResponseEntity<AccessToken> authenticationResponse = restTemplate.exchange(AUTHENTICATION_URL, HttpMethod.POST,
				formEntity, AccessToken.class);

		return authenticationResponse.getBody();
	}

	private void setTimeExpiredc() {
		this.timeExpired = getTimeMiliSec() + EXPISRES_IN - DELAY;
	}

	private Long getTimeMiliSec() {
		return Calendar.getInstance().getTimeInMillis();
	}
}
