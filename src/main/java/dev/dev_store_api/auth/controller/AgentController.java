package dev.dev_store_api.auth.controller;

import dev.dev_store_api.account.service.AccountService;
import dev.dev_store_api.auth.config.routes.AgentRoutes;
import dev.dev_store_api.auth.config.routes.AuthRoutes;
import dev.dev_store_api.auth.dto.MultiAgentResponse;
import dev.dev_store_api.auth.service.MultiAgentService;
import dev.dev_store_api.common.dto.BaseResponse;
import dev.dev_store_api.common.factory.ResponseFactory;
import dev.dev_store_api.common.model.type.EMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.context}" + AgentRoutes.PREFIX)
public class AgentController {
    private final MultiAgentService multiAgentService;

    public AgentController(MultiAgentService multiAgentService) {
        this.multiAgentService = multiAgentService;
    }

    @GetMapping(value = AgentRoutes.GET_LIST)
    public ResponseEntity<BaseResponse<List<MultiAgentResponse>>> getList(
            @RequestHeader(name = "Authorization") String token) {
        List<MultiAgentResponse> data = multiAgentService.getSessionsByToken(token);
        return ResponseFactory.success(data, EMessage.SUCCESS.getMessage(), HttpStatus.OK);
    }

    @PostMapping(AgentRoutes.LOG_OUT)
    public ResponseEntity<BaseResponse<Void>> logout(
            @RequestHeader(name = "Authorization") String token,
            @PathVariable Long id) {
        multiAgentService.logoutSession(token, id);
        return ResponseFactory.success(null, EMessage.SUCCESS.getMessage(), HttpStatus.OK);
    }

    @PostMapping(AgentRoutes.LOG_OUT_ALL)
    public ResponseEntity<BaseResponse<Void>> logoutAll(
            @RequestHeader(name = "Authorization") String token
    ) {
        multiAgentService.logoutAllSessions(token);
        return ResponseFactory.success(null, EMessage.SUCCESS.getMessage(), HttpStatus.OK);
    }

    @DeleteMapping(AgentRoutes.DELETE)
    public ResponseEntity<BaseResponse<Void>> delete(
            @RequestHeader(name = "Authorization") String token,
            @PathVariable Long id) {
        multiAgentService.deleteSession(token, id);
        return ResponseFactory.success(null, EMessage.SUCCESS.getMessage(), HttpStatus.OK);
    }

}
