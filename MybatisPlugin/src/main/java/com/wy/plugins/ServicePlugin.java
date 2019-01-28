package com.wy.plugins;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.DefaultJavaFormatter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.util.StringUtility;

/**
 * 自定义生成service和impl,此处不生成impl
 * 
 * @author paradiseWy 2018年8月30日
 */
public class ServicePlugin extends PluginAdapter {
	private String baseService;
	private String targetProject;
	private String targetPackage;

	@Override
	public void setProperties(Properties properties) {
		super.setProperties(properties);

		String baseService = this.properties.getProperty("baseService");
		if (StringUtility.stringHasValue(baseService)) {
			this.baseService = baseService;
		}

		String targetProject = this.properties.getProperty("targetProject");
		if (StringUtility.stringHasValue(targetProject)) {
			this.targetProject = targetProject;
		} else {
			throw new RuntimeException("targetProject 属性不能为空！");
		}

		String targetPackage = this.properties.getProperty("targetPackage");
		if (StringUtility.stringHasValue(targetPackage)) {
			this.targetPackage = targetPackage;
		} else {
			throw new RuntimeException("targetPackage 属性不能为空！");
		}
	}

	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}

	@Override
	public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable table) {
		return Arrays.asList(generateService(table));
	}

	private GeneratedJavaFile generateService(IntrospectedTable table) {
		// FullyQualifiedJavaType entityType = new
		// FullyQualifiedJavaType(table.getBaseRecordType());
		// FullyQualifiedJavaType primaryType =
		// table.getPrimaryKeyColumns().get(0).getFullyQualifiedJavaType();
		// String domainObjectName =
		// table.getFullyQualifiedTable().getDomainObjectName();
		// String service = targetPackage + "." + domainObjectName +
		// "Service";
		// String serviceImpl = targetPackage + ".impl." +
		// domainObjectName + "ServiceImpl";
		// TopLevelClass clazz = new TopLevelClass(new
		// FullyQualifiedJavaType(serviceImpl));
		// clazz.addImportedType(entityType);
		// clazz.addImportedType(new
		// FullyQualifiedJavaType(service));
		// clazz.addImportedType(new
		// FullyQualifiedJavaType("org.springframework.stereotype.Service"));
		// clazz.addAnnotation("@Service(\"" +
		// firstLetterLowerCase(domainObjectName + "Service") +
		// "\")");
		// clazz.setVisibility(JavaVisibility.PUBLIC);
		// clazz.setSuperClass(new FullyQualifiedJavaType(
		// baseServiceImpl + "<" + entityType.getShortName() + "," +
		// primaryType.getShortName() + ">"));
		// clazz.addSuperInterface(new
		// FullyQualifiedJavaType(service));
		// return new GeneratedJavaFile(clazz, targetProject, new
		// DefaultJavaFormatter());

		// 获取完整的实体类型
		FullyQualifiedJavaType entityType = new FullyQualifiedJavaType(table.getBaseRecordType());
		// 获得实体类的简称(简单的类名)
		String domainObjectName = table.getFullyQualifiedTable().getDomainObjectName();
		// 生成 Service 名称
		String service = MessageFormat.format("{0}.{1}Service", targetPackage, domainObjectName);
		// 构造 Service 文件
		TopLevelClass clazz = new TopLevelClass(new FullyQualifiedJavaType(service));
		// import
		clazz.addImportedType(entityType);
		clazz.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Service"));
		clazz.addAnnotation("@Service");
		clazz.setVisibility(JavaVisibility.PUBLIC);
		if (StringUtility.stringHasValue(baseService)) {
			clazz.setSuperClass(new FullyQualifiedJavaType(
					baseService + "<" + entityType.getShortName() + ">"));
		}
		return new GeneratedJavaFile(clazz, targetProject, new DefaultJavaFormatter());
	}
}