package org.example.customermanagementservice.Repository;

import jdk.jfr.Registered;
import org.example.customermanagementservice.Entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Customer_Repo extends JpaRepository<CustomerEntity,Integer>, JpaSpecificationExecutor<CustomerEntity> {
    Optional<CustomerEntity> findByEmail(String email);

    Optional<CustomerEntity> findById(Long id);


}
