package src;
import java.io.*;
import java.time.Clock;
import java.time.ZoneId;
import java.util.*;

public class Main { 
    public static void main(String[] args){
        HashMap<Integer, User> userMap = getRatings();
        final File validationFile = new File("validation.txt");
        System.out.println("RMSE is " + knnValidation(userMap, validationFile));
        test(userMap);
}

    public static void test(HashMap<Integer, User> userMap) {
        final File testFile = new File("test.txt");
        Clock clock = Clock.system(ZoneId.systemDefault());
        long initMilli = clock.millis();

        knnTest(userMap, testFile);

        long afterMilli = clock.millis();
        long diffMilli = afterMilli - initMilli;
        System.out.println("Test Predictions took " + diffMilli + "milliseconds");
    }

    public static HashMap<Integer, User> getRatings(){
        final File ratingsFile = new File("ratings.txt");
        HashMap<Integer, User> initUserMap = new HashMap<>();
         
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
        for (int i: userMap.keySet()) {
            if (userMap.get(i).getMovies().containsKey(movieKey)) {
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
        return Math.sqrt(errSum/n);

    }

    public static double knn(int userId, int movieId, HashMap<Integer, User> userMap){
        PriorityQueue<User> usersQueue = new PriorityQueue<>();
        User user = userMap.get(userId);
        double sum = 0;
        int n = 0;
        int k = 71;
        //computes the difference and fills the userQueue
        for(int i: userMap.keySet()){
            if(userMap.get(i).getMovies().containsKey(movieId)){
                User o = userMap.get(i);
                o.computeDistance(user);
                usersQueue.add(o);
            }
        }
        //sum the ratings
        int pollNum = k/2; //weight
        for(int i = 0; i<k; i++){
            User o = usersQueue.poll();//pulls the user
            if(o != null) {
                for(int j = 0; j<pollNum;j++){ //applies weight
                    sum += o.getMovies().get(movieId); //gets and sums the ratings
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
            Clock clock = Clock.systemDefaultZone();
            long initMilli = clock.millis();
            while(reader.hasNextLine()){
                double squaredErr;
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
                //System.out.println("Prediction is " +  knnPrediction);
                //System.out.println("Actual Rating is " + valMovieRating);
                squaredErr = Math.pow((knnPrediction - valMovieRating), 2);

                if(squaredErr != 0) {
                    MSE += squaredErr;
                    count++;
                }
            }
            reader.close();
            long afterMilli = clock.millis();
            long diffMilli = afterMilli - initMilli;
            System.out.println("Validation Predictions took " + diffMilli);
    
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
                //System.out.println("Prediction is " +  knnPrediction);
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