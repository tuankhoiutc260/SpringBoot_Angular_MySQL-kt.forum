package com.tuankhoi.backend.repository.Jpa;

import com.tuankhoi.backend.model.entity.Notification;
import com.tuankhoi.backend.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findByUserAndOriginTypeOrderByCreatedDateDesc(User user, String originType);

    List<Notification> findByUserAndIsReadFalseAndOriginTypeOrderByCreatedDateDesc(User user, String originType);
}
