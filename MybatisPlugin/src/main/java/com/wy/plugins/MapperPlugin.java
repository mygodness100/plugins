package com.wy.plugins;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.DefaultJavaFormatter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.util.StringUtility;

/**
 * 生成dao层方法
 * 
 * @author paradiseWy
 */
public class MapperPlugin extends PluginAdapter {

	// 通用 Mapper 接口
	private Set<String> baseMappers = new HashSet<>();
	private String targetProject;
//	private String targetPackage;

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

		String targetProject = this.properties.getProperty("targetProject");
		if (StringUtility.stringHasValue(targetProject)) {
			this.targetProject = targetProject;
		} else {
			throw new RuntimeException("targetProject 属性不能为空！");
		}

//		String targetPackage = this.properties.getProperty("targetPackage");
//		if (StringUtility.stringHasValue(targetPackage)) {
//			this.targetPackage = targetPackage;
//		} else {
//			throw new RuntimeException("targetPackage 属性不能为空！");
//		}
	}

	/**
	 * 使用mybatis生成mapper接口,返回true的时候表示生成
	 */
	@Override
	public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
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
		new GeneratedJavaFile(interfaze, targetProject, new DefaultJavaFormatter());
		return true;
	}
}