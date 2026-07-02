export function getPR(sets) {
  if (sets.length === 0) return 0;
  return Math.max(...sets.map((set) => set.weight));
}