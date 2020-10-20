package com.github.vincemann.springlemon.auth.service.token;

import com.nimbusds.jose.Payload;
import com.nimbusds.jwt.JWTClaimsSet;

public abstract class JsonJwtService implements JwtService {

    protected Payload createPayload(JWTClaimsSet jwtClaimsSet){
        return new Payload(jwtClaimsSet.toJSONObject());
    }
}
