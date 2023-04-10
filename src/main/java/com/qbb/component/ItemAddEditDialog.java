package com.qbb.component;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.qbb.dto.ConfigDTO;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

public class ItemAddEditDialog extends DialogWrapper {

    private final boolean isAdd;
    private final ConfigDTO configDTO;
    private final String projectPath;
    private JComboBox comboBox;
    private ItemComponent itemComponent;

    public ItemAddEditDialog(ConfigDTO configDTO, Project project) {
        super(true); // use current window as parent
        isAdd = configDTO == null;
        setTitle((configDTO == null ? "Add " : "Edit ") + project.getName() + " Project");
        this.configDTO = isAdd ? new ConfigDTO() : configDTO;
        projectPath = project.getBasePath();
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
//        comboBox.setModel(new DefaultComboBoxModel(moduleArr));
        comboBox.setBounds(15, 15, 100, 35);
        comboBox.setEnabled(false);
        comboBox.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle("选择当前配置的对应目录");
                fc.setSelectedFile(new File(projectPath));
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int option = fc.showOpenDialog(comboBox);
                if(option == JFileChooser.APPROVE_OPTION){
                    File file = fc.getSelectedFile();
                    comboBox.setModel(new DefaultComboBoxModel(new String[]{file.getAbsolutePath()}));
                    comboBox.setSelectedItem(file.getAbsolutePath());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

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
        configDTO.setModulePath((String) comboBox.getSelectedItem());
        configDTO.setYapiUrl(itemComponent.yapiTextArea.getText());
        configDTO.setProjectToken(itemComponent.projectTokenTextArea.getText());
        configDTO.setProjectId(itemComponent.projectIdTextArea.getText());
        configDTO.setProjectType((String) itemComponent.projectTypeComboBox.getSelectedItem());
        return configDTO;
    }
}
