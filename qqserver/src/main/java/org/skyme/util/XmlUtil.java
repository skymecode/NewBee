package org.skyme.util;


import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author:Skyme
 * @create: 2023-08-17 15:45
 * @Description:解析xml文件
 */
public class XmlUtil {
    private static SAXReader saxReader=new SAXReader();
    private static Document document;

    static {
        try {
            File file = new File("F:\\project\\QQ\\qqserver\\src\\main\\resources\\web.xml");
            document = saxReader.read(file);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }


    public static List<String> getServletName(){
        Element rootElement = document.getRootElement();
        Element element = rootElement.element("servlet-mapping");
        List<Element> name = element.elements("servlet-name");
        List<String> data=new ArrayList<>();
        for (Element element1 : name) {
            String text = element1.getText();
            data.add(text);
        }
        return data;

    }
    public static List<String> getServletPaths(){
        Element rootElement = document.getRootElement();
        Element element = rootElement.element("servlet-mapping");
        List<Element> path = element.elements("url-pattern");
        List<String> data=new ArrayList<>();
        for (Element element1 : path) {
            String text = element1.getText();
            data.add(text);
        }
        return data;
    }
    public static List<String> getServletClass(){
        Element rootElement = document.getRootElement();
        Element element = rootElement.element("servlet");
        List<Element> path = element.elements("servlet-class");
        List<String> data=new ArrayList<>();
        for (Element element1 : path) {
            String text = element1.getText();
            data.add(text);
        }
        return data;
    }
    public static List<String> servletName(){
        Element rootElement = document.getRootElement();
        Element element = rootElement.element("servlet");
        List<Element> path = element.elements("servlet-name");
        List<String> data=new ArrayList<>();
        for (Element element1 : path) {
            String text = element1.getText();
            data.add(text);
        }
        return data;
    }
}
