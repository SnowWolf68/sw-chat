package com.snwolf.chat.common.user.domain.vo.resp;

import com.snwolf.chat.common.user.domain.entity.IpDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaoBaoIpResp {

    private IpDetail data;
    private String msg;
    private Integer code;
}
