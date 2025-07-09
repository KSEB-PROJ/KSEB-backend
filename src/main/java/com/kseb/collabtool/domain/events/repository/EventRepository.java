package com.kseb.collabtool.domain.events.repository;


import com.kseb.collabtool.domain.events.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event,Long> {


}
