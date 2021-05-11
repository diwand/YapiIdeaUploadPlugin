package com.qbb.component;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.BaseComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.Messages;
import com.qbb.dto.ConfigDTO;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.api.CmdlineRemoteProto;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
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

    private static final int TEXT_AREA_ROWS = 1;

    private static final int TEXT_AREA_COLUMNS = 5;

    private static final int TEXT_AREA_FONTSIZE = 15;

    private JPanel panel;

    private JLabel yapiJLabel;
    private JTextField yapiTextArea;

    private JLabel projectTokenJLabel;
    private JTextField projectTokenTextArea;

    private JLabel projectIdJLabel;
    private JTextField projectIdTextArea;

    private JLabel projectTypeJLabel;
    private JComboBox projectTypeTextArea;

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

    @Nullable
    @Override
    public JComponent createComponent() {
        panel = new JPanel();
        GridBagLayout gridBagLayout = new GridBagLayout();
        panel.setLayout(gridBagLayout);
        //实例化这个对象用来对组件进行管理
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(10, 10, 0, 0);
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        yapiJLabel = new JLabel("yapi地址:");
        yapiTextArea = new JTextField(configPersistence.getConfigDTO() == null ? "" : configPersistence.getConfigDTO().getYapiUrl());
        yapiTextArea.setBorder(border);
        yapiTextArea.setFont(new Font(null, Font.PLAIN, TEXT_AREA_FONTSIZE));
        yapiTextArea.setSize(10, 10);
        yapiJLabel.setAlignmentY(yapiJLabel.LEFT_ALIGNMENT);

        projectTokenJLabel = new JLabel("项目Token:");
        projectTokenTextArea = new JTextField(configPersistence.getConfigDTO() == null ? "" : configPersistence.getConfigDTO().getProjectToken());
        projectTokenTextArea.setBorder(border);
        projectTokenTextArea.setFont(new Font(null, Font.PLAIN, TEXT_AREA_FONTSIZE));
        projectTokenJLabel.setAlignmentY(projectTokenJLabel.LEFT_ALIGNMENT);

        projectIdJLabel = new JLabel("项目ID:");
        projectIdTextArea = new JTextField(configPersistence.getConfigDTO() == null ? "" : configPersistence.getConfigDTO().getProjectId());
        projectIdTextArea.setBorder(border);
        projectIdTextArea.setFont(new Font(null, Font.PLAIN, TEXT_AREA_FONTSIZE));
        projectIdJLabel.setAlignmentY(projectIdJLabel.LEFT_ALIGNMENT);

        projectTypeJLabel = new JLabel("项目类型:");
        String[] select = {"api", "dubbo"};
        projectTypeTextArea = new JComboBox();
        projectTypeTextArea.setModel(new DefaultComboBoxModel(select));
        projectTypeTextArea.setBounds(15, 15, 100, 25);
        projectTypeJLabel.setAlignmentY(projectTypeJLabel.LEFT_ALIGNMENT);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagLayout.setConstraints(yapiJLabel, gridBagConstraints);
        gridBagConstraints.gridx = 20;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagLayout.setConstraints(yapiTextArea, gridBagConstraints);


        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagLayout.setConstraints(projectTokenJLabel, gridBagConstraints);
        gridBagConstraints.gridx = 20;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagLayout.setConstraints(projectTokenTextArea, gridBagConstraints);


        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagLayout.setConstraints(projectIdJLabel, gridBagConstraints);
        gridBagConstraints.gridx = 20;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagLayout.setConstraints(projectIdTextArea, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagLayout.setConstraints(projectTypeJLabel, gridBagConstraints);
        gridBagConstraints.gridx = 20;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagLayout.setConstraints(projectTypeTextArea, gridBagConstraints);

        panel.add(projectTypeJLabel);
        panel.add(projectTypeTextArea);
        panel.add(projectIdJLabel);
        panel.add(projectIdTextArea);
        panel.add(projectTokenJLabel);
        panel.add(projectTokenTextArea);
        panel.add(yapiJLabel);
        panel.add(yapiTextArea);

        return panel;
    }

    @Override
    public boolean isModified() {
        if (configPersistence.getConfigDTO() == null) {
            return true;
        }
        //当用户修改配置参数后，在点击“OK”“Apply”按钮前，框架会自动调用该方法，判断是否有修改，进而控制按钮“OK”“Apply”的是否可用。
        return !StringUtils.equals(yapiTextArea.getText(), configPersistence.getConfigDTO().getYapiUrl())
                || !StringUtils.equals(projectTokenTextArea.getText(), configPersistence.getConfigDTO().getProjectToken())
                || !StringUtils.equals(projectIdTextArea.getText(), configPersistence.getConfigDTO().getProjectId())
                || !StringUtils.equals((String) projectTypeTextArea.getSelectedItem(), configPersistence.getConfigDTO().getProjectType());
    }

    @Override
    public void apply() throws ConfigurationException {
        //用户点击“OK”或“Apply”按钮后会调用该方法，通常用于完成配置信息持久化。
        ConfigDTO configDTO = new ConfigDTO();
        configDTO.setYapiUrl(yapiTextArea.getText());
        configDTO.setProjectToken(projectTokenTextArea.getText());
        configDTO.setProjectId(projectIdTextArea.getText());
        configDTO.setProjectType((String) projectTypeTextArea.getSelectedItem());
        configPersistence.setConfigDTO(configDTO);
    }
}
