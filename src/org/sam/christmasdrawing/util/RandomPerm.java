package org.sam.christmasdrawing.util;

import java.util.Random;

public class RandomPerm {
  private Random rand;

  public RandomPerm(Random rand) {
    this.rand = rand;
  }

  public int[] getRandomPerm(int n) {
    //generate an array of size n with 1..n
    int[] returnArray = new int[n];
    for (int i = 0; i < n; i++) {
      returnArray[i] = i;
    }

    for (int i = n; i > 1; i--) {
      // Each iteration, randomly pick one of the remaining numbers.
      // We start from the end of the array and work to the front.
      int toSwap = rand.nextInt(i);
      int placeHolder = returnArray[toSwap];
      returnArray[toSwap] = returnArray[i - 1];
      returnArray[i - 1] = placeHolder;
    }

    return returnArray;
  }
}
