package io.spring.service.ship;

import io.spring.jparepos.ship.JpaLsshpdRepository;
import io.spring.jparepos.ship.JpaLsshpmRepository;
import io.spring.jparepos.ship.JpaLsshpsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JpaShipService {
    private final JpaLsshpdRepository jpaLsshpdRepository;
    private final JpaLsshpmRepository jpaLsshpmRepository;
    private final JpaLsshpsRepository jpaLsshpsRepository;


}
