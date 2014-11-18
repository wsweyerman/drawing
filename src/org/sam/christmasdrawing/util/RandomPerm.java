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

    for (int i = 0; i < n; i++) {
      //swap each element of the array out with some other (random) element
      int toSwap = rand.nextInt(n);
      int placeHolder = returnArray[toSwap];
      returnArray[toSwap] = returnArray[i];
      returnArray[i] = placeHolder;
    }

    return returnArray;
  }
}
