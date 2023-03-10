package com.mola.molachat.schedule;

import com.mola.molachat.config.SelfConfig;
import com.mola.molachat.entity.dto.ChatterDTO;
import com.mola.molachat.enumeration.ChatterStatusEnum;
import com.mola.molachat.server.ChatServer;
import com.mola.molachat.service.ChatterService;
import com.mola.molachat.service.ServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.websocket.EncodeException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: molamola
 * @Date: 19-8-18 下午10:53
 * @Version 1.0
 * 用于ws的定时任务
 */
@Configuration
@EnableScheduling
@Slf4j
public class ServerScheduleTask {

    @Autowired
    private SelfConfig config;

    @Autowired
    private ServerService serverService;

    @Autowired
    private ChatterService chatterService;

    /**
     * 检查所有服务器的最后心跳时间,大于15秒当做连接失败
     */
    @Scheduled(fixedRate = 30000)
    private void checkServersStatus(){
        log.info("check:开始检查所有连接状态");
        // 克隆到新list中，避免并发修改异常
        List<ChatServer> chatServerList = new ArrayList<>();
        for (ChatServer chatServer : serverService.list()) {
            chatServerList.add(chatServer);
        }
        for (ChatServer server : chatServerList){
            Long lastHeartBeat = server.getLastHeartBeat();

            if (null == lastHeartBeat){
                continue;
            }
            //距离上次心跳{自定义}分钟以上，将chatter状态设置为离线
            if (System.currentTimeMillis() - lastHeartBeat > config.getCONNECT_TIMEOUT()){
                log.error("超时为"+(System.currentTimeMillis() - lastHeartBeat)+",chatter离线");
                chatterService.setChatterStatus(server.getChatterId(),
                        ChatterStatusEnum.DISCONNECT.getCode());
            }

            //若距离上次心跳{自定义}分钟以上，删除服务器
            if (System.currentTimeMillis() - lastHeartBeat > config.getCLOSE_TIMEOUT()){
                log.error("超时为"+(System.currentTimeMillis() - lastHeartBeat)+",chatter关闭服务器");
                try {
                    //设置为离线
                    server.onClose();
                } catch (IOException | EncodeException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * 检测chatter是否没有server
     * 如果没有，设置为离线
     */
    @Scheduled(fixedRate = 3600000)
    private void checkChatterSingle() {
        log.info("开始检查chatter是否持有server");
        for (ChatterDTO chatter : chatterService.list()){
            if (chatter.isRobot()) {
                continue;
            }
            String chatterId = chatter.getId();
            if (null == serverService.selectByChatterId(chatterId)){
                chatterService.setChatterStatus(chatter.getId(),
                        ChatterStatusEnum.OFFLINE.getCode());
            }
        }
    }

    /**
     * 扫描删除未一一对应的server或chatter
     */
    @Scheduled(fixedRate = 120000)
    private void checkChatterAlive() {
        log.info("开始检查server是否持有chatter");
        for (ChatServer server : serverService.list()){
            String serverId = server.getChatterId();
            if (null == chatterService.selectById(serverId)){
                try {
                    //关闭服务器
                    server.onClose();
                } catch (IOException | EncodeException e) {
                }
            }
        }
    }
}
