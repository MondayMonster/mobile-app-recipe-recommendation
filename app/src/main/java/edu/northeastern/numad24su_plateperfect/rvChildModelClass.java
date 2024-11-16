package edu.northeastern.numad24su_plateperfect;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.URLEncoder;

public class rvChildModelClass implements Parcelable {
    private int Cook_time;
    private String Cuisine;
    private String Image_Link;
    private String Name;
    private int Prep_time;
    private double Rating;
    private String Tagline;
    private String Type;
    private String Description;

    // No-argument constructor required for Firebase
    public rvChildModelClass() {
    }

    public rvChildModelClass(int Cook_time, String Cuisine, String Image_Link, String Name, int Prep_time, double Rating, String Tagline, String Type, String Description) {
        this.Cook_time = Cook_time;
        this.Cuisine = Cuisine;
        this.Image_Link = Image_Link;
        this.Name = Name;
        this.Prep_time = Prep_time;
        this.Rating = Rating;
        this.Tagline = Tagline;
        this.Type = Type;
        this.Description = Description;
    }

    protected rvChildModelClass(Parcel in) {
        Cook_time = in.readInt();
        Cuisine = in.readString();
        Image_Link = in.readString();
        Name = in.readString();
        Prep_time = in.readInt();
        Rating = in.readDouble();
        Tagline = in.readString();
        Type = in.readString();
        Description = in.readString();
    }

    public static final Creator<rvChildModelClass> CREATOR = new Creator<rvChildModelClass>() {
        @Override
        public rvChildModelClass createFromParcel(Parcel in) {
            return new rvChildModelClass(in);
        }

        @Override
        public rvChildModelClass[] newArray(int size) {
            return new rvChildModelClass[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Cook_time);
        dest.writeString(Cuisine);
        dest.writeString(Image_Link);
        dest.writeString(Name);
        dest.writeInt(Prep_time);
        dest.writeDouble(Rating);
        dest.writeString(Tagline);
        dest.writeString(Type);
        dest.writeString(Description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters and Setters
    public int getCook_time() {
        return Cook_time;
    }

    public void setCook_time(int Cook_time) {
        this.Cook_time = Cook_time;
    }

    public String getCuisine() {
        return Cuisine;
    }

    public void setCuisine(String Cuisine) {
        this.Cuisine = Cuisine;
    }

    public String getImage_Link() {
        return Image_Link;
    }

    public void setImage_Link(String Image_Link) {
        this.Image_Link = Image_Link;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public int getPrep_time() {
        return Prep_time;
    }

    public void setPrep_time(int Prep_time) {
        this.Prep_time = Prep_time;
    }

    public double getRating() {
        return Rating;
    }

    public void setRating(double Rating) {
        this.Rating = Rating;
    }

    public String getTagline() {
        return Tagline;
    }

    public void setTagline(String Tagline) {
        this.Tagline = Tagline;
    }

    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getVideoUrl() {
        // Return the video URL for the recipe
        return "https://www.youtube.com/results?search_query="+getName(); // Example URL
    }
    public void shareRecipe(Context context) {
        // Encode the video URL
        String videoUrl = getVideoUrl();
        try {
            videoUrl = videoUrl.replace(" ","+");
        }catch (Exception e) {
            e.printStackTrace();
        }
        // Logic to handle share functionality
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getName() + "\n" + getDescription() + "\n" + "Watch the recipe video: " + videoUrl);
        shareIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
}
