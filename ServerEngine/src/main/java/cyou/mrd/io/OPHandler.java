package cyou.mrd.io;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface OPHandler {
	public final static int TCP = 0;	//TCP包 处理器
	public final static int HTTP = 1;	//HTTP包处理器
	public final static int EVENT = 2;  //EVENT处理器
	public final static int HTTP_EVENT = 3;  //既是HTTP包处理器又是EVENT处理器
	public final static int TCP_EVENT = 4;//既是TCP包处理器又是EVENT处理器
	int TYPE();
}
