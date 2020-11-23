package de.caritas.cob.uploadservice.api.repository;

import de.caritas.cob.uploadservice.api.model.UploadByUser;
import org.springframework.data.repository.CrudRepository;

public interface UploadByUserRepository extends CrudRepository<UploadByUser, Long> {

  Integer countAllByUserId(String userId);

}
