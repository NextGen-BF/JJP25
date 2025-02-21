package com.backend.service.user;

import com.backend.entity.dto.exp.RegisterResponse;
import com.backend.entity.dto.imp.RegisterRequest;

public interface UserService {

    RegisterResponse register(RegisterRequest registerRequest);

}
