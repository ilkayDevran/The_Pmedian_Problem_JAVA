package pmedianprob;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class PmedianProb {

    public static Random random = new Random();

    /*'''PARAMETERS (values are up to user)'''*/
    public static int amountOfCity = 51;  //a variable to choose input files 51 or 76
    public static int iterationParameter = 1000;
    public static int populationOfgeneration = 75;
    public static int pmedian = 3;
    public static int stopFactor = 300;

    public static ArrayList<City> cityList = new ArrayList<City>();
    public static ArrayList<Creature> generation = new ArrayList<Creature>();
    public static double[][] distanceTable = new double[amountOfCity][amountOfCity];

    public static void main(String[] args) throws FileNotFoundException {
        QuickSort quickSort;
        double previousFitness = 0.0;
        int diffCounter = 0;
  
        /*--------INITIALIZING PART--------*/
        createCityList();
        prepareDistanceTable();
        getPrimitiveGeneration();

        /*---------ITERATIVE PART METHODS---------*/
        for (int i = 0; i < iterationParameter; i++) {
            quickSort = new QuickSort(generation);
            if (previousFitness == 0.0) {
                quickSort.startQuickStart(0, generation.size() - 1);
                previousFitness = generation.get(0).getFitness();
            }
            quickSort = new QuickSort(generation);
            quickSort.startQuickStart(0, generation.size() - 1);
            killSomeCreatures(); //Kill bad genes from generation to get best gene
            maintainGeneration(); //Maintain the population with new creatures
            System.out.println((i + 1) + ". iteration: " + generation.get(0).getFitness());
            if (generation.get(0).getFitness() == previousFitness) {
                if (diffCounter == stopFactor) {
                    break;
                }
                diffCounter++;
            }
            else{
                diffCounter = 0;
            }
             previousFitness = generation.get(0).getFitness();
        }

        /*---------THE END---------*/
        endOfProcess();
    }

    //--------INITIALIZING PART METHODS--------//
    private static void createCityList() throws FileNotFoundException {
        String xfile = "";
        String yfile = "";
        String demandfile = "";
        if (amountOfCity == 51) {
            xfile = "x51.txt";
            yfile = "y51.txt";
            demandfile = "dem51.txt";
        } else if (amountOfCity == 76) {
            xfile = "x76.txt";
            yfile = "y76.txt";
            demandfile = "dem76.txt";
        } else {
            System.err.println("No such a File");
        }

        Scanner input;
        //open x file and get x coordinates of cities and create city objects and set x and index values
        File f = new File(xfile);
        input = new Scanner(f);
        while (input.hasNext()) {
            String index = input.next();
            String xcoordinate = input.next();
            //System.out.println("index: " + index + " x: " + xcoordinate);
            City c = new City();
            c.setIndex(Integer.parseInt(index));
            c.setX(Integer.parseInt(xcoordinate));
            cityList.add(c);
        }

        //open y file and get y coordinates of cities
        int counter = 0;
        f = new File(yfile);
        input = new Scanner(f);
        while (input.hasNext()) {
            String index = input.next();
            String ycoordinate = input.next();

            cityList.get(counter).setY(Integer.parseInt(ycoordinate));
            counter++;
        }

        //open demand file and get y coordinates of cities
        counter = 0;
        f = new File(demandfile);
        input = new Scanner(f);
        while (input.hasNext()) {
            String index = input.next();
            String demand = input.next();

            cityList.get(counter).setDemand(Integer.parseInt(demand));
            counter++;
        }
    }

    private static void prepareDistanceTable() {
        double distance;
        for (int i = 0; i < amountOfCity; i++) {
            for (int j = 0; j < amountOfCity; j++) {
                distance = Math.hypot(cityList.get(i).getX() - cityList.get(j).getX(), cityList.get(i).getY() - cityList.get(j).getY());
                String str = String.format("%1.2f", distance);
                distanceTable[i][j] = Double.valueOf(str);
            }
        }

    }

    private static void getPrimitiveGeneration() {
        int counter;
        int randomChromosome;

        // create creatures and add them into generation[] 
        for (int i = 0; i < populationOfgeneration; i++) {
            Creature creature = new Creature(amountOfCity);    //create default creature
            counter = 0;
            //define 1 chromosomes in DNA of current creature randomly untill ones is equal to the pmedian
            while (counter < pmedian) {
                randomChromosome = random.nextInt(amountOfCity);
                if (creature.getChromosome(randomChromosome) != 1) {
                    creature.setChromosome(randomChromosome, 1);
                    counter++;
                }
            }
            findFitness4Creature(creature);     // calculate fitness value 

            generation.add(creature);           //add into generation[]
        }
    }

    private static void findFitness4Creature(Creature creature) {
        double sum = 0;
        ArrayList<Integer> openedCityIndexes = creature.getOnesIndexes(); // get opened city indexes which is based on 1 chromosomes in gene of the creature
        double closestOpenedCityDistance;
        for (int i = 0; i < amountOfCity; i++) {
            if (creature.getChromosome(i) == 0) {
                closestOpenedCityDistance = getClosestOpenedCity(i, creature);
                sum = sum + (cityList.get(i).getDemand() * closestOpenedCityDistance);

            }
        }
        String str = String.format("%1.2f", sum);
        sum = Double.valueOf(str);
        creature.setFitness(sum);

    }

    private static double getClosestOpenedCity(int currentCityIndex, Creature creature) {

        ArrayList<Integer> openedCityIndexes = creature.getOnesIndexes();
        double closestOpenedCityDistance = distanceTable[currentCityIndex][openedCityIndexes.get(0)]; //assume refCity value as closestDistance at the beginning of search operation

        //search whether ther is any closer city to the current city in opened cities
        for (int i : openedCityIndexes) {
            if (distanceTable[currentCityIndex][i] - closestOpenedCityDistance < 0) {
                closestOpenedCityDistance = distanceTable[currentCityIndex][i];
            }
        }
        return closestOpenedCityDistance;
    }

    //---------ITERATIVE PART METHODS---------//
    private static void killSomeCreatures() {

        int livingPerc = (int) (populationOfgeneration * 0.3);              //let %30 stay alive 
        int killUntillHere = (int) (populationOfgeneration - livingPerc);

        for (int i = populationOfgeneration - 1; i >= killUntillHere - 1; i--) {   //kill last %30 of generation 
            generation.remove(i);
        }

        int currentPopulation = generation.size();
        for (int i = currentPopulation - 1; i >= livingPerc; i--) {
            if (random.nextInt(2) == 1) {
                generation.remove(i);
            }
        }
    }

    private static void maintainGeneration() {
        int currentPopulation = generation.size();
        Creature parent1;
        Creature parent2;
        for (int i = currentPopulation; i < populationOfgeneration; i++) {
            parent1 = generation.get(random.nextInt(currentPopulation));
            parent2 = generation.get(random.nextInt(currentPopulation));
            while (parent1.equals(parent2)) {
                parent2 = generation.get(random.nextInt(currentPopulation));
            }
            generation.add(getBabyCreature(parent1, parent2));
        }
    }

    private static Creature getBabyCreature(Creature parent1, Creature parent2) {
        int[] parent1Gene = parent1.getGene();
        int[] parent2Gene = parent2.getGene();
        //System.out.print("paren1:" + parent1.getAmountOfOnes() + " parent2:" + parent2.getAmountOfOnes());

        Creature babyCreature = new Creature(amountOfCity);  // Create babyCreature as default
        int[] babyGene = new int[amountOfCity];

        for (int i = 0; i < amountOfCity; i++) {
            if (parent1Gene[i] == parent2Gene[i]) {
                babyGene[i] = parent1Gene[i];
            } else {
                babyGene[i] = random.nextInt(2);
            }
        }

        /*Creature babyCreature = new Creature(amountOfCity);  // Create babyCreature as default
        int[] babyGene = new int[amountOfCity];

        //get halfs of genes from parent1 and parent2 then in babyGene combine them together
        int[] firstArray = Arrays.copyOf(parent1Gene, (int) parent1Gene.length / 2-1);
        int[] secondArray = Arrays.copyOfRange(parent2Gene, parent2Gene.length / 2, parent2Gene.length);
        
        System.arraycopy(firstArray, 0, babyGene, 0, firstArray.length);
        System.arraycopy(secondArray, 0, babyGene, firstArray.length, secondArray.length);
        
        babyCreature.setGene(babyGene); // set this babyGene array as gene[] of babyCrature*/
        ArrayList<Integer> onesIndexes = babyCreature.getOnesIndexes();
        int randomIndex;
        int diff = pmedian - onesIndexes.size();

        if (diff < 0) {
            for (int i = 0; i < Math.abs(diff); i++) {
                babyCreature.setChromosome(onesIndexes.get(random.nextInt(onesIndexes.size())), 0);
                onesIndexes.remove(onesIndexes.get(i));
            }
        } else if (diff > 0) {
            int i = 0;
            while (i != diff) {
                randomIndex = random.nextInt(babyGene.length);
                if (babyCreature.getChromosome(randomIndex) == 0) {
                    babyCreature.setChromosome(randomIndex, 1);
                    i++;
                }
            }
        }

        check4Mutation(babyCreature);
        //System.out.println(" baby:" + babyCreature.getAmountOfOnes() + "\n");
        findFitness4Creature(babyCreature);
        return babyCreature;
    }

    private static void check4Mutation(Creature babyCreature) {

        ArrayList<Integer> onesIndexes = new ArrayList<>(babyCreature.getOnesIndexes());
        if (random.nextInt(100) < 10) {
            babyCreature.setChromosome(onesIndexes.get(random.nextInt(onesIndexes.size())), 0);

            int randomIndex = random.nextInt(amountOfCity);
            while (babyCreature.getChromosome(randomIndex) == 1) {
                randomIndex = random.nextInt(amountOfCity);
            }
            babyCreature.setChromosome(randomIndex, 1);
        }
    }

    private static void endOfProcess() {
        System.out.println("\n\nTHE BEST FITNESS SO FAR: " + generation.get(0).getFitness());
        generation.get(0).getInfo();
    }
}
