package com.qbb.component;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.qbb.dto.ConfigDTO;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ItemAddEditDialog extends DialogWrapper {

    private final boolean isAdd;
    private final ConfigDTO configDTO;
    private final String[] moduleArr;
    private JComboBox comboBox;
    private ItemComponent itemComponent;

    public ItemAddEditDialog(ConfigDTO configDTO, Project project) {
        super(true); // use current window as parent
        isAdd = configDTO == null;
        setTitle((configDTO == null ? "Add " : "Edit ") + project.getName() + " Project");
        this.configDTO = isAdd ? new ConfigDTO() : configDTO;
        List<String> modules = Arrays.stream(ModuleManager.getInstance(project).getModules()).map(Module::getName).collect(Collectors.toList());
        moduleArr = modules.toArray(new String[]{});
        this.configDTO.setProjectName(project.getName());
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        JPanel panel = new JPanel();

        GridBagLayout gridBagLayout = new GridBagLayout();
        panel.setLayout(gridBagLayout);

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(10, 10, 0, 0);

        final JLabel label = new JLabel("选择模块:");
        label.setAlignmentY(JComponent.LEFT_ALIGNMENT);

        comboBox = new JComboBox();
        comboBox.setModel(new DefaultComboBoxModel(moduleArr));
        comboBox.setBounds(15, 15, 100, 35);

        itemComponent = new ItemComponent(configDTO, true);
        itemComponent.setPreferredSize(new Dimension(0, 230));

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagLayout.setConstraints(label, gridBagConstraints);
        gridBagConstraints.gridx = 20;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagLayout.setConstraints(comboBox, gridBagConstraints);

        gridBagConstraints.insets = new Insets(10, -10, 0, 0);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 20;
        gridBagConstraints.gridheight = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagLayout.setConstraints(itemComponent, gridBagConstraints);

        panel.add(label);
        panel.add(comboBox);
        panel.add(itemComponent);
        panel.setPreferredSize(new Dimension(500, 0));
        return panel;
    }

    @Override
    public boolean isOK() {
        if (StringUtils.isEmpty(itemComponent.yapiTextArea.getText())
        || StringUtils.isEmpty(itemComponent.projectTokenTextArea.getText())
        || StringUtils.isEmpty(itemComponent.projectIdTextArea.getText())
        || StringUtils.isEmpty((String) itemComponent.projectTypeComboBox.getSelectedItem())) {
            return false;
        }
        return super.isOK();
    }

    public ConfigDTO getConfigDTO() {
        configDTO.setModuleName((String) comboBox.getSelectedItem());
        configDTO.setYapiUrl(itemComponent.yapiTextArea.getText());
        configDTO.setProjectToken(itemComponent.projectTokenTextArea.getText());
        configDTO.setProjectId(itemComponent.projectIdTextArea.getText());
        configDTO.setProjectType((String) itemComponent.projectTypeComboBox.getSelectedItem());
        return configDTO;
    }
}
