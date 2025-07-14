package com.kseb.collabtool.domain.message.service;


import com.kseb.collabtool.domain.channel.entity.Channel;
import com.kseb.collabtool.domain.message.repository.MessageRepository;
import com.kseb.collabtool.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    private final UserRepository userRepository;



}
