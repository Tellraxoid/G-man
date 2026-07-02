import { Exercise } from "./exercise";

export type Workout = {
    id: string;
    name: string;
    date: string;
    exercises: Exercise[];
};

export type Set = {
    weight: number;
    reps: number;
};

export type Exercise = {
    name: string;
    sets: Set[];
};