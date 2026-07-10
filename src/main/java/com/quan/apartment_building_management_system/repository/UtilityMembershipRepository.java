package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.UtilityMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UtilityMembershipRepository extends JpaRepository<UtilityMembership, Integer> {

    List<UtilityMembership> findByProfileProfileId(Integer profileId);

    // Check for active memberships
    boolean existsByProfileProfileIdAndUtilityUtilityIdAndStatusAndPaymentStatusAndEndDateGreaterThanEqual(
            Integer profileId, Integer utilityId, Boolean status, Boolean paymentStatus, LocalDate currentDate);
    
    // Find active membership for details
    UtilityMembership findFirstByProfileProfileIdAndUtilityUtilityIdAndStatusAndPaymentStatusAndEndDateGreaterThanEqual(
            Integer profileId, Integer utilityId, Boolean status, Boolean paymentStatus, LocalDate currentDate);
}
