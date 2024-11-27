package az.ingress.client.dto;

import az.ingress.client.enums.ChannelType;
import lombok.*;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class NotificationDto {
    private ChannelType channelType;
    private String payload;
}
