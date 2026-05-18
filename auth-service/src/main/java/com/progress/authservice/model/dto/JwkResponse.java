package com.progress.authservice.model.dto;

import java.util.List;

public record JwkResponse(
   List<JwkDto> keys
) {}
