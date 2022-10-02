package src;
//import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.HashMap;
import java.util.PriorityQueue;


public class Main { 
    public static void main(String[] args){
        final File validationFile = new File("validation.txt");
        System.out.println("RMSE is " + knnValidation(getRatings(), validationFile));
        test();
}

    public static void test() {
        HashMap<Integer, User> userMap = getRatings();
        final File validationFile = new File("validation.txt");
        final File testFile = new File("test.txt");

        File testPredictions = new File("test-predictions.txt");
        knnTest(userMap, testFile);
    }

    public static HashMap<Integer, User> getRatings(){
        final File ratingsFile = new File("ratings.txt");
        HashMap<Integer, User> initUserMap = new HashMap<Integer, User>();
         
        try{
            Scanner reader = new Scanner(ratingsFile);
            while(reader.hasNextLine()){
                String line = reader.nextLine();
                final int movieId = Integer.parseInt(line.split(";")[0]);
                final int userId = Integer.parseInt(line.split(";")[1]);
                final int movieRating = Integer.parseInt(line.split(";")[2]);
                
                if(!initUserMap.containsKey(userId)){
                    User user = new User(userId);
                    user.addMovie(movieId, movieRating);
                    initUserMap.put(userId, user);
                }else{
                    User user = initUserMap.get(userId);
                    user.addMovie(movieId, movieRating);
                }
            }
            reader.close();

        } catch(FileNotFoundException e){
            System.out.println("This file cannot be found");
            e.printStackTrace();
        }

        return initUserMap;
    }
    

    

    public static double predict(int movieKey, HashMap<Integer, User> userMap){   
        double movieScoreSum = 0.00;
        int n = 0;
        for (int i: userMap.keySet()){
            if(userMap.get(i).getMovies().containsKey(movieKey)){
                //this could be because movieKey is an int while the key is an Integer
                movieScoreSum += userMap.get(i).getMovies().get(movieKey);
                n++;
            }
        }
        return movieScoreSum/n;
    }

    public static double validation(HashMap<Integer, User> userMap){
        final File validationFile = new File("validation.txt");
        double errSum = 0.00;
        int n = 0;

        try{
            Scanner reader = new Scanner(validationFile);
            while(reader.hasNextLine()){
                String line = reader.nextLine();
                int valMovieId = Integer.parseInt(line.split(";")[0]);
                int valUserId = Integer.parseInt(line.split(";")[1]);
                int valMovieRating = Integer.parseInt(line.split(";")[2]);
                
                double squaredErr = Math.pow((predict(valMovieId, userMap) - valMovieRating),2);
                errSum += squaredErr;
                n++;
            }
            reader.close();

        } catch(FileNotFoundException e){
            System.out.println("This file cannot be found");
            e.printStackTrace();
        }
        double err = errSum/n;
        return Math.sqrt(err);

    }

    public static double knn(int userId, int movieId, HashMap<Integer, User> userMap){
        PriorityQueue<User> usersQueue = new PriorityQueue<User>();
        int k = 71;
        User user = userMap.get(userId);
        double sum = 0;
        int n = 0;
        //computes the difference and fills the userQueue
        for(int i: userMap.keySet()){
            if(userMap.get(i).getMovies().containsKey(movieId)){
                User o = userMap.get(i);
                o.computeDistance(user);
                usersQueue.add(o);
            }
        }
        //sum the ratings
        int pollNum = (int) k/2; //weight
        for(int i = 0; i<k; i++){
            User o = usersQueue.poll();//pulls the user
            if(o != null) {
                for(int j = 0; j<pollNum;j++){ //applies weight
                    sum += o.getMovies().get(movieId); //gets the rating
                    n++;
                }
                if(pollNum!= 1){
                    pollNum--; 
                }     
            }
        }
        return sum/n;
    }

    public static double knnValidation(HashMap<Integer, User> userMap, File validationFile){
        double MSE = 0;
        int count = 0;
        try{
            Scanner reader = new Scanner(validationFile);
            while(reader.hasNextLine()){
                double squaredErr = 0;
                String line = reader.nextLine();
                int valMovieRating = Integer.parseInt(line.split(";")[2]);
                int movieId = Integer.parseInt(line.split(";")[0]);
                int userId = Integer.parseInt(line.split(";")[1]);

                double knnPrediction = knn(userId, movieId, userMap);
                if(!new File("validation-predictions").isFile()){
                    try{
                        File validationPredictions = new File("validation-predictions.txt");
                        BufferedWriter writer = new BufferedWriter(new FileWriter(validationPredictions,true));
                        String knnPredictionStr = String.valueOf(knnPrediction);
                        writer.append(knnPredictionStr);
                        writer.append("\n");
                        writer.close();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
                System.out.println("Prediction is " +  knnPrediction);
                System.out.println("Actual Rating is " + valMovieRating);
                squaredErr = Math.pow((knnPrediction - valMovieRating), 2);

                if(squaredErr != 0) {
                    MSE += squaredErr;
                    count++;
                }
            }
            reader.close();
    
        } catch(FileNotFoundException e){
            System.out.println("This file cannot be found");
            e.printStackTrace();
        }
        return Math.sqrt(MSE/count);
    }


    public static void knnTest(HashMap<Integer, User> userMap, File testFile){
        try{
            Scanner reader = new Scanner(testFile);
            while(reader.hasNextLine()){
                String line = reader.nextLine();
                int movieId = Integer.parseInt(line.split(";")[0]);
                int userId = Integer.parseInt(line.split(";")[1]);

                double knnPrediction = knn(userId, movieId, userMap);
                System.out.println("Prediction is " +  knnPrediction);
                if(!new File("validation-predictions").isFile()){
                    try{
                        File testPredictions = new File("test-predictions.txt");
                        BufferedWriter writer = new BufferedWriter(new FileWriter(testPredictions,true));
                        String knnPredictionStr = String.valueOf(knnPrediction);
                        writer.append(knnPredictionStr);
                        writer.append("\n");
                        writer.close();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }
            reader.close();
        } catch(FileNotFoundException e){
            System.out.println("This file cannot be found");
            e.printStackTrace();
        }
    }
}