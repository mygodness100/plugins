package com.wy.plugins;

import java.util.List;

import org.mybatis.generator.api.PluginAdapter;

/**
 * 在xml文件中生成公用的增删改查等方法,false表示不生成,此处他通过tk.mybatis自动使用
 * 
 * @author paradiseWy
 */
public class CommonMethodPlugin extends PluginAdapter {

	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}
}