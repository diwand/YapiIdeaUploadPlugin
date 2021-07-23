package com.qbb.component;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.qbb.dto.ConfigDTO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.lang.model.element.Element;

/**
 * @author zhangyunfan
 * @version 1.0
 * @ClassName: ConfigPersistence
 * @Description: 配置持久化
 * @date 2020/12/25
 */
@State(name = "yapiUpload", storages = {@Storage(value = "$APP_CONFIG$/yapiUpload.xml")})
public class ConfigPersistence implements PersistentStateComponent<ConfigDTO> {


    public ConfigDTO getConfigDTO() {
        return configDTO;
    }

    public void setConfigDTO(ConfigDTO configDTO) {
        this.configDTO = configDTO;
    }

    private ConfigDTO configDTO;

    public static ConfigPersistence getInstance(){
        return ServiceManager.getService(ConfigPersistence.class);
    }


    @Nullable
    @Override
    public ConfigDTO getState() {
        return this.configDTO;
    }

    @Override
    public void loadState(@NotNull ConfigDTO element) {
        this.configDTO = element;
    }
}
