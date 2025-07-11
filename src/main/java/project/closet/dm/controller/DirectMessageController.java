package project.closet.dm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.closet.dm.controller.api.DirectMessageApi;
import project.closet.dm.service.DirectMessageService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/direct-messages")
public class DirectMessageController implements DirectMessageApi {

    private final DirectMessageService directMessageService;
}
