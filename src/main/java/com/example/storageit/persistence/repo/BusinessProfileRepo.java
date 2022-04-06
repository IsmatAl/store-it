package com.example.storageit.persistence.repo;

import com.example.storageit.persistence.entity.BusinessProfile;
import org.springframework.data.repository.CrudRepository;

public interface BusinessProfileRepo extends CrudRepository<BusinessProfile, Long> {

}
