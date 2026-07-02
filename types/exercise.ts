import { WorkoutSet } from "./set";

export type Exercise = {
    id: string;
    name: string;
    sets: WorkoutSet[];
};