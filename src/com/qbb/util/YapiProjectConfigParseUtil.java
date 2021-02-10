package com.qbb.util;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.qbb.dto.YapiProjectConfig;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class YapiProjectConfigParseUtil {

    /**
     * 解析获取相关配置.
     */
    public static YapiProjectConfig parse(Project project, Module module) throws IOException, ParserConfigurationException, SAXException {
        VirtualFile yapiConfigFile = null;
        if (module != null) {
            VirtualFile[] moduleContentRoots = ModuleRootManager.getInstance(module).getContentRoots();
            if (moduleContentRoots.length > 0) {
                yapiConfigFile = moduleContentRoots[0].findFileByRelativePath("yapi.xml");
            }
        }
        if (yapiConfigFile != null && yapiConfigFile.exists()) {
            // yapi.xml && idea project config xml
            String xml = new String(yapiConfigFile.contentsToByteArray(), StandardCharsets.UTF_8);
            String projectConfigXml = new String(project.getProjectFile().contentsToByteArray(), StandardCharsets.UTF_8);
            return YapiProjectConfigParseUtil.readFromXml(xml, projectConfigXml, module != null ? module.getName() : null);
        } else {
            // 兼容旧版本配置读取.
            String xml = new String(project.getProjectFile().contentsToByteArray(), StandardCharsets.UTF_8);
            return YapiProjectConfigParseUtil.readFromXmlOldVersion(xml, module != null ? module.getName() : null);
        }
    }

    /**
     * 读取配置从指定格式的xml.
     */
    public static YapiProjectConfig readFromXml(String yapiXml, String projectXml, String moduleName) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(yapiXml.getBytes(StandardCharsets.UTF_8)));
        Element root = doc.getDocumentElement();
        YapiProjectConfig config = doReadXmlByNodeList(root.getChildNodes(), true, moduleName);
        if (StringUtils.isEmpty(config.getProjectToken()) && StringUtils.isNotEmpty(projectXml)) {
            String projectToken = doReadProjectTokenByProjectConfigXml(projectXml, moduleName);
            config.setProjectToken(projectToken);
        }
        return config;
    }

    private static String doReadProjectTokenByProjectConfigXml(String projectXml, String moduleName) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(projectXml.getBytes(StandardCharsets.UTF_8)));
        Element root = doc.getDocumentElement();
        NodeList componentNodes = root.getChildNodes();

        for (int i = 0; i < componentNodes.getLength(); i++) {
            Node componentNode = componentNodes.item(i);
            NamedNodeMap componentAttributes = componentNode.getAttributes();
            if (componentAttributes == null) continue;
            Node componentNameNode = componentAttributes.getNamedItem("name");
            if (componentNameNode == null) continue;
            String componentName = componentNameNode.getNodeValue().trim();
            if (!componentName.equals("yapi")) continue;
            NodeList optionNodes = componentNode.getChildNodes();

            String attributeName = moduleName + ".projectToken";
            for (int j = 0; j < optionNodes.getLength(); j++) {
                Node node = optionNodes.item(j);
                NamedNodeMap optionAttributes = node.getAttributes();
                if (optionAttributes == null) continue;
                Node optionNameNode = optionAttributes.getNamedItem("name");
                if (optionNameNode != null && optionNameNode.getNodeValue().trim().equals(attributeName)) {
                    return node.getTextContent().trim();
                }
            }

            attributeName = "projectToken";
            for (int j = 0; j < optionNodes.getLength(); j++) {
                Node node = optionNodes.item(j);
                NamedNodeMap optionAttributes = node.getAttributes();
                if (optionAttributes == null) continue;
                Node optionNameNode = optionAttributes.getNamedItem("name");
                if (optionNameNode != null && optionNameNode.getNodeValue().trim().equals(attributeName)) {
                    return node.getTextContent().trim();
                }
            }
        }
        return null;
    }

    /**
     * 读取配置从老版的项目的xml配置.
     */
    public static YapiProjectConfig readFromXmlOldVersion(String xml, String moduleName) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        Element root = doc.getDocumentElement();
        NodeList componentNodes = root.getChildNodes();

        // 单模块
        for (int i = 0; i < componentNodes.getLength(); i++) {
            Node componentNode = componentNodes.item(i);
            NamedNodeMap componentAttributes = componentNode.getAttributes();
            if (componentAttributes == null) continue;
            Node componentNameNode = componentAttributes.getNamedItem("name");
            if (componentNameNode == null) continue;
            String componentName = componentNameNode.getNodeValue().trim();
            if (!componentName.equals("yapi")) continue;
            NodeList optionNodes = componentNode.getChildNodes();

            boolean isMultipleModules = false;
            for (int j = 0; j < optionNodes.getLength(); j++) {
                NamedNodeMap optionAttributes = optionNodes.item(j).getAttributes();
                if (optionAttributes == null) continue;
                Node optionNameNode = optionNodes.item(j).getAttributes().getNamedItem("name");
                if (optionNameNode != null && optionNameNode.getNodeValue().trim().equals("moduleList")) {
                    isMultipleModules = true;
                    break;
                }
            }
            if (!isMultipleModules) {
                return doReadXmlByNodeList(optionNodes, false, null);
            }
        }

        // 多模块
        for (int i = 0; i < componentNodes.getLength(); i++) {
            Node componentNode = componentNodes.item(i);
            NamedNodeMap componentAttributes = componentNode.getAttributes();
            if (componentAttributes == null) continue;
            Node componentNameNode = componentAttributes.getNamedItem("name");
            if (componentNameNode == null) continue;
            String componentName = componentNameNode.getNodeValue().trim();
            if (!componentName.equals(moduleName)) continue;
            return doReadXmlByNodeList(componentNode.getChildNodes(), false, moduleName);
        }

        return new YapiProjectConfig();
    }


    @NotNull
    private static YapiProjectConfig doReadXmlByNodeList(NodeList nodes, boolean newVersion, String moduleName) {
        YapiProjectConfig config = new YapiProjectConfig();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            String attribute = null;
            if (newVersion) {
                // <projectId>...</projectId>
                attribute = node.getNodeName();
            } else {
                // <option name="projectId">...</option>
                NamedNodeMap attributes = node.getAttributes();
                if (attributes != null) {
                    Node name = attributes.getNamedItem("name");
                    if (name != null) {
                        attribute = name.getNodeValue().trim();
                    }
                    if (moduleName != null && attribute != null && attribute.startsWith(moduleName + ".")) {
                        attribute = StringUtils.substringAfter(attribute, moduleName + ".");
                    }
                }
            }
            attribute = attribute != null ? attribute : "";
            String value = node.getTextContent().trim();
            switch (attribute) {
                case "yapiUrl":
                    config.setYapiUrl(value);
                    break;
                case "projectToken":
                    config.setProjectToken(value);
                    break;
                case "projectId":
                    config.setProjectId(value);
                    break;
                case "projectType":
                    config.setProjectType(value);
                    break;
                case "returnClass":
                    config.setReturnClass(value);
                    break;
                case "attachUpload":
                    config.setAttachUpload(value);
                    break;
            }
        }
        return config;
    }

}
