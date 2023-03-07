package com.talenteo.hr.service;

import com.easyms.mail.domain.Mail;
import com.talenteo.hr.client.NotificationClient;
import com.talenteo.notification.dto.TemplateDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class NotificationRemote {

    private final NotificationClient notificationClient;

    public void send(Mail mail) {
        notificationClient.send(mail);
    }

}
