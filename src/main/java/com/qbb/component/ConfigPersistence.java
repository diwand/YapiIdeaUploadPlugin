package com.qbb.component;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.qbb.dto.ConfigDTO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author zhangyunfan
 * @version 1.0
 * @ClassName: ConfigPersistence
 * @Description: 配置持久化
 * @date 2020/12/25
 */
@State(name = "yapiUploads", storages = {@Storage(value = "$APP_CONFIG$/yapiUploads.xml")})
public class ConfigPersistence implements PersistentStateComponent<List<ConfigDTO>> {


    public List<ConfigDTO> getConfigs() {
        return configs;
    }

    public void setConfigs(List<ConfigDTO> configs) {
        this.configs = configs;
    }

    private List<ConfigDTO> configs;

    public static ConfigPersistence getInstance(){
        return ServiceManager.getService(ConfigPersistence.class);
    }


    @Nullable
    @Override
    public List<ConfigDTO> getState() {
        return this.configs;
    }

    @Override
    public void loadState(@NotNull List<ConfigDTO> element) {
        this.configs = element;
    }
}
