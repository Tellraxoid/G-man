export const exercisesDatabase = [
  {
    muscle: 'Chest',
    exercises: [
      { name: 'Bench Press', equipment: 'Barbell', difficulty: 'Intermediate', image: 'https://example.com/bench-press.jpg',description: 'The bench press is a compound exercise that targets the chest, shoulders, and triceps. It is performed by lying on a bench and pressing a barbell or dumbbells away from the chest.' },
      { name: 'Incline Dumbbell Press', equipment: 'Dumbbells', difficulty: 'Beginner', image: 'https://example.com/incline-dumbbell-press.jpg',description: 'The incline dumbbell press is an exercise that targets the upper chest muscles. It is performed by lying on an incline bench and pressing dumbbells upward.' },
      { name: 'Chest Fly', equipment: 'Dumbbells', difficulty: 'Beginner', image: 'https://example.com/chest-fly.jpg',description: 'The chest fly is an exercise that targets the chest muscles. It is performed by lying on a bench and bringing the arms together in a flying motion.' },
      { name: 'Push-Ups', equipment: 'Bodyweight', difficulty: 'Beginner', image: 'https://example.com/push-ups.jpg',description: 'Push-ups are a bodyweight exercise that targets the chest, shoulders, and triceps. They are performed by pushing the body away from the ground while keeping the body straight.' },
      { name: 'Cable Crossover', equipment: 'Cable Machine', difficulty: 'Intermediate', image: 'https://example.com/cable-crossover.jpg',description: 'The cable crossover is an exercise that targets the chest muscles. It is performed by pulling cables across the body from each side.' },
      { name: 'Dumbbell Pullover', equipment: 'Dumbbell', difficulty: 'Intermediate', image: 'https://example.com/dumbbell-pullover.jpg',description: 'The dumbbell pullover is an exercise that targets the chest and back muscles. It is performed by lying on a bench and pulling a dumbbell over the head.' },
      { name: 'Decline Bench Press', equipment: 'Barbell', difficulty: 'Advanced', image: 'https://example.com/decline-bench-press.jpg',description: 'The decline bench press is an exercise that targets the lower chest muscles. It is performed by lying on a decline bench and pressing a barbell or dumbbells upward.' },
      { name: 'Chest Dips', equipment: 'Parallel Bars', difficulty: 'Intermediate', image: 'https://example.com/chest-dips.jpg',description: 'Chest dips are an exercise that targets the chest, shoulders, and triceps. They are performed by gripping parallel bars and lowering the body by bending the elbows.' },
      { name: 'Pec Deck Machine', equipment: 'Machine', difficulty: 'Beginner', image: 'https://example.com/pec-deck-machine.jpg',description: 'The pec deck machine is an exercise that targets the chest muscles. It is performed by pulling the handles together in a squeezing motion.' },
    ],
  },
  {
    muscle: 'Back',
    exercises: [
      { name: 'Pull-Ups', equipment: 'Bodyweight', difficulty: 'Intermediate', image: 'https://example.com/pull-ups.jpg',description: 'Pull-ups are a bodyweight exercise that targets the back muscles. They are performed by pulling the body upward using the arms and back muscles.' },
      { name: 'Deadlifts', equipment: 'Barbell', difficulty: 'Advanced', image: 'https://example.com/deadlifts.jpg',description: 'Deadlifts are a compound exercise that targets the back, glutes, and legs. They are performed by lifting a barbell from the ground to hip level.' },
      { name: 'Rows', equipment: 'Cable Machine', difficulty: 'Beginner', image: 'https://example.com/rows.jpg',description: 'Rows are an exercise that targets the back muscles. They are performed by pulling a cable toward the body while keeping the back straight.' },
      { name: 'Lat Pulldowns', equipment: 'Cable Machine', difficulty: 'Beginner', image: 'https://example.com/lat-pulldowns.jpg',description: 'Lat pulldowns are an exercise that targets the latissimus dorsi muscles. They are performed by pulling a bar down toward the chest.' },
      { name: 'T-Bar Rows', equipment: 'Barbell', difficulty: 'Intermediate', image: 'https://example.com/t-bar-rows.jpg',description: 'T-bar rows are an exercise that targets the back muscles. They are performed by pulling a barbell toward the body while keeping the back straight.' },
      { name: 'Seated Cable Rows', equipment: 'Cable Machine', difficulty: 'Beginner', image: 'https://example.com/seated-cable-rows.jpg',description: 'Seated cable rows are an exercise that targets the back muscles. They are performed by pulling a cable toward the body while sitting.' },
      { name: 'Face Pulls', equipment: 'Cable Machine', difficulty: 'Intermediate', image: 'https://example.com/face-pulls.jpg',description: 'Face pulls are an exercise that targets the rear deltoids and rhomboids. They are performed by pulling a cable toward the face.' },
      { name: 'Back Extensions', equipment: 'Bodyweight', difficulty: 'Beginner', image: 'https://example.com/back-extensions.jpg',description: 'Back extensions are an exercise that targets the erector spinae muscles. They are performed by lying face down and lifting the upper body off the ground.' },
      { name: 'Inverted Rows', equipment: 'Parallel Bars', difficulty: 'Intermediate', image: 'https://example.com/inverted-rows.jpg',description: 'Inverted rows are an exercise that targets the back muscles. They are performed by hanging from parallel bars and pulling the body upward.' },
    ],
  },
  {
    muscle: 'Legs',
    exercises: [
      { name: 'Squats', equipment: 'Barbell', difficulty: 'Intermediate', image: 'https://example.com/squats.jpg',description: 'Squats are an exercise that targets the quadriceps, glutes, and hamstrings. They are performed by lowering the body by bending the knees and hips.' },
      { name: 'Lunges', equipment: 'Bodyweight', difficulty: 'Beginner', image: 'https://example.com/lunges.jpg',description: 'Lunges are an exercise that targets the quadriceps, glutes, and hamstrings. They are performed by stepping forward or backward and lowering the body until both knees are bent at 90 degrees.' },
      { name: 'Calf Raises', equipment: 'Machine', difficulty: 'Beginner', image: 'https://example.com/calf-raises.jpg',description: 'Calf raises are an exercise that targets the calf muscles. They are performed by standing on the edge of a step and raising the heels off the ground.' },
      { name: 'Leg Press', equipment: 'Machine', difficulty: 'Intermediate', image: 'https://example.com/leg-press.jpg',description: 'The leg press is an exercise that targets the quadriceps, glutes, and hamstrings. It is performed by pushing a platform away from the body using the legs.' },
    ],
  },
  {
    muscle: 'Shoulders',
    exercises: [
      { name: 'Overhead Press', equipment: 'Barbell', difficulty: 'Intermediate', image: 'https://example.com/overhead-press.jpg',description: 'The overhead press is an exercise that targets the shoulders and triceps. It is performed by pressing a barbell overhead.' },
      { name: 'Lateral Raises', equipment: 'Dumbbells', difficulty: 'Beginner', image: 'https://example.com/lateral-raises.jpg',description: 'Lateral raises are an exercise that targets the lateral deltoids. They are performed by raising dumbbells out to the sides until the arms are parallel to the ground.' },
      { name: 'Front Raises', equipment: 'Dumbbells', difficulty: 'Beginner', image: 'https://example.com/front-raises.jpg',description: 'Front raises are an exercise that targets the anterior deltoids. They are performed by raising dumbbells in front of the body until the arms are parallel to the ground.' },
      { name: 'Rear Delt Fly', equipment: 'Dumbbells', difficulty: 'Beginner', image: 'https://example.com/rear-delt-fly.jpg',description: 'Rear delt flyes are an exercise that targets the posterior deltoids. They are performed by raising dumbbells behind the body.' },
    ],
  },
  {
    muscle: 'Arms',
    exercises: [
      { name: 'Bicep Curls', equipment: 'Dumbbells', difficulty: 'Beginner', image: 'https://example.com/bicep-curls.jpg',description: 'Bicep curls are an exercise that targets the biceps. They are performed by curling dumbbells toward the shoulders.' },
      { name: 'Tricep Dips', equipment: 'Bodyweight', difficulty: 'Intermediate', image: 'https://example.com/tricep-dips.jpg',description: 'Tricep dips are an exercise that targets the triceps. They are performed by lowering the body using parallel bars or a bench.' },
      { name: 'Hammer Curls', equipment: 'Dumbbells', difficulty: 'Beginner', image: 'https://example.com/hammer-curls.jpg',description: 'Hammer curls are an exercise that targets the brachialis and brachioradialis muscles. They are performed by curling dumbbells with a neutral grip.' },
    ],
  },
];