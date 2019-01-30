package com.wy.plugins;

import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.DefaultJavaFormatter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.util.StringUtility;

/**
 * controller生成器
 * 
 * @author paradiseWy
 */
public class CrlPlugin extends PluginAdapter {

	private String baseCrl;
	private String targetProject;
	private String targetPackage;
	private String targetPackageService;

	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}

	/**
	 * 取得配置文件中的值
	 */
	@Override
	public void setProperties(Properties properties) {
		super.setProperties(properties);

		this.baseCrl = this.properties.getProperty("baseCrl");
		this.targetPackageService = this.properties.getProperty("targetPackageService");

		String targetProject = this.properties.getProperty("targetProject");
		if (StringUtility.stringHasValue(targetProject)) {
			this.targetProject = targetProject;
		} else {
			throw new RuntimeException("controller 缺少必要的工程路径");
		}

		String targetPackage = this.properties.getProperty("targetPackage");
		if (StringUtility.stringHasValue(targetPackage)) {
			this.targetPackage = targetPackage;
		} else {
			throw new RuntimeException("controller 缺少必要的包路径");
		}
	}

	/**
	 * 生成额外的class类,而不是生成service接口
	 */
	@Override
	public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(
			IntrospectedTable introspectedTable) {
		List<GeneratedJavaFile> result = new ArrayList<GeneratedJavaFile>();
		FullyQualifiedJavaType entityType = new FullyQualifiedJavaType(
				introspectedTable.getBaseRecordType());
		TopLevelClass clazz = new TopLevelClass(new FullyQualifiedJavaType(
				MessageFormat.format("{0}.{1}Crl", targetPackage, entityType.getShortName())));
		clazz.setVisibility(JavaVisibility.PUBLIC);
		clazz.addImportedType(new FullyQualifiedJavaType(
				"org.springframework.beans.factory.annotation.Autowired"));
		clazz.addImportedType(new FullyQualifiedJavaType(
				"org.springframework.web.bind.annotation.RequestMapping"));
		clazz.addImportedType(new FullyQualifiedJavaType(
				"org.springframework.web.bind.annotation.RestController"));
		clazz.addAnnotation("@RestController");
		clazz.addAnnotation(MessageFormat.format("{0}(\"{1}\")", "@RequestMapping",
				(Character.toLowerCase(entityType.getShortName().charAt(0))
						+ entityType.getShortName().substring(1))));
		// 添加service
		FullyQualifiedJavaType serviceType = null;
		if (StringUtility.stringHasValue(targetPackageService)) {
			serviceType = new FullyQualifiedJavaType(MessageFormat.format(
					"{0}.{1}{2}", targetPackageService, entityType.getShortName(), "Service"));
			clazz.addImportedType(serviceType);
			Field field = new Field((Character.toLowerCase(serviceType.getShortName().charAt(0))
					+ serviceType.getShortName().substring(1)), serviceType);
			field.addAnnotation("@Autowired");
			field.setVisibility(JavaVisibility.PRIVATE);
			clazz.addField(field);
		}
		// 添加父类
		if (StringUtility.stringHasValue(baseCrl)) {
			clazz.addImportedType(entityType);
			FullyQualifiedJavaType baseCrlClass = new FullyQualifiedJavaType(baseCrl);
			baseCrlClass.addTypeArgument(entityType);
			clazz.setSuperClass(baseCrlClass);
			// mybatis的method方法无效,需要使用java自带的反射
			// 自定义重写抽象方法,若没有则跳过,可改
			java.lang.reflect.Method[] ms;
			try {
				ms = Class.forName(baseCrl).getMethods();
				for (java.lang.reflect.Method m : ms) {
					Method method = new Method();
					if (Modifier.isAbstract(m.getModifiers())) {
						method.addAnnotation("@Override");
						method.setVisibility(JavaVisibility.PUBLIC);
						method.setReturnType(serviceType);
						method.setName(m.getName());
						method.addBodyLine("return "
								+ (Character.toLowerCase(serviceType.getShortName().charAt(0))
										+ serviceType.getShortName().substring(1))
								+ ";");
						clazz.addMethod(method);
					}
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		GeneratedJavaFile crlFile = new GeneratedJavaFile(clazz, targetProject,
				new DefaultJavaFormatter());
		result.add(crlFile);
		return result;
	}
}