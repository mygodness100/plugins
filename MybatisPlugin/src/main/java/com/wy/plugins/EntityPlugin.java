package com.wy.plugins;

import java.text.MessageFormat;
import java.util.List;
import java.util.Properties;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.internal.util.StringUtility;

import com.wy.common.Constant;

/**
 * 自定义根据数据库表生成实体类 FIXME
 * @author paradiseWy 2018年8月30日
 */
public class EntityPlugin extends PluginAdapter {

	// caseSensitive 默认 false,当数据库表名区分大小写时,可以将该属性设置为 true
	private boolean caseSensitive = false;
	// 数据库模式
	private String schema;
	// 是否使用javax.persistence持久化注解
	private boolean usePersistence;
	// Lombok 插件模式
	private LombokType lombok = LombokType.none;
	// 注释生成器
	private CommentGeneratorConfiguration configuration;
	// 基本实体类
	private String baseEntity;

	private FullyQualifiedJavaType serializable;
	private boolean suppressJavaInterface;

	enum LombokType {
		none, simple, builder, accessors
	}

	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}

	@Override
	public void setContext(Context context) {
		super.setContext(context);
		// 设置默认的注释生成器
		configuration = new CommentGeneratorConfiguration();
		// 设置自定义的注释生成器
		configuration.setConfigurationType(CommentPlugin.class.getCanonicalName());
		context.setCommentGeneratorConfiguration(configuration);
		// 支持 oracle 获取注释 #114
		context.getJdbcConnectionConfiguration().addProperty("remarksReporting", "true");

		// 获得所有表的集合,可结合数据库,不必在xml配置中写出所有表,在另外的文件中批量设置表
		// FIXME
		// this.context.getTableConfigurations();
	}

	@Override
	public void setProperties(Properties properties) {
		super.setProperties(properties);
		serializable = new FullyQualifiedJavaType("java.io.Serializable");
		this.caseSensitive = StringUtility.isTrue(this.properties.getProperty("caseSensitive"));
		suppressJavaInterface = Boolean
				.valueOf(this.properties.getProperty("suppressJavaInterface"));

		this.schema = this.properties.getProperty("schema", "");
		this.usePersistence = Boolean
				.valueOf(StringUtility.isTrue(this.properties.getProperty("usePersistence")));
		this.lombok = LombokType.valueOf(this.properties.getProperty("lombok", "none"));
		this.baseEntity = this.properties.getProperty("baseEntity");
	}

	private String getDelimiterName(String name) {
		StringBuilder nameBuilder = new StringBuilder();
		if (StringUtility.stringHasValue(schema)) {
			nameBuilder.append(schema).append(".");
		}
		return nameBuilder.append(context.getBeginningDelimiter()).append(name)
				.append(context.getEndingDelimiter()).toString();
	}

	/**
	 * 生成基础实体类
	 */
	@Override
	public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		// 处理lombok注解
		handlerLombok(topLevelClass);
		// 获得表名
		String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
		// 如果包含空格,或者需要分隔符
		if (StringUtility.stringContainsSpace(tableName)) {
			tableName = context.getBeginningDelimiter() + tableName + context.getEndingDelimiter();
		}

		// 如果有baseentity,继承baseentity
		if (StringUtility.stringHasValue(baseEntity)) {
			topLevelClass.setSuperClass(baseEntity + "<"
					+ introspectedTable.getFullyQualifiedTable().getDomainObjectName() + ">");
		}

		if (!usePersistence) {
			return true;
		}
		// 添加@Table注解
		topLevelClass.addImportedType("javax.persistence.Table");
		// 是否忽略大小写,对于区分大小写的数据库,会有用
		if (caseSensitive && !topLevelClass.getType().getShortName().equals(tableName)) {
			topLevelClass.addAnnotation(new StringBuilder("@Table(name=\"")
					.append(getDelimiterName(tableName)).append("\"").toString());
		} else if (!topLevelClass.getType().getShortName().equalsIgnoreCase(tableName)) {
			topLevelClass.addAnnotation(new StringBuilder("@Table(name=\"")
					.append(getDelimiterName(tableName)).append("\"").toString());
		} else if (StringUtility.stringHasValue(schema)
				|| StringUtility.stringHasValue(context.getBeginningDelimiter())
				|| StringUtility.stringHasValue(context.getEndingDelimiter())) {
			topLevelClass.addAnnotation(new StringBuilder("@Table(name=\"")
					.append(getDelimiterName(tableName)).append("\"").toString());
		} else {
			topLevelClass.addAnnotation(new StringBuilder("@Table(name=\"")
					.append(getDelimiterName(tableName)).append("\"").toString());
		}
		return true;
	}

	/**
	 * 处理实体类的字段
	 */
	@Override
	public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass,
			IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable,
			ModelClassType modelClassType) {
		// 若不使用持久化组件,直接返回true
		if (!usePersistence) {
			return true;
		}
		// 判断表中的主键
		for (IntrospectedColumn column : introspectedTable.getPrimaryKeyColumns()) {
			if (introspectedColumn == column) {
				if (introspectedColumn.isStringColumn()) {
					// 字符串类型
				} else {
					// 数字类型
					topLevelClass.addImportedType("javax.persistence.Id");
					field.addAnnotation("@Id");
				}
				break;
			}
		}
		String column = introspectedColumn.getActualColumnName();
		if (StringUtility.stringContainsSpace(column)
				|| introspectedTable.getTableConfiguration().isAllColumnDelimitingEnabled()) {
			column = introspectedColumn.getContext().getBeginningDelimiter() + column
					+ introspectedColumn.getContext().getEndingDelimiter();
		}
		// 添加@Column注解
		topLevelClass.addImportedType("javax.persistence.Column");
		if (!column.equals(introspectedColumn.getJavaProperty())) {
			if (!introspectedColumn.isNullable()) {
				field.addAnnotation("@Column(nullable=false)");
			}
		} else if (StringUtility.stringHasValue(context.getBeginningDelimiter())
				|| StringUtility.stringHasValue(context.getEndingDelimiter())) {
			if (!introspectedColumn.isNullable()) {
				field.addAnnotation("@Column(nullable=false)");
			}
		} else {
			if (!introspectedColumn.isNullable()) {
				field.addAnnotation("@Column(nullable=false)");
			}
		}
		// 若从数据库发现主键自增
		if (introspectedColumn.isIdentity()) {
			if (introspectedTable.getTableConfiguration().getGeneratedKey().getRuntimeSqlStatement()
					.equals("JDBC")) {
				topLevelClass.addImportedType("javax.persistence.GeneratedValue");
				field.addAnnotation("@GeneratedValue(generator =\"JDBC\")");
			} else {
				topLevelClass.addImportedType("javax.persistence.GeneratedValue");
				topLevelClass.addImportedType("javax.persistence.GenerationType");
				field.addAnnotation("@GeneratedValue(strategy = GenerationType.IDENTITY)");
			}
		} else if (introspectedColumn.isSequenceColumn()) {
			// 在 Oracle 中,如果需要是 SEQ_TABLENAME,那么可以配置为 select SEQ_{1}
			String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
			String sql = MessageFormat.format(introspectedTable.getTableConfiguration()
					.getGeneratedKey().getRuntimeSqlStatement(), tableName,
					tableName.toUpperCase());
			topLevelClass.addImportedType("javax.persistence.GeneratedValue");
			topLevelClass.addImportedType("javax.persistence.GenerationType");
			field.addAnnotation("@GeneratedValue(strategy = GenerationType.IDENTITY, generator = \""
					+ sql + "\")");
		}
		makeSerializable(topLevelClass, introspectedTable);
		return true;
	}

	/**
	 * 处理lombok注解
	 * @param topLevelClass 需要生成的实体类
	 */
	private void handlerLombok(TopLevelClass topLevelClass) {
		switch (lombok) {
		case none:
			break;
		case simple:
			topLevelClass.addImportedType("lombok.Getter");
			topLevelClass.addImportedType("lombok.Setter");
			topLevelClass.addImportedType("lombok.NoArgsConstructor");
			topLevelClass.addAnnotation("@Getter");
			topLevelClass.addAnnotation("@Setter");
			topLevelClass.addAnnotation("@NoArgsConstructor");
			break;
		case accessors:
			topLevelClass.addImportedType("lombok.Getter");
			topLevelClass.addImportedType("lombok.Setter");
			topLevelClass.addImportedType("lombok.NoArgsConstructor");
			topLevelClass.addImportedType("lombok.experimental.Accessors");
			topLevelClass.addAnnotation("@Getter");
			topLevelClass.addAnnotation("@Setter");
			topLevelClass.addAnnotation("@NoArgsConstructor");
			topLevelClass.addAnnotation("@Accessors(fluent = true)");
			break;
		case builder:
			topLevelClass.addImportedType("lombok.Getter");
			topLevelClass.addImportedType("lombok.Setter");
			topLevelClass.addImportedType("lombok.NoArgsConstructor");
			topLevelClass.addImportedType("lombok.AllArgsConstructor");
			topLevelClass.addImportedType("lombok.Builder");
			topLevelClass.addAnnotation("@Getter");
			topLevelClass.addAnnotation("@Setter");
			topLevelClass.addAnnotation("@NoArgsConstructor");
			topLevelClass.addAnnotation("@AllArgsConstructor");
			topLevelClass.addAnnotation("@Builder");
			break;
		default:
			break;
		}
	}

	private void makeSerializable(TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {

		if (!suppressJavaInterface) {
			topLevelClass.addImportedType(serializable);
			topLevelClass.addSuperInterface(serializable);

			Field field = new Field();
			field.setFinal(true);
			field.setInitializationString("1L");
			field.setName(Constant.SERIAL_VERSION_UID);
			field.setStatic(true);
			field.setType(new FullyQualifiedJavaType("long"));
			field.setVisibility(JavaVisibility.PRIVATE);
			context.getCommentGenerator().addFieldComment(field, introspectedTable);
			topLevelClass.addField(field);
		}
	}

	// 不生成getter和setter
	@Override
	public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass,
			IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable,
			ModelClassType modelClassType) {
		return false;
	}

	@Override
	public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass,
			IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable,
			ModelClassType modelClassType) {
		return false;
	}
}