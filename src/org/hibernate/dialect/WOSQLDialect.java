package org.hibernate.dialect;

import java.sql.Types;

import org.hibernate.Hibernate;

public class WOSQLDialect extends MySQLDialect {

	public WOSQLDialect() {
		super();
		registerHibernateType(Types.REAL, Hibernate.FLOAT.getName());
		registerHibernateType(Types.LONGVARCHAR, Hibernate.TEXT.getName());
	}
}