package com.wy.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * 实体类以及分页总类
 * @description 分页参数会在分页请求里使用,其他方法会在{@link com.wy.crl.BaseCrl}里使用,
 *              必须使用fastjson来序列化json,否则输出的结果中会带上pageIndex,pageSize
 * @author paradiseWy
 * @date 2019年2月15日 上午10:15:06
 */
@Getter
@Setter
public class BaseModel<T> extends Page implements Serializable {

	private static final long serialVersionUID = 1L;
}