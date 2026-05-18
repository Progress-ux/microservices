package com.progress.authservice.model.dto;

public record JwkDto(
   String kty,
   String alg,
   String use,
   String kid,
   String n,
   String e
) {}
