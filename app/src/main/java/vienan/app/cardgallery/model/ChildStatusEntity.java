package vienan.app.cardgallery.model;

/**
 * 二级Item实体类
 * 
 * @author zihao
 * 
 */
public class ChildStatusEntity {
	/** 预计完成时间 **/
	private String completeTime;
	/** 是否已完成 **/
	private boolean isfinished;

	private String card_type;

	public void setCard_type(String card_type) {
		this.card_type = card_type;
	}

	public String getCard_type() {
		return card_type;
	}

	public String getCompleteTime() {
		return completeTime;
	}

	public void setCompleteTime(String completeTime) {
		this.completeTime = completeTime;
	}

	public boolean isIsfinished() {
		return isfinished;
	}

	public void setIsfinished(boolean isfinished) {
		this.isfinished = isfinished;
	}

}
