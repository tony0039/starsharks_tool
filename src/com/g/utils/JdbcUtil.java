package com.g.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.mezingr.hibernate.HibernateTemplateFactory;

/**
 * 数据库操作工具类
 */
public class JdbcUtil {
	private static Log log = LogFactory.getLog(JdbcUtil.class);
	
	/**
	 * 获取数据库表name
	 */
	private static List<String> getTableColName(JdbcTemplate jdbcTemplate, String table) {
		String sql = "select * from " + table;
		List<String> cols = new ArrayList<String>();
		SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
		SqlRowSetMetaData sqlRsmd = sqlRowSet.getMetaData();
		int columnCount = sqlRsmd.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			cols.add(sqlRsmd.getColumnName(i));
		}
		return cols;
	}
	
	/**
	 * 获取插入语句
	 */
	private static String getInsertSql(List<String> cols,String table) {
		String result = "";
		StringBuffer sb = new StringBuffer();
		// 设置执行语句
		sb.append("insert into " + table + "( ");
		for (String s : cols) {
			sb.append(s).append(",");
		}
		if (sb.toString().endsWith(",")) {
			sb = sb.delete(sb.length() - 1, sb.length());
		}
		sb.append(" ) values( ");
		for (@SuppressWarnings("unused") String s : cols) {
			sb.append("?,");
		}
		if (sb.toString().endsWith(",")) {
			sb = sb.delete(sb.length() - 1, sb.length());
		}
		sb.append(" )");
		result = sb.toString();
		return result;
	}
	
	/**
	 * 获取修改语句
	 * @param where 如果为null,默认使用id
	 */
	private static String getUpdateSql(List<String> cols,String table,String... where) {
		String result = "";
		StringBuffer sb = new StringBuffer();
		// 设置执行语句
		sb.append(" update " + table + " set ");
		for (String s : cols) {
			sb.append(s).append(" = ? ").append(",");
		}
		if (sb.toString().endsWith(",")) {
			sb = sb.delete(sb.length() - 1, sb.length());
		}
		sb.append(" where ");
		if(where.length > 0) {
			for(String key : where) {
				sb.append(key).append(" = ? ");
				sb.append(" and ");
			}
			if (sb.toString().endsWith(" and ")) {
				sb = sb.delete(sb.length() - " and ".length(), sb.length());
			}
		}else {
			sb.append("id").append(" = ? ");
		}
		
		result = sb.toString();
		return result;
	}
	
	/**
	 * 获取count语句
	 * @param where 如果为null,不增加where语句
	 */
	private static String getCountSql(List<String> cols,String table,String... where) {
		String result = "";
		StringBuffer sb = new StringBuffer("select count(*) from "+table);
		//如果where是null,默认使用id筛选
		if(where.length > 0) {
			sb.append(" where ");
			for(String key : where) {
				sb.append(key).append(" = ? ");
				sb.append(" and ");
			}
			if (sb.toString().endsWith(" and ")) {
				sb = sb.delete(sb.length() - " and ".length(), sb.length());
			}
		}
		result = sb.toString();
		return result;
	}
	
	/**
	 * 批量执行修改数据
	 */
	private static boolean jdbcTemplateBatchUpdate(JdbcTemplate jdbcTemplate,final List<String> cols,String sql, final List<?> list,final String... where) {
		if(jdbcTemplate != null && cols !=null && cols.size() > 0 && StringUtils.isNotEmpty(sql) && list != null && list.size() > 0) {
			jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					Object obj = list.get(i);
					Map<String, Object> props = CommonUtil.beanToMap(obj);
					try {
						int j = 0;
						for (j = 0; j < cols.size(); j++) {
							String col = cols.get(j);
							Object value = props.get(col);
							ps.setObject(j + 1, value);
						}
						//设置where参数
						if(where.length > 0) {
							for(String key : where) {
								ps.setObject(j+1, props.get(key));
								j++;
							}
						}
					} catch (Exception e) {
						log.error(e.getMessage(),e);
					}
				}

				public int getBatchSize() {
					return list == null ? 0 : list.size();
				}
			});
			return true;
		}
		return false;
	}

	/**
	 * 批量执行新增数据
	 */
	public static boolean batchInsert(JdbcTemplate jdbcTemplate, String table, final List<?> list) {
		if (list == null || list.size() == 0) {
			return false;
		}
		final List<String> cols = getTableColName(jdbcTemplate, table);
		String insertSql = getInsertSql(cols,table);
		return jdbcTemplateBatchUpdate(jdbcTemplate,cols,insertSql,list);
	}
	
	
	
	/**
	 * 批量执行新增数据
	 * @param where 如果为null,默认使用id
	 */
	public static boolean batchUpdate(JdbcTemplate jdbcTemplate, String table, final List<?> list,String... where) {
		if (list == null || list.size() == 0) {
			return false;
		}
		final List<String> cols = getTableColName(jdbcTemplate, table);
		String updateSql = getUpdateSql(cols,table,where); 
		
		return jdbcTemplateBatchUpdate(jdbcTemplate,cols,updateSql,list,where);
	}
	
	/**
	 * 批量执行新增数据
	 */
	public static boolean batchInsertOrUpdate(JdbcTemplate jdbcTemplate, String table, List<?> list,String... where) {
		if (list == null || list.size() == 0) {
			return false;
		}
		final List<String> cols = getTableColName(jdbcTemplate, table);
		
		String insertSql = getInsertSql(cols,table);
		String updateSql = getUpdateSql(cols,table,where);
		String countSql = getCountSql(cols,table,where);
		List<Object> insertList = new ArrayList<Object>();
		List<Object> updateList = new ArrayList<Object>();
		
		
		for(Object obj : list) {
			Map<String, Object> props = CommonUtil.beanToMap(obj);
			Object[] params = new Object[where.length];
			if(where.length > 0) {
				for(int i = 0;i<where.length;i++) {
					params[i] = props.get(where[i]);
				}
			}
			int count = jdbcTemplate.queryForInt(countSql,params);
			if(count == 0) {
				//新增
				insertList.add(obj);
			}else if(count > 0){
				//修改
				updateList.add(obj);
			}
		}
		
		jdbcTemplateBatchUpdate(jdbcTemplate,cols,insertSql,insertList);
		jdbcTemplateBatchUpdate(jdbcTemplate,cols,updateSql,updateList,where);
		
		return true;
	}
	
	/**
	 * 返回传入class对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getUnique(final String sql,final Object[] params,@SuppressWarnings("rawtypes") final Class clazz,HibernateTemplateFactory htf) {
		Object result = null;
		result = htf.getHibernateTemplate().execute(new HibernateCallback<Object>() {
					public Object doInHibernate(Session session) throws HibernateException, SQLException {
						Object temp = null;
						try {
							Query query = session.createSQLQuery(sql).addEntity( clazz );
							if(params !=null && params.length > 0) {
								for(int i = 0;i<params.length;i++) {
									query.setParameter(i, params[i]);
								}
							}
							temp = query.uniqueResult();
						} catch (Exception e) {
							log.error(e.getMessage(),e);
						}finally {
							session.close();
						}
						return temp;
					}
		});
		return (T) result;
	}
	
	/**
	 * 返回查询结果对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getUnique(final String sql,final Object[] params,HibernateTemplateFactory htf) {
		Object result = null;
		result = htf.getHibernateTemplate().execute(new HibernateCallback<Object>() {
					public Object doInHibernate(Session session) throws HibernateException, SQLException {
						Object temp = null;
						try {
							Query query = session.createSQLQuery(sql);
							if(params !=null && params.length > 0) {
								for(int i = 0;i<params.length;i++) {
									query.setParameter(i, params[i]);
								}
							}
							temp = query.uniqueResult();
						} catch (Exception e) {
							log.error(e.getMessage(),e);
						}finally {
							session.close();
						}
						return temp;
					}
		});
		return (T) result;
	}
	
	/**
	 * 返回查询结果对象集合
	 */
	@SuppressWarnings("unchecked")
	public static <T>  List<T> getList(final String sql,final Object[] params,HibernateTemplateFactory htf) {
		List<Object> result = null;
		result = htf.getHibernateTemplate().execute(new HibernateCallback<List<Object>>() {
					public List<Object> doInHibernate(Session session) throws HibernateException, SQLException {
						List<Object> temp = null;
						try {
							Query query = session.createSQLQuery(sql);
							if(params !=null && params.length > 0) {
								for(int i = 0;i<params.length;i++) {
									query.setParameter(i, params[i]);
								}
							}
							temp = query.list();
						} catch (Exception e) {
							log.error(e.getMessage(),e);
						}finally {
							session.close();
						}
						return temp;
					}
		});
		result = result == null ? new ArrayList<Object>() : result;
		return (List<T>) result;
	}
	
	/**
	 * 返回class对象集合
	 */
	@SuppressWarnings("unchecked")
	public static <T>  List<T> getList(final String sql,final Object[] params,@SuppressWarnings("rawtypes") final Class clazz,HibernateTemplateFactory htf) {
		List<Object> result = null;
		result = htf.getHibernateTemplate().execute(new HibernateCallback<List<Object>>() {
					public List<Object> doInHibernate(Session session) throws HibernateException, SQLException {
						List<Object> temp = null;
						try {
							Query query = session.createSQLQuery(sql).addEntity( clazz );
							if(params !=null && params.length > 0) {
								for(int i = 0;i<params.length;i++) {
									query.setParameter(i, params[i]);
								}
							}
							temp = query.list();
						} catch (Exception e) {
							log.error(e.getMessage(),e);
						}finally {
							session.close();
						}
						session.close();
						return temp;
					}
		});
		result = result == null ? new ArrayList<Object>() : result;
		return (List<T>) result;
	}
	
	/**
	 * 批量执行修改数据
	 */
	public static List<Map<String, Object>> getList(JdbcTemplate jdbcTemplate,String sql, Object[] params) {
		if(params == null) {
			params = new Object[]{};
		}
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, params);
		if(list !=null && list.size() > 0) {
			return list;
		}
		return new ArrayList<Map<String,Object>>();
	}
	
}
