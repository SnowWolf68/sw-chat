package com.snwolf.chat.common.websocket.domain.vo.response;

import lombok.Data;

@Data
public class WSBaseResp<T> {

    private Integer type;

    private T data;
}
