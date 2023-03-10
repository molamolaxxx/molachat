package com.mola.molachat.form;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author: molamola
 * @Date: 19-8-14 下午5:20
 * @Version 1.0
 */
@Data
public class ChatterForm {

    /**
     * id唯一
     */
    @NotNull(message = "chatterId不可为空")
    private String id;

    @NotNull(message = "token不可为空")
    private String token;

    /**
     * 昵称
     */
    private String name;

    /**
     * 头像url
     */
    private String imgUrl;

    /**
     * 个性签名
     */
    private String signature;
}
