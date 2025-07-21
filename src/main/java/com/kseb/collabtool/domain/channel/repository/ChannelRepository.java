package com.kseb.collabtool.domain.channel.repository;

import com.kseb.collabtool.domain.channel.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChannelRepository extends JpaRepository<Channel,Long> {
    boolean existsByGroupIdAndChannelType_Id(Long groupId, Short channelTypeId);

    List<Channel> findByGroupId(Long groupId);
    Optional<Channel> findByGroupIdAndId(Long groupId, Long channelId);
}
