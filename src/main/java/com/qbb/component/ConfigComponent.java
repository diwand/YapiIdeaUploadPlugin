package com.qbb.component;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import com.qbb.dto.ConfigDTO;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

/**
 * @author zhangyunfan
 * @version 1.0
 * @ClassName: ConfigComponent
 * @Description: 配置界面
 * @date 2020/12/25
 */
public class ConfigComponent implements SearchableConfigurable {

    private ConfigPersistence configPersistence = ConfigPersistence.getInstance();

    @NotNull
    @Override
    public String getId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "YapiUpload";
    }

    private JBList<ConfigDTO> list;

    private DefaultListModel<ConfigDTO> defaultListModel;

    @Nullable
    @Override
    public JComponent createComponent() {
        final List<ConfigDTO> configDTOS = configPersistence.getConfigs();
        defaultListModel = new DefaultListModel<>();
        for (int i = 0, len = configDTOS == null ? 0 : configDTOS.size(); i < len; i++) {
            defaultListModel.addElement(configDTOS.get(i));
        }
        list = new JBList<>(defaultListModel);
        list.setLayout(new BorderLayout());
        list.setCellRenderer(new ItemComponent());

        // 工具栏
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(list);
        decorator.setPreferredSize(new Dimension(0, 300));
        // 新增
        decorator.setAddAction(actionButton -> addAction());
        // 编辑
        decorator.setEditAction(anActionButton -> editAction());

        return decorator.createPanel();
    }

    private void editAction() {
        int index = list.getSelectedIndex();
        final Project project = ProjectUtil.guessCurrentProject(list);
        ItemAddEditDialog itemAddEditDialog = new ItemAddEditDialog(defaultListModel.get(index), project);
        if (itemAddEditDialog.showAndGet()) {
            final ConfigDTO config = itemAddEditDialog.getConfigDTO();
            for (int i = 0; i < defaultListModel.getSize(); i++) {
                if (i == index) {
                    continue;
                }
                final ConfigDTO dto = defaultListModel.get(i);
                if (dto.getProjectName().equals(config.getProjectName()) && dto.getModuleName().equals(config.getModuleName())) {
                    Messages.showErrorDialog("编辑出错了，已添加该模块配置！", "Error");
                    return;
                }
            }
            defaultListModel.set(index, config);
            try {
                apply();
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }
        } else {
            Messages.showInfoMessage("关闭", "Info");
        }
    }

    private void addAction() {
        final Project project = ProjectUtil.guessCurrentProject(list);
        ItemAddEditDialog itemAddEditDialog = new ItemAddEditDialog(null, project);
        if (itemAddEditDialog.showAndGet()) {
            final ConfigDTO config = itemAddEditDialog.getConfigDTO();
            final Enumeration<ConfigDTO> elements = defaultListModel.elements();
            while (elements.hasMoreElements()) {
                final ConfigDTO dto = elements.nextElement();
                if (dto.getProjectName().equals(config.getProjectName()) && dto.getModuleName().equals(config.getModuleName())) {
                    Messages.showErrorDialog("添加出错了，已添加该模块配置！", "Error");
                    return;
                }
            }
            defaultListModel.addElement(config);
            try {
                apply();
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }
        } else {
            Messages.showInfoMessage("关闭", "Info");
        }
    }

    @Override
    public boolean isModified() {
        if (configPersistence.getConfigs() == null) {
            return true;
        }
        //当用户修改配置参数后，在点击“OK”“Apply”按钮前，框架会自动调用该方法，判断是否有修改，进而控制按钮“OK”“Apply”的是否可用。
        return defaultListModel.size() == configPersistence.getConfigs().size();
    }

    @Override
    public void apply() throws ConfigurationException {
        //用户点击“OK”或“Apply”按钮后会调用该方法，通常用于完成配置信息持久化。
        final Enumeration<ConfigDTO> elements = defaultListModel.elements();
        List<ConfigDTO> list = new ArrayList<>();
        while (elements.hasMoreElements()) {
            list.add(elements.nextElement());
        }
        configPersistence.setConfigs(list);
    }
}
