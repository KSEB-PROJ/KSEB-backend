package com.kseb.collabtool.domain.channel.service;

import com.kseb.collabtool.domain.channel.dto.ChannelCreateRequest;
import com.kseb.collabtool.domain.channel.entity.Channel;
import com.kseb.collabtool.domain.channel.entity.ChannelType;
import com.kseb.collabtool.domain.channel.repository.ChannelRepository;
import com.kseb.collabtool.domain.channel.repository.ChannelTypeRepository;
import com.kseb.collabtool.domain.groups.entity.Group;
import com.kseb.collabtool.domain.groups.repository.GroupRepository;
import com.kseb.collabtool.global.exception.GeneralException;
import com.kseb.collabtool.global.exception.Status;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChannelService {


    private final ChannelRepository channelRepository;
    private final ChannelTypeRepository channelTypeRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public Channel createChannel(Long groupId,Long userId, ChannelCreateRequest request) {
        //그룹이 존재하는지
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GeneralException(Status.GROUP_NOT_FOUND));

        //리더만 채널 생성 가능하도록
        if (!group.getOwner().getId().equals(userId)) {
            throw new GeneralException(Status.CHANNEL_CREATE_ONLY_LEADER);
        }
        //존재하는 채널 타입 유형인지 체크
        ChannelType channelType = channelTypeRepository.findById(request.getChannelTypeId())
                .orElseThrow(() -> new GeneralException(Status.CHANNEL_TYPE_NOT_FOUND));

        // CALENDAR(id=2), NOTICE(id=3) 채널은 한 개만 허용
        Short typeId = channelType.getId();
        if (typeId == 2 || typeId == 3) {
            boolean exists = channelRepository.existsByGroupIdAndChannelType_Id(groupId, typeId);
            if (exists) {
                throw new GeneralException(Status.CHANNEL_TYPE_ALREADY_EXISTS);
            }
        }

        Channel channel = new Channel();
        channel.setGroup(group);
        channel.setName(request.getName());
        channel.setChannelType(channelType);
        channel.setIsSystem(typeId == 2 || typeId == 3);

        return channelRepository.save(channel);
    }

}
