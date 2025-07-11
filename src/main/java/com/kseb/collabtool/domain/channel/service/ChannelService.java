package com.kseb.collabtool.domain.channel.service;

import com.kseb.collabtool.domain.channel.dto.*;
import com.kseb.collabtool.domain.channel.entity.Channel;
import com.kseb.collabtool.domain.channel.entity.ChannelType;
import com.kseb.collabtool.domain.channel.repository.ChannelRepository;
import com.kseb.collabtool.domain.channel.repository.ChannelTypeRepository;
import com.kseb.collabtool.domain.groups.entity.Group;
import com.kseb.collabtool.domain.groups.repository.GroupMemberRepository;
import com.kseb.collabtool.domain.groups.repository.GroupRepository;
import com.kseb.collabtool.global.exception.GeneralException;
import com.kseb.collabtool.global.exception.Status;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChannelService {


    private final ChannelRepository channelRepository;
    private final ChannelTypeRepository channelTypeRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional
    public ChannelResponse createChannel(Long groupId, Long userId, ChannelCreateRequest request) {
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
        //메세지, 스케쥴 공지 생성
        channel.setName(request.getName());
        channel.setChannelType(channelType);
        channel.setIsSystem(typeId == 2 || typeId == 3);

        channelRepository.save(channel);

        return ChannelResponse.fromEntity(channel);
    }


    public List<ChannelListDto> getChannels(Long groupId, Long userId) {
        // (실무 권장) 그룹 멤버가 아니면 조회 불가
        boolean isMember = groupMemberRepository.existsByGroupIdAndUserId(groupId, userId);
        if (!isMember) {
            throw new GeneralException(Status.FORBIDDEN);
        }

        List<Channel> channels = channelRepository.findByGroupId(groupId);
        return channels.stream()
                .map(ChannelListDto::new)
                .collect(Collectors.toList());
    }

    public ChannelDetailDto getChannelDetail(Long groupId, Long channelId, Long userId) {
        //그룹 멤버 권한 체크
        boolean isMember = groupMemberRepository.existsByGroupIdAndUserId(groupId, userId); //그룹에 소속되어있나?
        if (!isMember) throw new GeneralException(Status.FORBIDDEN);

        Channel channel = channelRepository.findByGroupIdAndId(groupId, channelId)
                .orElseThrow(() -> new GeneralException(Status.CHANNEL_NOT_FOUND));
        return new ChannelDetailDto(channel);
    }


    @Transactional
    public void updateChannel(Long groupId, Long channelId, Long userId, ChannelUpdateRequest request) {
        //그룹 멤버 체크
        Channel channel = channelRepository.findByGroupIdAndId(groupId, channelId)
                .orElseThrow(() -> new GeneralException(Status.CHANNEL_NOT_FOUND));

        Long leaderId = channel.getGroup().getOwner().getId();
        if (!leaderId.equals(userId)) {    //일단은 방 판 사람만. 오너만
            throw new GeneralException(Status.CHANNEL_UPDATE_ONLY_LEADER);
        }

        channel.setName(request.getName());
    }

    @Transactional
    public void deleteChannel(Long groupId, Long channelId, Long userId) {
        //채널 조회 (그룹 내 채널)
        Channel channel = channelRepository.findByGroupIdAndId(groupId, channelId)
                .orElseThrow(() -> new GeneralException(Status.CHANNEL_NOT_FOUND));

        //리더 검증
        Long leaderId = channel.getGroup().getOwner().getId();
        if (!leaderId.equals(userId)) {
            throw new GeneralException(Status.CHANNEL_DELETE_ONLY_LEADER);
        }

        /*//시스템 채널(공지/캘린더) 삭제 제한
        if (channel.getIsSystem()) {
            throw new GeneralException(Status.CHANNEL_SYSTEM_DELETE_FORBIDDEN);
        }*/

        //삭제
        channelRepository.delete(channel);
    }

}
