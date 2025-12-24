package com.abnov.infisicalbridge.dokploy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.abnov.infisicalbridge.dto.DokployComposeUpdateRequest;

@FeignClient(name = "dokployClient", url = "${dokploy.api-url}", configuration = DokployFeignConfig.class)
public interface DokployClient {

    @PostMapping("/compose.update")
    void updateCompose(@RequestBody DokployComposeUpdateRequest request);
}
