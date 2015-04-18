package com.fleenmobile.androidapp.training;

import com.thalmic.myo.Pose;

import java.util.List;

public class Training {

    private static final int checkpointFrequency = 3;

    private final List<Pose> trainingPoses;
    private int trainingPosition = 0;
    private int lastCheckpoint = 0;
    private boolean lastMoveCorrect = true;

    public Training(List<Pose> trainingPoses) {
        this.trainingPoses = trainingPoses;
    }

    public void performedPose(Pose pose) {
        if (isFinished()) {
            throw new IllegalStateException("Training already finished");
        }

        if (pose == trainingPoses.get(trainingPosition)) {
            trainingPosition++;
            lastMoveCorrect = true;

            if (trainingPosition % checkpointFrequency == 0) {
                lastCheckpoint = trainingPosition;
            }
        } else {
            trainingPosition = lastCheckpoint;
            lastMoveCorrect = false;
        }
    }

    public boolean isLastMoveCorrect() {
        return lastMoveCorrect;
    }

    public boolean isFinished() {
        return trainingPosition == trainingPoses.size();
    }
}
