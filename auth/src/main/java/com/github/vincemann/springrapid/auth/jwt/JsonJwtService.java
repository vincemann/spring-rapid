package com.github.vincemann.springrapid.auth.jwt;

import com.nimbusds.jose.Payload;
import com.nimbusds.jwt.JWTClaimsSet;

public abstract class JsonJwtService implements JwtService {

    protected Payload createPayload(JWTClaimsSet jwtClaimsSet){
        return new Payload(jwtClaimsSet.toJSONObject());
    }
}
