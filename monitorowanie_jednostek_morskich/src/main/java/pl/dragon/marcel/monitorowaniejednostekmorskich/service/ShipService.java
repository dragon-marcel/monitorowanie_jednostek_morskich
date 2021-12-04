package pl.dragon.marcel.monitorowaniejednostekmorskich.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.dragon.marcel.monitorowaniejednostekmorskich.model.Point;
import pl.dragon.marcel.monitorowaniejednostekmorskich.model.Ship;
import pl.dragon.marcel.monitorowaniejednostekmorskich.model.Track;
import pl.dragon.marcel.monitorowaniejednostekmorskich.repository.ShipRepository;
import pl.dragon.marcel.monitorowaniejednostekmorskich.utils.ListUtils;

@Service
public class ShipService {
	static Logger log = LogManager.getLogger(ShipService.class);
	@Autowired
	private TokenService tokenService;
	@Autowired
	private ShipRepository shipRepository;
	List<Point> point = new ArrayList<>();
	
	@Scheduled(fixedRate   = 50000)
	public void getShipFromAPI() {
		
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Authorization", "Bearer " + tokenService.getAccessToken());
		ResponseEntity<Track[]> exchange = restTemplate.exchange(
				"https://www.barentswatch.no/bwapi/v2/geodata/ais/openpositions?Xmin=10.09094&Xmax=10.67047&Ymin=63.3989&Ymax=63.58645",
				HttpMethod.GET, new HttpEntity(httpHeaders), Track[].class);
		
		Track[] tracks = exchange.getBody();
		trackParseShips(tracks);
	}
	private void trackParseShips(Track[] tracks) {
		boolean exist = false;
		List<Ship> ships = shipRepository.findAll();
		List<Ship> newShips = new ArrayList<>();

		for (int a = 0; a < tracks.length; a++) {
			Track track = tracks[a];
			Ship newShip = new Ship(track.getMmsi(), track.getName(), track.getCountry(), track.getDestination());

			for (int b = 0; b < ships.size(); b++) {
				Ship ship = ships.get(b);
				if (ship.getMmsi() == newShip.getMmsi()) {
					Point p = new Point(track.getGeometry().getCoordinates().get(1),
							track.getGeometry().getCoordinates().get(0));
					Point lastPoint = (Point) ListUtils.getlast(ship.getTrack());

					if (lastPoint != null && !p.equals(lastPoint))
						ship.setActive(true);
					else
						ship.setActive(false);

					ship.addPoint(p);
					ship.setInSope(true);
					shipRepository.save(ship);
					newShips.add(ship);
					exist = true;
					log.info("Update Ship mmsi: "+newShip.getMmsi() );
					break;
				}
			}
			if (!exist) {
				newShip.addPoint(new Point(track.getGeometry().getCoordinates().get(1),
						track.getGeometry().getCoordinates().get(0)));
				shipRepository.save(newShip);
				newShips.add(newShip);
				log.info("Add new Ship mmsi: "+newShip.getMmsi() );
			}
		}
		ships.forEach(ship -> {
			if (!newShips.contains(ship)) {
				ship.setInSope(false);
				log.info("Ship " + ship.getName() + " beyond the scope of the territory");
				shipRepository.save(ship);
			}
		});
	}

	public List<Ship> getShipsInSkope() {
		List<Ship> ships = shipRepository.findAll();
		//Set actual position ship
		ships.forEach(ship -> ship.setCurrentPoint(new Point(((Point) ListUtils.getlast(ship.getTrack())).getX(),
				((Point) ListUtils.getlast(ship.getTrack())).getY())));
		//Return only ships in skope
		return ships.stream().filter(ship -> ship.isInSope() == true).collect(Collectors.toList());
	}

	@Scheduled(cron = "0 0 * * * *")
	private void resetTrackOfShip() {
		List<Ship> ships = shipRepository.findAll();
		
		// clear track after one hour
		ships.forEach(ship -> {
			ship.getTrack().clear();
			shipRepository.save(ship);
		});

	}

}
