package kz.bitlab.robygroup.sppmid.core.repository;

import kz.bitlab.robygroup.sppmid.core.models.user.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Nurlan Altynbek
 * @date 2/18/20
 * @project sppmid
 **/
@Repository
public interface MessageRepository extends JpaRepository<Message,Long> {
    Message save(Message message);
    Message findMessageByEmailAndDeletedAtNull(String email);
}
