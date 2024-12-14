package com.example.intelligentwebscrapping.domain;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Stats {
    long missing;
    long hallucination;
    long accuracy;

    public void incrementMissing() {
        missing++;
    }

    public void incrementHallucination() {
        hallucination++;
    }

    public void incrementAccuracy() {
        accuracy++;
    }


    @Override
    public String toString() {
        long total = missing + hallucination + accuracy;
        char separator = ',';
        return "missing" + separator + "hallucination" + separator + "accuracy" + separator + "score\n" +
                Stream.of(missing, hallucination, accuracy, accuracy - hallucination)
                        .map(Objects::toString)
                        .collect(Collectors.joining(Character.toString(separator)))
                + "\n" + missing/total + hallucination/total + accuracy/total + (accuracy-hallucination)/total;
    }
}
