package edu.northeastern.numad24su_plateperfect;

import java.util.List;

public class rvParentModelClass {
    String foodCategory;
    List<rvChildModelClass> rvChildModelClassList;

    public rvParentModelClass(String foodCategory, List<rvChildModelClass> rvChildModelClassList) {
        this.foodCategory = foodCategory;
        this.rvChildModelClassList = rvChildModelClassList;
    }

    public String getFoodCategory() {
        return foodCategory;
    }

    public List<rvChildModelClass> getRvChildModelClassList() {
        return rvChildModelClassList;
    }

}
