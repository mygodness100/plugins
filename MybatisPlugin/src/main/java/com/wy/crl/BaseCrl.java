package com.wy.crl;

import com.wy.service.BaseService;

/**
 * @description
 * @author ParadiseWY
 * @date 2019年1月30日 下午3:41:15
 * @git {@link https://github.com/mygodness100}
 */
public abstract class BaseCrl<T> {

	public abstract BaseService<T> getService();

}