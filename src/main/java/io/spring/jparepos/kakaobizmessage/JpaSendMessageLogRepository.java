package io.spring.jparepos.kakaobizmessage;

import io.spring.model.nhncloud.entity.SendMessageLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSendMessageLogRepository extends JpaRepository<SendMessageLog, Long> {
}
