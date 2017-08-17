package ak.server;

import cyou.mrd.util.ErrorHandler;
/**
 * ErrorHandler扩展
 * @author xuepeng
 *
 */
public class ErrorHandlerEx extends ErrorHandler {
	
	//摇钱树错误
	/**
	 * 摇钱树系统配置未找到。
	 */
	public static final int ERROR_CODE_100 = 63;
	
	/**
	 * 玩家SNS记录异常，不能进行互动操作！
	 */
	public static final int ERROR_CODE_101 = 64;
	
	/**
	 * 玩家还没有摇钱树，不能浇水！
	 */
	public static final int ERROR_CODE_102 = 65;
	
	/**
	 * 当前摇钱树不能浇水！
	 */
	public static final int ERROR_CODE_103 = 66;
	
	/**
	 * 自己必须先造此树才能与好友进行交互。
	 */
	public static final int ERROR_CODE_104 = 67;
	
	/**
	 * 自己帮助次数达到上限，不能帮好友浇水！
	 */
	public static final int ERROR_CODE_105 = 68;
	
	/**
	 * 自身体力不够，不能帮好友浇水！
	 */
	public static final int ERROR_CODE_106 = 69;
	
	/**
	 * 已帮助此好友浇过水，不能再浇水！
	 */
	public static final int ERROR_CODE_107 = 70;
	
	/**
	 * 摇钱树状态不正确，当前不能收获！
	 */
	public static final int ERROR_CODE_108 = 71;
	
	/**
	 * 邮件系统（已经领取附件奖励，无法再次领取，如果确实没有收到附件，请联系管理员）
	 */
	public static final int ERROR_CODE_63 = 72;
	/**
	 * 邮件系统（不存在此邮件）
	 */
	public static final int ERROR_CODE_64 = 73;
	/**
	 * 邮件系统（该邮件没有奖励附件）
	 */
	public static final int ERROR_CODE_65 = 74;
	/**
	 * 不存在此爱心值道具
	 */
	public static final int ERROR_CODE_70 = 75;
	
	//此处为市场相关，如有冲突可改下代码值，然后再写入词典。
	/**
	 * 当前没有可用的货架，请检查货架并进行同步！
	 */
	public static final int ERROR_CODE_109 = 76;
	
	/**
	 * 没有找到任何货物！
	 */
	public static final int ERROR_CODE_110 = 77;
	
	/**
	 * 当前服务器正忙，请稍候再提交！
	 */
	public static final int ERROR_CODE_111 = 78;
	
	/**
	 * 该格子数据不存在！
	 */
	public static final int ERROR_CODE_112 = 79;
	/**
	 * 不存在这个npc
	 */
	public static final int ERROR_CODE_113 = 80;
	/**
	 * 体力值不足
	 */
	public static final int ERROR_CODE_114 = 81;
	/**
	 * 不存在这个操作
	 */
	public static final int ERROR_CODE_115 = 82;
	/**
	 * 今天不能在帮助好友劳务了，明天继续吧
	 */
	public static final int ERROR_CODE_116 = 83;
	/**
	 * 今天已经送过了，明天在送礼吧
	 */
	public static final int ERROR_CODE_117 = 84;
	/**
	 * 不存在的贸易
	 */
	public static final int ERROR_CODE_118 = 85;
	/**
	 * 该贸易已经完成
	 */
	public static final int ERROR_CODE_119 = 86;
	/**
	 * 该贸易正在进行中，或已经完成
	 */
	public static final int ERROR_CODE_120 = 87;
	/**
	 * 该贸易申请帮助已经达到最大数量
	 */
	public static final int ERROR_CODE_121 = 88;
	/**
	 * 服务器正在保存进度，稍后重试
	 */
	public static final int ERROR_CODE_122 = 89;
	/**
	 * 您已经登录到另一台服务器了，正在处理中，稍后重试
	 */
	public static final int ERROR_CODE_123 = 90;
	/**
	 * loginType不对
	 */
	public static final int ERROR_CODE_124 = 91;
	/**
	 * 该用户正在另一台服务器进行登录操作，稍后重试
	 */
	public static final int ERROR_CODE_125 = 92;
	/**
	 * 登录时间超时，稍后重试
	 */
	public static final int ERROR_CODE_126 = 93;
	/**
	 * 没有连接到world服务器，无法登录
	 */
	public static final int ERROR_CODE_127 = 94;
}
