package com.g.utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CommonUtil {
	private static Log log = LogFactory.getLog(CommonUtil.class);
	/**
	 * 格式转化
	 */
	public static <T> List<?> transList(List<?> list) {
		return list;
	}

	/**
	 * map属性转成对象，不是set属性方法忽视
	 */
	public static Object mapToBean(Map<String, Object> map, @SuppressWarnings("rawtypes") Class clazz) {
		Object obj = null;
		try {
			if (map != null) {
				obj = clazz.newInstance();
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					String key = entry.getKey();
					Object value = entry.getValue();
					if (StringUtils.isNotEmpty(key) && value != null) {
						try {
							// 反射设置值
							String setKey = "set" + key.toUpperCase().charAt(0) + key.substring(1, key.length());
							Method m = null;
							try {
								m = obj.getClass().getMethod(setKey, value.getClass());
							} catch (Exception e) {
								// 如果是报错没有找到方法
								if (e.toString().contains("NoSuchMethodException")) {
									// 参数是Integer,说明参数是int
									if (e.getMessage().contains("java.lang.Integer")) {
										m = obj.getClass().getMethod(setKey, int.class);
									} else
									// 参数是Boolean,说明参数是boolean
									if (e.getMessage().contains("java.lang.Boolean")) {
										m = obj.getClass().getMethod(setKey, boolean.class);
									} else
									// 参数是Double,说明参数是double
									if (e.getMessage().contains("java.lang.Double")) {
										m = obj.getClass().getMethod(setKey, double.class);
									} else
									// 参数是Float,说明参数是float
									if (e.getMessage().contains("java.lang.Float")) {
										m = obj.getClass().getMethod(setKey, float.class);
									} else
									// 参数是Float,说明参数是byte
									if (e.getMessage().contains("java.lang.Byte")) {
										m = obj.getClass().getMethod(setKey, byte.class);
									} else
									// 参数是Float,说明参数是short
									if (e.getMessage().contains("java.lang.Short")) {
										m = obj.getClass().getMethod(setKey, short.class);
									} else
									// 参数是Float,说明参数是long
									if (e.getMessage().contains("java.lang.Long")) {
										m = obj.getClass().getMethod(setKey, long.class);
									} else
									// 参数是Float,说明参数是char
									if (e.getMessage().contains("java.lang.Char")) {
										m = obj.getClass().getMethod(setKey, char.class);
									} else
									// 参数是Float,说明参数是char
									if (e.getMessage().contains("java.sql.Timestamp")) {
										Timestamp d = (Timestamp) value;
										Date date = new Date(d.getTime());
										value = date;
										m = obj.getClass().getMethod(setKey, Date.class);
									} else {
										log.error(e.getMessage(),e);
									}
								} else {
									// 其他类型忽视
									log.error(e.getMessage(),e);
								}
							}
							m.invoke(obj, value);
						} catch (Exception e) {
							// 如果报错忽视，报错说明字段不需要
							// log.error(e.getMessage(),e);
						}

					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return obj;
	}

	/**
	 * map属性转成对象，不是set属性方法忽视
	 */
	public static <T> List<?> mapToBean(List<Map<String, Object>> list, @SuppressWarnings("rawtypes") Class clazz) {
		List<Object> result = new ArrayList<Object>();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = list.get(i);
				Object obj = mapToBean(map, clazz);
				result.add(obj);
			}
		}
		return result;
	}

	/**
	 * 对象转成map属性，不是get方法的属性忽视
	 */
	public static Map<String, Object> beanToMap(Object obj) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (obj != null) {

			Field[] fields = obj.getClass().getDeclaredFields();

			for (Field f : fields) {
				String key = f.getName();
				String getKey = "get" + key.toUpperCase().charAt(0) + key.substring(1, key.length());
				Object value = null;
				try {
					// 如果报错忽视，报错说明字段不需要
					Method m = obj.getClass().getMethod(getKey);
					value = m.invoke(obj);
					result.put(key, value);
				} catch (Exception e) {
					// log.error(e.getMessage(),e);
				}
			}
		}
		return result;
	}

	/**
	 * 对象转成map属性，不是get方法的属性忽视
	 */
	public static List<Map<String, Object>> beanToMap(List<?> list) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			if (list != null && list.size() > 0) {
				for (Object obj : list) {
					Map<String, Object> map = beanToMap(obj);
					result.add(map);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return result;
	}

	/**
	 * 在source的下标 start和end中间插入newStr
	 */
	public static String cutInsertString(String source, int start, int end, String newStr) {
		String startStr = source.substring(0, start);
		String endStr = source.substring(end, source.length());
		String result = startStr + newStr + endStr;
		return result;
	}

	/**
	 * 获取中文类型长度，中文长度2，其他1
	 */
	public static int getChineseLength(String source) {
		int valueLength = 0;
		String chinese = "[\u0391-\uFFE5]";
		/* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
		for (int i = 0; i < source.length(); i++) {
			/* 获取一个字符 */
			String temp = source.substring(i, i + 1);
			/* 判断是否为中文字符 */
			if (temp.matches(chinese)) {
				/* 中文字符长度为2 */
				valueLength += 2;
			} else {
				/* 其他字符长度为1 */
				valueLength += 1;
			}
		}
		return valueLength;
	}

	/**
	 * 获取翻页数
	 * 
	 * @param itemTotal
	 *            总条数
	 * @param PageCount
	 *            一页数
	 * @return 翻页数
	 */
	public static int getPageTotal(Integer itemTotal, int PageCount) {
		// 设置总页数
		int total = 0;
		if (itemTotal % PageCount == 0) {
			total = itemTotal / PageCount;
		} else {
			total = (itemTotal / PageCount) + 1;
		}
		return total;
	}
	
	/**
	 * 自己写sql分页必须处理pageIndex
	 */
	public static int getPageIndex(int pageIndex,int showCount) {
		int result = 0;
		if(pageIndex <= 1) {
			result = 0;
		}else if(pageIndex > 1) {
			result = ((pageIndex * showCount) - showCount);
		}
		return result;
	}

	/**
	 * url中文编码
	 */
	public static String urlEncode(String url) {
		String s = url;
		try {
			s = URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage(),e);
		}
		return s;
	}

	/**
	 * url中文解码
	 */
	public static String urlDecode(String url) {
		String s = url;
		try {
			s = URLDecoder.decode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage(),e);
		}
		return s;
	}

	
	/**
	 * 排序
	 * @param list
	 * @param field 字段名称，必须有get方法
	 * @param clazz 字段类型
	 */
	public static void sort(List<?> list,final String field,final String clazz) {
		Collections.sort(list, new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				Method m = null;
				try {
					String getKey = "get" + field.toUpperCase().charAt(0) + field.substring(1, field.length());
					m = o1.getClass().getMethod(getKey);
					Object o1Value = m.invoke(o1);
					
					m = o2.getClass().getMethod(getKey);
					Object o2Value = m.invoke(o2);
					
					if("Integer".equals(clazz.toString())) {
						Integer _o1 = (Integer)o1Value;
						Integer _o2 = (Integer)o2Value;
						return _o1.compareTo(_o2);
					}else if("String".equals(clazz.toString())) {
						String _o1 = (String)o1Value;
						String _o2 = (String)o2Value;
						return _o1.compareTo(_o2);
					}
				}catch (Exception e) {
					log.error(e.getMessage(),e);
				}
				return 0;
			}
		});
	}
	
}
