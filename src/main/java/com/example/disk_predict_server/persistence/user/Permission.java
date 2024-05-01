package com.example.disk_predict_server.persistence.user;

import ch.qos.logback.core.net.SyslogOutputStream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {
    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete")
    ;

    @Getter
    private final  String permission;

}