package cyou.mrd.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cyou.mrd.io.Packet;
import cyou.mrd.io.http.HOpCode;
import cyou.mrd.io.http.HSession;
import cyou.mrd.io.http.JSONPacket;

public class ErrorHandler {

	private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);

	/**
	 * <"请重新登录","1">
	 */
	private static Map<Integer, String> errors = new HashMap<Integer, String>();

	public static final int ERROR_CODE_0 = 0;// 服务器错误
	public static final int ERROR_CODE_1 = 1;// 请重新登录
	public static final int ERROR_CODE_2 = 2;// [opcode]没有正确设置
	public static final int ERROR_CODE_3 = 3;// 请输入正确的好友id
	public static final int ERROR_CODE_4 = 4;// 无效的好友id
	public static final int ERROR_CODE_5 = 5;// 没有找到此玩家
	public static final int ERROR_CODE_6 = 6;// 对方已经是您的好友了
	public static final int ERROR_CODE_7 = 7;// 对方不是您的好友
	public static final int ERROR_CODE_8 = 8;// 您已经取消了对方好评
	public static final int ERROR_CODE_9 = 9;// 这里已经打扫干净了
	public static final int ERROR_CODE_10 = 10;// 没有这个好友
	public static final int ERROR_CODE_11 = 11;// 访问失败
	/**
	 * //没有找到此好友
	 */
	public static final int ERROR_CODE_12 = 12;
	public static final int ERROR_CODE_13 = 13;// 访问的好友不存在
	public static final int ERROR_CODE_14 = 14;// 今天已经不能在替好友招待客人了
	public static final int ERROR_CODE_15 = 15;// 参数异常
	public static final int ERROR_CODE_16 = 16;// 此玩家不存在
	public static final int ERROR_CODE_17 = 17;// 您今天已经不能在帮好友清理了
	public static final int ERROR_CODE_18 = 18;// 您已经给了对方好评
	public static final int ERROR_CODE_19 = 19;// 请设置有效密码
	public static final int ERROR_CODE_20 = 20;// 非法的昵称
	public static final int ERROR_CODE_21 = 21;// 非法的字符
	public static final int ERROR_CODE_22 = 22;// 昵称至少2个字符
	public static final int ERROR_CODE_23 = 23;// 昵称最多16个字符
	public static final int ERROR_CODE_24 = 24;// mid异常
	public static final int ERROR_CODE_25 = 25;// 帐号异常
	public static final int ERROR_CODE_26 = 26;// 注册失败, 请稍候重试
	public static final int ERROR_CODE_27 = 27;// 您的游戏版本需要更新
	public static final int ERROR_CODE_28 = 28;// 请设置有效ID
	public static final int ERROR_CODE_29 = 29;// 搬家时帐号或密码错误
	public static final int ERROR_CODE_30 = 30;// 每日邮件数不能超过
	public static final int ERROR_CODE_31 = 31;// 参数错误
	public static final int ERROR_CODE_32 = 32;// 有未处理完的订单 稍后重试
	public static final int ERROR_CODE_33 = 33;// 此商品数据异常
	public static final int ERROR_CODE_34 = 34;// 购买此商品不需要水晶
	public static final int ERROR_CODE_35 = 35;// 您的金币不足
	public static final int ERROR_CODE_36 = 36;// Illegality shop id
	public static final int ERROR_CODE_37 = 37;// 请重新登录SNS平台
	public static final int ERROR_CODE_38 = 38;// 暂时不支持此平台
	public static final int ERROR_CODE_39 = 39;// sns 获取好友好友列表失败
	public static final int ERROR_CODE_40 = 40;// 今天已经参观过这里了
	public static final int ERROR_CODE_41 = 41;// 数据出现异常, 请联系客服
	public static final int ERROR_CODE_42 = 42;// 对方未申请加你为好友
	public static final int ERROR_CODE_43 = 43;// 不能加自己为好友
	public static final int ERROR_CODE_44 = 44;// 每日邮件数已超上限
	public static final int ERROR_CODE_45 = 45;// 协议未实现
	public static final int ERROR_CODE_46 = 46;// 没有读到远程数据
	public static final int ERROR_CODE_47 = 47;// 您已经申请加对方好友了,请耐心等待
	public static final int ERROR_CODE_48 = 48;// 好友列表 不能删除自己
	public static final int ERROR_CODE_49 = 49;// 有未处理完的请求 请稍后重试
	public static final int ERROR_CODE_50 = 50;// 没有搜索到任何好友
	/**
	 * 客户端同步进度,同步金钱时，上传时间异常
	 */
	public static final int ERROR_CODE_CLIENT_TIME_ERROR = 51;
	/**
	 * 客户端同步进度，充值金额大于服务器值
	 */
	public static final int ERROR_CODE_IMONEY_BUYDOLLAR_NOLINEAR = 52;
	/**
	 * 客户端同步进度，补偿金额大于服务器值
	 */
	public static final int ERROR_CODE_IMONEY_COMPENSATEDOLLAR_NOLINEAR = 53;
	/**
	 * 客户端同步进度，话费总金钱小于服务器值
	 */
	public static final int ERROR_CODE_IMONEY_USEDDOLLAR_NOLINEAR = 54;
	/**
	 * 客户端同步进度，r等式不成立
	 */
	public static final int ERROR_CODE_IMONEY_REMAINDOLLAR_NOLINEAR = 55;
	/**
	 * 客户端同步进度，游戏内获得到金额超过最大限度
	 */
	public static final int ERROR_CODE_IMONEY_REWARDDOLLAR_OUT_MAX = 56;
	/**
	 * 客户端同步进度，上传协议格式错误
	 */
	public static final int ERROR_CODE_SYNC_FORMAT_ERROR = 57;
	/**
	 * 客户端同步进度，初始的钱大于系统允许的最大值
	 */
	public static final int ERROR_CODE_INITDOLLAR_NOLINEAR = 58;

	/**
	 * 客户端的加密模式和服务器不匹配
	 */
	public static final int ERROR_CODE_ENCODE = 59;
	/**
	 * 加好友时, 超过当前好友上限
	 */
	public static final int ERROR_CODE_FIREND_MAX_LIMIT = 60;
	/**
	 * 注册时有过去的存档
	 */
	public static final int ERROR_CODE_REG_HAS_OLD_PROCESS = 61;
	/**
	 * 请输入好友id
	 */
	public static final int ERROR_CODE_FRIENDID_NULL = 62;

	static {
		errors.put(1, "请重新登录");
		errors.put(2, "[opcode]没有正确设置");
		errors.put(3, "请输入正确的好友id");
		errors.put(4, "无效的好友id");
		errors.put(5, "没有找到此玩家.");
		errors.put(6, "对方已经是您的好友了");
		errors.put(7, "对方不是您的好友");
		errors.put(8, "您已经取消了对方好评");
		errors.put(9, "这里已经打扫干净了.");
		errors.put(10, "没有这个好友.");
		errors.put(11, "访问失败.");
		errors.put(12, "没有找到此好友.");
		errors.put(13, "访问的好友不存在");
		errors.put(14, "今天已经不能在替好友招待客人了");
		errors.put(15, "参数异常");
		errors.put(16, "此玩家不存在");
		errors.put(17, "您今天已经不能在帮好友清理了");
		errors.put(18, "您已经给了对方好评");
		errors.put(19, "请设置有效密码");
		errors.put(20, "非法的昵称");
		errors.put(21, "非法的字符");
		errors.put(22, "昵称至少2个字符");
		errors.put(23, "昵称最多16个字符");
		errors.put(24, "mid异常");
		errors.put(25, "帐号异常");
		errors.put(26, "注册失败, 请稍候重试");
		errors.put(27, "您的游戏版本需要更新");
		errors.put(28, "请设置有效ID");
		errors.put(29, "帐号或密码错误");
		errors.put(30, "每日邮件数不能超过");
		errors.put(31, "参数错误");
		errors.put(32, "有未处理完的订单  稍后重试");
		errors.put(33, "此商品数据异常");
		errors.put(34, "购买此商品不需要水晶");
		errors.put(35, "您的金币不足");
		errors.put(36, "Illegality shop id");
		errors.put(37, "请重新登录SNS平台");
		errors.put(38, "暂时不支持此平台");
		errors.put(39, "sns 获取好友好友列表失败");
		errors.put(40, "今天已经参观过这里了.");
		errors.put(41, "数据出现异常, 请联系客服.");
		errors.put(42, "对方未申请加你为好友");
		errors.put(43, "不能加自己为好友");
		errors.put(44, "每日邮件数已超上限");
		errors.put(45, "协议未实现");
		errors.put(46, "没有读到远程数据");
		errors.put(47, "您已经申请加对方好友了,请耐心等待");
		errors.put(48, "好友列表 不能删除自己");
		errors.put(49, "有未处理完的请求 请稍后重试");
		errors.put(50, "没有搜索到任何好友");
		errors.put(51, "客户端同步进度,同步金钱时，上传时间异常");
		errors.put(52, "客户端同步进度，充值金额大于服务器值");
		errors.put(53, "客户端同步进度，补偿金额大于服务器值");
		errors.put(54, "客户端同步进度，花费总金钱小于服务器值");
		errors.put(55, "客户端同步进度，r等式不成立");
		errors.put(56, "客户端同步进度，游戏内获得到金额超过最大限度");
		errors.put(57, "客户端同步进度，上传协议格式错误");
		errors.put(58, "客户端同步进度，初始的钱大于系统允许的最大值");
		errors.put(59, "客户端的加密模式和服务器不匹配");
		errors.put(60, "加好友时, 超过当前好友上限");
		errors.put(61, "注册时有过去的存档");
		errors.put(62, "请输入好友id");
	}

	public static void sendErrorMessage(HSession session, int errorCode, int opcode) {
		log.info("[sendError] session:{} msg:{} opcode:{} player({})", new Object[] { session.getSessionId(), errors.get(errorCode),
				opcode, session.client() });
		Packet packet = new JSONPacket(HOpCode.HTTP_ERROR);
		packet.put("error", errorCode);
		packet.put("opcode", opcode);
		session.send(packet);
	}

}
