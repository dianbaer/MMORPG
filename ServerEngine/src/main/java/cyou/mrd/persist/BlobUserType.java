package cyou.mrd.persist;

import java.util.Comparator;

public interface BlobUserType extends org.hibernate.usertype.UserType,Comparator{
	Object makeObject(byte[] bytes, Object owner) throws Exception;
	byte[] makeBytes(Object object);
}
