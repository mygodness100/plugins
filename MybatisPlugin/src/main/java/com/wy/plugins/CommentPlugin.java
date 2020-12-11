package com.wy.plugins;

import java.util.Properties;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.DefaultCommentGenerator;
import org.mybatis.generator.internal.util.StringUtility;

/**
 * 注释类生成器
 * 
 * @author ParadiseWY
 * @date 2018-08-31 09:57:44
 * @git {@link https://github.com/mygodness100}
 */
public class CommentPlugin extends DefaultCommentGenerator {

	/** 是否使用useSwagger生成文档 */
	private boolean supportSwagger2;

	/** 实体类是否需要继承基础类 */
	private String baseEntity;

	public CommentPlugin() {

	}

	@Override
	public void addConfigurationProperties(Properties properties) {
		this.supportSwagger2 = StringUtility.isTrue(properties.getProperty("supportSwagger2", ""));
		this.baseEntity = properties.getProperty("baseEntity");
		super.addConfigurationProperties(properties);
	}

	/**
	 * 给实体类文件中的字段添加数据库备注,不支持sqlserver;若是指定了swagger生成文档,那么将不生成注释
	 */
	@Override
	public void addFieldComment(Field field, IntrospectedTable introspectedTable,
			IntrospectedColumn introspectedColumn) {
		if (StringUtility.stringHasValue(introspectedColumn.getRemarks())) {
			if (this.supportSwagger2) {
				field.addAnnotation("@ApiModelProperty(\"" + introspectedColumn.getRemarks() + "\")");
			} else {
				String[] remarks = introspectedColumn.getRemarks().split("\r\n");
				for (String s : remarks) {
					field.addJavaDocLine("// " + s);
				}
			}
		}
	}

	/**
	 * 给实体类加上注解,并且引入相关包,该方法中添加类注解无效
	 */
	@Override
	public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		if (this.supportSwagger2) {
			// 可能会出现没有任何注释的表,此时会都引入,需手动删除
			topLevelClass.addImportedType(new FullyQualifiedJavaType("io.swagger.annotations.ApiModelProperty"));
			topLevelClass.addImportedType(new FullyQualifiedJavaType("io.swagger.annotations.ApiModel"));
			if (StringUtility.stringHasValue(introspectedTable.getRemarks())) {
				topLevelClass.addAnnotation("@ApiModel(description = \"" + introspectedTable.getRemarks()
						+ introspectedTable.getTableConfiguration().getTableName() + "\")");
			} else {
				topLevelClass.addAnnotation("@ApiModel(description = \""
						+ introspectedTable.getTableConfiguration().getTableName() + "\")");
			}
		}
		// 不使用generator自带的serializablePlugin插件是因为生成的字段都在类的最后,此处会生成为第一个字段
		makeSerializable(topLevelClass, introspectedTable);
	}

	private void makeSerializable(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		FullyQualifiedJavaType serializable = new FullyQualifiedJavaType("java.io.Serializable");
		// 若继承了基础类,让基础类实现seriliazable接口
		if (!StringUtility.stringHasValue(baseEntity)) {
			topLevelClass.addImportedType(serializable);
			topLevelClass.addSuperInterface(serializable);
		}
		Field field = new Field("serialVersionUID", new FullyQualifiedJavaType("long"));
		field.setFinal(true);
		field.setStatic(true);
		field.setInitializationString("1L");
		field.setVisibility(JavaVisibility.PRIVATE);
		topLevelClass.addField(field);
	}
}