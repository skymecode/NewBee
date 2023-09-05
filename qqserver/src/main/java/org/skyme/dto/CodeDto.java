package org.skyme.dto;

import java.io.Serializable;

/**
 * @author:Skyme
 * @create: 2023-09-05 23:23
 * @Description:
 */
public class CodeDto implements Serializable {
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
