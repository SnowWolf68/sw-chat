package com.snwolf.swchat.transaction.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author <a href="https://github.com/SnowWolf68">SnowWolf68</a>
 * @Version: V1.0
 * @Date: 6/6/2024
 * @Description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SecureInvokeDTO {

    private String className;
    private String methodName;
    private String parameterTypes;
    private String args;
}
