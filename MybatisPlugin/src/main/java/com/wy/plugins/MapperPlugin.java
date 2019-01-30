package com.wy.plugins;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.mybatis.generator.api.IntrospectedColumn;
//import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
//import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
//import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
//import org.mybatis.generator.api.dom.xml.TextElement;
//import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.util.StringUtility;

/**
 * 生成dao层方法
 * 
 * @author paradiseWy
 */
public class MapperPlugin extends PluginAdapter {

	// 通用 Mapper 接口
	private Set<String> baseMappers = new HashSet<>();

	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}

	@Override
	public void setProperties(Properties properties) {
		super.setProperties(properties);

		String baseMappers = this.properties.getProperty("baseMappers");
		if (StringUtility.stringHasValue(baseMappers)) {
			Collections.addAll(this.baseMappers, baseMappers.split(","));
		}
	}

	/**
	 * 生成 Mapper 文件中的 Element
	 */
	@Override
	public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
		XmlElement rootElement = document.getRootElement();
		XmlElement sqlElement = new XmlElement("sql");
		Attribute attr = new Attribute("id", "BaseColumns");
		sqlElement.addAttribute(attr);
		// 获取全部列名称
		StringBuilder columnsBuilder = new StringBuilder();
		List<IntrospectedColumn> columnList = introspectedTable.getAllColumns();
		for (IntrospectedColumn column : columnList) {
			columnsBuilder.append(column.getActualColumnName()).append(",");
		}
		String columns = columnsBuilder.substring(0, columnsBuilder.length() - 1);
		TextElement content = new TextElement(columns);
		sqlElement.addElement(content);
		rootElement.addElement(sqlElement);
		return true;
	}

	/**
	 * 使用mybatis生成mapper接口,返回true的时候表示生成
	 */
	@Override
	public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		if (this.baseMappers.size() == 0) {
			return true;
		}
		// 获取实体类
		FullyQualifiedJavaType entityType = new FullyQualifiedJavaType(
				introspectedTable.getBaseRecordType());
		// import 实体类
		interfaze.addImportedType(entityType);
		// 添加 @Mapper 注解
		interfaze.addAnnotation("@Mapper");
		// import 接口
		interfaze.addImportedType(
				new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper"));
		for (String mapper : baseMappers) {
			interfaze.addImportedType(new FullyQualifiedJavaType(mapper));
			interfaze.addSuperInterface(
					new FullyQualifiedJavaType(mapper + "<" + entityType.getShortName() + ">"));
		}
		return true;
	}
}