package cyou.mrd.game.relation;

import java.io.Serializable;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;

import cyou.mrd.persist.AbstractBlobUserType;

/**
 * 用于把数据库中的字符串映射为RelationList对象的Hibernate数据类型。
 */
public class RelationListUserType extends AbstractBlobUserType {
	private static final int[] SQL_TYPES = { Hibernate.STRING.sqlType() };

	public Object assemble(Serializable cached, Object owner)
			throws HibernateException {
		return cached;
	}

	public Object deepCopy(Object value) throws HibernateException {
		if (value == null)
			return null;
		return ((RelationList) value).clone();
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

	@Override
	public Object makeObject(byte[] bytes, Object owner) throws Exception {
		if (bytes == null) {
			return null;
		} else {
			RelationList ret = new RelationList();
			ret.parse(bytes);
			return ret;
		}
	}

	@Override
	public byte[] makeBytes(Object object) {
		if (object == null)
			return null;
		else {
			return ((RelationList) object).toDbData();
		}
	}

	public Object replace(Object original, Object target, Object owner) {
		return target;
	}

	@SuppressWarnings("rawtypes")
	public Class returnedClass() {
		return RelationList.class;
	}

	public int[] sqlTypes() {
		return SQL_TYPES;
	}

}
