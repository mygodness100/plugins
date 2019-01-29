package com.wy.plugins;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

//import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
//import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;
//import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
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
	// private String targetPackage;

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

		// String targetPackage =
		// this.properties.getProperty("targetPackage");
		// if (StringUtility.stringHasValue(targetPackage)) {
		// this.targetPackage = targetPackage;
		// } else {
		// throw new RuntimeException("targetPackage 属性不能为空！");
		// }
	}

	/**
	 * 生成 Mapper 文件中的 Element
	 */
	@Override
	public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
//		// 获取根元素
//		XmlElement rootElement = document.getRootElement();
//		// 新建 sql 元素标签
//		XmlElement sqlElement = new XmlElement("sql");
//		// 新建 sql 元素属性
//		Attribute attr = new Attribute("id", "BaseColumns");
//		sqlElement.addAttribute(attr);
//		// 新建 sql 元素内容
//		TextElement comment = new TextElement("");
//		// 获取全部列名称
//		StringBuilder columnsBuilder = new StringBuilder();
//		List<IntrospectedColumn> columnList = introspectedTable.getAllColumns();
//		for (IntrospectedColumn column : columnList) {
//			columnsBuilder.append(column.getActualColumnName()).append(", ");
//		}
//		// 删除最后一个逗号
//		String columns = columnsBuilder.substring(0, columnsBuilder.length() - 2);
//		TextElement content = new TextElement(columns);
//		sqlElement.addElement(comment);
//		sqlElement.addElement(content);
//		// 将 sql 元素放到根元素下
//		rootElement.addElement(new TextElement(""));
//		rootElement.addElement(sqlElement);
//		rootElement.addElement(new TextElement(""));
		return true;
	}

	/**
	 * 使用mybatis生成mapper接口,返回true的时候表示生成
	 */
	@Override
	public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
//		// 获取实体类
//		FullyQualifiedJavaType entityType = new FullyQualifiedJavaType(
//				introspectedTable.getBaseRecordType());
//		// import 实体类
//		interfaze.addImportedType(entityType);
//		// 添加 @Mapper 注解
//		interfaze.addAnnotation("@Mapper");
//		// import 接口
//		interfaze.addImportedType(
//				new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper"));
//		for (String mapper : baseMappers) {
//			interfaze.addImportedType(new FullyQualifiedJavaType(mapper));
//			interfaze.addSuperInterface(
//					new FullyQualifiedJavaType(mapper + "<" + entityType.getShortName() + ">"));
//		}
		return false;
	}
}