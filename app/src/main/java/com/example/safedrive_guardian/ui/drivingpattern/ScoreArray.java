package com.example.safedrive_guardian.ui.drivingpattern;
import java.util.List;
public class ScoreArray {
    private List<Double> scr;

    public ScoreArray(List<Double> score) {
        this.scr = score;
    }

    public Double getAverage() {
        double add = 0;
        for (Double i : scr)
            add = add + i;

        int count
                = scr.size();
        return add / count;
    }
}