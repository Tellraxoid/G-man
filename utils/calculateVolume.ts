export function calculateVolume(sets)
{  return sets.reduce(
    (total, set) =>
      total + set.reps * set.weight, 0
  );
}