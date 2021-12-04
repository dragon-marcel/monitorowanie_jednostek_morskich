package pl.dragon.marcel.monitorowaniejednostekmorskich.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pl.dragon.marcel.monitorowaniejednostekmorskich.model.Ship;


@Repository
public interface ShipRepository extends JpaRepository<Ship, Integer> {

}
