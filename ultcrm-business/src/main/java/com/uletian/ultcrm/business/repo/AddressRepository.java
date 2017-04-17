package com.uletian.ultcrm.business.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.uletian.ultcrm.business.entity.Address;

@RepositoryRestResource(collectionResourceRel = "address", path = "address")
public interface AddressRepository extends  PagingAndSortingRepository<Address, Long>{
  
	@Query("from Address where id = ?1")
	public Address findByAddressId(Long id);
}
