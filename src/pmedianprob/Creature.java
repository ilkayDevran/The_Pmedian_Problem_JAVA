package pmedianprob;

import java.util.ArrayList;

public class Creature {

    private int[] gene;
    private double fitness;

    public Creature(int rng) {
        this.gene = new int[rng];
        for (int i = 0; i < rng; i++) {
            this.gene[i] = 0;
        }
    }

    public void setFitness(double f) {
        this.fitness = f;
    }

    public double getFitness() {
        return this.fitness;
    }

    public void setChromosome(int index, int value) {
        this.gene[index] = value;
    }

    public int getChromosome(int index) {
        return this.gene[index];
    }

    public void setGene(int[] g) {
        System.arraycopy(g, 0, this.gene, 0, g.length);
    }

    public int[] getGene() {
        return this.gene;
    }

    public ArrayList<Integer> getOnesIndexes() {
        ArrayList<Integer> onesIndexes = new ArrayList<>();
        for (int i = 0; i < this.gene.length; i++) {
            if (this.gene[i] == 1) {
                onesIndexes.add(i);
            }
        }
        return onesIndexes;
    }
    
    public int getAmountOfOnes(){
        ArrayList<Integer> onesIndexes = this.getOnesIndexes();
        return onesIndexes.size();
    }

    public void printGene() {
        System.out.print("[ ");
        for (int i : this.gene) {
            System.out.print(i + " ");
        }
        System.out.println("]");
    }

    public void getInfo() {
        System.out.print("Fitness: " + this.fitness + "\n[ ");
        ArrayList<Integer> ones = getOnesIndexes();
        for (Integer i : ones) {
            System.out.print(i + " ");
        }
        System.out.println("] Ones: " + ones.size());
        this.printGene();
        System.out.println();
    }
}
