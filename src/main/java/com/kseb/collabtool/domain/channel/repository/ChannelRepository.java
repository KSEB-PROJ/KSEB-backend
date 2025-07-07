package com.kseb.collabtool.domain.channel.repository;

import com.kseb.collabtool.domain.channel.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel,Long> {
    boolean existsByGroupIdAndChannelType_Id(Long groupId, Short channelTypeId);
}
