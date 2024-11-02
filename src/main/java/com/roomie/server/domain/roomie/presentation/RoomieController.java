package com.roomie.server.domain.roomie.presentation;

import com.roomie.server.domain.roomie.application.RoomieService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RoomieController {

    private final RoomieService roomieService;

}
