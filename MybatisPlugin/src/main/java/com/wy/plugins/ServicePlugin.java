package com.wy.plugins;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.DefaultJavaFormatter;
import org.mybatis.generator.api.dom.java.Field;
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
	private String targetPackageMapper;

	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}

	@Override
	public void setProperties(Properties properties) {
		super.setProperties(properties);

		this.baseService = this.properties.getProperty("baseService");
		this.targetPackageMapper = this.properties.getProperty("targetPackageMapper");
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
	public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable table) {
		return Arrays.asList(generateService(table));
	}

	private GeneratedJavaFile generateService(IntrospectedTable table) {
		FullyQualifiedJavaType entityType = new FullyQualifiedJavaType(table.getBaseRecordType());
		String domainObjectName = table.getFullyQualifiedTable().getDomainObjectName();
		String service = MessageFormat.format("{0}.{1}Service", targetPackage, domainObjectName);
		TopLevelClass clazz = new TopLevelClass(new FullyQualifiedJavaType(service));
		clazz.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Service"));
		clazz.addAnnotation("@Service");
		clazz.setVisibility(JavaVisibility.PUBLIC);
		if (StringUtility.stringHasValue(baseService)) {
			clazz.addImportedType(entityType);
			clazz.setSuperClass(new FullyQualifiedJavaType(
					baseService + "<" + entityType.getShortName() + ">"));
		}
		// 添加mapper
		if (StringUtility.stringHasValue(targetPackageMapper)) {
			Field field = new Field();
			FullyQualifiedJavaType mapper = new FullyQualifiedJavaType(
					MessageFormat.format("{0}.{1}Mapper", targetPackageMapper, domainObjectName));
			clazz.addImportedType(mapper);
			clazz.addImportedType(new FullyQualifiedJavaType(
					"org.springframework.beans.factory.annotation.Autowired"));
			field.addAnnotation("@Autowired");
			field.setVisibility(JavaVisibility.PRIVATE);
			field.setType(mapper);
			field.setName(Character.toLowerCase(domainObjectName.charAt(0))
					+ domainObjectName.substring(1) + "Mapper");
			clazz.addField(field);
		}
		return new GeneratedJavaFile(clazz, targetProject, new DefaultJavaFormatter());
	}
}