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
 * 
 * @author paradiseWy 2018年8月30日
 */
public class EntityPlugin extends PluginAdapter {

	// 开始的分隔符,例如 mysql 为 `,sql server 为 [
	private String beginningDelimiter = "";
	// 结束的分隔符,例如 mysql 为 `,sql server 为 ]
	private String endingDelimiter = "";
	// caseSensitive 默认 false,当数据库表名区分大小写时,可以将该属性设置为 true
	private boolean caseSensitive = false;
	// 强制生成注解,默认 false,设置为 true 后一定会生成 @Table 和 @Column 注解
	private boolean forceAnnotation = true;
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
	private FullyQualifiedJavaType gwtSerializable;
	private boolean addGWTInterface;
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
	}

	@Override
	public void setProperties(Properties properties) {
		super.setProperties(properties);

		serializable = new FullyQualifiedJavaType("java.io.Serializable");
		gwtSerializable = new FullyQualifiedJavaType(
				"com.google.gwt.user.client.rpc.IsSerializable");
		this.forceAnnotation = StringUtility.isTrue(this.properties.getProperty("forceAnnotation"));
		this.caseSensitive = StringUtility.isTrue(this.properties.getProperty("caseSensitive"));
		addGWTInterface = Boolean.valueOf(this.properties.getProperty("addGWTInterface"));
		suppressJavaInterface = Boolean
				.valueOf(this.properties.getProperty("suppressJavaInterface"));

		String beginningDelimiter = this.properties.getProperty("beginningDelimiter");
		if (StringUtility.stringHasValue(beginningDelimiter)) {
			this.beginningDelimiter = beginningDelimiter;
		}
		configuration.addProperty("beginningDelimiter", this.beginningDelimiter);

		String endingDelimiter = this.properties.getProperty("endingDelimiter");
		if (StringUtility.stringHasValue(endingDelimiter)) {
			this.endingDelimiter = endingDelimiter;
		}
		configuration.addProperty("endingDelimiter", this.endingDelimiter);

		String schema = this.properties.getProperty("schema");
		if (StringUtility.stringHasValue(schema)) {
			this.schema = schema;
		}

		String persistence = this.properties.getProperty("usePersistence");
		if (StringUtility.isTrue(persistence)) {
			this.usePersistence = Boolean.valueOf(persistence);
		}

		String lombok = this.properties.getProperty("lombok");
		if (StringUtility.stringHasValue(lombok)) {
			this.lombok = LombokType.valueOf(lombok);
		}

		String baseEntity = this.properties.getProperty("baseEntity");
		if (StringUtility.stringHasValue(baseEntity)) {
			this.baseEntity = baseEntity;
		}
	}

	public String getDelimiterName(String name) {
		StringBuilder nameBuilder = new StringBuilder();
		if (StringUtility.stringHasValue(schema)) {
			nameBuilder.append(schema).append(".");
		}
		nameBuilder.append(beginningDelimiter);
		nameBuilder.append(name);
		nameBuilder.append(endingDelimiter);
		return nameBuilder.toString();
	}

	/**
	 * 生成带 BLOB 字段的对象
	 */
	@Override
	public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
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
		if (field.isTransient()) {
			field.addAnnotation("@Transient");
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
			} else {
				field.addAnnotation("@Column");
			}
		} else if (StringUtility.stringHasValue(beginningDelimiter)
				|| StringUtility.stringHasValue(endingDelimiter)) {
			if (!introspectedColumn.isNullable()) {
				field.addAnnotation("@Column(nullable=false)");
			} else {
				field.addAnnotation("@Column");
			}
		} else if (forceAnnotation) {
			if (!introspectedColumn.isNullable()) {
				field.addAnnotation("@Column(nullable=false)");
			} else {
				field.addAnnotation("@Column");
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
		return true;
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
		// 如果包含空格,或者需要分隔符,需要完善
		if (StringUtility.stringContainsSpace(tableName)) {
			tableName = context.getBeginningDelimiter() + tableName + context.getEndingDelimiter();
		}

		// 如果有baseentity,继承baseentity
		if (StringUtility.stringHasValue(baseEntity)) {
			topLevelClass.setSuperClass(baseEntity + "<"
					+ introspectedTable.getFullyQualifiedTable().getDomainObjectName() + ">");
		}
		makeSerializable(topLevelClass, introspectedTable);

		if (!usePersistence) {
			return true;
		}
		// 添加@Table注解
		topLevelClass.addImportedType("javax.persistence.Table");

		// 是否忽略大小写,对于区分大小写的数据库,会有用
		if (caseSensitive && !topLevelClass.getType().getShortName().equals(tableName)) {
			topLevelClass.addAnnotation("@Table(name=\"" + getDelimiterName(tableName) + "\")");
		} else if (!topLevelClass.getType().getShortName().equalsIgnoreCase(tableName)) {
			topLevelClass.addAnnotation("@Table(name=\"" + getDelimiterName(tableName) + "\")");
		} else if (StringUtility.stringHasValue(schema)
				|| StringUtility.stringHasValue(beginningDelimiter)
				|| StringUtility.stringHasValue(endingDelimiter)) {
			topLevelClass.addAnnotation("@Table(name=\"" + getDelimiterName(tableName) + "\")");
		} else if (forceAnnotation) {
			topLevelClass.addAnnotation("@Table(name=\"" + getDelimiterName(tableName) + "\")");
		}
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
		if (addGWTInterface) {
			topLevelClass.addImportedType(gwtSerializable);
			topLevelClass.addSuperInterface(gwtSerializable);
		}

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