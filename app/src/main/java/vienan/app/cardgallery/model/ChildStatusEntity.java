package vienan.app.cardgallery.model;

/**
 * 二级Item实体类
 * 
 * @author zihao
 * 
 */
public class ChildStatusEntity {
	private CardModel cardModel;

	public ChildStatusEntity(CardModel cardModel){
		this.cardModel=cardModel;
	}

	public CardModel getCardModel() {
		return cardModel;
	}
}
