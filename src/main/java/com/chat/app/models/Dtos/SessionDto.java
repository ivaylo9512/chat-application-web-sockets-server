package com.chat.app.models.Dtos;

import com.chat.app.models.Message;
import com.chat.app.models.Session;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class SessionDto {
    private LocalDate date;
    private List<MessageDto> messages;

    public SessionDto() {
    }

    public SessionDto(Session session){
        this.date = session.getDate();
        toMessageDto(session.getMessages());
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<MessageDto> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageDto> messages) {
        this.messages = messages;
    }

    public void toMessageDto(List<Message> messages) {
        if(messages != null){
            this.messages = messages.stream().map(MessageDto::new).collect(Collectors.toList());
        }
    }
}
