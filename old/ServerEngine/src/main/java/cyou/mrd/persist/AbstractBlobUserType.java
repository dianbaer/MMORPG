package cyou.mrd.persist;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;


public abstract class AbstractBlobUserType implements BlobUserType {

	@Override
	public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner)
			throws HibernateException, SQLException {
		byte[] bytes = resultSet.getBytes(names[0]);
		try {
			return makeObject(bytes, owner);
		} catch (Exception e) {
			throw new HibernateException(e);
		}
	}

	@Override
	public void nullSafeSet(PreparedStatement statement, Object value, int index)
			throws HibernateException, SQLException {
		byte[] bytes = makeBytes(value);
		if (bytes != null) {
			statement.setBytes(index, bytes);
		} else {
			statement.setNull(index, Hibernate.BINARY.sqlType());
		}
	}

	@Override
	public int compare(Object o1, Object o2) {
		return 0;
	}

}
