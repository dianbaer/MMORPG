package ak.quest;

import java.io.IOException;
import java.io.Serializable;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.persist.AbstractBlobUserType;

public class QuestListUserType extends AbstractBlobUserType {
	
	protected static final Logger log = LoggerFactory.getLogger(QuestListUserType.class);
	private static final int[] SQL_TYPES = { Hibernate.STRING.sqlType() };

	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return cached;
	}

	public Object deepCopy(Object value) throws HibernateException {
		if (value == null)
			return null;
		try {
			return ((QuestList) value).clone();
		} catch (CloneNotSupportedException e) {
			log.error("CloneNotSupportedException",e);
			return null;
		}
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

	public Object replace(Object original, Object target, Object owner) {
		return target;
	}

	@SuppressWarnings("rawtypes")
	public Class returnedClass() {
		return QuestList.class;
	}

	public int[] sqlTypes() {
		return SQL_TYPES;
	}

	public boolean isMutable() {
		return true;
	}

	@Override
	public Object makeObject(byte[] bytes, Object owner) throws Exception {
		if (bytes == null) {
			return null;
		} else {
			return QuestList.parse(bytes);
		}
	}

	@Override
	public byte[] makeBytes(Object object) {
		if (object == null)
			return null;
		else {
			try {
				return ((QuestList) object).toDbData();
			} catch (IOException e) {
				log.error("IOException",e);
			}
			return null;
		}
	}

}