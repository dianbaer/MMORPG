package ak.notice;

import java.io.Serializable;
import java.util.Date;
/**
 * 公告实体
 * @author xuepeng
 *
 */
public class Notice implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 公告id
	 */
	private int noticeId;
	/**
	 * 公告内容
	 */
	private String content;
	/**
	 * 添加时间
	 */
	private Date addTime;
	public int getNoticeId() {
		return noticeId;
	}
	public void setNoticeId(int noticeId) {
		this.noticeId = noticeId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getAddTime() {
		return addTime;
	}
	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}
}
