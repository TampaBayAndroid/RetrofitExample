package org.tbadg.retrofitexample;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Item {

    @SerializedName("has_synonyms")
    @Expose
    private Boolean hasSynonyms;
    @SerializedName("is_moderator_only")
    @Expose
    private Boolean isModeratorOnly;
    @SerializedName("is_required")
    @Expose
    private Boolean isRequired;
    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("name")
    @Expose
    private String name;

    /**
     * No args constructor for use in serialization
     */
    public Item() {
    }

    public Item(Boolean hasSynonyms, Boolean isModeratorOnly, Boolean isRequired, Integer count,
                String name) {
        super();
        this.hasSynonyms = hasSynonyms;
        this.isModeratorOnly = isModeratorOnly;
        this.isRequired = isRequired;
        this.count = count;
        this.name = name;
    }

    public Boolean getHasSynonyms() {
        return hasSynonyms;
    }

    public void setHasSynonyms(Boolean hasSynonyms) {
        this.hasSynonyms = hasSynonyms;
    }

    public Boolean getIsModeratorOnly() {
        return isModeratorOnly;
    }

    public void setIsModeratorOnly(Boolean isModeratorOnly) {
        this.isModeratorOnly = isModeratorOnly;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
