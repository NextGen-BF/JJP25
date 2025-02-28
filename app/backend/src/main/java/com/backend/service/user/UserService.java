package com.backend.service.user;

import com.backend.entity.dto.outgoing.RegisterResponse;
import com.backend.entity.dto.incoming.RegisterRequest;

public interface UserService {

    RegisterResponse register(RegisterRequest registerRequest);

}
