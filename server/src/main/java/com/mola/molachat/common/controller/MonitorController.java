package com.mola.molachat.common.controller;

import com.mola.molachat.common.model.ServerResponse;
import com.mola.molachat.session.data.SessionFactoryInterface;
import com.mola.molachat.group.model.Group;
import com.mola.molachat.session.model.VideoSession;
import com.mola.molachat.chatter.dto.ChatterDTO;
import com.mola.molachat.server.ChatServer;
import com.mola.molachat.chatter.service.ChatterService;
import com.mola.molachat.group.service.GroupService;
import com.mola.molachat.server.service.ServerService;
import com.mola.molachat.session.service.SessionService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: molamola
 * @Date: 19-8-6 下午6:22
 * @Version 1.0
 * 监控chat系统的controller
 */
@RestController
@RequestMapping("/monitor")
public class MonitorController {

    @Resource
    private ChatterService chatterService;

    @Resource
    private SessionService sessionService;

    @Resource
    private ServerService serverService;

    @Resource
    private GroupService groupService;

    @Resource
    private SessionFactoryInterface sessionFactory;

    /**
     * 查看在线的chatter
     * @return
     */
    @GetMapping("/chatterList")
    private ServerResponse catChatterList(){
        List<ChatterDTO> chatterDTOS = chatterService.list();
        return ServerResponse.createBySuccess(chatterDTOS);
    }

    @GetMapping("/sessionList")
    private ServerResponse catCurrentSessionList(){
        return ServerResponse.createBySuccess(sessionService.list());
    }

    @GetMapping("/serverList")
    private ServerResponse catServerList(){
        return ServerResponse.createBySuccess(serverService.list().stream().map(e -> {
            ChatServer cs = new ChatServer();
            cs.setChatterId(e.getChatterId());
            cs.setLastHeartBeat(e.getLastHeartBeat());
            cs.setConnectClientCount(e.getConnectClientCount());
            return cs;
        }).collect(Collectors.toList()));
    }

    @GetMapping("/groupList")
    private ServerResponse catGroupList(){
        List<Group> groupList = groupService.listAll();
        return ServerResponse.createBySuccess(groupList);
    }

    @GetMapping("/videoSessions")
    private ServerResponse videoSessions(){
        List<VideoSession> videoSessions = sessionFactory.listVideoSession();
        return ServerResponse.createBySuccess(videoSessions);
    }

    @PostMapping("/changeUserTag")
    public ServerResponse changeUserTag(@RequestParam String chatterId, @RequestParam Integer tag) {
        chatterService.setChatterTag(chatterId, tag);
        return ServerResponse.createBySuccess();
    }

}
