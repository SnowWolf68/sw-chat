package com.snwolf.chat.common.websocket.domain.vo.response;

import lombok.Data;

@Data
public class WSBaseResp<T> {

    /**
     * @see com.snwolf.chat.common.websocket.domain.enums.WSRespTypeEnum
     */
    private Integer type;

    private T data;
}
