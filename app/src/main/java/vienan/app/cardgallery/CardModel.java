package vienan.app.cardgallery;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

/**
 * Created by vienan on 2015/8/21.
 */
@Table(name = "Cards")
public class CardModel extends Model implements Serializable{
    public CardModel(String imgPath, String title, String description) {
        super();
        this.imgPath = imgPath;
        this.title = title;
        this.description = description;
    }

    public CardModel() {
        super();
    }
    @Column(name = "imgPath",index = true)
    public String imgPath;

    @Column(name = "title",index = true)
    public String title;

    @Column(name = "description")
    public String description;


}
