package com.qbb.component;

import com.intellij.util.ui.JBUI;
import com.qbb.dto.ConfigDTO;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * @author jiajixu
 * @date 2021/7/23 21:15
 */
public class ItemComponent extends JPanel implements ListCellRenderer<ConfigDTO> {


    private static final int TEXT_AREA_ROWS = 1;

    private static final int TEXT_AREA_COLUMNS = 5;

    private static final int TEXT_AREA_FONTSIZE = 15;

    JLabel title = null;

    final JLabel yapiJLabel;
    final JTextField yapiTextArea;

    final JLabel projectTokenJLabel;
    final JTextField projectTokenTextArea;

    final JLabel projectIdJLabel;
    final JTextField projectIdTextArea;

    final JLabel projectTypeJLabel;
    final JComboBox projectTypeComboBox;
    final JTextField projectTypeTextArea;

    public ItemComponent() {
        this(null, false);
    }

    public ItemComponent(ConfigDTO configDTO, boolean isInDialog) {
        ConfigPersistence configPersistence = ConfigPersistence.getInstance();
        GridBagLayout gridBagLayout = new GridBagLayout();
        setLayout(gridBagLayout);
        //实例化这个对象用来对组件进行管理
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(10, 10, 0, 0);
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        yapiJLabel = new JLabel("yapi地址:");
        yapiTextArea = new JTextField();
        if (isInDialog) {
            yapiTextArea.setBorder(border);
        }
        yapiTextArea.setFont(new Font(null, Font.PLAIN, TEXT_AREA_FONTSIZE));
        yapiTextArea.setSize(10, 10);
        yapiJLabel.setAlignmentY(yapiJLabel.LEFT_ALIGNMENT);

        projectTokenJLabel = new JLabel("项目Token:");
        projectTokenTextArea = new JTextField();
        if (isInDialog) {
            projectTokenTextArea.setBorder(border);
        }
        projectTokenTextArea.setFont(new Font(null, Font.PLAIN, TEXT_AREA_FONTSIZE));
        projectTokenJLabel.setAlignmentY(projectTokenJLabel.LEFT_ALIGNMENT);

        projectIdJLabel = new JLabel("项目ID:");
        projectIdTextArea = new JTextField();
        if (isInDialog) {
            projectIdTextArea.setBorder(border);
        }
        projectIdTextArea.setFont(new Font(null, Font.PLAIN, TEXT_AREA_FONTSIZE));
        projectIdJLabel.setAlignmentY(projectIdJLabel.LEFT_ALIGNMENT);

        projectTypeJLabel = new JLabel("项目类型:");
        String[] select = {"api", "dubbo"};
        projectTypeComboBox = new JComboBox();
        projectTypeComboBox.setModel(new DefaultComboBoxModel(select));
        projectTypeComboBox.setSelectedItem("api");
        projectTypeComboBox.setBounds(15, 15, 100, 25);
        projectTypeJLabel.setAlignmentY(projectTypeJLabel.LEFT_ALIGNMENT);

        projectTypeTextArea = new JTextField();
        if (isInDialog) {
            projectTypeTextArea.setBorder(border);
        }
        projectTypeTextArea.setFont(new Font(null, Font.PLAIN, TEXT_AREA_FONTSIZE));

        if (!isInDialog) {
            title = new JLabel("哪个项目模块", JLabel.CENTER);
            title.setForeground(Color.LIGHT_GRAY);
            gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.insets = new Insets(20, 10, 0, 0);
            gridBagLayout.setConstraints(title, gridBagConstraints);

            gridBagConstraints.insets = new Insets(10, 10, 0, 0);
            gridBagConstraints.anchor = GridBagConstraints.WEST;
        }

        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.weightx = 0;
        gridBagLayout.setConstraints(yapiJLabel, gridBagConstraints);
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1;
        gridBagLayout.setConstraints(yapiTextArea, gridBagConstraints);

        gridBagConstraints.weightx = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagLayout.setConstraints(projectTokenJLabel, gridBagConstraints);
        gridBagConstraints.weightx = 1;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagLayout.setConstraints(projectTokenTextArea, gridBagConstraints);

        gridBagConstraints.weightx = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagLayout.setConstraints(projectIdJLabel, gridBagConstraints);
        gridBagConstraints.weightx = 1;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagLayout.setConstraints(projectIdTextArea, gridBagConstraints);

        gridBagConstraints.weightx = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagLayout.setConstraints(projectTypeJLabel, gridBagConstraints);
        gridBagConstraints.weightx = 1;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;

        if (title != null) {
            add(title);
        }
        add(yapiJLabel);
        add(yapiTextArea);
        add(projectTokenJLabel);
        add(projectTokenTextArea);
        add(projectIdJLabel);
        add(projectIdTextArea);

        add(projectTypeJLabel);
        if (isInDialog) {
            gridBagLayout.setConstraints(projectTypeComboBox, gridBagConstraints);
            add(projectTypeComboBox);
        } else {
            gridBagLayout.setConstraints(projectTypeTextArea, gridBagConstraints);
            add(projectTypeTextArea);
        }

        if (isInDialog) {
            setPreferredSize(new Dimension(500, 0));
        }

        if (configDTO != null) {
            yapiTextArea.setText(configDTO.getYapiUrl());
            projectTokenTextArea.setText(configDTO.getProjectToken());
            projectIdTextArea.setText(configDTO.getProjectId());
            if (isInDialog) {
                projectTypeComboBox.setSelectedItem(configDTO.getProjectType());
            } else {
                projectTypeTextArea.setText(configDTO.getProjectType());
            }
        }
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends ConfigDTO> list, ConfigDTO value, int index, boolean isSelected, boolean cellHasFocus) {
        setComponentOrientation(list.getComponentOrientation());
        setBorder(JBUI.Borders.empty(1));
        Color bg, fg;
        JList.DropLocation dropLocation = list.getDropLocation();
        if (dropLocation != null && !dropLocation.isInsert() && dropLocation.getIndex() == index) {
            bg = UIManager.getColor("List.dropCellBackground");
            fg = UIManager.getColor("List.dropCellForeground");
            isSelected = true;
        }
        else {
            bg = isSelected ? list.getSelectionBackground() : list.getBackground();
            fg = isSelected ? list.getSelectionForeground() : list.getForeground();
        }
        setBackground(bg);
        setForeground(fg);
        setFont(list.getFont());
        setOpaque(isSelected);

        title.setText(value.getModulePath());
        yapiTextArea.setText(value.getYapiUrl());
        projectTokenTextArea.setText(value.getProjectToken());
        projectIdTextArea.setText(value.getProjectId());
        projectTypeTextArea.setText(value.getProjectType());
        return this;
    }
}
