package com.snwolf.chat.common.websocket.domain.vo.request;

import lombok.Data;

@Data
public class WSBaseReq {

    /**
     * @see com.snwolf.chat.common.websocket.domain.enums.WSReqTypeEnum
     */

    private Integer type;

    private String data;
}
