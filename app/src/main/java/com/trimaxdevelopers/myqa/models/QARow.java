package com.trimaxdevelopers.myqa.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Parcelable and Cloneable for Fragments and Activities
 */
public class QARow implements Parcelable, Cloneable {

    // primary key _id and question column of database
    private String _id, question;

    // original answer and user selected answer
    private String answer, selectedAnswer;

    // options for question
    private ArrayList<String> options = new ArrayList<>();

    public QARow() {
    }

    protected QARow(Parcel in) {
        _id = in.readString();
        question = in.readString();
        answer = in.readString();
        selectedAnswer = in.readString();
        options = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(question);
        dest.writeString(answer);
        dest.writeString(selectedAnswer);
        dest.writeStringList(options);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<QARow> CREATOR = new Creator<QARow>() {
        @Override
        public QARow createFromParcel(Parcel in) {
            return new QARow(in);
        }

        @Override
        public QARow[] newArray(int size) {
            return new QARow[size];
        }
    };

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(String selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    public QARow clone() {
        try {
            return (QARow) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
