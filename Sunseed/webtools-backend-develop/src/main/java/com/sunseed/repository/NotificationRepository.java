package com.sunseed.repository;


import com.sunseed.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // count notifications for user
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.destination.emailId=:emailId AND n.markAsRead=FALSE" )
    Long countByDestinationId(@Param("emailId") String emailId);

    @Query("SELECT n FROM Notification n WHERE n.destination.userProfileId = :destinationId ORDER BY n.createdAt DESC")
    List<Notification> getAllNotificationsForUserById(@Param("destinationId") Long destinationId);

    @Query("SELECT n FROM Notification n WHERE n.destination.userProfileId = :destinationId AND n.markAsRead = false ORDER BY n.createdAt DESC")
    List<Notification> findUnreadNotificationsByDestinationId(@Param("destinationId") Long destinationId);

}

