package com.qbb.component;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
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
public class ConfigPersistence implements PersistentStateComponent<ConfigPersistence> {


    public List<ConfigDTO> getConfigs() {
        if (stateValue == null) {
            return null;
        }
        return new Gson().fromJson(stateValue, new TypeToken<List<ConfigDTO>>(){}.getType());
    }

    public void setConfigs(List<ConfigDTO> configs) {
        stateValue = new Gson().toJson(configs);
    }

    public static ConfigPersistence getInstance(){
        return ServiceManager.getService(ConfigPersistence.class);
    }

    public String stateValue;

    @Nullable
    @Override
    public ConfigPersistence getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ConfigPersistence state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
