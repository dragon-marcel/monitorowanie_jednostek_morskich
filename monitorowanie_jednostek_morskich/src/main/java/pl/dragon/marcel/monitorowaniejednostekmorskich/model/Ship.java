package pl.dragon.marcel.monitorowaniejednostekmorskich.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import com.sun.istack.NotNull;

@Entity
public class Ship {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@NotNull
	private int mmsi;
	private String name;
	@Transient
	private Point currentPoint;
	private String country;
	private String destination;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "track_id")
	private List<Point> track;
	private boolean inSope;
	private boolean active;

	public Ship() {
	}

	public Ship(int mmsi, String name, String country, String destination) {
		super();
		this.name = name;
		this.mmsi = mmsi;
		this.country = country;
		this.destination = destination;
		this.inSope = true;
		this.active = false;
		track = new ArrayList<>();

	}

	public Ship(int mmsi, String name, String country, String destination, List<Point> track) {
		super();
		this.name = name;
		this.mmsi = mmsi;
		this.country = country;
		this.destination = destination;
		this.track = track;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Point getCurrentPoint() {
		return currentPoint;
	}

	public void setCurrentPoint(Point currentPoint) {
		this.currentPoint = currentPoint;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public List<Point> getTrack() {

		return track;
	}

	public void setTrack(List<Point> track) {
		this.track = track;
	}

	public int getMmsi() {
		return mmsi;
	}

	public void setMmsi(int mmsi) {
		this.mmsi = mmsi;
	}

	public void addPoint(Point point) {
		this.track.add(point);
	}

	public boolean isInSope() {
		return inSope;
	}

	public void setInSope(boolean inSope) {
		this.inSope = inSope;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + mmsi;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ship other = (Ship) obj;
		if (mmsi != other.mmsi)
			return false;
		return true;
	}

}
