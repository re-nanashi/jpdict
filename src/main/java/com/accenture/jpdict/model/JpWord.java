package com.accenture.jpdict.model;

import com.moji4j.MojiConverter;

public class JpWord {
    private String word;
    private String reading;
    private String romaji;
    private String english;
    private String otherDefs;

    @Override
    public String toString() {
        return String.format("%s;%s;%s;%s;%s", getEnglish(), getWord(), getReading(), getRomaji(), getOtherDefs());
    }

    public String getWord() {
        return this.word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getReading() {
        if (this.reading == null) return "該当なし";
        return this.reading;
    }

    /// Bad practice: this method has a side effect
    public void setReading(String reading) {
        this.reading = reading;
        // Set romaji reading from kana
        MojiConverter converter = new MojiConverter();
        this.romaji = converter.convertKanaToRomaji(reading);
    }

    public String getRomaji() {
        if (this.romaji == null) return "N/A";
        return romaji;
    }

    public String getEnglish() {
        if (this.english == null) return "N/A";
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getOtherDefs() {
        if (this.otherDefs == null) return "N/A";
        return otherDefs;
    }

    public void setOtherDefs(String otherDefs) {
        this.otherDefs = otherDefs;
    }
}
