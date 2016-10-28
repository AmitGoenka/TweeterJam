package org.agoenka.tweeterjam.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.agoenka.tweeterjam.TweeterJamDatabase;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Author: agoenka
 * Created At: 10/28/2016
 * Version: ${VERSION}
 */

/**
 * This is a sample model that demonstrates the basic structure of a SQLite persisted Model object.
 * Check out DBFlow wiki for more details: @link {https://github.com/pardom/ActiveAndroid/wiki/Creating-your-database-model}
 */

@Table(database = TweeterJamDatabase.class)
public class SampleModel extends BaseModel {

    @PrimaryKey
    @Column
    Long id;

    @Column
    private String name;

    public SampleModel() {
        super();
    }

    // Parse model from JSON
    public SampleModel(JSONObject object){
        super();

        try {
            this.name = object.getString("title");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Record Finders
    public static SampleModel byId(long id) {
        return new Select().from(SampleModel.class).where(SampleModel_Table.id.eq(id)).querySingle();
    }

    public static List<SampleModel> recentItems() {
        return new Select().from(SampleModel.class).orderBy(SampleModel_Table.id, false).limit(300).queryList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
