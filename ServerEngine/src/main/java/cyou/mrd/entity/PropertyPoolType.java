package cyou.mrd.entity;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.persist.BlobUserType;

/**
 * 用于把数据库中的字符串映射为PropertyPool对象的Hibernate数据类型。
 */
public class PropertyPoolType implements BlobUserType {
	
	protected static final Logger log = LoggerFactory.getLogger(PropertyPoolType.class);
	
	private static final int[] SQL_TYPES = { Hibernate.STRING.sqlType() };

	public Object assemble(Serializable cached, Object owner)
			throws HibernateException {
		return cached;
	}

	public Object deepCopy(Object value) throws HibernateException {
		if(value==null)
			return null;
		return ((PropertyPool)value).clone();
	}

	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	public boolean equals(Object x, Object y) throws HibernateException {
		if (x == y)
			return true;
		if (x == null || y == null)
			return false;
		return x.equals(y);
	}

	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	public boolean isMutable() {
		return true;
	}

	public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner)
			throws HibernateException, SQLException {
		String str = resultSet.getString(names[0]);
		try {
			return makeObject(str == null ? null : str.getBytes() ,owner);
		} catch (Exception e) {
			throw new HibernateException(e);
		}
	}

	public void nullSafeSet(PreparedStatement statement, Object value, int index)
			throws HibernateException, SQLException {
		byte[] bytes = makeBytes(value);
		if (bytes == null)
			statement.setNull(index, SQL_TYPES[0]);
		else {
			statement.setString(index, new String(bytes));
		}
	}
	
	

	@Override
	public int compare(Object o1, Object o2) {
		return 0;
	}

	@Override
	public Object makeObject(byte[] bytes, Object owner) throws Exception{
		PropertyPool pool = new PropertyPool();
		if(bytes == null)
			return pool;
		else
			try {
				pool.parse(new String(bytes));
			} catch (Exception e) {
				log.error("Exception", e);;
			}
		return pool;
	}

	@Override
	public byte[] makeBytes(Object object) {
		if(object == null)
			return null;
		else
			return object.toString().getBytes();
	}

	public Object replace(Object original, Object target, Object owner) {
		return target;
	}

	public Class<PropertyPool> returnedClass() {
		return PropertyPool.class;
	}

	public int[] sqlTypes() {
		return SQL_TYPES;
	}
}
