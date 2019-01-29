package com.wy.plugins;

import java.util.Properties;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.internal.DefaultCommentGenerator;
import org.mybatis.generator.internal.util.StringUtility;

/**
 * 注释生成类
 * @author paradiseWy 2018年8月31日
 */
public class CommentPlugin extends DefaultCommentGenerator {

	public CommentPlugin() {

	}

	@Override
	public void addConfigurationProperties(Properties properties) {
		properties.setProperty("suppressDate", "true");
		properties.setProperty("suppressAllComments", "true");
		properties.setProperty("addRemarkComments", "true");
		super.addConfigurationProperties(properties);
	}

	/**
	 * 给实体类文件中的字段添加数据库备注,不支持sqlserver
	 */
	@Override
	public void addFieldComment(Field field, IntrospectedTable introspectedTable,
			IntrospectedColumn introspectedColumn) {
		if (StringUtility.stringHasValue(introspectedColumn.getRemarks())) {
			String remark = introspectedColumn.getRemarks();
			String[] remarks = remark.split("\r\n");
			for (String s : remarks) {
				field.addJavaDocLine("//" + s);
			}
		}
	}
}