package com.likelion.likelionjwt.oauth.api.dto;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Token {

    @SerializedName("access_token")
    private String accessToken;

}
