package com.wy;

import org.mybatis.generator.api.ShellRunner;

/**
 * 利用mybatis根据数据库生成实体类以及其他配套文件,数据库层使用mapper
 * 
 * @author paradiseWy 2018年8月29日
 */
public class Application {

	public static void main(String[] args) {
		args = new String[] { "-configfile", "src\\main\\resources\\generator\\generatorConfig.xml", "-overwrite" };
        ShellRunner.main(args);
	}
}